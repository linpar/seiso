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
package com.expedia.seiso.web.controller.internal;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.domain.service.SearchEngine;
import com.expedia.seiso.domain.service.search.SearchQuery;
import com.expedia.seiso.web.assembler.GlobalSearchAssembler;
import com.expedia.seiso.web.assembler.SearchResultsDto;

import lombok.val;
import lombok.extern.slf4j.XSlf4j;

/**
 * Controller to perform global searches. This primarily supports the search box in the UI.
 * 
 * @author Ken Van Eyk
 * @author Willie Wheeler
 */
@RestController
@RequestMapping("/internal/search")
@XSlf4j
public class GlobalSearchController {
	@Autowired private SearchEngine searchEngine;
	@Autowired private GlobalSearchAssembler resourceAssembler;
	
	@RequestMapping(
			value = "",
			method = RequestMethod.GET,
			produces = "application/json")
	public SearchResultsDto globalSearch(
			@RequestParam("q") String keywords,
			@PageableDefault(
					page = 0,
					size = 100,
					direction = Direction.ASC)
			Pageable pageable) {
		
		log.trace("Doing global search: keywords={}", keywords);
		
		// TODO Use a handler method arg resolver instead. [WLW]
		SearchQuery query = new SearchQuery(keywords, toKeywordSet(keywords));
		
		val results = searchEngine.search(query, pageable);
		return resourceAssembler.toGlobalSearchResource(results);
	}
	
	private Set<String> toKeywordSet(String keywords) {
		Set<String> keywordSet = new HashSet<>();
		String[] keywordArr = keywords.split(" ");
		for (String keyword : keywordArr) { keywordSet.add("%" + keyword + "%"); }
		return keywordSet;
	}
}
