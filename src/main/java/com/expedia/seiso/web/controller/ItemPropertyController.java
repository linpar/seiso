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
package com.expedia.seiso.web.controller;

import java.util.List;

import javax.transaction.Transactional;

import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.domain.meta.DynaItem;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.seiso.web.assembler.ItemAssembler;
import com.expedia.seiso.web.assembler.ProjectionNode;

// Hm, how do we want to handle endpoints, or endpoint rotation status? This doesn't seem right:
// 
//     /nodes/foo/ip-addresses/1.2.3.4/endpoints/8080/rotation-status
// 
// Maybe it's OK:
// 
//     http://stackoverflow.com/questions/21142158/restful-resource-hierarchy

// TODO For now, putting @Transactional here because item assembly requires it. If we want to move it to ItemAssembler
// (and I think we do) then we will want to have an ItemAssembler interface and implementation class. [WLW]

@RestController
@RequestMapping(Controllers.REQUEST_MAPPING_VERSION)
@Transactional
@XSlf4j
public class ItemPropertyController {
	@Autowired
	private ItemMetaLookup itemMetaLookup;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemAssembler itemAssembler;
	
	@RequestMapping(value = "/{repoKey}/{itemKey}/{propKey}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object getProperty(@PathVariable String repoKey, @PathVariable String itemKey, @PathVariable String propKey) {

		log.trace("repoKey={}, itemKey={}, propKey={}", repoKey, itemKey, propKey);
		val itemClass = itemMetaLookup.getItemClass(repoKey);
		val itemMeta = itemMetaLookup.getItemMeta(itemClass);
		val item = itemService.find(new SimpleItemKey(itemClass, itemKey));
		val dynaItem = new DynaItem(item);
		val propName = itemMeta.getPropertyName(propKey);
		val propValue = dynaItem.getPropertyValue(propName);

		if (propValue instanceof Item) {
			val propItem = (Item) propValue;
			val projectionNode = extractProjectionNode(propItem);
			return itemAssembler.toDto(propItem, projectionNode);
		} else if (propValue instanceof List) {
			val list = (List) propValue;
			val projectionNode = extractProjectionNode(list);
			return itemAssembler.toDtoList(list, projectionNode);
		} else {
			String msg = "DTO assembly for type " + propValue.getClass().getName() + " not supported";
			throw new UnsupportedOperationException(msg);
		}
	}

	// @RequestMapping(
	// value = "/{repoKey}/{itemKey}/{propKey}/{elemKey}",
	// method = RequestMethod.GET,
	// produces = MediaType.APPLICATION_JSON_VALUE)
	// public Object getPropertyElement(
	// @PathVariable String repoKey,
	// @PathVariable String itemKey,
	// @PathVariable String propKey,
	// @PathVariable String elemKey) {
	//
	// log.trace("repoKey={}, itemKey={}, propKey={}, elemKey={}", repoKey, itemKey, propKey, elemKey);
	// val itemClass = itemMetaLookup.getItemClass(repoKey);
	// val itemMeta = itemMetaLookup.getItemMeta(itemClass);
	// val item = itemService.findByKey(itemClass, itemKey);
	// val dynaItem = new DynaItem(itemClass, item);
	// val propName = itemMeta.getPropertyName(propKey);
	//
	// // TODO Use the property meta model (or contents, if I must) to figure out which repo I need to query for the
	// // element. I envision something like repo.findByKeys(Serializable... keys). [WLW]
	//
	// throw new UnsupportedOperationException("Not yet implemented");
	// }

	private ProjectionNode extractProjectionNode(Item item) {
		val propClass = item.getClass();
		val propMeta = itemMetaLookup.getItemMeta(propClass);

		// Currently we just show the default projection. It would be easy enough to add support for other projections
		// if we wanted to. [WLW]
		return propMeta.getProjectionNode(Projection.Cardinality.SINGLE, Projection.DEFAULT);
	}

	private ProjectionNode extractProjectionNode(List<?> list) {

		// Not sure I'm happy with this approach. Would it make more sense to require collection properties to declare
		// their type param (e.g. List<NodeIpAddress> instead of List) and then use reflection to grab the type? [WLW]
		if (list.isEmpty()) {
			return ProjectionNode.FLAT_PROJECTION_NODE;
		}

		val firstElem = list.get(0);
		val elemClass = firstElem.getClass();
		val elemMeta = itemMetaLookup.getItemMeta(elemClass);

		// Currently we just show the default projection. It would be easy enough to add support for other projections
		// if we wanted to. [WLW]
		return elemMeta.getProjectionNode(Projection.Cardinality.COLLECTION, Projection.DEFAULT);
	}
}
