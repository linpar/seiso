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
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;

import com.expedia.serf.service.CrudService;
import com.expedia.serf.util.SerfBeanUtils;
import com.expedia.serf.util.SerfReflectionUtils;

/**
 * @author Willie Wheeler
 */
@Service
@Transactional
@XSlf4j
public class CrudServiceImpl implements CrudService {
	@Autowired private Repositories repositories;
	
	/* (non-Javadoc)
	 * @see com.expedia.serf.service.CrudService#save(java.lang.Object, java.lang.String[], java.lang.String[])
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void save(@NonNull Object entityData, String[] includeProps, String[] excludeProps) {
		log.trace("Saving entity: includeProps={}, excludeProps={}", includeProps, excludeProps);
		
		Class<?> entityClass = entityData.getClass();
		CrudRepository repo = (CrudRepository) repositories.getRepositoryFor(entityClass);
		DynaEntity dynaEntity = new DynaEntity(entityData);
		Long id = dynaEntity.getId();
		
		final Object entity = (id == null ? SerfReflectionUtils.createInstance(entityClass) : repo.findOne(id));
		String[] ignoreProps = SerfBeanUtils.determinePropertiesToIgnore(entityClass, includeProps, excludeProps);
		BeanUtils.copyProperties(entityData, entity, ignoreProps);
		
		log.trace("entity={}", entity);		
		repo.save(entity);
	}
}
