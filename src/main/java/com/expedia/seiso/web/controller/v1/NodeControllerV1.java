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
package com.expedia.seiso.web.controller.v1;

import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.NodeRepo;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.assembler.ResourceAssembler;
import com.expedia.serf.ResourceNotFoundException;
import com.expedia.serf.ann.SuppressBasePath;
import com.expedia.serf.hmedia.Resource;

/**
 * @author Willie Wheeler
 */
@RestController
@RequestMapping("/v1")
@SuppressBasePath
@Transactional
public class NodeControllerV1 {
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private NodeRepo nodeRepo;
	@Autowired private ResourceAssembler resourceAssembler;
	
	// Hand-coding this one because we don't want to return a page anymore, and don't want camel-case params, but also
	// don't want to break existing code. Make this one the way we want it to be and then convert the other one over in
	// coordination with Eos. Anyway in this case it's a single node we're returning, not a page. [WLW]
	@RequestMapping(
			value = "/nodes/search/find-by-ip-address-and-port",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Resource findNodeByIpAddressAndPort(
			@RequestParam("ip-address") String ipAddress,
			@RequestParam("port") Integer port) {

		val nodes = nodeRepo.findByIpAddressAndPort(ipAddress, port);

		if (nodes.isEmpty()) {
			throw new ResourceNotFoundException("No such node: ipAddress=" + ipAddress + ", port=" + port);
		} else if (nodes.size() > 1) {
			throw new RuntimeException("Found " + nodes.size() + " nodes but expected only 1");
		}

		val node = nodes.get(0);
		val nodeMeta = itemMetaLookup.getItemMeta(Node.class);
		val projectionNode =
				nodeMeta.getProjectionNode(ApiVersion.V1, Projection.Cardinality.SINGLE, Projection.DEFAULT);
		return resourceAssembler.toResource(ApiVersion.V1, node, projectionNode);
	}
}
