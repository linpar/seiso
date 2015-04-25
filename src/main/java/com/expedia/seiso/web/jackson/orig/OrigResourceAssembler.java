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
package com.expedia.seiso.web.jackson.orig;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.stereotype.Component;

import com.expedia.serf.hmedia.Link;
import com.expedia.serf.hmedia.PagedResources;
import com.expedia.serf.hmedia.Relations;
import com.expedia.serf.hmedia.Resource;
import com.expedia.serf.hmedia.Resources;

/**
 * @author Willie Wheeler
 */
@Component
@XSlf4j
public class OrigResourceAssembler {
	
	public List<OrigResource> toOrigResources(@NonNull Resources resources) {
		return toOrigResourceList(resources.getItems());
	}
	
	public List<OrigResource> toOrigResourceList(List<Resource> resourceList) {
		if (resourceList == null) { return null; }
		val origResourceList = new ArrayList<OrigResource>();
		for (val resource : resourceList) { origResourceList.add(toOrigResource(resource)); }
		return origResourceList;
	}
	
	public List<OrigResource> toOrigPagedResources(@NonNull PagedResources pagedResources) {
		val origResourceList = new ArrayList<OrigResource>();
		val resourceList = pagedResources.getItems();
		for (val resource : resourceList) { origResourceList.add(toOrigResource(resource)); }
		return origResourceList;
	}
	
	public OrigResource toOrigResource(@NonNull Resource resource) {
		val srcProps = resource.getProperties();
		val srcAssocs = resource.getAssociations();
		val destProps = new TreeMap<String, Object>();
		
		// Links
		val selfLink = findSelfLink(resource.getLinks());
		if (selfLink != null) {
			destProps.put("_self", selfLink.getHref());
		}
		
		// State
		for (val srcProp : srcProps.entrySet()) {
			val propKey = srcProp.getKey();
			val propVal = srcProp.getValue();
			destProps.put(propKey, propVal);
		}
		for (val srcAssoc : srcAssocs.entrySet()) {
			val assocKey = srcAssoc.getKey();
			val assocVal = srcAssoc.getValue();
			
			if (assocVal == null) {
				destProps.put(assocKey, null);
			} else {
				val assocValClass = assocVal.getClass();
				if (Resource.class.isAssignableFrom(assocValClass)) {
					destProps.put(assocKey, toOrigResource((Resource) assocVal));
				} else if (List.class.isAssignableFrom(assocValClass)) {
					destProps.put(assocKey, toOrigResourceList((List) assocVal));
				} else {
					log.warn("Skipping unknown association type: {}", assocValClass.getName());
				}
			}
		}
		
		val origResource = new OrigResource();
		origResource.setProperties(destProps);
		return origResource;
	}
	
	private Link findSelfLink(List<Link> links) {
		if (links == null) { return null; }
		for (val link : links) {
			if (Relations.SELF.equals(link.getRel())) {
				return link;
			}
		}
		return null;
	}
}
