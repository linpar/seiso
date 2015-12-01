package com.expedia.seiso.domain.entity.projection;

import org.springframework.data.rest.core.config.Projection;

import com.expedia.seiso.domain.entity.DataCenter;
import com.expedia.seiso.domain.entity.LoadBalancer;

/**
 * 
 * @author Ian McCunn
 *
 */
@Projection(name = "loadBalancerDetails", types = LoadBalancer.class)
public interface LoadBalancerDetails {
	
	String getType();
	
	String getIpAddress();
	
	String getName();
	
	String getApiUrl();
	
	DataCenter getDataCenter();
	
}
