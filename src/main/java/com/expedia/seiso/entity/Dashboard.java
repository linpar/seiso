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
package com.expedia.seiso.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Willie Wheeler
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = "key")
@ToString(callSuper = true, of = { "key", "name" })
@Entity
public class Dashboard extends AbstractItem {
	
	@NotNull
	@Size(min = 1, max = 80)
	@Pattern(regexp = "[a-z0-9-]+")
	@Column(name = "ukey")
	private String key;
	
	@NotNull
	@Size(min = 1, max = 250)
	private String name;
	
	@Size(max = 80)
	@Pattern(regexp = "[a-z0-9-]+")
	private String type;
	
	@Size(max = 1000)
	private String description;
	
	@Size(max = 255)
	private String apiUri;
	
	@Size(max = 255)
	private String uiUri;
}
