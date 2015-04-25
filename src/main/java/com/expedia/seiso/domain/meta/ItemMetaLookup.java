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
package com.expedia.seiso.domain.meta;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.repository.support.Repositories;

import com.expedia.seiso.NotFoundException;
import com.expedia.serf.ann.RestResource;

/**
 * Item metadata lookup.
 * 
 * @author Willie Wheeler
 */
@SuppressWarnings("rawtypes")
@XSlf4j
public class ItemMetaLookup {
	@Autowired private Repositories repositories;
	
	private final Map<String, Class> itemClassesByRepoKey = new HashMap<>();
	private final Map<Class, ItemMeta> itemMetasByItemClass = new HashMap<>();

	@PostConstruct
	public void postConstruct() {
		for (val itemClass : repositories) {
			log.trace("Registering item type: {}", itemClass.getName());
			val repoInfo = repositories.getRepositoryInformationFor(itemClass);
			val repoInterface = repoInfo.getRepositoryInterface();
			mapItemClass(itemClass, repoInterface);
			mapItemMeta(itemClass, repoInterface);
		}
	}

	private void mapItemClass(Class itemClass, Class repoInterface) {
		val ann = AnnotationUtils.findAnnotation(repoInterface, RestResource.class);
		if (ann == null) {
			log.warn("No @RestResource for {}", repoInterface.getSimpleName());
		} else {
			val repoKey = ann.path();
			log.info("Mapping repo key {} to item class {}", repoKey, itemClass.getName());
			itemClassesByRepoKey.put(repoKey, itemClass);
		}
	}

	private void mapItemMeta(Class itemClass, Class repoInterface) {
		val repoInfo = repositories.getRepositoryInformationFor(itemClass);
		val isPagingRepo = repoInfo.isPagingRepository();
		itemMetasByItemClass.put(itemClass, new ItemMetaImpl(itemClass, repoInterface, isPagingRepo));
	}

	/**
	 * Returns the item class for the given repository key.
	 * 
	 * @param repoKey
	 *            repository key
	 * @return item class for the given repo key
	 * @throws NotFoundException
	 *             if there's no item class for the given repo key
	 */
	public Class getItemClass(@NonNull String repoKey) {
		val itemClass = itemClassesByRepoKey.get(repoKey);
		if (itemClass == null) {
			throw new NotFoundException("No item class for repo key: " + repoKey);
		}
		return itemClass;
	}
	
	public ItemMeta getItemMeta(@NonNull String repoKey) {
		val itemClass = getItemClass(repoKey);
		return getItemMeta(itemClass);
	}
	
	/**
	 * Returns the metadata object for the given class.
	 * 
	 * @param itemClass
	 *            item class
	 * @return metadata object for the given class
	 * @throws NotFoundException
	 *             if there's no metadata object for the given class
	 */
	public ItemMeta getItemMeta(@NonNull Class<?> itemClass) {
		val meta = itemMetasByItemClass.get(itemClass);
		if (meta == null) {
			throw new NotFoundException("No meta for item class: " + itemClass.getName());
		}
		return meta;
	}
}
