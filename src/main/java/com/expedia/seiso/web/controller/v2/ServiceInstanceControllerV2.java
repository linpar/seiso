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
package com.expedia.seiso.web.controller.v2;

import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.repo.NodeRepo;
import com.expedia.seiso.web.MediaTypes;
import com.expedia.seiso.web.Relations;
import com.expedia.seiso.web.hateoas.Resource;
import com.expedia.seiso.web.hateoas.link.LinkFactory;

/**
 * @author Willie Wheeler
 */
@RestController
@RequestMapping("/v2/service-instances")
public class ServiceInstanceControllerV2 {
	@Autowired private NodeRepo nodeRepo;
	@Autowired @Qualifier("linkFactoryV2") private LinkFactory linkFactoryV2;
	
	@RequestMapping(
			value = "/{key}/node-stats",
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public Resource getNodeStats(@PathVariable String key) {
		val itemLinksV2 = linkFactoryV2.getItemLinks();
		
		val dummy = new ServiceInstance();
		dummy.setKey(key);
		
		val statsArr = nodeRepo.findNodeStatsByServiceInstanceKey(key).get(0);
		
		val stats = new Resource();
		stats.addLink(itemLinksV2.serviceInstanceNodeStatsLink(Relations.SELF, dummy));
		stats.addLink(itemLinksV2.itemLink(Relations.UP, dummy));
		
		// Logic for deciding whether a node is healthy:
		// Health status key is 'healthy'.
		// FIXME This is a hardcode!!
		
		// Logic for deciding whether a node is enabled (enabled = taking traffic):
		// true <=> (at least one endpoint & all endpoints enabled)

		// work in progress
//		select
//		  n.id node_id,
//		  n.name node_name,
//		  hs.ukey health,
//		  sum(case when nip_rs.ukey = 'enabled' then 0 else 1 end) nip_oor,
//		  sum(case when e_rs.ukey = 'enabled' then 0 else 1 end) endpoint_oor
//		from
//		  node n,
//		  service_instance si,
//		  health_status hs,
//		  node_ip_address nip,
//		  rotation_status nip_rs,
//		  endpoint e,
//		  rotation_status e_rs
//		where
//		  n.service_instance_id = si.id
//		  and n.health_status_id = hs.id
//		  and nip.node_id = n.id
//		  and nip.rotation_status_id = nip_rs.id
//		  and e.node_ip_address_id = nip.id
//		  and e.rotation_status_id = e_rs.id
//		  and si.ukey = 'hotel-shopping-dr'
//		group by
//		  n.id
//		  ;
		
		
		// FIXME Hardcoded dummy values
		stats.setProperty("numNodes", statsArr[0]);
		stats.setProperty("numHealthy", statsArr[1]);
		stats.setProperty("numEnabled", 300);
		stats.setProperty("numHealthyGivenEnabled", 300);
		
		return stats;
	}
}
