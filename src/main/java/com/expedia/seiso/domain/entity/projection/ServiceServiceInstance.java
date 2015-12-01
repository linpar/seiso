
package com.expedia.seiso.domain.entity.projection;

import org.springframework.data.rest.core.config.Projection;

import com.expedia.seiso.domain.entity.DataCenter;
import com.expedia.seiso.domain.entity.Environment;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.entity.ServiceInstance;

import java.util.List;

/**
 * 
 * @author Ian McCunn
 *
 */
@Projection(name = "serviceServiceInstances", types = ServiceInstance.class)
public interface ServiceServiceInstance {
	
	String getKey();
	
	Environment getEnvironment();
	
	Service getService();
	
	DataCenter getDataCenter();
	
	List<Node> getNodes();

}
