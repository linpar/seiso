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
package com.expedia.seiso.web.converter;

import lombok.NonNull;
import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import com.expedia.rf.exception.ResourceNotFoundException;
import com.expedia.seiso.domain.entity.key.IpAddressRoleKey;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.NodeIpAddressKey;
import com.expedia.seiso.domain.entity.key.ServiceInstancePortKey;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.RepoKeys;

/**
 * @author Willie Wheeler
 */
@Component
public class UriToItemKeyConverter implements Converter<String, ItemKey> {
	@Autowired private ItemMetaLookup itemMetaLookup;
	
	// Don't use UriTemplate here, because it does a greedy match against the URI, which isn't what we want.
	// (E.g., the simple URI template matches /service-instances/foo/ports/8080, which isn't the intent.)
//	private UriTemplate simpleUriTemplate;
//	private UriTemplate compoundUriTemplate;
	
	// AntPatchMatcher recognizes / as a path separator, so we use it instead.
	private AntPathMatcher matcher = new AntPathMatcher();
	
	private String simpleUriTemplate;
	private String compoundUriTemplate;
	
	public UriToItemKeyConverter(@NonNull String versionUri) {
		this.simpleUriTemplate = versionUri + "/{repoKey}/{itemKey}";
		this.compoundUriTemplate = versionUri + "/{repoKey}/{itemKey}/{collKey}/{elemKey}";
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public ItemKey convert(String uri) {
		if (uri == null) {
			return null;
		} else if (matcher.match(simpleUriTemplate, uri)) {
			return convertSimple(uri);
		} else if (matcher.match(compoundUriTemplate, uri)) {
			return convertCompound(uri);
		} else {
			throw new ResourceNotFoundException("No such resource: " + uri);
		}
	}
	
	private ItemKey convertSimple(String uri) {
		val vars = matcher.extractUriTemplateVariables(simpleUriTemplate, uri);
		val repoKey = vars.get("repoKey");
		val itemKey = vars.get("itemKey");
		val itemClass = itemMetaLookup.getItemClass(repoKey);
		return new SimpleItemKey(itemClass, itemKey);
	}
	
	private ItemKey convertCompound(String uri) {
		val vars = matcher.extractUriTemplateVariables(compoundUriTemplate, uri);
		val repoKey = vars.get("repoKey");
		val itemKey = vars.get("itemKey");
		val collKey = vars.get("collKey");
		val elemKey = vars.get("elemKey");
		if (RepoKeys.NODES.equals(repoKey) && "ip-addresses".equals(collKey)) {
			return new NodeIpAddressKey(itemKey, elemKey);
		} else if (RepoKeys.SERVICE_INSTANCES.equals(repoKey) && "ip-address-roles".equals(collKey)) {
			return new IpAddressRoleKey(itemKey, elemKey);
		} else if (RepoKeys.SERVICE_INSTANCES.equals(repoKey) && "ports".equals(collKey)) {
			return new ServiceInstancePortKey(itemKey, Integer.parseInt(elemKey));
		} else {
			throw new ResourceNotFoundException("No such resource: " + uri);
		}
	}
}
