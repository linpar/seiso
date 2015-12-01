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
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.NonNull;
import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.expedia.seiso.domain.repo.custom.ServiceInstanceRepoCustom;
import com.expedia.seiso.web.resource.BreakdownItem;
import com.expedia.seiso.web.resource.NodeSummary;

/**
 * @author Willie Wheeler
 */
public class ServiceInstanceRepoImpl implements ServiceInstanceRepoCustom {
	
	// It might make more sense to move this into ServiceInstanceServiceImpl.
	// But there's not really much domain logic going on so maybe it's OK here.
	private static final String NODE_SUMMARY_SQL =
			"select " +
			"  count(*) num_nodes, " +
			"  sum(healthy) num_healthy, " +
			"  sum(nip_enabled * all_endpoints_enabled) num_enabled, " +
			"  sum(healthy * nip_enabled * all_endpoints_enabled) num_healthy_given_enabled " +
			"from ( " +
			"  select " +
			"    if(st.ukey in ('success', 'info'), 1, 0) healthy, " +
			"    if(sum(case nip_rs.ukey when 'enabled' then 0 else 1 end) = 0, 1, 0) nip_enabled, " +
			"    if(sum(case e_rs.ukey when 'enabled' then 0 else 1 end) = 0, 1, 0) all_endpoints_enabled " +
			"  from " +
			"    node n " +
			"    left outer join health_status hs on n.health_status_id = hs.id " +
			"    left outer join status_type st on hs.status_type_id = st.id " +
			"    left outer join node_ip_address nip on nip.node_id = n.id " +
			"    left outer join rotation_status nip_rs on nip_rs.id = nip.rotation_status_id " +
			"    left outer join endpoint e on e.node_ip_address_id = nip.id " +
			"    left outer join rotation_status e_rs on e_rs.id = e.rotation_status_id, " +
			"    service_instance si " +
			"  where " +
			"    n.service_instance_id = si.id " +
			"    and si.id = ? " +
			"  group by " +
			"    nip.id) nip_stats";
	
	private static final String HEALTH_BREAKDOWN_SQL = 
			"select " +
			"  if(n.health_status_id is null, 'Unknown', hs.name) status, " +
			"  if(n.health_status_id is null, 'warning', st.ukey) type, " +
			"  count(*) num_nodes " +
			"from " +
			"  node n " +
			"  left outer join health_status hs on n.health_status_id = hs.id " +
			"  left outer join status_type st on hs.status_type_id = st.id, " +
			"  service_instance si " +
			"where " +
			"  n.service_instance_id = si.id " +
			"  and si.id = ? " +
			"group by " +
			"  if(n.health_status_id is null, 0, n.health_status_id)";
	
	private static final String ROTATION_BREAKDOWN_SQL = 
			"select " +
			"  if(n.aggregate_rotation_status_id is null, 'Unknown', rs.name) status, " +
			"  if(n.aggregate_rotation_status_id is null, 'warning', st.ukey) type, " +
			"  count(*) num_nodes " +
			"from " +
			"  node n " +
			"  left outer join rotation_status rs on n.aggregate_rotation_status_id = rs.id " +
			"  left outer join status_type st on rs.status_type_id = st.id, " +
			"  service_instance si " +
			"where " +
			"  n.service_instance_id = si.id " +
			"  and si.id = ? " +
			"group by " +
			"  if(n.aggregate_rotation_status_id is null, 0, n.aggregate_rotation_status_id)";
		
	@PersistenceContext private EntityManager entityManager;
	
	@Autowired private JdbcTemplate jdbcTemplate;

	@Override
	public NodeSummary getServiceInstanceNodeSummary(@NonNull Long id) {
		val mapper = new RowMapper<NodeSummary>() {
			@Override
			public NodeSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new NodeSummary(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4));
			}
		};
		return jdbcTemplate.query(NODE_SUMMARY_SQL, args(id), mapper).get(0);
	}

	@Override
	public List<BreakdownItem> getServiceInstanceHealthBreakdown(@NonNull Long id) {
		val mapper = new RowMapper<BreakdownItem>() {
			@Override
			public BreakdownItem mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new BreakdownItem(rs.getString(1), rs.getString(2), rs.getInt(3));
			}
		};
		return jdbcTemplate.query(HEALTH_BREAKDOWN_SQL, args(id), mapper);
	}

	@Override
	public List<BreakdownItem> getServiceInstanceRotationBreakdown(@NonNull Long id) {
		val mapper = new RowMapper<BreakdownItem>() {
			@Override
			public BreakdownItem mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new BreakdownItem(rs.getString(1), rs.getString(2), rs.getInt(3));
			}
		};
		return jdbcTemplate.query(ROTATION_BREAKDOWN_SQL, args(id), mapper);
	}
	
	private Long[] args(Long id) {
		return new Long[] { id };
	}
}
