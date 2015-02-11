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

import java.beans.PropertyDescriptor;
import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.repo.NodeIpAddressRepo;
import com.expedia.seiso.domain.repo.ServiceInstancePortRepo;
import com.expedia.seiso.domain.repo.adapter.RepoAdapterLookup;

/**
 * Merges client-submitted item data into persistent (or to-be-persistent) items.
 * 
 * @author Willie Wheeler
 */
@Component
@RequiredArgsConstructor
@XSlf4j
public class ItemMerger {
	@NonNull private RepoAdapterLookup repoAdapterLookup;
	
	// FIXME Temporary til we can map URIs to items.
	@Autowired private NodeIpAddressRepo nodeIpAddressRepo;
	@Autowired private ServiceInstancePortRepo serviceInstancePortRepo;
	
	/**
	 * Merges a source item into a destination item so we can write the destination item to the database.
	 * 
	 * @param src
	 *            Source item
	 * @param dest
	 *            Destination item
	 * @param mergeAssociations
	 *            Flag indicating whether to merge the associations.
	 */
	public void merge(Item src, Item dest, boolean mergeAssociations) {
		val itemClass = src.getClass();
		val propDescs = BeanUtils.getPropertyDescriptors(itemClass);
		for (val propDesc : propDescs) {
			val propName = propDesc.getName();
			if (isMergeable(propName)) {
				val propClass = propDesc.getPropertyType();
				if (BeanUtils.isSimpleProperty(propClass)) {
					mergeSimpleProperty(src, dest, propDesc);
				} else if (Item.class.isAssignableFrom(propClass)) {
					if (mergeAssociations) {
						mergeSingleAssociation(src, dest, propClass, propName);
					}
				} else if (List.class.isAssignableFrom(propClass)) {
					// Skip lists. No warning need.
				} else {
					log.warn("Property '{}' has unrecognized class {}; skipping", propName, propClass.getSimpleName());
				}
			}
		}
	}

	private boolean isMergeable(String propName) {
		// Prevent clients from supplying IDs.
		return !("class".equals(propName) || "id".equals(propName));
	}

	@SneakyThrows
	private void mergeSimpleProperty(Item src, Item dest, PropertyDescriptor propDesc) {
		val getter = propDesc.getReadMethod();
		val setter = propDesc.getWriteMethod();

		if (getter == null || setter == null) {
			log.trace("Skipping simple property: {}", propDesc.getName());
			return;
		}

		val propValue = getter.invoke(src);
		setter.invoke(dest, propValue);
	}

	/**
	 * @param src
	 *            non-persistent data we want to merge into the persistent entity
	 * @param dest
	 *            persistent entity
	 * @param assocClass
	 * @param assocName
	 */
	@SneakyThrows
	@SuppressWarnings("rawtypes")
	private void mergeSingleAssociation(Item src, Item dest, Class assocClass, String assocName) {
		val itemDesc = BeanUtils.getPropertyDescriptor(src.getClass(), assocName);
		log.trace("src.class={}, dest.class={}, itemDesc={}",
				src.getClass().getName(),
				dest.getClass().getName(),
				itemDesc);

		val getter = itemDesc.getReadMethod();
		val setter = itemDesc.getWriteMethod();

		if (getter == null || setter == null) {
			log.trace("Skipping single association: {}", itemDesc.getName());
			return;
		}

		val assocData = (Item) getter.invoke(src);
		log.trace("assocData={}", assocData);

		Item persistentAssoc = null;
		if (assocData != null) {
			
			// FIXME This fails when the associated item isn't sufficiently hydrated to generate its item key. This
			// happens for example when trying to generate a NodeIpAddress key when we haven't loaded the backing Node.
			// Also happens with ServiceInstancePorts missing a backing service instance.
			// https://github.com/ExpediaDotCom/seiso/issues/54
			// 
			// Need to get away from using ItemKeys here, and use the URI instead. We need a way (besides controllers,
			// which help only at the top level) to resolve URIs into the objects that they reference.
			// 
			// In the meantime we'll just use the ID to support v1.
			
			if (assocClass == NodeIpAddress.class) {
				persistentAssoc = nodeIpAddressRepo.findOne(assocData.getId());
			} else if (assocClass == ServiceInstancePort.class) {
				persistentAssoc = serviceInstancePortRepo.findOne(assocData.getId());
			} else {
				val repoAdapter = repoAdapterLookup.getRepoAdapterFor(assocClass);
				val assocKey = assocData.itemKey();
				persistentAssoc = repoAdapter.find(assocKey);
			}
		}

		setter.invoke(dest, persistentAssoc);
	}
}
