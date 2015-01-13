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
package com.expedia.seiso.web.controller.v1;

import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.domain.entity.key.ServiceInstancePortKey;
import com.expedia.seiso.web.controller.PEResource;
import com.expedia.seiso.web.controller.delegate.BasicItemDelegate;
import com.expedia.seiso.web.hateoas.BaseResource;

/**
 * @author Willie Wheeler
 */
@RestController
@RequestMapping("/v1")
public class ServiceInstancePortControllerV1 {
	private static final String SINGLE_URI_TEMPLATE = "/service-instances/{serviceInstanceKey}/ports/{number}";
	
	@Autowired private BasicItemDelegate basicItemDelegate;
	
	@RequestMapping(
			value = SINGLE_URI_TEMPLATE,
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public BaseResource get(@PathVariable String serviceInstanceKey, @PathVariable Integer number) {
		val itemKey = new ServiceInstancePortKey(serviceInstanceKey, number);
		return basicItemDelegate.getOne(itemKey);
	}

	@RequestMapping(
			value = SINGLE_URI_TEMPLATE,
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void put(@PathVariable String serviceInstanceKey, @PathVariable Integer number, PEResource sipResource) {
		basicItemDelegate.put(sipResource.getItem(), true);
	}

	// FIXME Cascade deletion of endpoints isn't working here for some reason. [WLW]
	@RequestMapping(value = SINGLE_URI_TEMPLATE, method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable String serviceInstanceKey, @PathVariable Integer number) {
		val itemKey = new ServiceInstancePortKey(serviceInstanceKey, number);
		basicItemDelegate.delete(itemKey);
	}
}
