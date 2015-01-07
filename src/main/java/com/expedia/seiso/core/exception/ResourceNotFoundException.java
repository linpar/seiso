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
package com.expedia.seiso.core.exception;

import com.expedia.seiso.domain.entity.key.ItemKey;

/**
 * Exception indicating that either the requested resource doesn't exist, or else the client doesn't have permission to
 * access it. We don't indicate which of the two is the case since revealing existence to unauthorized clients can
 * create security risks (e.g., client trying to guess protected resources).
 * 
 * @author Willie Wheeler
 */
@SuppressWarnings("serial")
public class ResourceNotFoundException extends NotFoundException {

	public ResourceNotFoundException() {
	}

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public ResourceNotFoundException(ItemKey itemKey) {
		super(itemKey.toString());
	}
}
