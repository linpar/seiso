package com.expedia.seiso.entity.projection;

import org.springframework.data.rest.core.config.Projection;

import com.expedia.seiso.entity.RotationStatus;
import com.expedia.seiso.entity.StatusType;

@Projection(name = "rotationStatusDetails", types = RotationStatus.class)
public interface RotationStatusDetails {
	String getKey();

	String getName();

	String getDescription();

	StatusType getStatusType();
}
