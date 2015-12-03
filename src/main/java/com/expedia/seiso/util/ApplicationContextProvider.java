/* 
 * Copyright 2013-2016 the original author or authors.
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
package com.expedia.seiso.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Hacky approach to injecting dependencies into non-managed objects, since the standard {@code @Configurable} approach
 * doesn't work for us. (See {@linkplain https://github.com/williewheeler/spring-configurable-demo} for more info on the
 * challenges.)
 * </p>
 * <p>
 * For more information on {@code ApplicationContextProvider} see
 * {@linkplain http://mythinkpond.com/2010/03/22/spring-application-context/} and
 * {@linkplain http://springinpractice.com/2013/09/14/optimistic-locking-with-spring-data-rest}.
 * </p>
 * 
 * @author Willie Wheeler
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {
	private static ApplicationContext applicationContext;
	
	public static ApplicationContext applicationContext() {
		return applicationContext;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext appContext) throws BeansException {
		applicationContext = appContext;
	}
}
