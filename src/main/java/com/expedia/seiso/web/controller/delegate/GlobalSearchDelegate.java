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
package com.expedia.seiso.web.controller.delegate;

import lombok.NonNull;
import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.service.SearchEngine;
import com.expedia.seiso.domain.service.search.SearchQuery;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.assembler.ResourceAssembler;
import com.expedia.serf.hmedia.Resource;

/**
 * @author Willie Wheeler
 */
@Component
public class GlobalSearchDelegate {
	@Autowired private ResourceAssembler resourceAssembler;
	@Autowired private SearchEngine searchEngine;
	
	public Resource globalSearch(
			@NonNull ApiVersion apiVersion,
			@NonNull SearchQuery query,
			@NonNull Pageable pageable) {
		
		val results = searchEngine.search(query, pageable);
		return resourceAssembler.toGlobalSearchResource(apiVersion, results);
	}
}
