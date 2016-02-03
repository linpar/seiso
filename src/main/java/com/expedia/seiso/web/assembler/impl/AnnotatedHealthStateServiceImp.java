package com.expedia.seiso.web.assembler.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expedia.seiso.domain.entity.HealthStatus;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.repo.HealthStatusRepo;
import com.expedia.seiso.domain.repo.NodeRepo;
import com.expedia.seiso.web.assembler.AnnotatedHealthStateService;
import com.expedia.seiso.web.resource.AnnotatedHealthStateResource;

import lombok.val;

@Service
@Transactional
public class AnnotatedHealthStateServiceImp implements AnnotatedHealthStateService {

	@Autowired
	private NodeRepo nodeRepo;

	@Autowired
	private HealthStatusRepo healthStatusRepo;
	
	@Override
	public AnnotatedHealthStateResource getAnnotatedHealthState(Long nodeID) {
		val node = nodeRepo.findOne(nodeID);
		AnnotatedHealthStateResource healthState = new AnnotatedHealthStateResource();
		healthState.setDetails(node.getDetails());
		healthState.setHealthStateID(node.getHealthStatus().getId());
		return healthState;
	}

	@Override
	public Boolean setAnnotatedHealthState(Long nodeID, AnnotatedHealthStateResource resource) {
		Node node = nodeRepo.findOne(nodeID);
		HealthStatus hs = healthStatusRepo.findOne(resource.getHealthStateID());
		node.setDetails(resource.getDetails());
		node.setHealthStatus(hs);
		try {
			nodeRepo.save(node);
			return true;
		}
		catch (Exception e){
			return false;
		}
	}
}