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
package com.expedia.seiso.web.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.text.NumberFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.expedia.seiso.resource.BreakdownItem;
import com.expedia.seiso.resource.NodeSummary;
import com.expedia.seiso.service.ServiceInstanceService;
import com.expedia.seiso.web.link.IanaLinkRelation;

import lombok.val;

// Is there a way to add the links here using a ResourceProcessor? [WLW]

// FIXME The base path isn't appearing in the links. See
// http://stackoverflow.com/questions/33092440/base-path-doesnt-appear-in-resourceprocessor-custom-links

/**
 * @author Willie Wheeler
 */
@BasePathAwareController
@RequestMapping("/serviceInstances")
public class ServiceInstanceController {
	@Autowired private ServiceInstanceService serviceInstanceService;
	
	private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();
	static {
		percentFormat.setMinimumFractionDigits(1);
		percentFormat.setMaximumFractionDigits(1);
	}
	
	@RequestMapping(value = "/{id}/nodeSummary", method = RequestMethod.GET)
	@ResponseBody
	public NodeSummary getNodeSummary(@PathVariable("id") Long id) {
		val resource = serviceInstanceService.getNodeSummary(id);
		resource.add(baseLink(id).slash("nodeSummary").withSelfRel());
		resource.add(baseLink(id).withRel(IanaLinkRelation.UP));
		return resource;
	}
	
	@RequestMapping(value = "/{id}/healthBreakdown", method = RequestMethod.GET)
	@ResponseBody
	public Resources<BreakdownItem> getHealthBreakdown(@PathVariable("id") Long id) {
		val resources = serviceInstanceService.getHealthBreakdown(id);
		resources.add(baseLink(id).slash("healthBreakdown").withSelfRel());
		resources.add(baseLink(id).withRel(IanaLinkRelation.UP));
		return resources;
	}
	
	@RequestMapping(value = "/{id}/rotationBreakdown", method = RequestMethod.GET)
	@ResponseBody
	public Resources<BreakdownItem> getRotationBreakdown(@PathVariable("id") Long id) {
		val resources = serviceInstanceService.getRotationBreakdown(id);
		resources.add(baseLink(id).slash("rotationBreakdown").withSelfRel());
		resources.add(baseLink(id).withRel(IanaLinkRelation.UP));
		return resources;
	}
	
	private ControllerLinkBuilder baseLink(Long id) {
		return linkTo(ServiceInstanceController.class).slash(id);
	}
	
}
