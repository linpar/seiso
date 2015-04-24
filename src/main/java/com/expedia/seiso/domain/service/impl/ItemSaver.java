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

import javax.transaction.Transactional;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;

import com.expedia.rf.util.ReflectionUtils;
import com.expedia.seiso.domain.entity.Item;

/**
 * Service delegate to save items to the database.
 * 
 * @author Willie Wheeler
 */
@Component
@Transactional
@RequiredArgsConstructor
public class ItemSaver {
	@NonNull private Repositories repositories;
	@NonNull private ItemMerger itemMerger;	
	
	public void create(@NonNull Item item, boolean mergeAssociations) {
		val itemClass = item.getClass();
		val itemToSave = ReflectionUtils.createInstance(itemClass);
		doSave(item, itemToSave, mergeAssociations);
	}
	
	public void update(@NonNull Item srcItem, @NonNull Item destItem, boolean mergeAssociations) {
		doSave(srcItem, destItem, mergeAssociations);
	}
	
	private void doSave(Item itemData, Item itemToSave, boolean mergeAssociations) {
		itemMerger.merge(itemData, itemToSave, mergeAssociations);
		val itemClass = itemData.getClass();
		val repo = (CrudRepository) repositories.getRepositoryFor(itemClass);
		repo.save(itemToSave);
	}
}
