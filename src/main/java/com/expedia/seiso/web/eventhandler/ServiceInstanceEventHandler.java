/* 
 * Copyright 2013-2016 the original author or authors.
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
package com.expedia.seiso.web.eventhandler;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.ServiceInstance;


/**
 * @author Wayne Warren
 */
@RepositoryEventHandler(ServiceInstance.class)
@Component
public class ServiceInstanceEventHandler {
	
	@Autowired private RabbitMQSender mqMessenger;
	
	@PostConstruct
	public void postConstruct() {

	}
	
	/**
	 * If the health or aggregate rotation status is {@code null}, we initialize it to the corresponding "unknown"
	 * status entity instead of leaving it null. This allows the UI to render missing/unknown statuses without doing
	 * explicit null checks.
	 * 
	 * @param node
	 *            node to create
	 */
	@HandleBeforeCreate
	public void handleBeforeCreate(ServiceInstance si) {
		mqMessenger.serviceInstanceCreated(si);
	}
	
	@HandleBeforeDelete
	public void handleBeforeDelete(ServiceInstance si) {
		mqMessenger.serviceInstanceDeleted(si);
	}
	
	/**
	 * <p>
	 * If the health or aggregate rotation status is {@code null}, we initialize it to the corresponding "unknown"
	 * status entity instead of leaving it null. This allows the UI to render missing/unknown statuses without doing
	 * explicit null checks.
	 * </p>
	 * <p>
	 * Generally we shouldn't have to do this, since we try to prevent nodes from having {@code null} statuses, but
	 * we're just being paranoid.
	 * </p>
	 * 
	 * @param node
	 *            node to save
	 */
	@HandleBeforeSave
	public void handleBeforeSave(ServiceInstance si) {
		mqMessenger.serviceInstanceUpdated(si);
	}
	
}