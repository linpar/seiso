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
package com.expedia.seiso.integration.eos.connector.impl;

import java.io.IOException;
import java.util.Collections;

import lombok.NonNull;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.expedia.seiso.conf.CustomProperties;
import com.expedia.seiso.integration.eos.connector.Eos;
import com.expedia.seiso.integration.eos.connector.EosConvictRequest;
import com.expedia.seiso.integration.eos.connector.EosDeployRequest;
import com.expedia.seiso.integration.eos.connector.EosInterrogateRequest;
import com.expedia.seiso.integration.eos.connector.EosMaintenanceModeRequest;
import com.expedia.seiso.integration.eos.connector.EosResponse;
import com.expedia.seiso.integration.eos.connector.EosSoakRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Willie Wheeler
 */
@Component
@XSlf4j
public class EosTemplate implements Eos {
	private static final String EOS_BASE_PATH = "/eos/ServiceInstances/{key}";
	private static final String EOS_CONVICT_PATH = EOS_BASE_PATH + "/Convict";
	private static final String EOS_DEPLOY_PATH = EOS_BASE_PATH + "/Deploy";
	private static final String EOS_INTERROGATE_PATH = EOS_BASE_PATH + "/Interrogate";
	private static final String EOS_MAINTENANCE_MODE_PATH = EOS_BASE_PATH + "/MaintenanceMode";
	private static final String EOS_RELOAD_PATH = EOS_BASE_PATH + "/Reload";
	private static final String EOS_SET_ACTIVE_PATH = EOS_BASE_PATH + "/SetActive";
	private static final String EOS_SOAK_PATH = EOS_BASE_PATH + "/Soak";
	
	private static final HttpHeaders REQUEST_HEADERS = new HttpHeaders();
	
	static {
		REQUEST_HEADERS.setContentType(MediaType.APPLICATION_JSON);
		REQUEST_HEADERS.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
	}
	
	@Autowired private RestTemplate restTemplate;
	@Autowired private CustomProperties customProperties;
	
	// Temporarily require this because Eos always returns its JSON messages as text/plain. :-/
	// Don't inject as we already have a couple of special object mappers.
	private ObjectMapper objectMapper = new ObjectMapper();
	
	/* (non-Javadoc)
	 * @see com.expedia.seiso.integration.eos.connector.Eos#convict(java.lang.String, com.expedia.seiso.integration.eos.connector.EosConvictRequest)
	 */
	@Override
	public EosResponse convict(@NonNull String serviceInstanceKey, @NonNull EosConvictRequest request) {
		log.info("Convicting: serviceInstanceKey={}", serviceInstanceKey);
		final String uri = actionUri(EOS_CONVICT_PATH);
		final HttpEntity<EosConvictRequest> requestEntity = new HttpEntity<>(request, REQUEST_HEADERS);
		return exchange(uri, requestEntity, serviceInstanceKey);
	}

	/* (non-Javadoc)
	 * @see com.expedia.seiso.integration.eos.connector.Eos#deploy(java.lang.String, com.expedia.seiso.integration.eos.connector.EosDeployRequest)
	 */
	@Override
	public EosResponse deploy(@NonNull String serviceInstanceKey, @NonNull EosDeployRequest request) {
		log.info("Deploying: serviceInstanceKey={}", serviceInstanceKey);
		final String uri = actionUri(EOS_DEPLOY_PATH);
		final HttpEntity<EosDeployRequest> requestEntity = new HttpEntity<>(request, REQUEST_HEADERS);
		return exchange(uri, requestEntity, serviceInstanceKey);
	}

	/* (non-Javadoc)
	 * @see com.expedia.seiso.integration.eos.connector.Eos#interrogate(java.lang.String, com.expedia.seiso.integration.eos.connector.EosInterrogateRequest)
	 */
	@Override
	public EosResponse interrogate(@NonNull String serviceInstanceKey, @NonNull EosInterrogateRequest request) {
		log.info("Interrogate: serviceInstanceKey={}", serviceInstanceKey);
		final String uri = actionUri(EOS_INTERROGATE_PATH);
		final HttpEntity<EosInterrogateRequest> requestEntity = new HttpEntity<>(request, REQUEST_HEADERS);
		return exchange(uri, requestEntity, serviceInstanceKey);
	}

