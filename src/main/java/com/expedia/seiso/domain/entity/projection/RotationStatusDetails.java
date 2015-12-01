package com.expedia.seiso.domain.entity.projection;

import org.springframework.data.rest.core.config.Projection;

import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.entity.StatusType;

@Projection(name = "rotationStatusDetails", types = RotationStatus.class)
public interface RotationStatusDetails {
	String getKey();

	String getName();

	String getDescription();

	StatusType getStatusType();
}
