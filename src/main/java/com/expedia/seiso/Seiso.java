/* 
 * Copyright 2013-2016 the original author or authors.
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
package com.expedia.seiso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author Willie Wheeler
 */
@SpringBootApplication
@EnableConfigurationProperties
public class Seiso {
	
	@Autowired
	private DataSourceProperties dataSourceProps;
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Seiso.class, args);
	}
	
	@Bean
	public HikariDataSource dataSource() {
		
		// The HikariCP docs note that there's a slight performance benefit to configuring the HikariDataSource via the
		// HikariConfig as opposed to configuring the HikariDataSource directly.
		HikariConfig config = new HikariConfig();
		config.setDriverClassName(dataSourceProps.getDriverClassName());
		config.setJdbcUrl(dataSourceProps.getUrl());
		config.setUsername(dataSourceProps.getUsername());
		config.setPassword(dataSourceProps.getPassword());
		config.setMaximumPoolSize(dataSourceProps.getMaximumPoolSize());
		config.setMinimumIdle(dataSourceProps.getMinimumIdle());
		
		return new HikariDataSource(config);
	}
	
	// TODO Upgrade once Spring Data REST compiles against Spring 4.2. See
	// - http://stackoverflow.com/questions/31724994/spring-data-rest-and-cors
	// - https://jira.spring.io/browse/DATAREST-573
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("PATCH");
        config.addAllowedMethod("DELETE");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
