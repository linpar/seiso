/* 
 * Copyright 2013-2015 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.expedia.seiso.web.assembler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lombok.NonNull;
import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.mapping.model.BeanWrapper;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.Person;
import com.expedia.seiso.domain.service.SearchResults;
import com.expedia.seiso.web.Relations;
import com.expedia.seiso.web.hateoas.Link;
import com.expedia.seiso.web.hateoas.PageMetadata;
import com.expedia.seiso.web.hateoas.PagedResources;
import com.expedia.seiso.web.hateoas.Resource;
import com.expedia.seiso.web.hateoas.Resources;
import com.expedia.seiso.web.hateoas.link.LinkFactory;

/**
 * Assembles items into resources, which include links (in support of the REST HATEOAS principle). Subsequent
 * processing (outside this class) serializes the resource into special representation formats, such as HAL.
 * 
 * @author Willie Wheeler
 */
@Component
public class ResourceAssembler {
	private static final MultiValueMap<String, String> EMPTY_PARAMS = new LinkedMultiValueMap<String, String>();
	
	@Autowired private Repositories repositories;
	@Autowired @Qualifier("linkFactoryV1") private LinkFactory linkFactoryV1;
	@Autowired @Qualifier("linkFactoryV2") private LinkFactory linkFactoryV2;
	
	
	// =================================================================================================================
	// CRUD repo resources
	// =================================================================================================================
	
	public Resources toResources(
			@NonNull Class<?> itemClass,
			List<?> itemList,
			ProjectionNode proj) {
		
		if (itemList == null) { return null; }
		
		val links = toRepoListLinksV2(itemClass, itemList);
		val resourceList = new ArrayList<Resource>();
		for (val item : itemList) {
			// Don't pass params from the collection view to the single view.
			resourceList.add(toResource((Item) item, proj, false));
		}
		
		return new Resources(links, resourceList);
	}
	
	
	// =================================================================================================================
	// Paging repo resources
	// =================================================================================================================
	
	public PagedResources toPagedResources(@NonNull Class<?> itemClass, Page<?> itemPage, ProjectionNode proj) {
		return toPagedResources(itemClass, itemPage, proj, EMPTY_PARAMS);
	}
	
	public PagedResources toPagedResources(
			@NonNull Class<?> itemClass,
			Page<?> itemPage,
			ProjectionNode proj,
			MultiValueMap<String, String> params) {
		
		if (itemPage == null) { return null; }
		
		val links = toRepoPageLinksV2(itemClass, itemPage, params);
		val pageMeta = toPageMetadata(itemPage);
		val items = toResourceList(itemPage.getContent(), proj);
		return new PagedResources(links, pageMeta, items);
	}
	
	
	// =================================================================================================================
	// Bare resources
	// =================================================================================================================
	
	public List<Resource> toResourceList(List<?> itemList, ProjectionNode proj) {
		if (itemList == null) { return null; }
		val resourceList = new ArrayList<Resource>();
		for (val item : itemList) {
			// Don't pass params from the collection view to the single view.
			resourceList.add(toResource((Item) item, proj, false));
		}
		return resourceList;
	}
	
	public Resource toResource(Item item, ProjectionNode proj) { return toResource(item, proj, false); }
	
	public Resource toResource(Item item, ProjectionNode proj, boolean includeCuries) {
		if (item == null) { return null; }
		
		val itemClass = item.getClass();
		val resource = new Resource();
		val pEntity = repositories.getPersistentEntity(item.getClass());
		val itemWrapper = BeanWrapper.create(item, null);
		
		val itemLinksV1 = linkFactoryV1.getItemLinks();
		val itemLinksV2 = linkFactoryV2.getItemLinks();
		
		resource.addV1Link(itemLinksV1.itemLink(item));
		resource.addV2Link(itemLinksV2.itemLink(item));
		resource.addV2Link(itemLinksV2.repoLink(Relations.UP, itemClass));
		pEntity.doWithProperties(new ItemPropertyHandler(itemWrapper, resource.getProperties()));
		pEntity.doWithAssociations(new ItemAssociationHandler(this, itemLinksV2, proj, itemWrapper, resource));
		doSpecialNonPersistentAssociations(item, resource.getAssociations());
		
		return resource;
	}
	
	
	// =================================================================================================================
	// Repo search resources
	// =================================================================================================================
	
	/**
	 * Assembles a resource page from a repo search result page.
	 * 
	 * @param resultPage
	 *            Search result page
	 * @param itemClass
	 *            Item class
	 * @param path
	 *            Search path
	 * @param params
	 *            Search parameters
	 * @param proj
	 *            Projection
	 * 
	 * @return Repository search result resource page
	 */
	public PagedResources toRepoSearchResource(
			@NonNull Page resultPage,
			@NonNull Class itemClass,
			@NonNull String path,
			@NonNull MultiValueMap<String, String> params,
			@NonNull ProjectionNode proj) {
		
		val repoSearchLinksV2 = linkFactoryV2.getRepoSearchLinks();
		val links = toRepoSearchLinksV2(resultPage, itemClass, path, params);
		val pageMeta = toPageMetadata(resultPage);
		val itemList = resultPage.getContent();
		val itemResourceList = toResourceList(itemList, proj);
		return new PagedResources(links, pageMeta, itemResourceList);
	}
	
	
	// =================================================================================================================
	// Global search resources
	// =================================================================================================================
	
