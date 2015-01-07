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

import com.expedia.seiso.core.ann.FindByKey;
import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.core.ann.RestResource;
import com.expedia.seiso.core.exception.NotFoundException;
import com.expedia.seiso.web.assembler.ProjectionNode;
import com.expedia.seiso.web.assembler.ProjectionParser;

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
			val key = new Key(projectionAnn.cardinality(), projectionAnn.name());
			val projection = new ProjectionParser(projectionAnn.paths()).parse();
			log.debug("Adding projection: key={}, projection={}", key, projection);
			projections.put(key, projection);
		}
	}

	private void initPropertyNames() {
		// Annotations are on the fields, not the getters. [WLW]
		val fields = itemClass.getDeclaredFields();
		for (val field : fields) {
			val ann = field.getAnnotation(RestResource.class);
			if (ann != null) {
				propNamesByPropKey.put(ann.path(), field.getName());
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
	public ProjectionNode getProjectionNode(Projection.Cardinality cardinality, String projectionKey) {
		Key key = new Key(cardinality, projectionKey);
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
	public boolean isPagingRepo() {
		return pagingRepo;
	}

	@Data
	private static class Key {
		private Projection.Cardinality cardinality;
		private String viewKey;

		public Key(Projection.Cardinality cardinality, String viewKey) {
			this.cardinality = cardinality;
			this.viewKey = viewKey;
		}
	}
}
