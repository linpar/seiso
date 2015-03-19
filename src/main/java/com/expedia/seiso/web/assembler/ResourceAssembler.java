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
import lombok.RequiredArgsConstructor;
import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.mapping.model.BeanWrapper;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projection.Cardinality;
import com.expedia.seiso.core.ann.RestResource;
import com.expedia.seiso.domain.entity.Dashboard;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.Person;
import com.expedia.seiso.domain.entity.RotationStatus;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.entity.SeyrenCheck;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.service.SearchResults;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.Relations;
import com.expedia.seiso.web.hateoas.Link;
import com.expedia.seiso.web.hateoas.PageMetadata;
import com.expedia.seiso.web.hateoas.PagedResources;
import com.expedia.seiso.web.hateoas.Resource;
import com.expedia.seiso.web.hateoas.Resources;
import com.expedia.seiso.web.hateoas.link.ItemLinks;
import com.expedia.seiso.web.hateoas.link.LinkFactory;
import com.expedia.seiso.web.hateoas.link.RepoSearchLinks;

/**
 * Assembles items into resources, which include links (in support of the REST HATEOAS principle). Subsequent
 * processing (outside this class) serializes the resource into special representation formats, such as HAL.
 * 
 * @author Willie Wheeler
 */
@Component
@RequiredArgsConstructor
public class ResourceAssembler {
	private static final MultiValueMap<String, String> EMPTY_PARAMS = new LinkedMultiValueMap<String, String>();
	
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private Repositories repositories;
	@Autowired @Qualifier("linkFactoryV1") private LinkFactory linkFactoryV1;
	@Autowired @Qualifier("linkFactoryV2") private LinkFactory linkFactoryV2;
	
	
	// =================================================================================================================
	// CRUD repo resources
	// =================================================================================================================
	
	/**
	 * @param apiVersion
	 * @param itemClass
	 * @param itemList
	 * @param proj
	 * @return
	 */
	public Resources toResources(
			@NonNull ApiVersion apiVersion,
			@NonNull Class<?> itemClass,
			List<?> itemList,
			ProjectionNode proj) {
		
		if (itemList == null) { return null; }
		
		val links = toRepoListLinks(apiVersion, itemClass, itemList);
		
		val resourceList = new ArrayList<Resource>();
		for (val item : itemList) {
			// Don't pass params from the collection view to the single view.
			resourceList.add(toResource(apiVersion, (Item) item, proj, false));
		}
		
		return new Resources(links, resourceList);
	}
	
	
	// =================================================================================================================
	// Paging repo resources
	// =================================================================================================================
	
	/**
	 * @param apiVersion
	 * @param itemClass
	 * @param itemPage
	 * @param proj
	 * @return
	 */
	public PagedResources toPagedResources(
			@NonNull ApiVersion apiVersion,
			@NonNull Class<?> itemClass,
			Page<?> itemPage,
			ProjectionNode proj) {
		
		return toPagedResources(apiVersion, itemClass, itemPage, proj, EMPTY_PARAMS);
	}
	
	/**
	 * @param apiVersion
	 * @param itemClass
	 * @param itemPage
	 * @param proj
	 * @param params
	 * @return
	 */
	public PagedResources toPagedResources(
			@NonNull ApiVersion apiVersion,
			@NonNull Class<?> itemClass,
			Page<?> itemPage,
			ProjectionNode proj,
			MultiValueMap<String, String> params) {
		
		if (itemPage == null) { return null; }
		
		val links = toRepoPageLinks(apiVersion, itemClass, itemPage, params);
		val pageMeta = toPageMetadata(itemPage);
		val items = toResourceList(apiVersion, itemPage.getContent(), proj);
		return new PagedResources(links, pageMeta, items);
	}
	
	
	// =================================================================================================================
	// Bare resources
	// =================================================================================================================
	
	/**
	 * @param apiVersion
	 * @param itemList
	 * @param proj
	 * @return
	 */
	public List<Resource> toResourceList(@NonNull ApiVersion apiVersion, List<?> itemList, ProjectionNode proj) {
		if (itemList == null) { return null; }
		val resourceList = new ArrayList<Resource>();
		for (val item : itemList) {
			// Don't pass params from the collection view to the single view.
			resourceList.add(toResource(apiVersion, (Item) item, proj, false));
		}
		return resourceList;
	}
	
	/**
	 * @param apiVersion
	 * @param item
	 * @param proj
	 * @return
	 */
	public Resource toResource(@NonNull ApiVersion apiVersion, Item item, ProjectionNode proj) {
		return toResource(apiVersion, item, proj, false);
	}
	
