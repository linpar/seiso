package com.expedia.seiso.web.resource;

import org.springframework.hateoas.ResourceSupport;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


public class AnnotatedHealthStateResource extends ResourceSupport {

	private String details;

	private Long healthStateID;
	
	public AnnotatedHealthStateResource(){
		
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Long getHealthStateID() {
		return healthStateID;
	}

	public void setHealthStateID(Long healthStateID) {
		this.healthStateID = healthStateID;
	}
	
	
	
	
}
