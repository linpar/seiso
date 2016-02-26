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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.gateway.NotificationGateway;

/**
 * @author Wayne Warren
 * @author Willie Wheeler
 */
@RepositoryEventHandler(ServiceInstance.class)
@Component
public class ServiceInstanceEventHandler {
	
	@Autowired
	private NotificationGateway notificationGateway;
	
	@HandleAfterCreate
	public void handleAfterCreate(ServiceInstance si) {
		notify(si, NotificationGateway.OP_CREATE);
	}
	
	@HandleAfterSave
	public void handleAfterSave(ServiceInstance si) {
		notify(si, NotificationGateway.OP_UPDATE);
	}
	
	@HandleAfterDelete
	public void handleAfterDelete(ServiceInstance si) {
		notify(si, NotificationGateway.OP_DELETE);
	}
	
	private void notify(ServiceInstance si, String op) {
		notificationGateway.notify(si, si.getKey(), op);
	}
}