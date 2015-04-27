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
package com.expedia.seiso.web.controller.v2;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.PEResource;
import com.expedia.seiso.web.controller.delegate.BasicItemDelegate;
import com.expedia.serf.ann.SuppressBasePath;
import com.expedia.serf.web.MediaTypes;

/**
 * @author Willie Wheeler
 */
@RestController
@RequestMapping("/v2/{repoKey}/{itemKey}/{propKey}")
@SuppressBasePath
@Transactional
public class ItemPropertyControllerV2 {
	@Autowired private BasicItemDelegate delegate;
	
	/**
	 * Returns the given property. This can be single- or collection-valued.
	 * 
	 * @param repoKey
	 * @param itemKey
	 * @param propKey
	 * @param view
	 * @return
	 */
	@RequestMapping(
			method = RequestMethod.GET,
			produces = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	public Object getProperty(
			@PathVariable String repoKey,
			@PathVariable String itemKey,
			@PathVariable String propKey,
			@RequestParam(defaultValue = Projection.DEFAULT) String view) {
		
		return delegate.getProperty(ApiVersion.V2, repoKey, itemKey, propKey, view);
	}
	
	@RequestMapping(
			method = RequestMethod.POST,
			consumes = MediaTypes.APPLICATION_HAL_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	// TODO Location header
	public void postCollectionPropertyElement(
			@PathVariable String repoKey,
			@PathVariable String itemKey,
			@PathVariable String propKey,
			PEResource peResource) {
		
		delegate.postCollectionPropertyElement(ApiVersion.V2, repoKey, itemKey, propKey, peResource);
	}
	
	/**
	 * Assigns an item to a given property.
	 * 
	 * @param repoKey
	 *            Repository key
	 * @param itemKey
	 *            Item key
	 * @param propKey
	 *            Property key
	 * @param propItemKey
	 *            Key for the item to assign to the property
	 */
//	@RequestMapping(
//			method = RequestMethod.PUT,
//			consumes = MediaTypes.TEXT_URI_LIST_VALUE)
//	@ResponseStatus(HttpStatus.NO_CONTENT)
//	public void putProperty(
//			@PathVariable String repoKey,
//			@PathVariable String itemKey,
//			@PathVariable String propKey,
//			@RequestBody(required = false) ItemKey propItemKey) {
//		
//		delegate.putProperty(repoKey, itemKey, propKey, propItemKey);
//	}
	
	@RequestMapping(
			value = "/{elemId}",
			method = RequestMethod.DELETE)
	public void deleteCollectionPropertyElement(
			@PathVariable String repoKey,
			@PathVariable String itemKey,
			@PathVariable String propKey,
			@PathVariable Long elemId) {
		
		delegate.deleteCollectionPropertyElement(ApiVersion.V2, repoKey, itemKey, propKey, elemId);
	}
}
