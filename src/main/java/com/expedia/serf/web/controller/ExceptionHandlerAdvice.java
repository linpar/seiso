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
package com.expedia.serf.web.controller;

import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

import com.expedia.serf.C;
import com.expedia.serf.exception.NotFoundException;
import com.expedia.serf.exception.ResourceNotFoundException;
import com.expedia.serf.exception.SaveAllException;
import com.expedia.serf.exception.ValidationException;
import com.expedia.serf.util.ErrorObject;
import com.expedia.serf.util.ResourceValidationError;
import com.expedia.serf.util.SaveAllResult;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Willie Wheeler
 */
@ControllerAdvice
@XSlf4j
public class ExceptionHandlerAdvice {

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ErrorObject handleNotFoundException(NotFoundException e, WebRequest request) {
		return new ErrorObject(C.EC_RESOURCE_NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ErrorObject handleResourceNotFoundException(ResourceNotFoundException e, WebRequest request) {
		return new ErrorObject(C.EC_RESOURCE_NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorObject handleHttpMessageNotReadableException(HttpMessageNotReadableException e, WebRequest request) {
		return new ErrorObject(C.EC_REQUEST_BODY_REQUIRED, "This request requires a request body.");
	}
	
	// FIXME Oh, this doesn't fire, because it's the deserializer that throws the exception, not the controller.
	@ExceptionHandler(JsonMappingException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorObject handleJsonMappingException(JsonMappingException e, WebRequest request) {
		return new ErrorObject(C.EC_INVALID_REQUEST_BODY, e.getMessage());
	}
	
	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(value = HttpStatus.CONFLICT)
	@ResponseBody
	public ResourceValidationError handleValidationException(ValidationException e, WebRequest request) {
		// TODO Harmonize the response body with that of other errors
		return e.getErrors();
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(value = HttpStatus.CONFLICT)
	@ResponseBody
	public ErrorObject handleDataIntegrityViolationException(DataIntegrityViolationException e, WebRequest request) {
		val message = "Database constraint violation. " +
				"This could be a missing required field, a duplicate value for a unique field, " +
				"a bad foreign key, etc.";		
		return new ErrorObject(C.EC_VALIDATION_ERROR, message);
	}
	
	@ExceptionHandler(SaveAllException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public SaveAllResult handleSaveAllException(SaveAllException e, WebRequest request) {
		// TODO Harmonize the response body with that of other errors
		return e.getSaveAllResult();
	}
	
	@ExceptionHandler(HttpClientErrorException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorObject handleHttpClientErrorException(HttpClientErrorException e, WebRequest request) {
		val message = "Seiso called a web service, but the result was a client error.";
		return new ErrorObject(C.EC_HTTP_CLIENT_ERROR, message);
	}
	
	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorObject handleRuntimeException(RuntimeException e, WebRequest request) {
		log.error("Internal server error", e);
		val fullMsg = e.getClass().getName() + ": " + e.getMessage();
		return new ErrorObject(C.EC_INTERNAL_ERROR, fullMsg);
	}

//	@ExceptionHandler(BindException.class)
//	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
//	@ResponseBody
//	public ResourceValidationError handleBindException(BindException bindException) {
//		return ResourceValidationErrorFactory.buildFrom(bindException);
//	}
}
