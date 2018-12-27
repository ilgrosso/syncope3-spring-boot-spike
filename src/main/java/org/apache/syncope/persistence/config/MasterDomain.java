package org.apache.syncope.persistence.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.apache.syncope.core.persistence.jpa.spring.CommonEntityManagerFactoryConf;
import org.apache.syncope.core.persistence.jpa.spring.DomainEntityManagerFactoryBean;
import org.apache.syncope.core.spring.ResourceWithFallbackLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.vendor.OpenJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class MasterDomain {

    @Autowired
    private CommonEntityManagerFactoryConf commonEMFConf;

    @Value("${Master.driverClassName}")
    private String driverClassName;

    @Value("${Master.url}")
    private String url;

    @Value("${Master.username}")
    private String username;

    @Value("${Master.password}")
    private String password;

    @Value("${Master.pool.transactionIsolation:java.sql.Connection.TRANSACTION_READ_COMMITTED}")
    private String transactionIsolation;

    @Value("${Master.pool.maxActive:8}")
    private String maximumPoolSize;

    @Value("{Master.pool.minIdle:0}")
    private String minimumIdle;

    @Value("classpath:/audit/${Master.audit.sql}")
    private Resource auditSql;

    @Value("${Master.orm}")
    private String orm;

    @Value("${Master.databasePlatform}")
    private String databasePlatform;

    @Value("${content.directory}")
    private String contentDirectory;

    public DataSource localMasterDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.addDataSourceProperty("transactionIsolation", transactionIsolation);
        hikariConfig.addDataSourceProperty("maximumPoolSize", maximumPoolSize);
        hikariConfig.addDataSourceProperty("minimumIdle", minimumIdle);
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "MasterDataSource")
    public JndiObjectFactoryBean masterDataSource() {
        JndiObjectFactoryBean masterDataSource = new JndiObjectFactoryBean();
        masterDataSource.setJndiName("java:comp/env/jdbc/syncopeMasterDataSource");
        masterDataSource.setDefaultObject(localMasterDataSource());
        return masterDataSource;
    }

    @Bean
    public ResourceDatabasePopulator masterResourceDatabasePopulator() {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.setContinueOnError(true);
        databasePopulator.setIgnoreFailedDrops(true);
        databasePopulator.setSqlScriptEncoding("UTF-8");
        databasePopulator.addScript(auditSql);
        return databasePopulator;
    }

    @Bean
    public DataSourceInitializer masterDataSourceInitializer() {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource((DataSource) masterDataSource().getObject());
        dataSourceInitializer.setEnabled(true);
        dataSourceInitializer.setDatabasePopulator(masterResourceDatabasePopulator());
        return dataSourceInitializer;
    }

    @Bean(name = "MasterEntityManagerFactory")
    @DependsOn("commonEMFConf")
    public DomainEntityManagerFactoryBean masterEntityManagerFactory() {
        OpenJpaVendorAdapter vendorAdapter = new OpenJpaVendorAdapter();
        vendorAdapter.setShowSql(false);
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setDatabasePlatform(databasePlatform);
        DomainEntityManagerFactoryBean masterEntityManagerFactory = new DomainEntityManagerFactoryBean();
        masterEntityManagerFactory.setMappingResources(orm);
        masterEntityManagerFactory.setPersistenceUnitName("Master");
        masterEntityManagerFactory.setDataSource((DataSource) masterDataSource().getObject());
        masterEntityManagerFactory.setJpaVendorAdapter(vendorAdapter);
        masterEntityManagerFactory.setCommonEntityManagerFactoryConf(commonEMFConf);
        return masterEntityManagerFactory;
    }

    @Bean(name = "MasterTransactionManager")
    @Qualifier("Master")
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(masterEntityManagerFactory().getObject());
    }

    @Bean(name = "MasterProperties")
    public ResourceWithFallbackLoader masterProperties() {
        ResourceWithFallbackLoader masterProperties = new ResourceWithFallbackLoader();
        masterProperties.setPrimary("file:" + contentDirectory + "/domains/Master.properties");
        masterProperties.setFallback("classpath:domains/Master.properties");
        return masterProperties;
    }

    @Bean(name = "MasterContentXML")
    public ResourceWithFallbackLoader masterContentXML() {
        ResourceWithFallbackLoader masterContentXML = new ResourceWithFallbackLoader();
        masterContentXML.setPrimary("file:" + contentDirectory + "/domains/MasterContent.xml");
        masterContentXML.setFallback("classpath:domains/MasterContent.xml");
        return masterContentXML;
    }
}
