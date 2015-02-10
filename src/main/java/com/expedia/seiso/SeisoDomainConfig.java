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
package com.expedia.seiso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.val;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.repository.support.Repositories;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import com.expedia.seiso.SeisoDomainConfig.RepoConfig;
import com.expedia.seiso.SeisoDomainConfig.ServiceConfig;
import com.expedia.seiso.core.config.DataSourceProperties;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.EndpointRepo;
import com.expedia.seiso.domain.repo.IpAddressRoleRepo;
import com.expedia.seiso.domain.repo.NodeIpAddressRepo;
import com.expedia.seiso.domain.repo.RepoPackageMarker;
import com.expedia.seiso.domain.repo.ServiceInstancePortRepo;
import com.expedia.seiso.domain.repo.adapter.EndpointRepoAdapter;
import com.expedia.seiso.domain.repo.adapter.IpAddressRoleRepoAdapter;
import com.expedia.seiso.domain.repo.adapter.NodeIpAddressRepoAdapter;
import com.expedia.seiso.domain.repo.adapter.RepoAdapter;
import com.expedia.seiso.domain.repo.adapter.RepoAdapterLookup;
import com.expedia.seiso.domain.repo.adapter.ServiceInstancePortRepoAdapter;
import com.expedia.seiso.domain.repo.adapter.SimpleItemRepoAdapter;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.seiso.domain.service.SearchEngine;
import com.expedia.seiso.domain.service.impl.ItemDeleter;
import com.expedia.seiso.domain.service.impl.ItemMerger;
import com.expedia.seiso.domain.service.impl.ItemSaver;
import com.expedia.seiso.domain.service.impl.ItemServiceImpl;
import com.expedia.seiso.domain.service.impl.SearchEngineImpl;
import com.expedia.seiso.gateway.NotificationGateway;
import com.expedia.seiso.gateway.aop.NotificationAspect;
import com.expedia.seiso.gateway.impl.NotificationGatewayImpl;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Seiso domain configuration. This includes everything from the service layer down to database connectivity.
 * 
 * @author Willie Wheeler
 */
@Configuration
@Import({ RepoConfig.class, ServiceConfig.class })
public class SeisoDomainConfig {
	
	@Configuration
	@EnableJpaRepositories(basePackageClasses = { RepoPackageMarker.class })
	//@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
	public static class RepoConfig {
		@Autowired private ListableBeanFactory beanFactory;
		
		@Bean
		public DataSourceProperties dataSourceProperties() { return new DataSourceProperties(); }
		
		// DriverManagerDataSource is OK for dev, but not for production. It's not a connection pool.
		// HikariCP is good for prod use.
		// http://stackoverflow.com/questions/24655247/any-reason-why-spring-hibernate-is-taking-more-time
		// https://github.com/spring-projects/spring-boot/issues/418
		@Bean
		public HikariDataSource dataSource() {
			val settings = dataSourceProperties();
			
			// FIXME Remove hardcodes
			// val hikariConfig = new HikariConfig();
			// hikariConfig.setMaximumPoolSize(10);
			// hikariConfig.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
			// hikariConfig.addDataSourceProperty("serverName", "localhost");
			// hikariConfig.addDataSourceProperty("port", 3306);
			// hikariConfig.addDataSourceProperty("databaseName", "seiso");
			// hikariConfig.addDataSourceProperty("user", env.getProperty(SEISO_DB_USERNAME_KEY));
			// hikariConfig.addDataSourceProperty("password", env.getProperty(SEISO_DB_PASSWORD_KEY));
			// return new HikariDataSource(hikariConfig);
			
			// FIXME Legacy configuration.
			// TODO Add other Hikari data source options.
			// See https://github.com/brettwooldridge/HikariCP to upgrade.
			val dataSource = new HikariDataSource();
			dataSource.setDriverClassName(settings.getDriverClassName());
			dataSource.setJdbcUrl(settings.getUrl());
			dataSource.setUsername(settings.getUsername());
			dataSource.setPassword(settings.getPassword());
			return dataSource;
		}
		
		@Bean
		public Repositories repositories() { return new Repositories(beanFactory); }
		
		@Bean
		@SuppressWarnings("rawtypes")
		public PersistentEntities persistentEntities() {
			val contexts = new ArrayList<MappingContext<?, ?>>();
			val beans = BeanFactoryUtils.beansOfTypeIncludingAncestors(beanFactory, MappingContext.class);
			for (val context : beans.values()) { contexts.add(context); }
			return new PersistentEntities(contexts);
		}
	}
	
	@Configuration
	@EnableAspectJAutoProxy
	@EnableTransactionManagement
	public static class ServiceConfig {
		@Autowired private PlatformTransactionManager txManager;
		@Autowired private Repositories repositories;
		@Autowired private EndpointRepo endpointRepo;
		@Autowired private IpAddressRoleRepo ipAddressRoleRepo;
		@Autowired private NodeIpAddressRepo nodeIpAddressRepo;
		@Autowired private ServiceInstancePortRepo serviceInstancePortRepo;
		
		@Bean
		public TransactionTemplate transactionTemplate() {
			return new TransactionTemplate(txManager);
		}
		
		@Bean
		public ItemMetaLookup itemMetaLookup() { return new ItemMetaLookup(); }
		
		@Bean
		public RepoAdapterLookup repoAdapterLookup() {
			// TODO Should be pretty easy to collapse these into a single class using lambdas.
			// @formatting:off
			return new RepoAdapterLookup((List<RepoAdapter>) Arrays.asList(
					new SimpleItemRepoAdapter(itemMetaLookup(), repositories),
					new EndpointRepoAdapter(endpointRepo),
					new IpAddressRoleRepoAdapter(ipAddressRoleRepo),
					new NodeIpAddressRepoAdapter(nodeIpAddressRepo),
					new ServiceInstancePortRepoAdapter(serviceInstancePortRepo)));
			// @formatting:on
		}
		
		@Bean
		public ItemMerger itemMerger() { return new ItemMerger(repoAdapterLookup()); }
		
		@Bean
		public ItemSaver itemSaver() { return new ItemSaver(repositories, itemMerger()); }
		
		@Bean
		public ItemDeleter itemDeleter() { return new ItemDeleter(repositories); }
		
		@Bean
		public ItemService itemService() { return new ItemServiceImpl(); }
		
		@Bean
		public SearchEngine searchEngine() { return new SearchEngineImpl(repositories); }
		
		@Bean
		public NotificationGateway notificationGateway() {
			return new NotificationGatewayImpl();
		}
		
		@Bean
		public NotificationAspect notificationAspect() {
			return new NotificationAspect(notificationGateway());
		}
	}
}
