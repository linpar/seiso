package com.expedia.seiso.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationErrorMap {
	public Map<String, List<String>> errorMap;

	public ValidationErrorMap() {
		this.errorMap = new HashMap<String, List<String>>();
	}

	public void addError(String name, String message) {
		List<String> errorsForName = this.errorMap.get(name);
		if (errorsForName == null) {
			errorsForName = new ArrayList<String>();
			this.errorMap.put(name, errorsForName);
		}

		errorsForName.add(message);
	}
}