package org.apache.syncope.persistence.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Collections;
import javax.sql.DataSource;
import org.apache.syncope.core.persistence.jpa.spring.CommonEntityManagerFactoryConf;
import org.apache.syncope.core.persistence.jpa.spring.DomainEntityManagerFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.vendor.OpenJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

/*
 * Replacement of TwoDomain.xml
 */
@Configuration
@Import(ResourcesContext.class)
public class TwoDomain {

    @Autowired
    private CommonEntityManagerFactoryConf commonEMFConf;

    @Value("${Two.driverClassName}")
    private String driverClassName;

    @Value("${Two.url}")
    private String url;

    @Value("${Two.username}")
    private String username;

    @Value("${Two.password}")
    private String password;

    @Value("${Two.pool.transactionIsolation:java.sql.Connection.TRANSACTION_READ_COMMITTED}")
    private String transactionIsolation;

    @Value("${Two.pool.maxActive:8}")
    private String maximumPoolSize;

    @Value("{Two.pool.minIdle:0}")
    private String minimumIdle;

    @Value("classpath:/audit/${Two.audit.sql}")
    private Resource auditSql;

    @Value("${Two.orm}")
    private String orm;

    @Value("${Two.databasePlatform}")
    private String databasePlatform;

    //        <TwoDomain.xml>
    @Bean
    public DataSource localTwoDataSource() {
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

    @Bean(name = "TwoDataSource")
    @Primary
    @DependsOn("localTwoDataSource")
    public JndiObjectFactoryBean twoDataSource() {
        JndiObjectFactoryBean twoDataSource = new JndiObjectFactoryBean();
        twoDataSource.setJndiName("java:comp/env/jdbc/syncopeTwoDataSource");
        twoDataSource.setDefaultObject(localTwoDataSource());
        return twoDataSource;
    }

    @Bean
    public ResourceDatabasePopulator twoResourceDatabasePopulator() {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.setContinueOnError(true);
        databasePopulator.setIgnoreFailedDrops(true);
        databasePopulator.setSqlScriptEncoding("UTF-8");
        databasePopulator.addScript(auditSql);
        return databasePopulator;
    }

    @Bean
    public DataSourceInitializer twoDataSourceInitializer() {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource((DataSource) twoDataSource().getObject());
        dataSourceInitializer.setEnabled(true);
        dataSourceInitializer.setDatabasePopulator(twoResourceDatabasePopulator());
        return dataSourceInitializer;
    }

    @Bean(name = "TwoEntityManagerFactory")
    @DependsOn(value = "commonEMFConf")
    public DomainEntityManagerFactoryBean twoEntityManagerFactory() {
        OpenJpaVendorAdapter vendorAdapter = new OpenJpaVendorAdapter();
        vendorAdapter.setShowSql(false);
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setDatabasePlatform(databasePlatform);
        DomainEntityManagerFactoryBean twoEntityManagerFactory = new DomainEntityManagerFactoryBean();
        twoEntityManagerFactory.setMappingResources(Collections.singletonList(orm).toArray(new String[0]));
        twoEntityManagerFactory.setPersistenceUnitName("Two");
        twoEntityManagerFactory.setDataSource((DataSource) twoDataSource().getObject());
        twoEntityManagerFactory.setJpaVendorAdapter(vendorAdapter);
        twoEntityManagerFactory.setCommonEntityManagerFactoryConf(commonEMFConf);
        return twoEntityManagerFactory;
    }

    @Bean(name = "TwoTransactionManager")
    @Qualifier(value = "Two")
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(twoEntityManagerFactory().getObject());
    }
    //        </TwoDomain.xml>
}
