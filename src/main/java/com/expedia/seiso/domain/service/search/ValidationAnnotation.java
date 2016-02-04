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
package com.expedia.seiso.domain.service.search;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

/**
 * @author Ken Van Eyk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
	ElementType.METHOD,
	ElementType.FIELD,
	ElementType.CONSTRUCTOR,
	ElementType.PARAMETER,
	ElementType.ANNOTATION_TYPE
})
@Constraint(validatedBy = Validator.class)
public @interface ValidationAnnotation {
	
	Class<? extends Tokenizer> tokenizer() default SpaceDelimitedDatabaseWildCardTokenizer.class;

	String queryPattern() default "^.{3,500}$";

	String tokenPattern() default "^.{5,50}$";

	String message() default "invalid search query";
}