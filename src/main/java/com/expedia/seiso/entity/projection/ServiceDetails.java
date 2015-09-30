
package com.expedia.seiso.entity.projection;

import org.springframework.data.rest.core.config.Projection;

import java.util.List;

import com.expedia.seiso.entity.Service;
import com.expedia.seiso.entity.ServiceType;
import com.expedia.seiso.entity.ServiceGroup;
import com.expedia.seiso.entity.ServiceInstance;
import com.expedia.seiso.entity.DocLink;
import com.expedia.seiso.entity.Person;

/**
 * 
 * @author Ian McCunn
 *
 */
@Projection(name = "serviceDetails", types = Service.class)
public interface ServiceDetails {
	
	String getKey();
	
	String getName();
	
	ServiceType getType();
	
	ServiceGroup getGroup();
	
	Person getOwner();
	
	List<ServiceInstance> getServiceInstances();
	
	List<DocLink> getDocLinks();
	
	String getPlatform();
	
}
