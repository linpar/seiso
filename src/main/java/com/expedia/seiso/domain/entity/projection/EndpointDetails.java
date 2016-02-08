package com.expedia.seiso.domain.entity.projection;

import org.springframework.data.rest.core.config.Projection;

import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.entity.ServiceInstancePort;

@Projection(name = "endpointDetails", types = Endpoint.class)
public interface EndpointDetails {
	NodeIpAddress getIpAddress();
	RotationStatus getRotationStatus();
	ServiceInstancePort getPort();
}
