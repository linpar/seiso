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
package com.expedia.serf.service;

/**
 * @author Willie Wheeler
 */
public abstract class AbstractPersistenceInterceptor implements PersistenceInterceptor {

	/* (non-Javadoc)
	 * @see com.expedia.serf.service.PersistenceInterceptor#preCreate(java.lang.Object)
	 */
	@Override
	public void preCreate(Object entity) { }

	/* (non-Javadoc)
	 * @see com.expedia.serf.service.PersistenceInterceptor#postCreate(java.lang.Object)
	 */
	@Override
	public void postCreate(Object entity) { }

	/* (non-Javadoc)
	 * @see com.expedia.serf.service.PersistenceInterceptor#preUpdate(java.lang.Object)
	 */
	@Override
	public void preUpdate(Object entity) { }

	/* (non-Javadoc)
	 * @see com.expedia.serf.service.PersistenceInterceptor#postUpdate(java.lang.Object)
	 */
	@Override
	public void postUpdate(Object entity) { }

	/* (non-Javadoc)
	 * @see com.expedia.serf.service.PersistenceInterceptor#preDelete(java.lang.Object)
	 */
	@Override
	public void preDelete(Object entity) { }

	/* (non-Javadoc)
	 * @see com.expedia.serf.service.PersistenceInterceptor#postDelete(java.lang.Object)
	 */
	@Override
	public void postDelete(Object entity) { }
}
