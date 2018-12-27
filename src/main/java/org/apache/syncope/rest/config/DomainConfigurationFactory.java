package org.apache.syncope.rest.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.nio.charset.StandardCharsets;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.syncope.core.persistence.jpa.spring.DomainEntityManagerFactoryBean;
import org.apache.syncope.core.spring.ApplicationContextProvider;
import org.apache.syncope.core.spring.ResourceWithFallbackLoader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.vendor.OpenJpaVendorAdapter;
import org.springframework.stereotype.Component;

@Component
public class DomainConfigurationFactory {

    @Value("${content.directory}")
    private String contentDirectory;

    private void registerSingleton(final String name, final Object bean) {
        if (ApplicationContextProvider.getBeanFactory().containsSingleton(name)) {
            ApplicationContextProvider.getBeanFactory().destroySingleton(name);
        }
        ApplicationContextProvider.getBeanFactory().registerSingleton(name, bean);
    }

    private void registerBeanDefinition(final String name, final BeanDefinition beanDefinition) {
        if (ApplicationContextProvider.getBeanFactory().containsBeanDefinition(name)) {
            ApplicationContextProvider.getBeanFactory().removeBeanDefinition(name);
        }
        ApplicationContextProvider.getBeanFactory().registerBeanDefinition(name, beanDefinition);
    }

    public void registerDomainConfiguration(final DomainCR domainConfiguration) {
        // localDomainDataSource
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(domainConfiguration.getJdbcDriver());
        hikariConfig.setJdbcUrl(domainConfiguration.getJdbcURL());
        hikariConfig.setUsername(domainConfiguration.getDbUsername());
        hikariConfig.setPassword(domainConfiguration.getDbPassword());
        hikariConfig.setSchema(domainConfiguration.getDbSchema());
        hikariConfig.setTransactionIsolation(domainConfiguration.getTransactionIsolation());
        hikariConfig.setMaximumPoolSize(domainConfiguration.getMaxPoolSize());
        hikariConfig.setMinimumIdle(domainConfiguration.getMinIdle());
        String domainName = StringUtils.capitalize(domainConfiguration.getDomainName());

        HikariDataSource localDomainDataSource = new HikariDataSource(hikariConfig);

        // domainDataSource
        registerBeanDefinition(
                domainName + "DataSource",
                BeanDefinitionBuilder.rootBeanDefinition(JndiObjectFactoryBean.class).
                        addPropertyValue("jndiName", "java:comp/env/jdbc/syncope" + domainName + "DataSource").
                        addPropertyValue("defaultObject", localDomainDataSource).
                        getBeanDefinition());
        DataSource initedDataSource = ApplicationContextProvider.getBeanFactory().
                getBean(domainName + "DataSource", DataSource.class);

        // domainResourceDatabasePopulator
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.setContinueOnError(true);
        databasePopulator.setIgnoreFailedDrops(true);
        databasePopulator.setSqlScriptEncoding(StandardCharsets.UTF_8.name());
        databasePopulator.addScript(new ClassPathResource("/audit/" + domainConfiguration.getAuditSql()));

        registerSingleton(domainName.toLowerCase() + "ResourceDatabasePopulator", databasePopulator);

        // domainDataSourceInitializer
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(initedDataSource);
        dataSourceInitializer.setEnabled(true);
        dataSourceInitializer.setDatabasePopulator(databasePopulator);
        registerSingleton(domainName.toLowerCase() + "DataSourceInitializer", dataSourceInitializer);

        // domainEntityManagerFactory
        OpenJpaVendorAdapter vendorAdapter = new OpenJpaVendorAdapter();
        vendorAdapter.setShowSql(false);
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setDatabasePlatform(domainConfiguration.getDatabasePlatform());

        registerBeanDefinition(
                domainName + "EntityManagerFactory",
                BeanDefinitionBuilder.rootBeanDefinition(DomainEntityManagerFactoryBean.class).
                        addPropertyValue("mappingResources", domainConfiguration.getOrm()).
                        addPropertyValue("persistenceUnitName", domainName).
                        addPropertyReference("dataSource", domainName + "DataSource").
                        addPropertyValue("jpaVendorAdapter", vendorAdapter).
                        addPropertyReference("commonEntityManagerFactoryConf", "commonEMFConf").
                        getBeanDefinition());
        ApplicationContextProvider.getBeanFactory().getBean(domainName + "EntityManagerFactory");

        // domainTransactionManager
        AbstractBeanDefinition domainTransactionManager =
                BeanDefinitionBuilder.rootBeanDefinition(JpaTransactionManager.class).
                        addPropertyReference("entityManagerFactory", domainName + "EntityManagerFactory").
                        getBeanDefinition();
        domainTransactionManager.addQualifier(new AutowireCandidateQualifier(Qualifier.class, domainName));
        registerBeanDefinition(domainName + "TransactionManager", domainTransactionManager);

        // domainContentXML
        registerBeanDefinition(domainName + "ContentXML",
                BeanDefinitionBuilder.rootBeanDefinition(ResourceWithFallbackLoader.class)
                        .addPropertyValue(
                                "primary", "file:" + contentDirectory + "/domains/" + domainName + "Content.xml")
                        .addPropertyValue(
                                "fallback", "classpath:domains/" + domainName + "Content.xml")
                        .getBeanDefinition());
    }
}
