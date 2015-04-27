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

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.SimpleAssociationHandler;
import org.springframework.data.mapping.model.BeanWrapper;

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.hypermedia.ItemLinks;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.serf.ann.RestResource;
import com.expedia.serf.hypermedia.Resource;

/**
 * @author Willie Wheeler
 */
@AllArgsConstructor
@XSlf4j
public class ItemAssociationHandler implements SimpleAssociationHandler {
	private final ResourceAssembler assembler;
	private final ItemLinks itemLinks;
	private final ApiVersion apiVersion;
	private final ProjectionNode projection;
	private final BeanWrapper<? extends Item> wrapper;
	private final Resource resource;
	private final boolean topLevel;

	@Override
	public void doWithAssociation(Association<? extends PersistentProperty<?>> assoc) {
		val item = wrapper.getBean();
		
		// val doesn't work here for some reason.
		PersistentProperty<?> prop = assoc.getInverse();
		val propName = prop.getName();
		val propType = prop.getType();
		val child = projection.getChild(propName);
		
		// Link
		if (topLevel) {
			val restResource = prop.findAnnotation(RestResource.class);
			val path = (restResource == null ? propName : restResource.path());
			resource.addLink(itemLinks.itemPropertyLink(item, path));
		}
		
		// Property
		if (child != null) {
			if (Item.class.isAssignableFrom(propType)) {
				val propEntity = (Item) wrapper.getProperty(prop);
				val propResource = assembler.toResource(apiVersion, propEntity, child, false);
				resource.setAssociation(propName, propResource);
			} else if (List.class.isAssignableFrom(propType)) {
				val propEntityList = (List<?>) wrapper.getProperty(prop);
				val propResourceList = assembler.toResourceList(apiVersion, propEntityList, child);
				resource.setAssociation(propName, propResourceList);
			} else {
				log.warn("Don't know how to handle association type {}", propType);
			}
		}
	}
}
