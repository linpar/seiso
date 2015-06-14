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

import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.integration.eos.connector.Eos;
import com.expedia.seiso.integration.eos.connector.EosDeployRequest;
import com.expedia.seiso.integration.eos.connector.EosMaintenanceModeRequest;
import com.expedia.seiso.integration.eos.connector.EosResponse;
import com.expedia.serf.ann.SuppressBasePath;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Willie Wheeler
 */
@RestController
@SuppressBasePath
@RequestMapping("/internal")
public class EosActionController {
	@Autowired private Eos eos;
	
	@RequestMapping(
			value = "/service-instances/{key}/convict",
			method = RequestMethod.POST)
	public void convict(@PathVariable String key, @RequestBody ConvictRequest request) {
		// TODO
	}
	
	@RequestMapping(
			value = "/service-instances/{key}/deploy",
			method = RequestMethod.POST)
	public void deploy(@PathVariable String key, @RequestBody DeployRequest request) {
		EosDeployRequest eosRequest = toEosDeployRequest(request);
		EosResponse response = eos.deploy(key, eosRequest);
		if (response.isError()) {
			throw new RuntimeException(response.getMessage());
		}
	}
	
	@RequestMapping(
			value = "/service-instances/{key}/interrogate",
			method = RequestMethod.POST)
	public void interrogate(@PathVariable String key) {
		// TODO
	}
	
	@RequestMapping(
			value = "/service-instances/{key}/maintenance-mode",
			method = RequestMethod.POST)
	public void maintenanceMode(@PathVariable String key, @RequestBody MaintenanceModeRequest request) {
		EosMaintenanceModeRequest eosRequest = toEosMaintenanceModeRequest(request);
		EosResponse response = eos.maintenanceMode(key, eosRequest);
		if (response.isError()) {
			throw new RuntimeException(response.getMessage());
		}
	}
	
	@RequestMapping(
			value = "/service-instances/{key}/reload",
			method = RequestMethod.POST)
	public void reload(@PathVariable String key) {
		EosResponse response = eos.reload(key);
		if (response.isError()) {
			throw new RuntimeException(response.getMessage());
		}
	}
	
	@RequestMapping(
			value = "/service-instances/{key}/set-active",
			method = RequestMethod.POST)
	public void setActive(@PathVariable String key) {
		EosResponse response = eos.setActive(key);
		if (response.isError()) {
			throw new RuntimeException(response.getMessage());
		}
	}
	
	@RequestMapping(
			value = "/service-instances/{key}/soak",
			method = RequestMethod.POST)
	public void soak(@PathVariable String key) {
		// TODO
	}
	
	private EosDeployRequest toEosDeployRequest(DeployRequest request) {
		EosDeployRequest eosRequest = new EosDeployRequest();
		eosRequest.setVersion(request.getVersion());
		eosRequest.setArguments(request.getArguments());
		eosRequest.setNodeNameList(request.getNodeList());
		eosRequest.setDeploySameVersion(request.getDeploySameVersion());
		eosRequest.setOverrideStateRestriction(request.getOverrideStateRestriction());
		eosRequest.setSkipRotateIn(request.getSkipRotateIn());
		eosRequest.setSkipRotateOut(request.getSkipRotateOut());
		eosRequest.setSkipDvt(request.getSkipDvt());
		eosRequest.setSkipSetActive(request.getSkipSetActive());
		return eosRequest;
	}
	
	private EosMaintenanceModeRequest toEosMaintenanceModeRequest(MaintenanceModeRequest request) {
		EosMaintenanceModeRequest eosRequest = new EosMaintenanceModeRequest();
		eosRequest.setNodes(request.getNodeList());
		eosRequest.setMinutes(request.getMinutes());
		eosRequest.setEnable(request.getEnable());
		eosRequest.setOverrideOthers(request.getOverrideOthers());
		return eosRequest;
	}
	
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class ConvictRequest {
		private String nodeList;
		private String reason;
		private Boolean overrideCapacity;
		private Boolean skipRotateIn;
	}
	
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class DeployRequest {
		private String version;
		private String arguments;
		private String nodeList;
		private Boolean deploySameVersion;
		private Boolean overrideStateRestriction;
		private Boolean skipRotateIn;
		private Boolean skipRotateOut;
		private Boolean skipDvt;
		private Boolean skipSetActive;
	}
	
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class MaintenanceModeRequest {
		private String nodeList;
		private Integer minutes;
		private Boolean enable;
		private Boolean overrideOthers;
	}
}
