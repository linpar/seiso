package com.expedia.seiso.web.assembler;

import com.expedia.seiso.web.resource.AnnotatedHealthStateResource;

public interface AnnotatedHealthStateService {
	
	AnnotatedHealthStateResource getAnnotatedHealthState(Long id);
	
	Boolean setAnnotatedHealthState(Long id, AnnotatedHealthStateResource resource);

}