	/* (non-Javadoc)
	 * @see com.expedia.seiso.integration.eos.connector.Eos#maintenanceMode(java.lang.String, com.expedia.seiso.integration.eos.connector.EosMaintenanceModeRequest)
	 */
	@Override
	public EosResponse maintenanceMode(@NonNull String serviceInstanceKey, @NonNull EosMaintenanceModeRequest request) {
		log.info("Setting maintenance mode: serviceInstanceKey={}", serviceInstanceKey);
		final String uri = actionUri(EOS_MAINTENANCE_MODE_PATH);
		final HttpEntity<EosMaintenanceModeRequest> requestEntity = new HttpEntity<>(request, REQUEST_HEADERS);
		return exchange(uri, requestEntity, serviceInstanceKey);
	}

	/* (non-Javadoc)
	 * @see com.expedia.seiso.integration.eos.connector.Eos#reload(java.lang.String)
	 */
	@Override
	public EosResponse reload(@NonNull String serviceInstanceKey) {
		log.info("Reloading: serviceInstanceKey={}", serviceInstanceKey);
		final String uri = actionUri(EOS_RELOAD_PATH);
		final HttpEntity<String> requestEntity = new HttpEntity<>(REQUEST_HEADERS);
		return exchange(uri, requestEntity, serviceInstanceKey);
	}

	/* (non-Javadoc)
	 * @see com.expedia.seiso.integration.eos.connector.Eos#setActive(java.lang.String)
	 */
	@Override
	public EosResponse setActive(@NonNull String serviceInstanceKey) {
		log.info("Reloading: serviceInstanceKey={}", serviceInstanceKey);
		final String uri = actionUri(EOS_SET_ACTIVE_PATH);
		final HttpEntity<String> requestEntity = new HttpEntity<>(REQUEST_HEADERS);
		return exchange(uri, requestEntity, serviceInstanceKey);
	}
	
	/* (non-Javadoc)
	 * @see com.expedia.seiso.integration.eos.connector.Eos#soak(java.lang.String, com.expedia.seiso.integration.eos.connector.EosSoakRequest)
	 */
	@Override
	public EosResponse soak(@NonNull String serviceInstanceKey, @NonNull EosSoakRequest request) {
		log.info("Soaking: serviceInstanceKey={}", serviceInstanceKey);
		final String uri = actionUri(EOS_SOAK_PATH);
		final HttpEntity<EosSoakRequest> requestEntity = new HttpEntity<>(request, REQUEST_HEADERS);
		return exchange(uri, requestEntity, serviceInstanceKey);
	}
	
	private String actionUri(String path) {
		return customProperties.getActionsBaseUri() + path;
	}
	
	private EosResponse exchange(String uri, HttpEntity<?> requestEntity, String serviceInstanceKey) {
		try {
			// This doesn't work because Eos returns JSON message as text/plain.
//			return restTemplate.exchange(
//					uri,
//					HttpMethod.POST,
//					requestEntity,
//					EosResponse.class,
//					serviceInstanceKey).getBody();
			
			// So do this as a workaround til they fix it.
			String responseBody = restTemplate.exchange(
					uri,
					HttpMethod.POST,
					requestEntity,
					String.class,
					serviceInstanceKey).getBody();
			
			return objectMapper.readValue(responseBody, EosResponse.class);
		} catch (HttpClientErrorException e) {
			log.error("Error calling Eos: POST {}, error={}", uri, e.getMessage());
			throw e;
		} catch (HttpServerErrorException e) {
			// Eos incorrectly generates HTTP 500 for client errors, so just recast this as an HTTP 400.
			// If it's a genuine server error, we're out of luck.
			log.error("Error calling Eos: POST {}, error={}", uri, e.getMessage());
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
