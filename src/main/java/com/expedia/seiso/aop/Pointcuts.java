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
package com.expedia.seiso.aop;

/**
 * @author Willie Wheeler
 */
public class Pointcuts {
	
	public static final String CREATE_ITEM_POINTCUT =
			"execution(* com.expedia.seiso.domain.service.impl.ItemSaver" +
			".create(com.expedia.seiso.domain.entity.Item, boolean))";
	
	public static final String UPDATE_ITEM_POINTCUT =
			"execution(* com.expedia.seiso.domain.service.impl.ItemSaver" +
			".update(com.expedia.seiso.domain.entity.Item, com.expedia.seiso.domain.entity.Item, boolean))";
	
	public static final String DELETE_ITEM_POINTCUT =
			"execution(* com.expedia.seiso.domain.service.impl.ItemDeleter" +
			".delete(com.expedia.seiso.domain.entity.Item))";
}
