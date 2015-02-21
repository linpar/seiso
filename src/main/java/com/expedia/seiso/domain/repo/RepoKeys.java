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
package com.expedia.seiso.domain.repo;

/**
 * @author Willie Wheeler
 */
public interface RepoKeys {
	
	// Core repos
	public static final String DATA_CENTERS = "data-centers";
	public static final String ENDPOINTS = "endpoints";
	public static final String ENVIRONMENTS = "environments";
	public static final String HEALTH_STATUSES = "health-statuses";
	public static final String INFRASTRUCTURE_PROVIDERS = "infrastructure-providers";
	public static final String IP_ADDRESS_ROLES = "ip-address-roles";
	public static final String LOAD_BALANCERS = "load-balancers";
	public static final String MACHINES = "machines";
	public static final String NODE_IP_ADDRESSES = "node-ip-addresses";
	public static final String NODES = "nodes";
	public static final String PEOPLE = "people";
	public static final String REGIONS = "regions";
	public static final String ROTATION_STATUSES = "rotation-statuses";
	public static final String SERVICE_GROUPS = "service-groups";
	public static final String SERVICE_INSTANCE_PORTS = "service-instance-ports";
	public static final String SERVICE_INSTANCES = "service-instances";
	public static final String SERVICE_TYPES = "service-types";
	public static final String SERVICES = "services";
	public static final String SOURCES = "sources";
	public static final String STATUS_TYPES = "status-types";
	
	// Custom integrations
	public static final String SEYREN_CHECKS = "seyren-checks";
}
