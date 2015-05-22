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

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.domain.entity.Person;
import com.expedia.seiso.domain.repo.PersonRepo;
import com.expedia.seiso.util.MbMatrix;
import com.expedia.seiso.web.dto.MbPeople;
import com.expedia.seiso.web.dto.MbPerson;
import com.expedia.serf.ann.SuppressBasePath;
import com.expedia.serf.web.MediaTypes;

/**
 * @author Willie Wheeler
 */
@RestController
@SuppressBasePath
@RequestMapping("/internal/mb")
@Transactional
public class MbController {
	@Autowired private MbMatrix mbMatrix;
	@Autowired private PersonRepo personRepo;
	
	@RequestMapping(
			value = "/{type}",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public MbPeople getPeople(@PathVariable String type) {
		List<Person> same = personRepo.findByMbType(type);
		List<Person> allies = personRepo.findByMbTypeIn(mbMatrix.getAllies(type));
		List<Person> enemies = personRepo.findByMbTypeIn(mbMatrix.getEnemies(type));
		return new MbPeople(mbPeople(same), mbPeople(allies), mbPeople(enemies));
	}
	
	private List<MbPerson> mbPeople(List<Person> people) {
		return people
				.stream()
				.map(person -> {
					MbPerson mbPerson = new MbPerson();
					mbPerson.setUsername(person.getUsername());
					mbPerson.setFullName(person.getFirstName() + " " + person.getLastName());
					mbPerson.setMbType(person.getMbType());
					return mbPerson;
				})
				.collect(Collectors.toList());
	}
}
