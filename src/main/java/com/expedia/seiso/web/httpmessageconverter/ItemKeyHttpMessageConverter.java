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
package com.expedia.seiso.web.httpmessageconverter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.expedia.rf.web.MediaTypes;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.web.converter.UriToItemKeyConverter;

/**
 * @author Willie Wheeler
 */
@Component
public class ItemKeyHttpMessageConverter extends AbstractHttpMessageConverter<ItemKey> {
	@Autowired private UriToItemKeyConverter uriToItemKeyConverter;
	
	public ItemKeyHttpMessageConverter() {
		super(MediaTypes.TEXT_URI_LIST);
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.http.converter.AbstractHttpMessageConverter#supports(java.lang.Class)
	 */
	@Override
	protected boolean supports(Class<?> clazz) {
		return ItemKey.class.isAssignableFrom(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.converter.AbstractHttpMessageConverter#readInternal(java.lang.Class, org.springframework.http.HttpInputMessage)
	 */
	@Override
	protected ItemKey readInternal(Class<? extends ItemKey> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		
		val uri = StreamUtils.copyToString(inputMessage.getBody(), StandardCharsets.UTF_8);
		return uriToItemKeyConverter.convert(uri);
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.converter.AbstractHttpMessageConverter#writeInternal(java.lang.Object, org.springframework.http.HttpOutputMessage)
	 */
	@Override
	protected void writeInternal(ItemKey t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		
		// TODO Auto-generated method stub
		
	}
}
