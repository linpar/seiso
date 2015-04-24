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

import java.util.Map;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.SimplePropertyHandler;
import org.springframework.data.mapping.model.BeanWrapper;

import com.expedia.rf.ann.RestResource;
import com.expedia.seiso.domain.entity.Item;

/**
 * Implemented as a standalone class (rather than as an anonymous inner class) to facilitate unit testing.
 * 
 * @author Willie Wheeler
 */
@RequiredArgsConstructor
public class ItemPropertyHandler implements SimplePropertyHandler {
	@NonNull private final BeanWrapper<? extends Item> wrapper;
	@NonNull private final Map<String, Object> properties;

	@Override
	public void doWithPersistentProperty(PersistentProperty<?> prop) {
		val propName = prop.getName();
		
		// Copy the ID over since the V1 schema exposes database IDs. (The V2 HAL schema doesn't, though.)
		val restResource = prop.findAnnotation(RestResource.class);
		if (restResource == null || restResource.exported()) {
			properties.put(propName, wrapper.getProperty(prop));
		}
	}
}
