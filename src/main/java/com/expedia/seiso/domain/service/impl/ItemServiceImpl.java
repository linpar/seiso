/* 
 * Copyright 2013-2015 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.expedia.seiso.domain.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.adapter.RepoAdapterLookup;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.seiso.hypermedia.LinkFactory;
import com.expedia.serf.exception.ResourceNotFoundException;
import com.expedia.serf.util.ErrorObject;
import com.expedia.serf.util.ResourceValidationErrorFactory;
import com.expedia.serf.util.SaveAllResult;
import com.expedia.serf.util.SerfCollectionUtils;

/**
 * <p>
 * CRUD (create, read, update, delete) service implementation.
 * </p>
 * <p>
 * To avoid polluting domain code with integration code, notifications don't happen here. Instead,
 * {@link com.expedia.seiso.gateway.impl.NotificationAspect} handles those.
 * </p>
 * 
 * @author Willie Wheeler
 */
@Service
@Transactional(readOnly = true)
@XSlf4j
public class ItemServiceImpl implements ItemService {
	@Autowired private Repositories repositories;
	@Autowired private RepoAdapterLookup repoAdapterLookup;
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private ItemMerger itemMerger;
	@Autowired private ItemDeleter itemDeleter;
	@Autowired private ItemSaver itemSaver;
	@Autowired private TransactionTemplate txTemplate;
	@Autowired private Validator validator;
	@Autowired @Qualifier("linkFactoryV1") private LinkFactory linkFactoryV1;
//	@Autowired @Qualifier("linkFactoryV2") private LinkFactory linkFactoryV2;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	/**
	 * Using {@link Propagation.NEVER} because we don't want a single error to wreck the entire operation.
	 */
	@Override
	@Transactional(propagation = Propagation.NEVER)
	public SaveAllResult saveAll(
			@NonNull Class itemClass,
			@NonNull List<? extends Item> items,
			boolean mergeAssociations) {
		
		val numItems = items.size();
		val itemClassName = itemClass.getSimpleName();
		log.info("Batch saving {} items ({})", numItems, itemClass.getSimpleName());
		
		val errors = new ArrayList<ErrorObject>();

		for (val item : items) {
			val itemKey = item.itemKey();
			
			// First validate the item. If this fails then don't try to save it.
			// Not sure that we want the URIs inside the service here. Am thinking that once we get
			// into the service, we're not dealing with resources anymore. But we're doing just-in-time
			// validation here, and when there's an issue we have to be able to report the issue in
			// terms the user understands (i.e. URIs, resources). Might want to do a translation from item
			// to URI. [WLW]
			BindException bindException = new BindException(item, itemKey.toString());
			validator.validate(item, bindException);
			if (bindException.hasErrors()) {
				val itemLinks = linkFactoryV1.getItemLinks();
				val itemUri = itemLinks.itemLink(item).getHref();
				errors.add(ResourceValidationErrorFactory.buildFrom(itemUri, bindException));
				continue;
			}
			
			try {
				// Have to doInTransaction() since calling save() happens behind the transactional proxy.
				// Also, see http://stackoverflow.com/questions/5568409/java-generics-void-void-types
				txTemplate.execute(new TransactionCallback<Void>() {

					@Override
					public Void doInTransaction(TransactionStatus status) {
						save(item, mergeAssociations);
						return null;
					}
				});
			} catch (RuntimeException e) {
				e.printStackTrace();
				val message = e.getClass() + ": " + e.getMessage();
				errors.add(new ErrorObject(itemKey.toString(), message));
			}
		}

		val numErrors = errors.size();
		if (numErrors == 0) {
			log.info("Batch saved {} items ({}) with no errors", numItems, itemClassName);
		} else {
			log.warn("Batch saved {} items ({}) with {} errors", numItems, itemClassName, numErrors);
		}

		return new SaveAllResult(numItems, errors);
	}

	@Override
	@Transactional(readOnly = false)
	public void save(@NonNull Item itemData, boolean mergeAssociations) {
		val itemKey = itemData.itemKey();
		if (itemKey == null) {
			// No key, so the item is new.
			itemSaver.create(itemData, mergeAssociations);
		} else {
			val itemToSave = doFind(itemKey);
			if (itemToSave == null) {
				// Has key, but item is new.
				itemSaver.create(itemData, mergeAssociations);
			} else {
				// Item already exists in database.
				if (itemData instanceof Node) {
					
					// Special logic to handle diamond dependencies per https://github.com/ExpediaDotCom/seiso/issues/33.
					// When moving a node, we need to delete the old node and create the new node. The deletion and creation
					// automatically cascades to node IP addresses and endpoints.
					val oldNode = (Node) itemToSave;
					val oldSiKey = oldNode.getServiceInstance().getKey();
					val newNode = (Node) itemData;
					val newNodeName = newNode.getName();
					val newSiKey = newNode.getServiceInstance().getKey();
					
					log.trace("oldServiceInstance={}, newServiceInstance={}", oldSiKey, newSiKey);
					
					// Sanity check
					if (oldSiKey == null || newSiKey == null) {
						throw new IllegalStateException("Node save failed: null service instance key");
					}
					
					if (newSiKey.equals(oldSiKey)) {
						log.trace("Updating node: {}", newNode);
						itemSaver.update(newNode, oldNode, mergeAssociations);
					} else {
						log.trace("Moving node {} from service instance {} to service instance {}",
								newNodeName, oldSiKey, newSiKey);
						
						log.trace("Deleting node: {}", oldNode.getId());
						itemDeleter.delete(oldNode);
						
						// Hibernate reorders the operations, performing inserts before deletes, so we have to flush the
						// session to force the deletes to happen. See https://forum.hibernate.org/viewtopic.php?t=934483.
						// Note that Gavin says that usually when you delete and then reinsert, you're usually doing it
						// wrong, but I don't think that's the case here. Our delete cascades down to node IP addresses and
						// endpoints, and our reinsertion creates new entities through JPA listeners. So we really do want
						// to wipe out the old entity (or at least its dependencies). [WLW]
						entityManager.flush();
						
						log.trace("Creating node: {}", newNode.getId());
						itemSaver.create(newNode, mergeAssociations);
					}
					
				} else {
					itemSaver.update(itemData, itemToSave, mergeAssociations);
				}
			}
		}
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public List findAll(@NonNull Class itemClass) {
		val repo = getRepositoryFor(itemClass);
		val items = repo.findAll();
		return SerfCollectionUtils.toList(items);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Page findAll(@NonNull Class itemClass, @NonNull Pageable pageable) {
		val repo = (PagingAndSortingRepository) getRepositoryFor(itemClass);
		return repo.findAll(pageable);
	}

	@Override
	public Item find(@NonNull ItemKey key) {
		val item = doFind(key);
		if (item == null) {
			throw new ResourceNotFoundException(key);
		}
		return item;
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(@NonNull Item item) {
		itemDeleter.delete(item);
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(@NonNull ItemKey key) {
		// We look up the actual item here, as opposed to simply calling a delete(key) method, because we want to throw
		// a ResourceNotFoundException if the item doesn't exist.
		itemDeleter.delete(find(key));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private CrudRepository getRepositoryFor(Class<?> itemClass) {
		return (CrudRepository<?, Long>) repositories.getRepositoryFor(itemClass);
	}

	private Item doFind(ItemKey key) {
		return repoAdapterLookup.getRepoAdapterFor(key.getItemClass()).find(key);
	}
}
