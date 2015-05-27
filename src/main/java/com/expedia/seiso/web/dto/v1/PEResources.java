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
package com.expedia.seiso.web.dto.v1;

import java.util.ArrayList;

import lombok.ToString;

import org.springframework.data.mapping.PersistentEntity;

import com.expedia.seiso.domain.entity.Item;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Willie Wheeler
 */
@ToString
@SuppressWarnings("serial")
public class PEResources extends ArrayList<Item> {
	private final PersistentEntity<?, ?> persistentEntity;

	public PEResources(PersistentEntity<?, ?> persistentEntity) {
		this.persistentEntity = persistentEntity;
	}

	@JsonIgnore
	public PersistentEntity<?, ?> getPersistentEntity() {
		return persistentEntity;
	}
}
