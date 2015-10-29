package com.expedia.seiso.service;

import org.springframework.hateoas.Resources;

import com.expedia.seiso.resource.ServiceInstanceResource;

public interface EnvironmentService {
	
	Resources<ServiceInstanceResource> getServiceInstances(Long id);
}
