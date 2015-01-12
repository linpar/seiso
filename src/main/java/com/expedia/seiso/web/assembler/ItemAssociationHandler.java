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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.SimpleAssociationHandler;
import org.springframework.data.mapping.model.BeanWrapper;

import com.expedia.seiso.core.ann.RestResource;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.web.hateoas.BaseResource;
import com.expedia.seiso.web.hateoas.link.ItemLinks;

/**
 * @author Willie Wheeler
 */
@RequiredArgsConstructor
@XSlf4j
public class ItemAssociationHandler implements SimpleAssociationHandler {
	@NonNull private final ItemAssembler assembler;
	@NonNull private final ItemLinks itemLinksV2;
	@NonNull private final ProjectionNode projection;
	@NonNull private final BeanWrapper<? extends Item> wrapper;
	@NonNull private final BaseResource baseResource;

	@Override
	public void doWithAssociation(Association<? extends PersistentProperty<?>> assoc) {
		val item = wrapper.getBean();
		
		// val doesn't work here for some reason.
		PersistentProperty<?> prop = assoc.getInverse();
		val propName = prop.getName();
		val propType = prop.getType();
		val child = projection.getChild(propName);
		
		// Link
		val restResource = prop.findAnnotation(RestResource.class);
		val path = (restResource == null ? propName : restResource.path());
		baseResource.addV2Link(itemLinksV2.itemPropertyLink(item, path));
		
		// Property
		if (child != null) {
			if (Item.class.isAssignableFrom(propType)) {
				val propEntity = (Item) wrapper.getProperty(prop);
				val propBaseResource = assembler.toBaseResource(propEntity, child, false);
				baseResource.setAssociation(propName, propBaseResource);
			} else if (List.class.isAssignableFrom(propType)) {
				val propEntityList = (List<?>) wrapper.getProperty(prop);
				val propBaseResourceList = assembler.toBaseResourceList(propEntityList, child);
				baseResource.setAssociation(propName, propBaseResourceList);
			} else {
				log.warn("Don't know how to handle association type {}", propType);
			}
		}
	}
}
