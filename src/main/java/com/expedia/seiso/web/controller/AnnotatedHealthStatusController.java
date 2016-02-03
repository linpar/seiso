package com.expedia.seiso.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.expedia.seiso.web.assembler.AnnotatedHealthStateService;
import com.expedia.seiso.web.resource.AnnotatedHealthStateResource;

@BasePathAwareController
@RequestMapping("/annotatedHealthStatus")
public class AnnotatedHealthStatusController {
	
	@Autowired
	AnnotatedHealthStateService healthStateService;
	
	@RequestMapping(
			value = "/{nodeID}/",
			method = RequestMethod.GET)
	@ResponseBody
	public AnnotatedHealthStateResource getAnnotatedHealthState(@PathVariable("nodeID") Long nodeID) {;
		return healthStateService.getAnnotatedHealthState(nodeID);
	}
	
	@RequestMapping(
			value = "/{nodeID}/",
			method = RequestMethod.PATCH)
	@ResponseBody
	public Boolean setAnnotatedHealthState(@PathVariable("nodeID") Long nodeID, 
			@RequestBody AnnotatedHealthStateResource ahs) {
		return healthStateService.setAnnotatedHealthState(nodeID, ahs);
	}
}
