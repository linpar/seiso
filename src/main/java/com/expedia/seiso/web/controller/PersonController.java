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

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.core.util.C;
import com.expedia.seiso.domain.entity.Person;
import com.expedia.seiso.domain.repo.PersonRepo;
import com.expedia.seiso.web.assembler.PageLinks;
import com.expedia.seiso.web.assembler.ResponseHeadersBuilder;

// TODO Support patching. The reason is that due to the recursive relationship between people and their managers, we
// want to be able to update people in two passes:
// 1) Create all people (need to do this first so we can resolve managers in pass #2)
// 2) Link to managers
// When updating people in pass #1, we don't want to null out their managers.

// TODO Support batch linking of people to managers. But require pagination.

// TODO Support batch deleting of people. But require pagination.

/**
 * <p>
 * Controller to handle operations on people.
 * </p>
 * <p>Eventually I expect to fold this into a more general controller (or set of controllers), but right now there are
 * some different concepts that I want to experiment with. See
 * <a href="https://github.com/ExpediaDotCom/seiso/issues/18">https://github.com/ExpediaDotCom/seiso/issues/18</a>
 * for more detailed information on the goals and design approach.
 * </p>
 * 
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@RestController
@RequestMapping("/v1/people")
public class PersonController {
	@Autowired private PersonRepo personRepo;
	@Autowired private EntityLinks entityLinks;
	@Autowired private PageLinks pageLinks;
	@Autowired private ResponseHeadersBuilder responseHeadersBuilder;
	
	@RequestMapping(method = RequestMethod.GET, params = "view=keys")
	public HttpEntity<List<String>> getUsernames(
			@RequestParam @NonNull String source,
			@PageableDefault(
					page = C.DEFAULT_PAGE_NUMBER,
					size = C.DEFAULT_PAGE_SIZE,
					direction = Direction.ASC,
					sort = "username")
			Pageable pageable) {
		
		val entityPage = personRepo.findBySource(source, pageable);
		val usernames = buildUsernames(entityPage);
		val pageMeta = buildPageMetadata(entityPage);
		val headers = responseHeadersBuilder.buildForPagingAndSortingRepo("people", "keys", pageMeta);
		return new HttpEntity<List<String>>(usernames, headers);
	}
	
	private List<String> buildUsernames(Page<Person> people) {
		val usernames = new ArrayList<String>();
		for (val person : people) {
			usernames.add(person.getUsername());
		}
		return usernames;
	}
	
	private PageMetadata buildPageMetadata(Page<Person> people) {
		val pageSize = people.getSize();
		val pageNumber = people.getNumber();
		val totalElems = people.getTotalElements();
		return new PageMetadata(pageSize, pageNumber, totalElems);
	}
}
