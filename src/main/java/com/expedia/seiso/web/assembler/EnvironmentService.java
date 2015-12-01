package com.expedia.seiso.web.assembler;

import org.springframework.hateoas.Resources;

import com.expedia.seiso.web.resource.ServiceInstanceResource;

public interface EnvironmentService {
	
	Resources<ServiceInstanceResource> getServiceInstances(Long id);
}
