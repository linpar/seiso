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

import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.expedia.seiso.core.exception.ResourceNotFoundException;
import com.expedia.seiso.core.util.CollectionsUtils;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.adapter.RepoAdapterLookup;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.seiso.domain.service.SaveAllError;
import com.expedia.seiso.domain.service.SaveAllResponse;

/**
 * <p>
 * CRUD (create, read, update, delete) service implementation.
 * </p>
 * <p>
 * To avoid polluting domain code with integration code, notifications don't happen here. Instead,
 * {@link com.expedia.seiso.gateway.aop.NotificationAspect} handles those.
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
	
	/**
	 * Using {@link Propagation.NEVER} because we don't want a single error to wreck the entire operation.
	 */
	@Override
	@Transactional(propagation = Propagation.NEVER)
	public SaveAllResponse saveAll(
			@NonNull Class itemClass,
			@NonNull List<? extends Item> items,
			boolean mergeAssociations) {
		
		val numItems = items.size();
		val itemClassName = itemClass.getSimpleName();
		log.info("Batch saving {} items ({})", numItems, itemClass.getSimpleName());
		
		val errors = new ArrayList<SaveAllError>();

		for (val item : items) {
			try {
				save(item, mergeAssociations);
			} catch (RuntimeException e) {
				e.printStackTrace();
				val message = e.getClass() + ": " + e.getMessage();
				errors.add(new SaveAllError(item.itemKey(), message));
			}
		}

		val numErrors = errors.size();
		if (numErrors == 0) {
			log.info("Batch saved {} items ({}) with no errors", numItems, itemClassName);
		} else {
			log.warn("Batch saved {} items ({}) with {} errors: {}", numItems, itemClassName, numErrors, errors);
		}

		return new SaveAllResponse(numItems, numErrors, errors);
	}

	@Override
	@Transactional(readOnly = false)
	public void save(@NonNull Item itemData, boolean mergeAssociations) {
		val itemToSave = doFind(itemData.itemKey());
		if (itemToSave == null) {
			itemSaver.create(itemData, mergeAssociations);
		} else {
			itemSaver.update(itemData, itemToSave, mergeAssociations);
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List findAll(@NonNull Class itemClass) {
		val repo = getRepositoryFor(itemClass);
		val items = repo.findAll();
		return CollectionsUtils.toList(items);
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
