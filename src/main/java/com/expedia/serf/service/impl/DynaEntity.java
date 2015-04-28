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
package com.expedia.serf.service.impl;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * @author Willie Wheeler
 */
@Data
@RequiredArgsConstructor
public class DynaEntity {
	@NonNull private Object entity;
	
	public Long getId() {
		val entityClass = entity.getClass();
		try {
			val getIdMethod = entityClass.getMethod("getId");
			return (Long) getIdMethod.invoke(entity);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setId(Long id) {
		val entityClass = entity.getClass();
		try {
			val setIdMethod = entityClass.getMethod("setId", Long.class);
			setIdMethod.invoke(entity, id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
