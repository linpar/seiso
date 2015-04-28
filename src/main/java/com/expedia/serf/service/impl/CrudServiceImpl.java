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

import javax.transaction.Transactional;

import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;

import com.expedia.serf.service.CrudService;

/**
 * @author Willie Wheeler
 */
@Service
@Transactional
@XSlf4j
public class CrudServiceImpl implements CrudService {
	@Autowired private Repositories repositories;
	
	/* (non-Javadoc)
	 * @see com.expedia.serf.service.CrudService#save(java.lang.Object)
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void save(@NonNull Object entityData) {
		log.trace("Saving entity");
		
		val entityClass = entityData.getClass();
		val repo = (CrudRepository) repositories.getRepositoryFor(entityClass);
		val dynaEntity = new DynaEntity(entityData);
		val id = dynaEntity.getId();
		
		Object dbEntity;
		if (id == null) {
			dbEntity = entityData;
		} else {
			dbEntity = repo.findOne(id);
			merge(entityData, dbEntity);
		}
		
		repo.save(dbEntity);
	}
	
	private void merge(Object entityData, Object dbEntity) {
		// TODO
	}
}
