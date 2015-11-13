package com.expedia.seiso.entity.projection;

import java.util.List;

import org.springframework.data.rest.core.config.Projection;

import com.expedia.seiso.entity.DataCenter;
import com.expedia.seiso.entity.Machine;
import com.expedia.seiso.entity.Node;

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
