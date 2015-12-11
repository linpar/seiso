package com.expedia.seiso.domain.entity.projection;

import java.util.List;

import org.springframework.data.rest.core.config.Projection;

import com.expedia.seiso.domain.entity.HealthStatus;
import com.expedia.seiso.domain.entity.Machine;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.RotationStatus;

@Projection(name = "serviceInstanceNodes", types = Node.class)
public interface ServiceInstanceNodes {
	
	String getName();
	
	Machine getMachine();

	HealthStatus getHealthStatus();

	RotationStatus getAggregateRotationStatus();

	List<NodeIpAddress> getIpAddresses();
	
	String getBuildVersion();
}
