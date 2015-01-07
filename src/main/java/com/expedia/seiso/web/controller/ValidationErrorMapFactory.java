package com.expedia.seiso.web.controller;

import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;

public class ValidationErrorMapFactory {
	public static ValidationErrorMap buildFrom(BindException bindException) {
		ValidationErrorMap validationErrorMap = new ValidationErrorMap();

		if (bindException != null) {
			for (ObjectError objectError : bindException.getAllErrors()) {
				validationErrorMap.addError(objectError.getObjectName(), objectError.getDefaultMessage());
			}
		}

		return validationErrorMap;
	}
}
