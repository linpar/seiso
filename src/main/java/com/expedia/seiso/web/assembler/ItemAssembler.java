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
package com.expedia.seiso.web.assembler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lombok.NonNull;
import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mapping.model.BeanWrapper;
import org.springframework.data.repository.support.Repositories;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.meta.DynaItem;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.web.dto.MapItemDto;

// TODO Need to separate graph initialization from assembly, since graph initialization belongs in the domain layer
// (and involves transactions) whereas assembly belongs in the web layer. Right now we are mixing the two concerns and
// the result is that we're creating transactional proxies for controllers, which we don't want to do. [WLW]

/**
 * Projection-aware DTO assembler. We do the work here instead of inside a Jackson serializer because here we can see
 * the projection, whereas there we can't.
 * 
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@Component
public class ItemAssembler {
	@Autowired private Repositories repositories;
	@Autowired private EntityLinks links;
	@Autowired private ItemMetaLookup itemMetaLookup;

	public MapItemDto toDto(Item item) {
		return toDto(item, ProjectionNode.FLAT_PROJECTION_NODE);
	}

	public MapItemDto toDto(Item item, @NonNull final ProjectionNode projectionNode) {
		if (item == null) {
			return null;
		}

		val itemClass = item.getClass();
		val itemMeta = itemMetaLookup.getItemMeta(itemClass);
		val persistentEntity = repositories.getPersistentEntity(itemClass);
		val itemWrapper = BeanWrapper.create(item, null);

		val model = new TreeMap<String, Object>();
		persistentEntity.doWithProperties(new ItemPropertyHandler(itemWrapper, model));
		persistentEntity.doWithAssociations(new ItemAssociationHandler(this, projectionNode, itemWrapper, model));
		doSpecialNonPersistentProperties(item, model);

		val selfUri = (itemMeta.isExported() ? getSelfLinkFor(item).getHref() : null);

		return new MapItemDto(selfUri, item.getId(), model);
	}

	// This is a temporary hack to handle special-case non-persistent properties.
	// Specifically, we need to be able to map NodeIpAddress.aggregateRotationStatus. [WLW]
	@Deprecated
	private void doSpecialNonPersistentProperties(Item item, Map<String, Object> model) {
		val itemClass = item.getClass();
		if (itemClass == NodeIpAddress.class) {
			val nip = (NodeIpAddress) item;
			model.put("aggregateRotationStatus", nip.getAggregateRotationStatus());
		}
	}

	public List<MapItemDto> toDtoList(List<? extends Item> items) {
		return toDtoList(items, ProjectionNode.FLAT_PROJECTION_NODE);
	}

	// FIXME Need links on the list, so need a dedicated DTO class
	public List<MapItemDto> toDtoList(List<? extends Item> items, @NonNull ProjectionNode projectionNode) {
		if (items == null) {
			return null;
		}
		val itemDtos = new ArrayList<MapItemDto>();
		for (val item : items) {
			itemDtos.add(toDto(item, projectionNode));
		}
		return itemDtos;
	}

	public PagedResources<MapItemDto> toDtoPage(Page<? extends Item> items) {
		return toDtoPage(items, ProjectionNode.FLAT_PROJECTION_NODE);
	}

	// FIXME Temporary implementation that assumes a single page
	public PagedResources<MapItemDto> toDtoPage(Page<? extends Item> items, @NonNull ProjectionNode projectionNode) {
		if (items == null) {
			return null;
		}

		val pageNumber = items.getNumber();
		val pageSize = items.getSize();
		val totalElems = items.getTotalElements();

		val itemDtos = toDtoList(items.getContent(), projectionNode);
		val pageMeta = new PageMetadata(pageSize, pageNumber, totalElems);
		return new PagedResources<MapItemDto>(itemDtos, pageMeta);
	}

	private Link getSelfLinkFor(Item item) {
		val itemClass = item.getClass();
		val itemWrapper = new DynaItem(item);
		val itemKey = itemWrapper.getMetaKey();

		// TODO Probably want to enforce trim/lowercase in the item itself. [WLW]
		val cleanItemKey = itemKey.toString().trim().toLowerCase();

		return links.linkForSingleResource(itemClass, cleanItemKey).withSelfRel();
	}
}
