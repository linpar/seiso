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
package com.expedia.seiso.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Willie Wheeler
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = { "ipAddress", "port" })
@ToString(callSuper = true, of = { "ipAddress", "port", "rotationStatus" })
@Entity
public class Endpoint extends AbstractItem {
	
	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "node_ip_address_id")
	private NodeIpAddress ipAddress;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "service_instance_port_id")
	private ServiceInstancePort port;

	@ManyToOne(optional = true)
	@JoinColumn(name = "rotation_status_id")
	private RotationStatus rotationStatus;
}
