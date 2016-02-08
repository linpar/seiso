package com.expedia.seiso.web.assembler;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.entity.LoadBalancer;
import com.expedia.seiso.domain.entity.Machine;
import com.expedia.seiso.domain.entity.Person;
import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.service.SearchResults;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Component
public class GlobalSearchAssembler {
	
	@SuppressWarnings("unchecked")
	public SearchResultsDto toGlobalSearchResource(@NonNull SearchResults searchResults) {
		Page<Machine> machines = (Page<Machine>) searchResults.getTypedSerp(Machine.class);
		Page<Person> people = (Page<Person>) searchResults.getTypedSerp(Person.class);
		Page<Service> services = (Page<Service>) searchResults.getTypedSerp(Service.class);
		Page<ServiceInstance> serviceInstances = (Page<ServiceInstance>) searchResults.getTypedSerp(ServiceInstance.class);
		Page<LoadBalancer> loadBalancers = (Page<LoadBalancer>) searchResults.getTypedSerp(LoadBalancer.class);
		Page<Node> nodes = (Page<Node>) searchResults.getTypedSerp(Node.class);
		
		// This is the DTO we're assembling.
		SearchResultsDto resultsDto = new SearchResultsDto();
		resultsDto.setServices(toServiceDtos(services.getContent()));
		resultsDto.setServiceInstances(toServiceInstanceDtos(serviceInstances.getContent()));
		resultsDto.setLoadBalancers(toLoadBalancerDtos(loadBalancers.getContent()));
		resultsDto.setPeople(toPersonDtos(people.getContent()));
		resultsDto.setMachines(toMachineDtos(machines.getContent()));
		resultsDto.setNodes(toNodeDtos(nodes.getContent()));
		
		return resultsDto;
	}
	
	// Machine
	private List<MachineDto> toMachineDtos(List<Machine> machines) {
		return machines.stream().map(m -> toMachineDto(m)).collect(Collectors.toList());
	}
	
	private MachineDto toMachineDto(Machine machine) {
		return new MachineDto(machine.getName());
	}
	
	// Person
	private List<PersonDto> toPersonDtos(List<Person> people) {
		return people.stream().map(p -> toPersonDto(p)).collect(Collectors.toList());
	}
	
	private PersonDto toPersonDto(Person person) {
		return new PersonDto(person.getUsername(), person.getFirstName(), person.getLastName());
	}
	
	// Service
	private List<ServiceDto> toServiceDtos(List<Service> services) {
		return services.stream().map(p -> toServiceDto(p)).collect(Collectors.toList());
	}
	
	private ServiceDto toServiceDto(Service service) {
		return new ServiceDto(service.getName(), service.getKey());
	}
	
	// ServiceInstance
	private List<ServiceInstanceDto> toServiceInstanceDtos(List<ServiceInstance> serviceInstances) {
		return serviceInstances.stream().map(p -> toServiceInstanceDto(p)).collect(Collectors.toList());
	}
	
	private ServiceInstanceDto toServiceInstanceDto(ServiceInstance serviceInstance) {
		return new ServiceInstanceDto(serviceInstance.getKey());
	}
	
	// LoadBalancer
	private List<LoadBalancerDto> toLoadBalancerDtos(List<LoadBalancer> loadBalancers) {
		return loadBalancers.stream().map(p -> toLoadBalancerDto(p)).collect(Collectors.toList());
	}
	
	private LoadBalancerDto toLoadBalancerDto(LoadBalancer loadBalancer) {
		return new LoadBalancerDto(loadBalancer.getName());
	}
	
	// Nodes
	private List<NodeDto> toNodeDtos(List<Node> nodes) {
		return nodes.stream().map(n -> toNodeDto(n)).collect(Collectors.toList());
	}
	
	private NodeDto toNodeDto(Node node) {
		return new NodeDto(node.getName());
	}
	
	@Data
	@AllArgsConstructor
	private static class MachineDto {
		private String name;
	}
	
	@Data
	@AllArgsConstructor
	private static class PersonDto {
		private String username;
		private String firstName;
		private String lastName;
	}
	
	@Data
	@AllArgsConstructor
	private static class ServiceDto {
		private String name;
		private String key;
	}
	
	@Data
	@AllArgsConstructor
	private static class ServiceInstanceDto {
		private String key;
	}
	
	@Data
	@AllArgsConstructor
	private static class LoadBalancerDto {
		private String name;
	}
	
	@Data
	@AllArgsConstructor
	private static class NodeDto {
		private String name;
	}
}
