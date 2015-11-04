
package com.expedia.seiso.entity.projection;

import java.util.List;

import org.springframework.data.rest.core.config.Projection;

import com.expedia.seiso.entity.Dashboard;
import com.expedia.seiso.entity.DataCenter;
import com.expedia.seiso.entity.Environment;
import com.expedia.seiso.entity.IpAddressRole;
import com.expedia.seiso.entity.LoadBalancer;
import com.expedia.seiso.entity.Node;
import com.expedia.seiso.entity.Service;
import com.expedia.seiso.entity.ServiceInstance;
import com.expedia.seiso.entity.ServiceInstancePort;

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
