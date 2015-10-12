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
package com.expedia.seiso.web.link;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import lombok.val;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;

import com.expedia.seiso.entity.ServiceInstance;
import com.expedia.seiso.web.controller.ServiceInstanceController;

/**
 * @author Willie Wheeler
 */
@Component
public class ServiceInstanceProcessor implements ResourceProcessor<Resource<ServiceInstance>> {

	@Override
	public Resource<ServiceInstance> process(Resource<ServiceInstance> resource) {
		val id = resource.getContent().getId();
		val baseLink = linkTo(ServiceInstanceController.class).slash(id);
		resource.add(link(baseLink, "nodeSummary"));
		resource.add(link(baseLink, "healthBreakdown"));
		resource.add(link(baseLink, "rotationBreakdown"));
		return resource;
	}
	
	private Link link(ControllerLinkBuilder builder, String rel) {
		return builder.slash(rel).withRel(rel);
	}
}
