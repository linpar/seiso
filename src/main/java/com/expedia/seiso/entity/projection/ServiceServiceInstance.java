
package com.expedia.seiso.entity.projection;

import org.springframework.data.rest.core.config.Projection;

import com.expedia.seiso.entity.ServiceInstance;
import com.expedia.seiso.entity.Environment;
import com.expedia.seiso.entity.DataCenter;
import com.expedia.seiso.entity.Node;
import com.expedia.seiso.entity.Service;
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
