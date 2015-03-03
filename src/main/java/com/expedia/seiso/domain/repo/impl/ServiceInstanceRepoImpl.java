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
package com.expedia.seiso.domain.repo.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.NonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.expedia.seiso.domain.entity.NodeStats;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.repo.custom.ServiceInstanceRepoCustom;

/**
 * @author Willie Wheeler
 */
public class ServiceInstanceRepoImpl implements ServiceInstanceRepoCustom {
	private static final String ENTITY_NAME = "ServiceInstance";
	private static final Set<String> FIELD_NAMES = Collections.singleton("key");
	
	private static final String NODE_STATS_SQL =
			"select " +
			"  count(*) num_nodes, " +
			"  sum(healthy) num_healthy, " +
			"  sum(nip_enabled * all_endpoints_enabled) num_enabled, " +
			"  sum(healthy * nip_enabled * all_endpoints_enabled) num_healthy_given_enabled " +
			"from ( " +
			"  select " +
			"    if(hs.ukey = 'healthy', 1, 0) healthy, " +
			"    if(sum(case nip_rs.ukey when 'enabled' then 0 else 1 end) = 0, 1, 0) nip_enabled, " +
			"    if(sum(case e_rs.ukey when 'enabled' then 0 else 1 end) = 0, 1, 0) all_endpoints_enabled " +
			"  from " +
			"    node n " +
			"    left outer join health_status hs on n.health_status_id = hs.id " +
			"    left outer join node_ip_address nip on nip.node_id = n.id " +
			"    left outer join rotation_status nip_rs on nip_rs.id = nip.rotation_status_id " +
			"    left outer join endpoint e on e.node_ip_address_id = nip.id " +
			"    left outer join rotation_status e_rs on e_rs.id = e.rotation_status_id, " +
			"    service_instance si " +
			"  where " +
			"    n.service_instance_id = si.id " +
			"    and si.ukey = ? " +
			"  group by " +
			"    nip.id) nip_stats";
	
	@PersistenceContext private EntityManager entityManager;
	
	@Autowired private JdbcTemplate jdbcTemplate;
	@Autowired private RepoImplUtils repoUtils;
	
	@Override
	public Class<ServiceInstance> getResultType() { return ServiceInstance.class; }

	@Override
	public Page<ServiceInstance> search(Set<String> searchTokens, Pageable pageable) {
		return repoUtils.search(ENTITY_NAME, entityManager, FIELD_NAMES, searchTokens, pageable);
	}

	/* (non-Javadoc)
	 * @see com.expedia.seiso.domain.repo.custom.ServiceInstanceRepoCustom#getServiceInstanceNodeStats(java.lang.String)
	 */
	@Override
	public NodeStats getServiceInstanceNodeStats(@NonNull String key) {
		return jdbcTemplate.query(NODE_STATS_SQL, new String[] { key }, new RowMapper<NodeStats>() {
			
			@Override
			public NodeStats mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new NodeStats(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4));
			}
		}).get(0);
	}
}