	/**
	 * @param apiVersion
	 * @param item
	 * @param proj
	 * @param topLevel
	 *            Top-level resource? (Only top-level resources have links beyond the self-link.)
	 * @return
	 */
	public Resource toResource(
			@NonNull ApiVersion apiVersion,
			Item item,
			ProjectionNode proj,
			boolean topLevel) {
		
		if (item == null) { return null; }
		
		val itemClass = item.getClass();
		val resource = new Resource();
		val pEntity = repositories.getPersistentEntity(itemClass);
		
		if (pEntity == null) {
			val msg = "No PersistentEntity for itemClass=" + itemClass.getName() +
					". Is there an associated repository interface?";
			throw new RuntimeException(msg);
		}
		
		val itemWrapper = BeanWrapper.create(item, null);
		
		// All resources get a self link, whether top-level or not.
		resource.addLink(itemLinks(apiVersion).itemLink(item));
		
		// Only top-level resources get non-self links.
		if (topLevel) {
			resource.addLink(itemLinks(apiVersion).repoLink(Relations.UP, itemClass));
			addSpecialLinks(apiVersion, item, resource);
		}
		
		val propHandler = new ItemPropertyHandler(itemWrapper, resource.getProperties());
		val assocHandler =
				new ItemAssociationHandler(this, itemLinks(apiVersion), apiVersion, proj, itemWrapper, resource, topLevel);
		
		pEntity.doWithProperties(propHandler);
		pEntity.doWithAssociations(assocHandler);
		doSpecialNonPersistentAssociations(apiVersion, item, resource.getAssociations());
		
		return resource;
	}
	
	
	// =================================================================================================================
	// Repo search resources
	// =================================================================================================================
	
	/**
	 * @param apiVersion
	 *            API version
	 * @param repoKey
	 * @return
	 */
	public Resource toRepoSearchList(@NonNull ApiVersion apiVersion, @NonNull String repoKey) {
		val itemClass = itemMetaLookup.getItemClass(repoKey);
		val repoInfo = repositories.getRepositoryInformationFor(itemClass);
		val queryMethods = repoInfo.getQueryMethods();
		
		val resource = new Resource();
		resource.addLink(repoSearchLinks(apiVersion).repoSearchListLink(Relations.SELF, itemClass));
		resource.addLink(itemLinks(apiVersion).repoLink(Relations.UP, itemClass));
		
		// Query method links
		for (val queryMethod : queryMethods) {
			val restResource = AnnotationUtils.getAnnotation(queryMethod, RestResource.class);
			if (restResource == null) { continue; }
			
			val path = restResource.path();
			if (path.isEmpty()) { continue; }
			
			val requestParams = new LinkedMultiValueMap<String, String>();
			val methodParams = queryMethod.getParameters();
			for (val methodParam : methodParams) {
				val paramAnn = methodParam.getAnnotation(Param.class);
				if (paramAnn != null) {
					val requestParamName = paramAnn.value();
					// FIXME Formatting the URI template variables belongs with the xxxLinks objects, not here. [WLW]
					requestParams.set(requestParamName, "{" + requestParamName + "}"); 
				}
			}
			
			val rel = "s:" + (restResource.rel().isEmpty() ? path : restResource.rel());
			val link = repoSearchLinks(apiVersion).toRepoSearchLinkTemplate(rel, itemClass, path, requestParams);
			resource.addLink(link);
		}
		
		return resource;
	}
	
	/**
	 * Assembles a resource page from a repo search result page.
	 * 
	 * @param apiVersion
	 *            API version
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
	public PagedResources toRepoSearchPagedResources(
			@NonNull ApiVersion apiVersion,
			@NonNull Page resultPage,
			@NonNull Class itemClass,
			@NonNull String path,
			@NonNull MultiValueMap<String, String> params,
			@NonNull ProjectionNode proj) {
		
		val links = toRepoSearchLinks(apiVersion, resultPage, itemClass, path, params);
		val pageMeta = toPageMetadata(resultPage);
		val itemList = resultPage.getContent();
		val itemResourceList = toResourceList(apiVersion, itemList, proj);
		return new PagedResources(links, pageMeta, itemResourceList);
	}
	
	
	// =================================================================================================================
	// Global search resources
	// =================================================================================================================
	
	/**
	 * Assembles a resource from a global search result set.
	 * 
	 * @param apiVersion
	 *            API version
	 * @param results
	 *            Global search result set
	 * @return Resource for the result set
	 */
	public Resource toGlobalSearchResource(@NonNull ApiVersion apiVersion, @NonNull SearchResults results) {
		val resultsResource = new Resource();
		val itemClasses = results.getItemClasses();
		for (val itemClass : itemClasses) {
			val propName = StringUtils.uncapitalize(itemClass.getSimpleName());
			val typedSerp = results.getTypedSerp(itemClass);
			
			// TODO Not sure we want flat projection here. Shouldn't we do the default collection projection?
			val typedSerpResourceList =
					toResourceList(apiVersion, typedSerp.getContent(), ProjectionNode.FLAT_PROJECTION_NODE);
			
			resultsResource.setProperty(propName, typedSerpResourceList);
		}
		return resultsResource;
	}
	
	
	// =================================================================================================================
	// Special resources
	// =================================================================================================================
	
