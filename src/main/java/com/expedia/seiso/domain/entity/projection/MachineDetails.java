package com.expedia.seiso.domain.entity.projection;

import java.util.List;

import org.springframework.data.rest.core.config.Projection;

import com.expedia.seiso.domain.entity.DataCenter;
import com.expedia.seiso.domain.entity.Machine;
import com.expedia.seiso.domain.entity.Node;

@Projection(name = "machineDetails", types = Machine.class)
public interface MachineDetails {
	
	String getName();

	String getHostname();

	String getDomain();

	String getFqdn();

	String getIpAddress();

	DataCenter getDataCenter();

	List<Node> getNodes();
	
}
