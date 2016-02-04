package com.expedia.seiso.web.assembler;

import java.util.List;

import lombok.Data;

@Data
public class SearchResultsDto {
	
	private List<?> machines;
	private List<?> people;
	private List<?> services;
	private List<?> serviceInstances;
	private List<?> loadBalancers;
	private List<?> nodes;
	
}
