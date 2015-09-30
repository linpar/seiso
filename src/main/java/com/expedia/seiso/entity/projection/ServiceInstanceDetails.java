
package com.expedia.seiso.entity.projection;

import org.springframework.data.rest.core.config.Projection;

import com.expedia.seiso.entity.ServiceInstance;
import com.expedia.seiso.entity.Environment;
import com.expedia.seiso.entity.DataCenter;
import com.expedia.seiso.entity.LoadBalancer;
import com.expedia.seiso.entity.Node;
import com.expedia.seiso.entity.Service;
import java.util.List;

/**
 * 
 * @author Ian McCunn
 *
 */
@Projection(name = "serviceInstanceDetails", types = ServiceInstance.class)
public interface ServiceInstanceDetails {
	
	String getKey();
	
	Environment getEnvironment();
	
	Service getService();
	
	DataCenter getDataCenter();
	
	LoadBalancer getLoadBalancer();
	
	List<Node> getNodes();
	
}
