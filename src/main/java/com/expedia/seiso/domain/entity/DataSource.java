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
package com.expedia.seiso.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a source of Seiso item data. In general we create sync processes to copy data from the data source into
 * Seiso, sometimes as a batch process and sometimes in real time. Examples include:
 * 
 * <ul>
 * <li>Active Directory</li>
 * <li>Chef server</li>
 * <li>Load balancers</li>
 * <li>Git repositories</li>
 * <li>Seyren servers</li>
 * </ul>
 * 
 * and so forth.
 * 
 * @author Willie Wheeler
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataSource {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String key;
	private String baseUri;
}
