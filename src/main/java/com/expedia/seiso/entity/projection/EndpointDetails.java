package com.expedia.seiso.entity.projection;

import org.springframework.data.rest.core.config.Projection;

import com.expedia.seiso.entity.Endpoint;
import com.expedia.seiso.entity.NodeIpAddress;
import com.expedia.seiso.entity.RotationStatus;
import com.expedia.seiso.entity.ServiceInstancePort;

@Projection(name = "endpointDetails", types = Endpoint.class)
public interface EndpointDetails {
	NodeIpAddress getNodeIpAddress();
	RotationStatus getRotationStatus();
	ServiceInstancePort getServiceInstancePort();
}
