package com.expedia.seiso.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.expedia.seiso.web.assembler.EnvironmentService;
import com.expedia.seiso.web.resource.ServiceInstanceResource;

@BasePathAwareController
@RequestMapping("/environments")
public class EnvironmentController {
	
	@Autowired
	EnvironmentService envService;
	
	@RequestMapping(
			value = "/{id}/serviceInstances",
			method = RequestMethod.GET,
			params = "mode=nodeDetails")
	@ResponseBody
	public Resources<ServiceInstanceResource> getServiceInstances(@PathVariable("id") Long id) {;
		return envService.getServiceInstances(id);
	}
}
