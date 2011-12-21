package com.apress.prospringmvc.pizzarus.config;

import java.sql.SQLException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.sql.DataSource;

import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.FilterType;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = { "com.apress.prospringmvc.pizzarus" }, excludeFilters = { @Filter(type = FilterType.CUSTOM, value = CustomComponentScanFilter.class) })
public class InfrastructureContextConfiguration {

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private DataSource dataSource;

	@PersistenceUnit
	private EntityManagerFactory entityManagerFactory;

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		localContainerEntityManagerFactoryBean.setDataSource(dataSource());
		localContainerEntityManagerFactoryBean.setPersistenceUnitName("pizzas-r-us");
		return localContainerEntityManagerFactoryBean;
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setGenerateDdl(true);
		jpaVendorAdapter.setShowSql(true);
		return jpaVendorAdapter;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(this.entityManagerFactory);
		transactionManager.setDataSource(this.dataSource);
		return transactionManager;
	}

	@Bean
	public DataSource dataSource() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		builder.setType(EmbeddedDatabaseType.H2);
		return builder.build();
	}

	@Bean(initMethod = "start", destroyMethod = "shutdown")
	@DependsOn("dataSource")
	public Server dataSourceTcpConnector() {
		try {
			return Server.createTcpServer();
		} catch (SQLException sqlException) {
			throw new RuntimeException(sqlException);
		}
	}

	@Bean
	public TransactionTemplate transactionTemplate() {
		return new TransactionTemplate(transactionManager);
	}
}
