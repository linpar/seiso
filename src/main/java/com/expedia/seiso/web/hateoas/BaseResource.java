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
package com.expedia.seiso.web.hateoas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author Willie Wheeler
 */
@Data
@NoArgsConstructor
public class BaseResource {
	private final List<Link> v1Links = new ArrayList<>();
	private final List<Link> v2Links = new ArrayList<>();
	private Map<String, Object> properties = new TreeMap<>();
	private Map<String, Object> associations = new TreeMap<>();
	
	public void addV1Link(@NonNull Link link) { v1Links.add(link); }
	
	public void addV2Link(@NonNull Link link) { v2Links.add(link); }
	
	public void setProperty(String name, Object value) { properties.put(name, value); }
	
	public void setAssociation(String name, Object value) { associations.put(name, value); }
}
