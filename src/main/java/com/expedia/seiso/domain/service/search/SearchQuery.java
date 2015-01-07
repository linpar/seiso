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
package com.expedia.seiso.domain.service.search;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Ken Van Eyk
 */
public class SearchQuery {
	private String query;
	private Set<String> tokens;

	public SearchQuery() {
		this(null);
	}

	public SearchQuery(String query) {
		this(query, null);
	}

	public SearchQuery(String query, Set<String> tokens) {
		this.setQuery(query);
		this.setTokens(tokens);
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return this.query;
	}

	public void setTokens(Set<String> tokens) {
		if (this.tokens == null) {
			this.tokens = new LinkedHashSet<String>();
		} else {
			this.tokens.clear();
		}

		if (tokens != null) {
			this.tokens.addAll(tokens);
		}
	}

	public Set<String> getTokens() {
		return Collections.unmodifiableSet(this.tokens);
	}

	public void clearTokens() {
		this.tokens.clear();
	}

}