package com.expedia.seiso.web.assembler.impl;

import java.text.NumberFormat;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.repo.EnvironmentRepo;
import com.expedia.seiso.web.assembler.EnvironmentService;
import com.expedia.seiso.web.resource.ServiceInstanceResource;

import lombok.NonNull;
import lombok.val;

@Service
@Transactional
public class EnvironmentServiceImpl implements EnvironmentService {
	
	@Autowired
	private EnvironmentRepo envRepo;
	
	private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();

	static {
		percentFormat.setMinimumFractionDigits(1);
		percentFormat.setMaximumFractionDigits(1);
	}

	@Override
	public Resources<ServiceInstanceResource> getServiceInstances(@NonNull Long serviceId) {
		
		val env = envRepo.findOne(serviceId);
		val serviceInstances = env.getServiceInstances();
		val serviceServiceInstances = serviceInstances.stream().map(si -> toServiceInstanceResource(si))
				.collect(Collectors.toList());

		return new Resources<ServiceInstanceResource>(serviceServiceInstances);
	}

	private ServiceInstanceResource toServiceInstanceResource(ServiceInstance serviceInstance) {

		val siNodes = serviceInstance.getNodes();
		val siResource = new ServiceInstanceResource();
		val numNodes = siNodes.size();
		int numHealthy = 0;
		for (Node siN : siNodes) {
			String healthKey = siN.getHealthStatus() != null ? siN.getHealthStatus().getKey() : "unknown";
			// TODO: There are more cases in which a node deemed healthy.
			if (healthKey.equalsIgnoreCase("Healthy"))
				numHealthy++;
		}

		String percentHealthy = (numNodes == 0L ? "N/A"
				: percentFormat.format((double) numHealthy / (double) numNodes));

		String statusKey = ((numHealthy < numNodes || numNodes == 0) ? "warning" : "success");
		siResource.setKey(serviceInstance.getKey());
		siResource.setNumNodes(numNodes);
		siResource.setHealthKey(statusKey);
		siResource.setNumHealthy(numHealthy);
		siResource.setPercentHealthy(percentHealthy);

		return siResource;
	}
}
