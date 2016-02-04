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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.util.StringUtils;

/**
 * @author Ken Van Eyk
 */
public class SpaceDelimitedDatabaseWildCardTokenizer implements Tokenizer {
	private static final String TERM_DELIMITER = " ";
	private static final String WILD_CARD = "%";

	public Set<String> tokenize(String termsString) {
		HashSet<String> tokens = new LinkedHashSet<String>();

		if (!StringUtils.isEmpty(termsString)) {
			String[] terms = termsString.split(TERM_DELIMITER);
			for (String term : terms) {
				if (!StringUtils.isEmpty(term)) {
					tokens.add(new StringBuilder(WILD_CARD).append(term.trim()).append(WILD_CARD).toString());
				}
			}
		}

		return tokens;
	}
}
