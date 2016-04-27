package com.expedia.seiso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.expedia.seiso.web.eventhandler.RestCallListener;

@Configuration 
@EnableWebMvc  
public class InterceptorInjector extends WebMvcConfigurerAdapter {
	
	@Autowired
	private RestCallListener listener;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	    registry.addInterceptor(listener);
	}
	
}
