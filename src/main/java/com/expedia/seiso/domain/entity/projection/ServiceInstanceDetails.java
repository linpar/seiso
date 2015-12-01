
package com.expedia.seiso.domain.entity.projection;

import java.util.List;

import org.springframework.data.rest.core.config.Projection;

import com.expedia.seiso.domain.entity.Dashboard;
import com.expedia.seiso.domain.entity.DataCenter;
import com.expedia.seiso.domain.entity.Environment;
import com.expedia.seiso.domain.entity.IpAddressRole;
import com.expedia.seiso.domain.entity.LoadBalancer;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.entity.ServiceInstancePort;

/**
 * 
 * @author Ian McCunn
 *
 */
@Projection(name = "serviceInstanceDetails", types = ServiceInstance.class)
public interface ServiceInstanceDetails {
	
	String getKey();
	
	String getDescription();
	
	Environment getEnvironment();
	
	Service getService();
	
	DataCenter getDataCenter();
	
	LoadBalancer getLoadBalancer();
	
	List<IpAddressRole> getIpAddressRoles();
	
	List<ServiceInstancePort> getPorts();
	
	List<Node> getNodes();
	
	Boolean getLoadBalanced();
	
	Integer getMinCapacityDeploy();
	
	List<Dashboard> getDashboards();
	
}
