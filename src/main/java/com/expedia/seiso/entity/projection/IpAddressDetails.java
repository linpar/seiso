package com.expedia.seiso.entity.projection;

import java.util.List;

import org.springframework.data.rest.core.config.Projection;

import com.expedia.seiso.entity.Endpoint;
import com.expedia.seiso.entity.IpAddressRole;
import com.expedia.seiso.entity.Node;
import com.expedia.seiso.entity.NodeIpAddress;
import com.expedia.seiso.entity.RotationStatus;

@Projection(name = "ipAddressDetails", types = NodeIpAddress.class)
public interface IpAddressDetails {
	
	Node getNode();

	IpAddressRole getIpAddressRole();

	String getIpAddress();

	List<Endpoint> getEndpoints();

	RotationStatus getRotationStatus();

	RotationStatus getAggregateRotationStatus();
}
