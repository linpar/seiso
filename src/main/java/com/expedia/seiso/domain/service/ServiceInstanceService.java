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
package com.expedia.seiso.domain.service;

import java.util.List;

import com.expedia.seiso.domain.dto.BreakdownItem;
import com.expedia.seiso.domain.dto.NodeSummary;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;

/**
 * @author Willie Wheeler
 */
public interface ServiceInstanceService {
	
	/**
	 * @param key
	 *            Service instance key.
	 * @return
	 */
	NodeSummary getNodeSummary(String key);
	
	/**
	 * @param key
	 *            Service instance key.
	 * @return
	 */
	List<BreakdownItem> getHealthBreakdown(String key);
	
	/**
	 * @param key
	 *            Service instance key.
	 * @return
	 */
	List<BreakdownItem> getRotationBreakdown(String key);
	
	void recalculateAggregateRotationStatus(Node node);
	
	void recalculateAggregateRotationStatus(NodeIpAddress nip);
}
