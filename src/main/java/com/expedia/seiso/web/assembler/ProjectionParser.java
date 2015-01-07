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
package com.expedia.seiso.web.assembler;

import lombok.val;

/**
 * @author Willie Wheeler
 */
public class ProjectionParser {
	private String[] paths;

	public ProjectionParser(String[] paths) {
		this.paths = paths;
	}

	public ProjectionNode parse() {
		ProjectionNode root = new ProjectionNode("/");
		for (String path : paths) {
			addPath(path.split("\\."), 0, root);
		}
		return root;
	}

	private void addPath(String[] segments, int index, ProjectionNode query) {
		val currSegment = segments[index];

		ProjectionNode subquery = query.getChild(currSegment);
		if (subquery == null) {
			subquery = new ProjectionNode(currSegment);
			query.add(subquery);
		}

		if (++index < segments.length) {
			addPath(segments, index, subquery);
		}
	}
}
