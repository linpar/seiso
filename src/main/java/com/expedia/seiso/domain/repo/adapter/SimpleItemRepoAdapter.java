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
package com.expedia.seiso.domain.repo.adapter;

import java.lang.reflect.Method;

import lombok.NonNull;
import lombok.val;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.domain.meta.ItemMetaLookup;


/**
 * Repo adapter for single-key entities. There is a single adapter instance spanning all such repos, as opposed to each
 * such repo getting its own adapter instance.
 * 
 * @author Willie Wheeler
 */
@Component
public class SimpleItemRepoAdapter implements RepoAdapter {
	private Repositories repositories;
	private ItemMetaLookup itemMetaLookup;
	
	public SimpleItemRepoAdapter(@NonNull Repositories repositories, @NonNull ItemMetaLookup itemMetaLookup) {
		this.repositories = repositories;
		this.itemMetaLookup = itemMetaLookup;
	}
	
	@Override
	public boolean supports(@NonNull Class<?> itemClass) {
		return getFindByKeyMethod(itemClass) != null;
	}

	@Override
	public Item find(@NonNull ItemKey key) {
		val itemClass = key.getItemClass();
		val findByKeyMethod = getFindByKeyMethod(itemClass);
		val repo = (CrudRepository) repositories.getRepositoryFor(itemClass);
		val simpleItemKey = (SimpleItemKey) key;
		return (Item) ReflectionUtils.invokeMethod(findByKeyMethod, repo, simpleItemKey.getValue());
	}

	@Override
	public void delete(@NonNull ItemKey key) {
		val itemClass = key.getItemClass();
		val repo = (CrudRepository) repositories.getRepositoryFor(itemClass);
		val item = find(key);
		if (item != null) {
			repo.delete(item);
		}
	}

	private Method getFindByKeyMethod(Class<?> itemClass) {
		val itemMeta = itemMetaLookup.getItemMeta(itemClass);
		return itemMeta.getRepositoryFindByKeyMethod();
	}
}
