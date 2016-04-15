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
package com.expedia.seiso.domain.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

// http://docs.spring.io/spring-data/rest/docs/current/reference/html/
// The SDR docs say to use this instead of JPA @Version, but the Etag header doesn't show up if we use SD @Version.
// Not sure what's up.
// See http://stackoverflow.com/questions/31882180/why-is-the-version-property-not-set-with-spring-data-jpa
//import org.springframework.data.annotation.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Willie Wheeler
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = { "name" })
@ToString(callSuper = true, of = { "name", "serviceInstance", "machine" })
@Entity
public class Node extends AbstractItem {
	
	// TODO Lock this down to lowercase, but let people update first. [WLW]
	// Also some people have periods in their nodes.
	@NotNull
//	@Pattern(regexp = "[A-Za-z0-9-]+")
	@Size(min = 1, max = 80)
	private String name;

	/**
	 * Optional description to support cases where service instance nodes aren't entirely interchangeable. For instance
	 * we have Splunk service instances where each service instance has its own purpose (ad hoc searches, summary
	 * searches, alerts, dashboards, etc.).
	 */
	@Size(max = 250)
	private String description;

	/**
	 * Version identifier for latest health status
	 */
	@Version
	private Long version;

	@Column(name = "build_version")
	@Size(max = 128)
	private String buildVersion;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "service_instance_id")
	private ServiceInstance serviceInstance;
	
	@ManyToOne
	@JoinColumn(name = "machine_id")
	private Machine machine;

	@OneToMany(mappedBy = "node", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<NodeIpAddress> ipAddresses = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "health_status_id")
	private HealthStatus healthStatus;
	
	/**
	 * Link to diagnostic page around health status transitions.
	 */
	@Size(max = 250)
	private String healthStatusLink;
	
	/**
	 * Short reason for a health status transition.
	 */
	@Size(max = 250)
	private String healthStatusReason;
	
	// We use this primarily to find node alerts. See NodeRepo.
	@ManyToOne
	@JoinColumn(name = "aggregate_rotation_status_id")
	private RotationStatus aggregateRotationStatus;
}