	/**
	 * Assembles a resource from a global search result set.
	 * 
	 * @param results
	 *            Global search result set
	 * @return Resource for the result set
	 */
	public Resource toGlobalSearchResource(@NonNull SearchResults results) {
		val resultsResource = new Resource();
		val itemClasses = results.getItemClasses();
		for (val itemClass : itemClasses) {
			val propName = StringUtils.uncapitalize(itemClass.getSimpleName());
			val typedSerp = results.getTypedSerp(itemClass);
			
			// TODO Not sure we want flat projection here. Shouldn't we do the default collection projection?
			val typedSerpResourceList = toResourceList(typedSerp.getContent(), ProjectionNode.FLAT_PROJECTION_NODE);
			
			resultsResource.setProperty(propName, typedSerpResourceList);
		}
		return resultsResource;
	}
	
	
	// =================================================================================================================
	// Special resources
	// =================================================================================================================
	
	// TODO Generalize
	@Deprecated
	public PagedResources toUsernamePage(Page<Person> personPage, MultiValueMap<String, String> params) {
		if (personPage == null) { return null; }
		val links = toRepoPageLinksV2(Person.class, personPage, params);
		val pageMeta = toPageMetadata(personPage);
		val usernames = toUsernameList(personPage.getContent());
		return new PagedResources(links, pageMeta, usernames);
	}
	
	// TODO Generalize
	@Deprecated
	public List<Resource> toUsernameList(List<Person> people) {
		if (people == null) { return null; }
		val usernameResources = new ArrayList<Resource>();
		for (val person : people) {
			val props = new TreeMap<String, Object>();
			props.put("username", person.getUsername());
			val usernameResource = new Resource();
			usernameResource.setProperties(props);
			usernameResources.add(usernameResource);
		}
		return usernameResources;
	}
	
	
	// =================================================================================================================
	// Private
	// =================================================================================================================
	
	private List<Link> toRepoListLinksV2(Class<?> itemClass, List<?> itemList) {
		val itemLinksV2 = linkFactoryV2.getItemLinks();
		val repoSearchLinksV2 = linkFactoryV2.getRepoSearchLinks();
		
		val links = new ArrayList<Link>();
		links.add(itemLinksV2.repoLink(itemClass, EMPTY_PARAMS));
		links.add(repoSearchLinksV2.repoSearchListLink(Relations.S_SEARCH, itemClass));
		return links;
	}
	
	private List<Link> toRepoPageLinksV2(
			Class<?> itemClass,
			Page<?> itemPage,
			MultiValueMap<String, String> params) {
		
		// 0-indexed
		val pageNumber = itemPage.getNumber();
		val totalPages = itemPage.getTotalPages();
		val firstPageNumber = 0;
		val lastPageNumber = totalPages - 1;
		
		val itemLinksV2 = linkFactoryV2.getItemLinks();
		val repoSearchLinksV2 = linkFactoryV2.getRepoSearchLinks();
		
		val links = new ArrayList<Link>();
		links.add(itemLinksV2.repoLink(itemClass, params));
		
		// Pagination links
		if (totalPages > 0) {
			links.add(itemLinksV2.repoFirstLink(itemClass, itemPage, params));
		}
		if (pageNumber > 0 && pageNumber <= lastPageNumber) {
			links.add(itemLinksV2.repoPrevLink(itemClass, itemPage, params));
		}
		if (pageNumber >= firstPageNumber && pageNumber < lastPageNumber) {
			links.add(itemLinksV2.repoNextLink(itemClass, itemPage, params));
		}
		if (totalPages > 0) {
			links.add(itemLinksV2.repoLastLink(itemClass, itemPage, params));
		}
		
		links.add(repoSearchLinksV2.repoSearchListLink(Relations.S_SEARCH, itemClass));
		return links;
	}
	
	private List<Link> toRepoSearchLinksV2(
			Page resultPage,
			Class itemClass,
			String path,
			MultiValueMap<String, String> params) {
		
		val repoSearchLinks = linkFactoryV2.getRepoSearchLinks();
		val linkBuilder = repoSearchLinks.toPaginationLinkBuilder(resultPage, itemClass, path, params);
		val firstLink = linkBuilder.buildFirstLink();
		val prevLink = linkBuilder.buildPreviousLink();
		val nextLink = linkBuilder.buildNextLink();
		val lastLink = linkBuilder.buildLastLink();
		
		val links = new LinkedList<Link>();
		links.add(linkBuilder.buildSelfLink());
		links.add(repoSearchLinks.repoSearchListLink(Relations.UP, itemClass));
		if (firstLink != null) { links.add(firstLink); }
		if (prevLink != null) { links.add(prevLink); }
		if (nextLink != null) { links.add(nextLink); }
		if (lastLink != null) { links.add(lastLink); }
		return links;
	}
	
	private PageMetadata toPageMetadata(Page<?> itemPage) {
		val pageSize = itemPage.getSize();
		val pageNumber = itemPage.getNumber();
		val totalItems = itemPage.getTotalElements();
		return new PageMetadata(pageSize, pageNumber, totalItems);
	}
	
	// This is a temporary hack to handle special-case non-persistent properties.
	// Specifically, we need to be able to map NodeIpAddress.aggregateRotationStatus. [WLW]
	@Deprecated
	private void doSpecialNonPersistentAssociations(Item item, Map<String, Object> model) {
		val itemClass = item.getClass();
		if (itemClass == NodeIpAddress.class) {
			val nip = (NodeIpAddress) item;
			model.put("aggregateRotationStatus", nip.getAggregateRotationStatus());
		}
	}
}
