package org.apache.syncope.rest.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.syncope.core.persistence.jpa.spring.DomainEntityManagerFactoryBean;
import org.apache.syncope.core.spring.ResourceWithFallbackLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.vendor.OpenJpaVendorAdapter;
import org.springframework.stereotype.Component;

@Component
public class DomainConfigurationFactory {

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${content.directory}")
    private String contentDirectory;

    public void registerDomainConfiguration(final DomainConfiguration domainConfiguration) {
        ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();

        // localDomainDataSource
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(domainConfiguration.getDriverClassName());
        hikariConfig.setJdbcUrl(domainConfiguration.getUrl());
        hikariConfig.setUsername(domainConfiguration.getUsername());
        hikariConfig.setPassword(domainConfiguration.getPassword());
        hikariConfig.addDataSourceProperty("transactionIsolation",
                StringUtils.isBlank(domainConfiguration.getTransactionIsolation())
                ? "java.sql.Connection.TRANSACTION_READ_COMMITTED"
                : domainConfiguration.getTransactionIsolation());
        hikariConfig.addDataSourceProperty("maximumPoolSize", StringUtils.isBlank(domainConfiguration.
                getMaximumPoolSize())
                        ? "8"
                        : domainConfiguration.getMaximumPoolSize());
        hikariConfig.addDataSourceProperty("minimumIdle", StringUtils.isBlank(domainConfiguration.getMinimumIdle())
                ? "0"
                : domainConfiguration.getMinimumIdle());
        final String domainName = StringUtils.capitalize(domainConfiguration.getDomainName());
        final String localDataSourceName = "local" + domainName + "DataSource";

        final HikariDataSource localDomainDataSource = new HikariDataSource(hikariConfig);
        beanFactory.registerSingleton(localDataSourceName, localDomainDataSource);

        // domainDataSource
        beanFactory.registerBeanDefinition(domainName + "DataSource",
                BeanDefinitionBuilder.rootBeanDefinition(JndiObjectFactoryBean.class)
                        .applyCustomizers(bd -> bd.setPrimary(true))
                        .addDependsOn(localDataSourceName)
                        .addPropertyValue("jndiName", "java:comp/env/jdbc/syncope" + domainName + "DataSource")
                        .addPropertyValue("defaultObject", localDomainDataSource)
                        .getBeanDefinition());
        final DataSource domainDataSource = context.getBean(domainName + "DataSource", DataSource.class);

        // domainResourceDatabasePopulator
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.setContinueOnError(true);
        databasePopulator.setIgnoreFailedDrops(true);
        databasePopulator.setSqlScriptEncoding("UTF-8");
        databasePopulator.addScript(new ClassPathResource("/audit/" + domainConfiguration.getAuditSql()));

        beanFactory.registerSingleton(domainName.toLowerCase() + "ResourceDatabasePopulator",
                databasePopulator);

        //domainDataSourceInitializer
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(domainDataSource);
        dataSourceInitializer.setEnabled(true);
        dataSourceInitializer.setDatabasePopulator(databasePopulator);
        beanFactory.registerSingleton(domainName.toLowerCase() + "DataSourceInitializer", dataSourceInitializer);

        // domainEntityManagerFactory
        OpenJpaVendorAdapter vendorAdapter = new OpenJpaVendorAdapter();
        vendorAdapter.setShowSql(false);
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setDatabasePlatform(domainConfiguration.getDatabasePlatform());
        beanFactory.registerSingleton(domainName + "JpaVendorAdapter", vendorAdapter);

        // domainEntityManagerFactory
        beanFactory.registerBeanDefinition(domainName + "EntityManagerFactory", BeanDefinitionBuilder
                .rootBeanDefinition(DomainEntityManagerFactoryBean.class)
                .addPropertyValue("mappingResources", domainConfiguration.getOrm())
                .addPropertyValue("persistenceUnitName", domainName)
                .addPropertyReference("dataSource", domainName + "DataSource")
                .addPropertyReference("jpaVendorAdapter", domainName + "JpaVendorAdapter")
                .addPropertyReference("commonEntityManagerFactoryConf", "commonEMFConf")
                .getBeanDefinition());
        // domainTransactionManager
        final AbstractBeanDefinition domainTransactionManager =
                BeanDefinitionBuilder.rootBeanDefinition(JpaTransactionManager.class)
                        .addPropertyReference("entityManagerFactory", domainName + "EntityManagerFactory")
                        .getBeanDefinition();
        domainTransactionManager.addQualifier(new AutowireCandidateQualifier(Qualifier.class, domainName));
        beanFactory.registerBeanDefinition(domainName + "TransactionManager", domainTransactionManager);
        // resources
        beanFactory.registerBeanDefinition(domainName + "ContentXML", BeanDefinitionBuilder.rootBeanDefinition(
                ResourceWithFallbackLoader.class)
                .addPropertyValue("primary", "file:" + contentDirectory + "/domains/" + domainName + "Content.xml")
                .addPropertyValue("fallback", "classpath:domains/" + domainName + "Content.xml")
                .getBeanDefinition());
    }

}
