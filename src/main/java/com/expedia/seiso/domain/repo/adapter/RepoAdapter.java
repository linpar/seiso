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

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.key.ItemKey;

/**
 * Supports repo access via a uniform {@link ItemKey} identifier.
 * 
 * @author Willie Wheeler
 * 
 * @param <T>
 *            item type
 */
public interface RepoAdapter {

	boolean supports(Class<?> itemClass);

	/**
	 * Finds the corresponding persistent item.
	 * 
	 * @param key
	 *            an item serving as a lookup key
	 * @return persistent item, or null if it doesn't exist
	 */
	Item find(ItemKey key);

	void delete(ItemKey key);
}
