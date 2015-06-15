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

import lombok.val;

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.SeyrenCheck;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.domain.repo.SeyrenCheckRepo;

/**
 * @author Willie Wheeler
 */
public class SeyrenCheckRepoAdapter implements RepoAdapter {
	private SeyrenCheckRepo seyrenCheckRepo;
	
	public SeyrenCheckRepoAdapter(SeyrenCheckRepo seyrenCheckRepo) {
		this.seyrenCheckRepo = seyrenCheckRepo;
	}
	
	@Override
	public boolean supports(Class<?> itemClass) {
		return itemClass == SeyrenCheck.class;
	}

	@Override
	public Item find(ItemKey key) {
		val simpleItemKey = (SimpleItemKey) key;
		return seyrenCheckRepo.findOne((Long) simpleItemKey.getValue());
	}

	@Override
	public void delete(ItemKey key) {
		val simpleItemKey = (SimpleItemKey) key;
		seyrenCheckRepo.delete((Long) simpleItemKey.getValue());
	}
}
