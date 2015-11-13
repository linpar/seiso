package com.expedia.seiso.entity.projection;

import org.springframework.data.rest.core.config.Projection;

import com.expedia.seiso.entity.LoadBalancer;
import com.expedia.seiso.entity.DataCenter;

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
