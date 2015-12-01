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
package com.expedia.seiso.domain.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Willie Wheeler
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = { "name" })
@ToString(of = { "name", "type" })
@Entity
public class LoadBalancer extends AbstractItem {

	@NotNull
	@Size(min = 1, max = 80)
	private String name;

	@ManyToOne
	@JoinColumn(name = "data_center_id")
	private DataCenter dataCenter;

	// FIXME This is probably just temporary. Eventually we'll tie the service instance to a load balancer through a
	// VIP. [WLW]
	@NonNull
	@OneToMany(mappedBy = "loadBalancer")
	@OrderBy("key")
	private List<ServiceInstance> serviceInstances = new ArrayList<>();

	@NotNull
	@Size(min = 1, max = 80)
	private String type;
	
	@Size(max = 20)
	private String ipAddress;

	@Size(max = 250)
	private String apiUrl;
}
