package com.expedia.seiso.entity.projection;

import org.springframework.data.rest.core.config.Projection;

import com.expedia.seiso.entity.LoadBalancer;
import com.expedia.seiso.entity.DataCenter;

/**
 * 
 * @author Ian McCunn
 *
 */
@Projection(name = "loadBalancersList", types = LoadBalancer.class)
public interface LoadBalancersList {
	
	String getType();
	
	String getIpAddress();
	
	String getName();
	
	String getApiUrl();
	
	DataCenter getDataCenter();
	
}
