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

import java.lang.reflect.Method;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.RestResource;
import com.expedia.seiso.web.ApiVersion;
import com.expedia.seiso.web.assembler.ProjectionNode;

// TODO Consider adding getSingleResourceRel() method here (e.g. people vs person)

/**
 * Item type metamodel.
 * 
 * @author Willie Wheeler
 */
public interface ItemMeta {

	Class<?> getRepositoryInterface();

	boolean isExported();

	String getRel();

	String getRepoKey();

	Method getRepositoryFindByKeyMethod();
	
	Method getRepositorySearchMethod(String query);
	
	ProjectionNode getProjectionNode(ApiVersion apiVersion, Projection.Cardinality cardinality, String projectionKey);
	
	/**
	 * @param propKey
	 *            as defined by {@link RestResource}
	 * @return
	 */
	String getPropertyName(String propKey);
	
	Class<?> getCollectionPropertyElementType(String propName);
	
	boolean isPagingRepo();
	
	/**
	 * Returns the name of the item's parent's property, or <code>null</code> if the item doesn't have a parent.
	 * 
	 * @return parent property name or <code>null</code>
	 */
	String getParentPropertyName();
}
