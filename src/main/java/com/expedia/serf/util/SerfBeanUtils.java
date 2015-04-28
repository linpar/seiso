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
package com.expedia.serf.util;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;

/**
 * @author Willie Wheeler
 */
public class SerfBeanUtils {
	
	public static String[] determinePropertiesToIgnore(Class<?> clazz, String[] includeProps, String[] excludeProps) {
		PropertyDescriptor[] descs = BeanUtils.getPropertyDescriptors(clazz);
		
		// Default behavior: don't ignore anything.
		Set<String> propsToIgnore = new HashSet<>();
		
		// Handle explicit inclusions.
		if (includeProps != null) {
			for (PropertyDescriptor desc : descs) {
				propsToIgnore.add(desc.getName());
			}
			for (String prop : includeProps) {
				propsToIgnore.remove(prop);
			}
		}
		
		// Handle explicit exclusions.
		if (excludeProps != null) {
			for (String prop : excludeProps) {
				propsToIgnore.add(prop);
			}
		}
		
		return new ArrayList<>(propsToIgnore).toArray(new String[propsToIgnore.size()]);
	}
}
