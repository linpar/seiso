package com.expedia.seiso.domain.entity.projection;

import org.springframework.data.rest.core.config.Projection;

import com.expedia.seiso.domain.entity.DataCenter;
import com.expedia.seiso.domain.entity.Region;

@Projection(name = "dataCenterDetails", types = DataCenter.class)
public interface DataCenterDetails {
	String getName();
	Region getRegion();
}
