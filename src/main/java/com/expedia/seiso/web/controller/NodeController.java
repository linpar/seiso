package com.expedia.seiso.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.repo.NodeRepo;

@BasePathAwareController
@RequestMapping("/nodes")
public class NodeController {
	
	@Autowired
	NodeRepo nodeRepo;

	@RequestMapping(
			value = "/search/findByServiceInstanceKeyAndHealthStatusKey", 
			method = RequestMethod.GET,
			params = "mode=nodeDetails")
	@ResponseBody
	public HttpEntity<PagedResources<Node>> searchBySiAndHsKey(@Param("siKey") String siKey,
			@Param("hsKey") String hsKey, Pageable pageable, PagedResourcesAssembler assembler) {
		Page<Node> nodes = nodeRepo.findByServiceInstanceKeyAndHealthStatusKey(siKey, hsKey, pageable);
		if (nodes.getSize() > 0){
			return new ResponseEntity<>(assembler.toResource(nodes), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(assembler.toResource(nodes), HttpStatus.NOT_FOUND);
		}
	}
}
