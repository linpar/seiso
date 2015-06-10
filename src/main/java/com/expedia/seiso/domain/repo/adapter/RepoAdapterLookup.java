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

import java.util.List;

import lombok.NonNull;
import lombok.val;

// TODO Can we get rid of these adapters? It's a nuisance to have to keep implementing them. [WLW]

/**
 * Repo adapter lookup.
 * 
 * @author Willie Wheeler
 */
public class RepoAdapterLookup {
	private List<RepoAdapter> repoAdapters;
	
	public RepoAdapterLookup(List<RepoAdapter> repoAdapters) {
		this.repoAdapters = repoAdapters;
	}
	
	public RepoAdapter getRepoAdapterFor(@NonNull Class<?> itemClass) {

		// Consider using a map or even hardcodes to avoid linear search through the adapters.
		// But I'm leaving it as-is for now since I doubt it's really an issue. [WLW]
		for (val adapter : repoAdapters) {
			if (adapter.supports(itemClass)) {
				return adapter;
			}
		}
		throw new IllegalArgumentException("No repo adapter for item class: " + itemClass.getName());
	}
}
