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
package com.expedia.seiso.core.util;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import lombok.val;
import lombok.experimental.UtilityClass;

/**
 * @author Willie Wheeler
 */
@UtilityClass
public class CollectionsUtils {

	public static Class<?> getElementClass(@NonNull List<?> list) {
		return (list.isEmpty() ? null : list.get(0).getClass());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List toList(Iterable iterable) {
		val list = new ArrayList();
		for (Object element : iterable) {
			list.add(element);
		}
		return list;
	}
}
