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
package com.expedia.seiso.web.controller;

import lombok.NonNull;
import lombok.val;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.meta.RepoMeta;

/**
 * Converts a repo key to the corresponding repo metadata object.
 * 
 * @author Willie Wheeler
 */
@Component
public class RepoConverter implements Converter<String, RepoMeta> {
	@NonNull private ItemMetaLookup itemMetaLookup;
	
	public RepoConverter(ItemMetaLookup itemMetaLookup) {
		this.itemMetaLookup = itemMetaLookup;
	}
	
	@Override
	public RepoMeta convert(@NonNull String repoKey) {
		val itemClass = itemMetaLookup.getItemClass(repoKey);
		return new RepoMeta(itemClass);
	}
}
