package com.expedia.seiso.entity.projection;

import org.springframework.data.rest.core.config.Projection;

import com.expedia.seiso.entity.HealthStatus;
import com.expedia.seiso.entity.StatusType;

@Projection(name = "healthStatusDetails", types = HealthStatus.class)
public interface HealthStatusDetails {
	String getKey();

	String getName();
	
	String getDescription();

	StatusType getStatusType();
}
