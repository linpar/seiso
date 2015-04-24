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
package com.expedia.rf.hmedia;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * <p>
 * Base resource data transfer object. Downstream processors serialize this into acceptable formats (e.g., HAL).
 * </p>
 * <p>
 * We separate associations from properties to make it easier to do the same in representations that require it, such
 * as HAL. This is especially useful in cases involving <code>null</code> values. See
 * <a href="https://github.com/ExpediaDotCom/seiso/issues/31">https://github.com/ExpediaDotCom/seiso/issues/31</a> for
 * more information.
 * </p>
 * 
 * @author Willie Wheeler
 */
@Data
@NoArgsConstructor
public class Resource {
	private final List<Link> links = new ArrayList<>();
	private Map<String, Object> properties = new TreeMap<>();
	private Map<String, Object> associations = new TreeMap<>();
	
	public void addLink(@NonNull Link link) { links.add(link); }
	
	public void setProperty(String name, Object value) { properties.put(name, value); }
	
	public void setAssociation(String name, Object value) { associations.put(name, value); }
}
