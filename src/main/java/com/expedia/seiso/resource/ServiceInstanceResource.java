package com.expedia.seiso.resource;

import org.springframework.hateoas.ResourceSupport;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@RequiredArgsConstructor
public class ServiceInstanceResource extends ResourceSupport {
	@NonNull
	private Integer numNodes;
	@NonNull
	private Integer numHealthy;
	@NonNull
	private String percentHealthy;
	@NonNull
	private String healthKey;
	@NonNull
	private String key;

}
