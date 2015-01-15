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
package com.expedia.seiso.web.controller.delegate;

import java.util.Collections;
import java.util.List;

import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.domain.meta.DynaItem;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.seiso.domain.service.SaveAllResponse;
import com.expedia.seiso.web.assembler.ResourceAssembler;
import com.expedia.seiso.web.controller.PEResourceList;
import com.expedia.seiso.web.hateoas.PagedResources;
import com.expedia.seiso.web.hateoas.Resource;

/**
 * Handles basic REST requests, such as getting, putting and deleting items. This exists as a delegate object so we can
 * reuse it across different API versions. (For example, both v1 and v2 use this.)
 *  
 * @author Willie Wheeler
 */
@Component
@XSlf4j
public class BasicItemDelegate {
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private ItemService itemService;
	@Autowired private ResourceAssembler itemAssembler;
	
	/**
	 * Returns a {@link Resources} or {@link PagedResources}, depending on the repo type.
	 * 
	 * @param repoKey
	 *            repository key
	 * @param view
	 *            view key
	 * @param pageable
	 *            page request parameters
	 * @param params
	 *            all HTTP parameters
	 * @return page of items
	 */
	public Object getAll(
			@NonNull String repoKey,
			@NonNull String view,
			Pageable pageable,
			MultiValueMap<String, String> params) {
		
		val itemClass = itemMetaLookup.getItemClass(repoKey);
		val itemMeta = itemMetaLookup.getItemMeta(itemClass);
		val proj = itemMeta.getProjectionNode(Projection.Cardinality.COLLECTION, view);
		if (itemMeta.isPagingRepo()) {
			val itemPage = itemService.findAll(itemClass, pageable);
			return itemAssembler.toPagedResources(itemClass, itemPage, proj, params);
		} else {
			val itemList = itemService.findAll(itemClass);
			return itemAssembler.toResources(itemClass, itemList, proj);
		}
	}
	
	/**
	 * Returns a single item.
	 * 
	 * @param repoKey
	 *            repository key
	 * @param itemKey
	 *            item key
	 * @param view
	 *            view key
	 * @param params
	 *            all HTTP parameters
	 * @return a single item
	 */
	public Resource getOne(
			@NonNull String repoKey,
			@NonNull String itemKey,
			String view) {
		
		val itemClass = itemMetaLookup.getItemClass(repoKey);
		return getOne(new SimpleItemKey(itemClass, itemKey), view);
	}
	
	public Resource getOne(@NonNull ItemKey itemKey) {
		return getOne(itemKey, Projection.DEFAULT);
	}
	
	public Resource getOne(@NonNull ItemKey itemKey, String view) {
		val item = itemService.find(itemKey);
		val itemMeta = itemMetaLookup.getItemMeta(item.getClass());
		val proj = itemMeta.getProjectionNode(Projection.Cardinality.SINGLE, view);
		return itemAssembler.toResource(item, proj, true);
	}
	
	/**
	 * Returns an item property value.
	 * 
	 * @param repoKey
	 *            repository key
	 * @param itemKey
	 *            item key
	 * @param propKey
	 *            property key
	 * @param view
	 *            view key
	 * @return returns the item property value, which is either a single item or a list of items, depending on the item
	 *         property value type
	 */
	public Object getProperty(
			@NonNull String repoKey,
			@NonNull String itemKey,
			@NonNull String propKey,
			String view) {
		
		log.trace("Getting item property: /{}/{}/{}", repoKey, itemKey, propKey);
		val itemClass = itemMetaLookup.getItemClass(repoKey);
		val itemMeta = itemMetaLookup.getItemMeta(itemClass);
		val item = itemService.find(new SimpleItemKey(itemClass, itemKey));
		val dynaItem = new DynaItem(item);
		val propName = itemMeta.getPropertyName(propKey);
		val propValue = dynaItem.getPropertyValue(propName);
		
		// Use the metamodel to route processing, as opposed to using `propValue instanceof Item` etc. That way we can
		// handle null values too. See https://github.com/ExpediaDotCom/seiso/issues/32
		val propDesc = BeanUtils.getPropertyDescriptor(itemClass, propName);
		val propClass = propDesc.getPropertyType();
		if (Item.class.isAssignableFrom(propClass)) {
			return getItemProperty((Item) propValue, view);
		} else if (List.class.isAssignableFrom(propClass)) {
			return getListProperty((List<?>) propValue, view);
		} else {
			String msg = "Resource assembly for type " + propClass.getName() + " not supported";
			throw new UnsupportedOperationException(msg);
		}
		
		// Do we need to handle paging property lists here?
		// Usually property lists will be reasonably short. But it is easy to imagine real cases where this isn't true,
		// such as a service instance with hundreds of nodes.
	}
	
