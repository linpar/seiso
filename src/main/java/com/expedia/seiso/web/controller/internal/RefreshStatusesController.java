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

import com.expedia.seiso.domain.entity.NodeIpAddress;
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
		List<NodeIpAddress> nips = itemService.findAll(NodeIpAddress.class);
		int numNips = nips.size();
		for (int i = 0; i < numNips; i++) {
			NodeIpAddress nip = nips.get(i);
			log.info("Refreshing node IP address " + (i + 1) + " of " + numNips);
			itemService.save(nip, true);
		}
	}
}
