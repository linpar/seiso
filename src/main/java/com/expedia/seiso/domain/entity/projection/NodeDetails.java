package com.expedia.seiso.domain.entity.projection;

import java.util.List;

import org.springframework.data.rest.core.config.Projection;

import com.expedia.seiso.domain.entity.HealthStatus;
import com.expedia.seiso.domain.entity.Machine;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.RotationStatus;

@Projection(name = "nodeDetails", types = Node.class)
public interface NodeDetails {
	String getName();
	String getDescription();
	String getBuildVersion();
	Machine getMachine();
	List<NodeIpAddress> getIpAddresses();
	HealthStatus getHealthStatus();
	RotationStatus getAggregateRotationStatus();
}
