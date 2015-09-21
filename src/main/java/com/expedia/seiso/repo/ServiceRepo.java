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
package com.expedia.seiso.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.expedia.seiso.entity.Service;

/**
 * @author Willie Wheeler
 */
public interface ServiceRepo extends PagingAndSortingRepository<Service, Long> {

	@Query("from Service order by name")
	List<Service> findAll();

	// @Query(
	// "from Service s " +
	// "left join fetch s.group " +
	// "left join fetch s.type " +
	// "left join fetch s.owner " +
	// "order by s.name")
	// Page<Service> findAllWithJoins(Pageable pageable);

	// @Query(
	// "from Service s " +
	// "left join fetch s.group " +
	// "left join fetch s.type " +
	// "left join fetch s.owner " +
	// "where s.key = :key")
	Service findByKey(@Param("key") String key);
	
	Service findByName(@Param("name") String name);
}
