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

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.expedia.seiso.core.ann.Key;
import com.expedia.seiso.core.ann.RestResource;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;

// TODO Currently implements Serializable because the servlet container wants to save this out on shutdown. But don't
// really want this to be transient (none of the other entities are), and it just makes sense to disable the saveout in
// Tomcat. See http://stackoverflow.com/questions/8181664/how-to-turn-off-session-persistence-in-geronimo-tomcat. [WLW]

/**
 * @author Willie Wheeler
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = "username")
@ToString(callSuper = true, of = "username")
@Entity
@SuppressWarnings("serial")
public class User extends AbstractItem implements Serializable {
	
	@Key
	private String username;

	// FIXME Need to suppress this field from the JSON serialized view!! [WLW]
	// @JsonIgnore doesn't work. Need to avoid pulling it into the LegacyResource. Probably want something like this:
	// FIXME Also need to suppress this from the Tomcat HTTP serialization. Can't use transient here because that
	// prevents Spring Data from saving it. [WLW]
	@RestResource(exported = false)
	private String password;

	private Boolean enabled;

	@ManyToMany
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private List<Role> roles;

	@Override
	public ItemKey itemKey() {
		return new SimpleItemKey(User.class, username);
	}
}
