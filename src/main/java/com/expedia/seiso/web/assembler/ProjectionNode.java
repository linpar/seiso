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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lombok.NonNull;
import lombok.val;

/**
 * @author Willie Wheeler
 */
public class ProjectionNode {
	public static final ProjectionNode FLAT_PROJECTION_NODE = new ProjectionNode("flat");

	private String name;
	private final Map<String, ProjectionNode> children = new TreeMap<>();

	public ProjectionNode(@NonNull String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public ProjectionNode add(@NonNull ProjectionNode child) {
		children.put(child.getName(), child);
		return this;
	}

	public List<ProjectionNode> getChildren() {
		return new ArrayList<>(children.values());
	}

	public ProjectionNode getChild(String name) {
		return children.get(name);
	}

	@Override
	public String toString() {
		val builder = new StringBuilder();
		buildString(builder, 0);
		return builder.toString();
	}

	private void buildString(StringBuilder builder, int level) {
		for (int i = 0; i < level; i++) {
			builder.append("  ");
		}
		builder.append(name);
		builder.append("\n");
		val children = getChildren();
		for (val child : children) {
			child.buildString(builder, level + 1);
		}
	}
}
