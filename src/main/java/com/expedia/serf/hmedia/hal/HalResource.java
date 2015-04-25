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
package com.expedia.serf.hmedia.hal;

import java.util.Map;

import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Willie Wheeler
 */
public class HalResource {
	@Setter private Map<String, Object> links;
	@Setter private Map<String, Object> embedded;
	@Setter private Map<String, Object> state;
	
	@JsonProperty("_links")
	@JsonInclude(Include.NON_NULL)
	public Map<String, Object> getLinks() { return links; }
	
	@JsonProperty("_embedded")
	@JsonInclude(Include.NON_NULL)
	public Map<String, Object> getEmbedded() { return embedded; }
	
	@JsonAnyGetter
	public Map<String, Object> getState() { return state; }
}
