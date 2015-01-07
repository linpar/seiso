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
package com.expedia.seiso.domain.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.NonNull;
import lombok.val;

import org.springframework.data.domain.Page;

/**
 * Search results, aggregating the result of repository-specific searches.
 * 
 * @author Ken Van Eyk
 * @author Willie Wheeler
 */
@SuppressWarnings("rawtypes")
public class SearchResults {
	private Map<Class, Page> typedResults = new HashMap<>();
	
	public List<Class> getItemClasses() {
		// TODO Use lambda to sort list by class name
		val set = typedResults.keySet();
		return new ArrayList<Class>(set);
	}
	
	public Page getTypedSerp(@NonNull Class itemClass) {
		return typedResults.get(itemClass);
	}
	
	/**
	 * @param itemClass
	 *            Item class
	 * @param serp
	 *            Search engine results page
	 */
	public void putTypedSerp(@NonNull Class itemClass, Page serp) {
		typedResults.put(itemClass, serp);
	}
}
