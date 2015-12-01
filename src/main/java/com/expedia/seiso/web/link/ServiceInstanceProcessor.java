/* 
 * Copyright 2013-2016 the original author or authors.
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
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import lombok.val;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.web.controller.ServiceInstanceController;

// FIXME The base path isn't appearing in the links. See
// http://stackoverflow.com/questions/33092440/base-path-doesnt-appear-in-resourceprocessor-custom-links

/**
 * @author Willie Wheeler
 */
@Component
public class ServiceInstanceProcessor implements ResourceProcessor<Resource<ServiceInstance>> {

	@Override
	public Resource<ServiceInstance> process(Resource<ServiceInstance> resource) {
		val id = resource.getContent().getId();
		val controller = methodOn(ServiceInstanceController.class);
		resource.add(linkTo(controller.getNodeSummary(id)).withRel("nodeSummary"));
		resource.add(linkTo(controller.getHealthBreakdown(id)).withRel("healthBreakdown"));
		resource.add(linkTo(controller.getRotationBreakdown(id)).withRel("rotationBreakdown"));
		return resource;
	}
}
