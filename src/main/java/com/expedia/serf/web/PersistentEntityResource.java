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
package com.expedia.serf.web;

import lombok.NoArgsConstructor;
import lombok.ToString;

import org.springframework.data.mapping.PersistentEntity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Willie Wheeler
 */
@NoArgsConstructor
@ToString
public class PersistentEntityResource {
	private PersistentEntity<?, ?> persistentEntity;
	private Object entity;
	
	public PersistentEntityResource(PersistentEntity<?, ?> persistentEntity, Object entity) {
		this.persistentEntity = persistentEntity;
		this.entity = entity;
	}
	
	@JsonIgnore
	public PersistentEntity<?, ?> getPersistentEntity() {
		return persistentEntity;
	}
	
	@JsonAnyGetter
	public Object getEntity() {
		return entity;
	}
}
