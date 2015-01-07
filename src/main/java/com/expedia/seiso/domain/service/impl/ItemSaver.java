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
import lombok.val;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;

import com.expedia.seiso.core.util.ReflectionUtils;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.repo.adapter.RepoAdapterLookup;

@Component
@Transactional
public class ItemSaver {
	private Repositories repositories;
	private RepoAdapterLookup repoAdapterLookup;
	private ItemMerger itemMerger;

	// FIXME Use the NotificationAspect instead of this.
//	@Autowired private NotificationGateway notificationGateway;
	
	public ItemSaver(
			@NonNull Repositories repositories,
			@NonNull RepoAdapterLookup repoAdapterLookup,
			@NonNull ItemMerger itemMerger) {
		
		this.repositories = repositories;
		this.repoAdapterLookup = repoAdapterLookup;
		this.itemMerger = itemMerger;
	}
	
	public void save(@NonNull Item itemData) {
		val itemClass = itemData.getClass();

		Item itemToSave = doFind(itemData.itemKey());
		val newItem = (itemToSave == null);
		if (newItem) {
			itemToSave = ReflectionUtils.createInstance(itemClass);
		}

		itemMerger.merge(itemData, itemToSave);
		getRepositoryFor(itemClass).save(itemToSave);

		// FIXME Move to NotificationAspect.
		// FIXME This can probably generate a stack overflow, because there are bidirectional associations, and the
		// serialization will just follow the cycle. We need to send the notification once we have the BaseResource. So
		// again push this up. [WLW]
//		log.info("Sending notification");
//		val op = (newItem ? ConfigManagementEvent.OP_CREATE : ConfigManagementEvent.OP_UPDATE);
//		notificationGateway.notify(itemToSave, op);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private CrudRepository getRepositoryFor(Class<?> itemClass) {
		return (CrudRepository<?, Long>) repositories.getRepositoryFor(itemClass);
	}

	private Item doFind(ItemKey key) {
		return repoAdapterLookup.getRepoAdapterFor(key.getItemClass()).find(key);
	}
}
