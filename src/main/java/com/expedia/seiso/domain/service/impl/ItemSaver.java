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

import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.entity.Item;
import com.expedia.serf.service.PersistenceInterceptor;
import com.expedia.serf.util.SerfReflectionUtils;

/**
 * Service delegate to save items to the database.
 * 
 * @author Willie Wheeler
 */
@Component
@Transactional
public class ItemSaver {
	@Autowired private ItemMerger itemMerger;
	@Autowired private Repositories repositories;
	
	@Getter
	private final Map<Class<?>, PersistenceInterceptor> persistenceInterceptorMap = new HashMap<>();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void create(@NonNull Item item, boolean mergeAssociations) {
		val itemClass = item.getClass();
		val persistenceInterceptor = persistenceInterceptorMap.get(itemClass);
		val repo = (CrudRepository) repositories.getRepositoryFor(itemClass);
		
		val itemToSave = SerfReflectionUtils.createInstance(itemClass);
		itemMerger.merge(item, itemToSave, mergeAssociations);
		
		if (persistenceInterceptor == null) {
			repo.save(itemToSave);
		} else {
			persistenceInterceptor.preCreate(itemToSave);
			repo.save(itemToSave);
			persistenceInterceptor.postCreate(itemToSave);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void update(@NonNull Item itemData, @NonNull Item itemToSave, boolean mergeAssociations) {
		val itemClass = itemData.getClass();
		val persistenceInterceptor = persistenceInterceptorMap.get(itemClass);
		val repo = (CrudRepository) repositories.getRepositoryFor(itemClass);
		
		itemMerger.merge(itemData, itemToSave, mergeAssociations);
		
		if (persistenceInterceptor == null) {
			repo.save(itemToSave);
		} else {
			persistenceInterceptor.preUpdate(itemToSave);
			repo.save(itemToSave);
			persistenceInterceptor.postUpdate(itemToSave);
		}
	}
}