	public SaveAllResponse postAll(
			@NonNull Class<?> itemClass,
			@NonNull PEResourceList peResourceList,
			boolean mergeAssociations) {
		
		// FIXME The SaveAllResponse contains a SaveAllError, which in turn contains an Item. If the Item has a cycle,
		// then JSON serialization results in a stack overflow exception. [WLW]
		//
		// See
		// http://stackoverflow.com/questions/10065002/jackson-serialization-of-entities-with-birectional-relationships-avoiding-cyc
		// for a possible solution. But do we really want to leave it up to Jackson to decide on the serialized
		// representation, when in general we control that ourselves? We should be assembling a DTO here, or else just
		// returning ID info. [WLW]
		//
		// http://www.cowtowncoder.com/blog/archives/2012/03/entry_466.html [WLW]
		return itemService.saveAll(itemClass, peResourceList, mergeAssociations);
	}
	
	public void put(@NonNull Item item, boolean mergeAssociations) {
		log.trace("Putting item: {}", item.itemKey());
		itemService.save(item, mergeAssociations);
	}
	
	/**
	 * Assigns an item to a given property.
	 * 
	 * @param repoKey
	 *            Repository key
	 * @param itemKey
	 *            Item key
	 * @param propKey
	 *            Property key
	 * @param itemUri
	 *            Item URI, or null to null out the property
	 */
	public void putProperty(
			@NonNull String repoKey,
			@NonNull String itemKey,
			@NonNull String propKey,
			ItemKey propItemKey) {
		
		log.trace("propItemKey={}", propItemKey);
		
		// Metamodel
		val itemClass = itemMetaLookup.getItemClass(repoKey);
		val itemMeta = itemMetaLookup.getItemMeta(itemClass);
		val propName = itemMeta.getPropertyName(propKey);
		
		// Update and save
		val item = itemService.find(new SimpleItemKey(itemClass, itemKey));
		val dynaItem = new DynaItem(item);
		val propItem = (propItemKey == null ? null : itemService.find(propItemKey));
		dynaItem.setPropertyValue(propName, propItem);
		itemService.save(item, true);
	}
	
	/**
	 * Deletes the specified item.
	 * 
	 * @param itemKey
	 *            Item key
	 */
	public void delete(@NonNull ItemKey itemKey) {
		log.trace("Deleting item: {}", itemKey);
		itemService.delete(itemKey);
	}
	
	private Object getItemProperty(Item itemPropValue, String view) {
		if (itemPropValue == null) { return null; }
		val propClass = itemPropValue.getClass();
		val propMeta = itemMetaLookup.getItemMeta(propClass);
		val proj = propMeta.getProjectionNode(Projection.Cardinality.SINGLE, view);
		return itemAssembler.toResource(itemPropValue, proj);
	}
	
	private Object getListProperty(List<?> listPropValue, String view) {
		if (listPropValue == null) { return null; }

		// Not sure I'm happy with this approach. Would it make more sense to require collection properties to declare
		// their type param (e.g. List<NodeIpAddress> instead of List) and then use reflection to grab the type? [WLW]
		if (listPropValue.isEmpty()) { return Collections.EMPTY_LIST; }

		val elemClass = CollectionUtils.findCommonElementType(listPropValue);
		val elemMeta = itemMetaLookup.getItemMeta(elemClass);
		val proj = elemMeta.getProjectionNode(Projection.Cardinality.COLLECTION, view);
		return itemAssembler.toResourceList(listPropValue, proj);
	}
}
