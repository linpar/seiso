package com.expedia.seiso;

import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MessageConverterConfig extends WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // It appears some other dependency is adding Jax2bRootElementHttpMessageConverter, this
        // will override that behavior.
        List<HttpMessageConverter<?>> baseConverters = new ArrayList<HttpMessageConverter<?>>();
        super.configureMessageConverters(baseConverters);

        for(HttpMessageConverter<?> c : baseConverters){
            if(!(c instanceof Jaxb2RootElementHttpMessageConverter)){
                converters.add(c);
            }
        }
    }
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(true).
                favorParameter(false).
                ignoreAcceptHeader(false).
                defaultContentType(MediaType.APPLICATION_JSON).
                mediaType("json", MediaType.APPLICATION_JSON);
    }

}