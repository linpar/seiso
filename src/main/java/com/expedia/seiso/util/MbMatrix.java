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
package com.expedia.seiso.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * @author Willie Wheeler
 */
@Component
public class MbMatrix {
	private static final String[] EMPTY = new String[0];
	
	private Map<String, String[]> allies = new HashMap<>();
	private Map<String, String[]> enemies = new HashMap<>();
	
	// http://www.truity.com/personality-type/INTP/relationships etc.
	public MbMatrix() {
		allies.put("enfj", new String[] { "esfj", "infj", "enfp" });
		enemies.put("enfj", new String[] { "istp", "istj", "isfp", "estp" });
		
		allies.put("enfp", new String[] { "entp", "entj", "infp" });
		enemies.put("enfp", new String[] { "istp", "istj", "isfj", "estj" });
		
		allies.put("entj", new String[] { "intj", "entp", "estj" });
		enemies.put("entj", new String[] { "isfp", "isfj", "infp", "esfp" });
		
		allies.put("entp", new String[] { "enfp", "intp", "entj" });
		enemies.put("entp", new String[] {"istj", "isfp", "isfj", "esfj" });
		
		allies.put("esfj", new String[] { "isfj", "esfp", "estj" });
		enemies.put("esfj", new String[] { "intp", "intj", "infp", "entp" });
		
		allies.put("esfp", new String[] { "estp", "isfp", "esfj" });
		enemies.put("esfp", new String[] { "intp", "intj", "infj", "entj" });
		
		allies.put("estj", new String[] { "istj", "entj", "estp" });
		enemies.put("estj", new String[] { "isfp", "intp", "infp", "enfp" }); 
		
		allies.put("estp", new String[] { "entp", "estj", "istp" });
		enemies.put("estp", new String[] { "isfj", "intp", "infp", "infj" });
		
		allies.put("infj", new String[] { "infp", "enfj", "isfj" });
		enemies.put("infj", new String[] { "istp", "estp", "estj", "esfp" });
		
		allies.put("infp", new String[] { "infj", "intp", "enfp" });
		enemies.put("infp", new String[] { "istj", "estp", "estj", "esfj" });
		
		allies.put("intj", new String[] { "entj", "intp", "istj" });
		enemies.put("intj", new String[] { "isfp", "isfj", "esfp", "esfj" });
		
		allies.put("intp", new String[] { "entp", "infp", "intj" });
		enemies.put("intp", new String[] { "isfj", "estj", "esfp", "esfj" });
		
		allies.put("isfj", new String[] { "infj", "esfj", "istj" });
		enemies.put("isfj", new String[] { "intp", "intj", "entp", "entj" });
		
		allies.put("isfp", new String[] { "istp", "esfp", "isfj" });
		enemies.put("isfp", new String[] { "intj", "estj", "entp", "entj" });
		
		allies.put("istj", new String[] { "estj", "isfj", "istp" });
		enemies.put("istj", new String[] { "infp", "entp", "enfp", "enfj" });
		
		allies.put("istp", new String[] { "istj", "estp", "isfp" });
		enemies.put("istp", new String[] { "infp", "infj", "enfp", "enfj" });
	}
	
	public String[] getAllies(String type) {
		String[] myAllies = allies.get(type);
		return (myAllies == null ? EMPTY : myAllies);
	}
	
	public String[] getEnemies(String type) {
		String[] myEnemies = enemies.get(type);
		return (myEnemies == null ? EMPTY : myEnemies);
	}
}
