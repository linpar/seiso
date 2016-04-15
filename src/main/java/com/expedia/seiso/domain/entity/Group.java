/* 
 * Copyright 2013-2016 the original author or authors.
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
package com.expedia.seiso.domain.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

// TODO Support group description

/**
 * <p>
 * Represents a group of people.
 * </p>
 * <p>
 * The table name "person_group" kind of sucks, but "group" is a SQL reserved word, and I didn't want to use "team"
 * since we're essentially modeling AD distribution groups.
 * </p>
 * 
 * @author Willie Wheeler
 */
@Data
@Accessors(chain = true)
@ToString(of = { "name", "alias" })
@EqualsAndHashCode(callSuper = false, of = { "name", "alias" })
@Entity
@Table(name = "person_group")
public class Group extends AbstractItem {
	
	@NotNull
	@Size(min = 1, max = 80)
	private String name;
	
	@Size(min = 1, max = 80)
	private String alias;
	
	/**
	 * Using a link table here since a group can have multiple owners and vice versa.
	 */
	@ManyToMany(mappedBy = "groupsOwned")
	private List<Person> owners;
	
	@ManyToMany(mappedBy = "groupsMemberOf")
	private List<Person> members;
}
