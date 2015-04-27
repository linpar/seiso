package com.expedia.serf.hypermedia.hal;

import lombok.NonNull;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Component
@SuppressWarnings("serial")
public class HalMapper extends ObjectMapper {
	
	public HalMapper(@NonNull HalModule halModule) {
		enable(SerializationFeature.INDENT_OUTPUT);
		registerModule(halModule);
	}
}
