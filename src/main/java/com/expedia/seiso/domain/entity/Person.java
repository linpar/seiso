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
package com.expedia.seiso.domain.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.expedia.seiso.core.ann.Key;
import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.core.ann.RestResource;
import com.expedia.seiso.core.ann.Projection.Cardinality;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = "username")
@ToString(of = { "username", "firstName", "lastName" })
@Entity
//@formatter:off
@Projections({
	@Projection(cardinality = Cardinality.COLLECTION, paths = {}),
	@Projection(cardinality = Cardinality.SINGLE, paths = { "manager", "directReports" }),
	@Projection(cardinality = Cardinality.SINGLE, name = "popover")
	})
//@formatter:on
public class Person extends AbstractItem {
	
	@Key
	private String username;
	
	private String firstName;
	private String lastName;
	private String title;
	private String company;
	private String department;
	private String division;
	private String subdivision;
	private String location;
	private String streetAddress;
	private String workPhone;
	private String email;

	// Not sure we want this here for the long term, but maybe. After all we will want to store Amazon instance IDs
	// with our own machines. [WLW]
	private String ldapDn;

	// Setting this lazy. Otherwise Hibernate is issuing a separate select for it. :-O
	// UGH, having lazy here means there will be a proxy, and this was causing an EntityLinks lookup to fail since it
	// was doing that by classname. (The proxy has a funky classname.) [WLW]
	// @ManyToOne(fetch = FetchType.LAZY)
	@ManyToOne
	@JoinColumn(name = "manager_id")
	@RestResource(path = "manager")
	private Person manager;

	@OrderBy("firstName, lastName, id")
	@OneToMany(mappedBy = "manager")
	@RestResource(path = "direct-reports")
	private List<Person> directReports;
	
	private String source;
	
	@Override
	public ItemKey itemKey() {
		return new SimpleItemKey(Person.class, username);
	}
}
