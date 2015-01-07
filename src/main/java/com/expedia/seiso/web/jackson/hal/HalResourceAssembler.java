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
package com.expedia.seiso.web.jackson.hal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lombok.val;

import org.springframework.stereotype.Component;

import com.expedia.seiso.web.hateoas.Link;
import com.expedia.seiso.web.hateoas.BaseResource;
import com.expedia.seiso.web.hateoas.BaseResourcePage;

/**
 * @author Willie Wheeler
 */
@Component
public class HalResourceAssembler {
	
	public HalResource toHalResourcePage(BaseResourcePage baseResourcePage) {
		val halResourcePage = new HalResource();
		halResourcePage.setLinks(toHalLinks(baseResourcePage.getLinks(), true));
		
		// Embedded
		val embedded = new TreeMap<String, Object>();
		val halResourceItems = new ArrayList<HalResource>();
		val baseResourceItems = (List<BaseResource>) baseResourcePage.getItems();
		for (val baseResourceItem : baseResourceItems) {
			halResourceItems.add(toHalResource(baseResourceItem, false));
		}
		embedded.put("items", halResourceItems);
		halResourcePage.setEmbedded(embedded);
		
		// State (page metadata)
		val state = new TreeMap<String, Object>();
		state.put("metadata", baseResourcePage.getMetadata());
		halResourcePage.setState(state);
		
		return halResourcePage;
	}
	
	public HalResource toHalResource(BaseResource baseResource, boolean topLevel) {
		val halResource = new HalResource();
		halResource.setLinks(toHalLinks(baseResource.getV2Links(), topLevel));
		
		// Embedded and state
		val embedded = new TreeMap<String, Object>();
		val state = new TreeMap<String, Object>();
		
		val props = baseResource.getProperties();
		for (val prop : props.entrySet()) {
			val propName = prop.getKey();
			val propValue = prop.getValue();
			if ("id".equals(propName)) {
				continue;
			} else if (propValue instanceof BaseResource) {
				embedded.put(propName, toHalResource((BaseResource) propValue, false));
			} else if (propValue instanceof List) {
				val halResourceKids = new ArrayList<HalResource>();
				val baseResourceKids = (List<BaseResource>) propValue;
				for (val resourceKid : baseResourceKids) {
					halResourceKids.add(toHalResource(resourceKid, false));
				}
				embedded.put(propName,  halResourceKids);
			} else {
				state.put(propName, propValue);
			}
		}
		
		halResource.setEmbedded(embedded.isEmpty() ? null : embedded);
		halResource.setState(state);
		
		return halResource;
	}
	
	private Map<String, Object> toHalLinks(List<Link> links, boolean includeCuries) {
		
		// Don't show _links unless there are actually links.
		if (links == null || links.isEmpty()) { return null; }
		
		val halLinks = new LinkedHashMap<String, Object>();
		
		// FIXME If the link relation can have multiple links, then we need to generate an array here. Note that this
		// depends on the nature of the relation itself, not the actual number of links in any given case. So for
		// example if a manager has a single direct report, we would still generate an array of direct report links
		// since in principle the manager could have many.
		for (val link : links) {
			val halLink = new LinkedHashMap<String, Object>();
			halLink.put("href", link.getHref());
			
			val title = link.getTitle();
			if (title != null) { halLink.put("title", title); }
			
			val templated = link.getTemplated();
			if (templated != null) { halLink.put("templated", templated); }
			
			halLinks.put(link.getRel(), halLink);
		}
		
		if (includeCuries) { halLinks.put("curies", Curie.SEISO_CURIES); }
		
		return halLinks;
	}
}
