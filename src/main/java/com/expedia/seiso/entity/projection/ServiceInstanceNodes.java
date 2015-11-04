package com.expedia.seiso.entity.projection;

import java.util.List;

import org.springframework.data.rest.core.config.Projection;

import com.expedia.seiso.entity.HealthStatus;
import com.expedia.seiso.entity.Machine;
import com.expedia.seiso.entity.Node;
import com.expedia.seiso.entity.NodeIpAddress;
import com.expedia.seiso.entity.RotationStatus;

@Projection(name = "serviceInstanceNodes", types = Node.class)
public interface ServiceInstanceNodes {
	
	String getName();
	
	Machine getMachine();

	HealthStatus getHealthStatus();

	RotationStatus getAggregateRotationStatus();

	List<NodeIpAddress> getIpAddresses();
	
	String getVersion();
}
