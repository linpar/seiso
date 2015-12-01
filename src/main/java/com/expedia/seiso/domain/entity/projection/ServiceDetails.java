
package com.expedia.seiso.domain.entity.projection;

import org.springframework.data.rest.core.config.Projection;

import java.util.List;

import com.expedia.seiso.domain.entity.DocLink;
import com.expedia.seiso.domain.entity.Person;
import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.entity.ServiceGroup;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.entity.ServiceType;

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