	// TODO Generalize
	@Deprecated
	public PagedResources toUsernamePage(
			ApiVersion apiVersion,
			Page<Person> personPage,
			MultiValueMap<String, String> params) {
		
		if (personPage == null) { return null; }
		val links = toRepoPageLinks(apiVersion, Person.class, personPage, params);
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
	
	private ItemLinks itemLinks(ApiVersion apiVersion) {
		switch (apiVersion) {
		case V1:
			return linkFactoryV1.getItemLinks();
		case V2:
			return linkFactoryV2.getItemLinks();
		default:
			throw new IllegalArgumentException("Illegal API version: " + apiVersion);
		}
	}
	
	private RepoSearchLinks repoSearchLinks(ApiVersion apiVersion) {
		switch (apiVersion) {
		case V1:
			return linkFactoryV1.getRepoSearchLinks();
		case V2:
			return linkFactoryV2.getRepoSearchLinks();
		default:
			throw new IllegalArgumentException("Illegal API version: " + apiVersion);
		}
	}
	
	private List<Link> toRepoListLinks(ApiVersion apiVersion, Class<?> itemClass, List<?> itemList) {
		val links = new ArrayList<Link>();
		links.add(itemLinks(apiVersion).repoLink(itemClass, EMPTY_PARAMS));
		links.add(repoSearchLinks(apiVersion).repoSearchListLink(Relations.S_SEARCH, itemClass));
		return links;
	}
	
	private List<Link> toRepoPageLinks(
			ApiVersion apiVersion,
			Class<?> itemClass,
			Page<?> itemPage,
			MultiValueMap<String, String> params) {
		
		// 0-indexed
		val pageNumber = itemPage.getNumber();
		val totalPages = itemPage.getTotalPages();
		val firstPageNumber = 0;
		val lastPageNumber = totalPages - 1;
		
		val links = new ArrayList<Link>();
		links.add(itemLinks(apiVersion).repoLink(itemClass, params));
		
		// Pagination links
		if (totalPages > 0) {
			links.add(itemLinks(apiVersion).repoFirstLink(itemClass, itemPage, params));
		}
		if (pageNumber > 0 && pageNumber <= lastPageNumber) {
			links.add(itemLinks(apiVersion).repoPrevLink(itemClass, itemPage, params));
		}
		if (pageNumber >= firstPageNumber && pageNumber < lastPageNumber) {
			links.add(itemLinks(apiVersion).repoNextLink(itemClass, itemPage, params));
		}
		if (totalPages > 0) {
			links.add(itemLinks(apiVersion).repoLastLink(itemClass, itemPage, params));
		}
		
		links.add(repoSearchLinks(apiVersion).repoSearchListLink(Relations.S_SEARCH, itemClass));
		return links;
	}
	
	private List<Link> toRepoSearchLinks(
			ApiVersion apiVersion,
			Page resultPage,
			Class itemClass,
			String path,
			MultiValueMap<String, String> params) {
		
		val linkBuilder = repoSearchLinks(apiVersion).toPaginationLinkBuilder(resultPage, itemClass, path, params);
		val firstLink = linkBuilder.buildFirstLink();
		val prevLink = linkBuilder.buildPreviousLink();
		val nextLink = linkBuilder.buildNextLink();
		val lastLink = linkBuilder.buildLastLink();
		
		val links = new LinkedList<Link>();
		links.add(linkBuilder.buildSelfLink());
		links.add(repoSearchLinks(apiVersion).repoSearchListLink(Relations.UP, itemClass));
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
	
	private void addSpecialLinks(ApiVersion apiVersion, Item item, Resource resource) {
		val itemLinks = itemLinks(apiVersion);
		if (item instanceof Dashboard) {
			val apiLink = itemLinks.dashboardApiLink((Dashboard) item);
			if (apiLink != null) { resource.addLink(apiLink); }
			val uiLink = itemLinks.dashboardUiLink((Dashboard) item);
			if (uiLink != null) { resource.addLink(uiLink); }
		} else if (item instanceof ServiceInstance) {
			// TODO Add link to service instance node-stats
		} else if (item instanceof SeyrenCheck) {
			resource.addLink(itemLinks.seyrenCheckApiLink((SeyrenCheck) item));
			resource.addLink(itemLinks.seyrenCheckUiLink((SeyrenCheck) item));
		}
	}
	
	// FIXME This is a temporary hack to handle special-case non-persistent properties.
	// Specifically, we need to be able to map NodeIpAddress.aggregateRotationStatus. [WLW]
	@Deprecated
	private void doSpecialNonPersistentAssociations(ApiVersion apiVersion, Item item, Map<String, Object> model) {
		val itemClass = item.getClass();
		if (itemClass == NodeIpAddress.class) {
			val nip = (NodeIpAddress) item;
			val rotationStatus = nip.getAggregateRotationStatus();
			
			// Use default projection (not flat) because we need the status type to color code the labels.
			val itemMeta = itemMetaLookup.getItemMeta(RotationStatus.class);
			val proj = itemMeta.getProjectionNode(apiVersion, Cardinality.SINGLE, Projection.DEFAULT);
			val rotationStatusResource = toResource(apiVersion, rotationStatus, proj);
			
			model.put("aggregateRotationStatus", rotationStatusResource);
		}
	}
}
