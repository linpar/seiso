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
package com.expedia.seiso.web.jackson.hal;

import java.io.IOException;

import lombok.NonNull;

import org.springframework.stereotype.Component;

import com.expedia.seiso.web.hateoas.BaseResourcePage;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * @author Willie Wheeler
 */
@Component
public class HalResourcePageSerializer extends StdSerializer<BaseResourcePage> {
	private HalResourceAssembler assembler;
	
	public HalResourcePageSerializer(@NonNull HalResourceAssembler assembler) {
		super(BaseResourcePage.class, false);
		this.assembler = assembler;
	}

	@Override
	public void serialize(@NonNull BaseResourcePage baseResourcePage, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonGenerationException {
		
		jgen.writeObject(assembler.toHalResourcePage(baseResourcePage));
	}
}
