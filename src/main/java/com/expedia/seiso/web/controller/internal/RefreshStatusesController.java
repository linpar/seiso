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
package com.expedia.seiso.web.controller.internal;

import java.util.List;

import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.serf.ann.SuppressBasePath;

/**
 * @author Willie Wheeler
 */
@RestController
@SuppressBasePath
@XSlf4j
public class RefreshStatusesController {
	@Autowired private ItemService itemService;
	
	@RequestMapping("/internal/refresh")
	public void refresh() {
		List<Node> nodes = itemService.findAll(Node.class);
		int numNodes = nodes.size();
		for (int i = 0; i < numNodes; i++) {
			Node node = nodes.get(i);
			log.info("Refreshing node " + (i + 1) + " of " + numNodes + ": " + node.getName());
			itemService.save(node, true);
		}
	}
}
