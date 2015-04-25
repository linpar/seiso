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
package com.expedia.seiso.domain.meta;

import static org.springframework.util.Assert.notNull;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.core.annotation.AnnotationUtils;

import com.expedia.seiso.NotFoundException;
import com.expedia.seiso.core.ann.FindByKey;
import com.expedia.seiso.core.ann.Parent;
import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.domain.entity.Dashboard;
import com.expedia.seiso.domain.entity.DocLink;
import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.entity.SeyrenCheck;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.assembler.ProjectionNode;
import com.expedia.seiso.web.assembler.ProjectionParser;
import com.expedia.serf.ann.RestResource;

/**
 * @author Willie Wheeler
 */
@XSlf4j
public class ItemMetaImpl implements ItemMeta {
	private final Class<?> itemClass;
	private final Class<?> itemRepoInterface;
	private boolean pagingRepo;
	private boolean exported;
	private String rel;
	private String repoKey;
	private Method findByKeyMethod;
	private final Map<Key, ProjectionNode> projections = new HashMap<>();
	private final Map<String, String> propNamesByPropKey = new HashMap<>();
	private String parentPropertyName;

	public ItemMetaImpl(Class<?> itemClass, Class<?> repoInterface, boolean pagingRepo) {
		log.trace("Initializing resource mapping for {}", itemClass);

		this.itemClass = itemClass;
		this.itemRepoInterface = repoInterface;
		this.pagingRepo = pagingRepo;

		val ann = AnnotationUtils.findAnnotation(repoInterface, RestResource.class);
		if (ann != null) {
			this.exported = ann.exported();
			this.repoKey = ann.path();

			// Not sure this is how we want this to work, but LegacyItemLinks.linkToSingleResource() doesn't like null rels.
			boolean emptyRel = ("".equals(ann.rel()));
			this.rel = (emptyRel ? ann.path() : ann.rel());

			// TODO Take steps to ensure that neither repoKey nor rel is null.
		}

		// Initialize the findByKey method no matter whether we're exporting the repo or not. For instance, we don't
		// want to export the UserRepo, but we do want to be able to serialize and deserialize createdBy/updatedBy
		// users using the generic machinery. [WLW]
		initFindByKeyMethod();

		initProjections();
		initPropertyNames();
	}

	private void initFindByKeyMethod() {
		val methods = itemRepoInterface.getMethods();
		for (val method : methods) {
			val findByKeyAnn = AnnotationUtils.findAnnotation(method, FindByKey.class);
			if (findByKeyAnn != null) {
				this.findByKeyMethod = method;
				return;
			}
		}
	}

	private void initProjections() {
		val projectionsAnn = AnnotationUtils.findAnnotation(itemClass, Projections.class);

		if (projectionsAnn == null) {
			return;
		}

		val projectionsAnnArr = projectionsAnn.value();
		for (val projectionAnn : projectionsAnnArr) {
			val apiVersions = projectionAnn.apiVersions();
			for (val apiVersion : apiVersions) {
				val key = new Key(apiVersion, projectionAnn.cardinality(), projectionAnn.name());
				val projection = new ProjectionParser(projectionAnn.paths()).parse();
				log.debug("Adding projection: key={}, projection={}", key, projection);
				projections.put(key, projection);
			}
		}
	}

	private void initPropertyNames() {
		
		// Annotations are on the fields, not the getters. [WLW]
		val fields = itemClass.getDeclaredFields();
		for (val field : fields) {
			val fieldName = field.getName();
			
			val parentAnn = field.getAnnotation(Parent.class);
			if (parentAnn != null) {
				this.parentPropertyName = fieldName;
			}
			
			val restResourceAnn = field.getAnnotation(RestResource.class);
			if (restResourceAnn != null) {
				propNamesByPropKey.put(restResourceAnn.path(), fieldName);
			}
		}
	}
	
	@Override
	public Class<?> getRepositoryInterface() { return itemRepoInterface; }

	@Override
	public boolean isExported() { return exported; }

	@Override
	public String getRel() { return rel; }

	@Override
	public String getRepoKey() { return repoKey; }

	@Override
	public Method getRepositoryFindByKeyMethod() { return findByKeyMethod; }
	
	@Override
	public Method getRepositorySearchMethod(String search) {
		val methods = itemRepoInterface.getMethods();
		for (val method : methods) {
			val ann = AnnotationUtils.getAnnotation(method, RestResource.class);
			if (ann != null && search.equals(ann.path())) {
				return method;
			}
		}
		return null;
	}

	@Override
	public ProjectionNode getProjectionNode(
			ApiVersion apiVersion,
			Projection.Cardinality cardinality,
			String projectionKey) {
		
		// TODO add one key per api version
		
		Key key = new Key(apiVersion, cardinality, projectionKey);
		ProjectionNode node = projections.get(key);
		notNull(node, "No projection for key=" + key);
		return node;
	}

	@Override
	public String getPropertyName(@NonNull String propertyKey) {
		val propName = propNamesByPropKey.get(propertyKey);
		if (propName == null) {
			throw new NotFoundException("No property with key " + propertyKey);
		}
		return propName;
	}
	
	@Override
	public Class<?> getCollectionPropertyElementType(@NonNull String propName) {
		
		// FIXME For now I'm just hardcoding a few special cases we need right now. In the future we'll generalize this
		// to handle other cases. Need to figure out exactly how we'll extract type information given type erasure.
		// Might have to do explicit annotations, but hope not. [WLW]
		if (itemClass == Service.class) {
			if ("docLinks".equals(propName)) {
				return DocLink.class;
			}
		} else if (itemClass == ServiceInstance.class) {
			if ("dashboards".equals(propName)) {
				return Dashboard.class;
			} else if ("seyrenChecks".equals(propName)) {
				return SeyrenCheck.class;
			}
		}
		
		val msg = "Collection property " + propName + " not yet supported.";
		throw new UnsupportedOperationException(msg);
	}
	
	@Override
	public boolean isPagingRepo() {
		return pagingRepo;
	}
	
	@Override
	public String getParentPropertyName() {
		return parentPropertyName;
	}

	@Data
	private static class Key {
		private ApiVersion apiVersion;
		private Projection.Cardinality cardinality;
		private String viewKey;

		public Key(ApiVersion apiVersion, Projection.Cardinality cardinality, String viewKey) {
			this.apiVersion = apiVersion;
			this.cardinality = cardinality;
			this.viewKey = viewKey;
		}
	}
}
