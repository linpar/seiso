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

import java.io.IOException;

import lombok.Data;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.expedia.seiso.conf.CustomProperties;
import com.expedia.serf.ann.SuppressBasePath;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Willie Wheeler
 */
@RestController
@SuppressBasePath
@RequestMapping("/internal")
@XSlf4j
public class ActionController {
	private static final String INTERROGATE_PATH = "/eos/ServiceInstances/{key}/Interrogate";
	private static final String RELOAD_PATH = "/eos/ServiceInstances/{key}/Reload";
	
	@Autowired private RestTemplate restTemplate;
	@Autowired private CustomProperties customProperties;
	
	@RequestMapping(
			value = "/service-instances/{key}/interrogate",
			method = RequestMethod.POST)
	public void interrogate(@PathVariable String key) throws IOException {
		log.info("Interrogating service instance {} in Eos", key);
		try {
			// Though Eos returns a JSON object, the Content-Type is text/plain,
			// so here we treat the type as String.class.
			restTemplate.postForEntity(actionUri(INTERROGATE_PATH), "", String.class, key);
		} catch (HttpClientErrorException e) {
			throw e;
		} catch (HttpServerErrorException e) {
			// Eos incorrectly generates HTTP 500 for client errors, so just recast this as an HTTP 400.
			// If it's a genuine server error, we're out of luck.
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(
			value = "/service-instances/{key}/reload",
			method = RequestMethod.POST)
	public void reload(@PathVariable String key) throws IOException {
		log.info("Reloading service instance {} in Eos", key);
		try {
			// Though Eos returns a JSON object, the Content-Type is text/plain,
			// so here we treat the type as String.class.
			restTemplate.postForEntity(actionUri(RELOAD_PATH), "", String.class, key);
		} catch (HttpClientErrorException e) {
			throw e;
		} catch (HttpServerErrorException e) {
			// Eos incorrectly generates HTTP 500 for client errors, so just recast this as an HTTP 400.
			// If it's a genuine server error, we're out of luck.
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
		}
	}
	
	private String actionUri(String path) {
		return customProperties.getActionsBaseUri() + path;
	}
	
	@Data
	private static class EosResponse {
		
		@JsonProperty("Message")
		private String message;
		
		@JsonProperty("ErrorCode")
		private String errorCode;
	}
}
