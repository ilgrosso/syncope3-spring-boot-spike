package org.apache.syncope;

import java.io.IOException;
import org.apache.syncope.core.spring.ApplicationContextProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableAutoConfiguration(
        exclude = { DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class })
@ComponentScan("org.apache.syncope")
@PropertySource("classpath:persistence.properties")
@PropertySource("classpath:domains/Master.properties")
@PropertySource("classpath:domains/Two.properties")
@PropertySource("classpath:security.properties")
@PropertySource("classpath:connid.properties")
@PropertySource("classpath:mail.properties")
@PropertySource("classpath:logic.properties")
@PropertySource("classpath:workflow.properties")
@PropertySource("classpath:provisioning.properties")
@PropertySource(value = "file:${conf.directory}/persistence.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${conf.directory}/domains/Master.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${conf.directory}/security.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${conf.directory}/connid.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${conf.directory}/mail.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${conf.directory}/logic.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${conf.directory}/workflow.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${conf.directory}/provisioning.properties", ignoreResourceNotFound = true)
public class SyncopeCore extends SpringBootServletInitializer {

    public static void main(final String[] args) {
        SpringApplication.run(SyncopeCore.class, args);
    }

    @Bean
    public ApplicationContextProvider applicationContextProvider() {
        return new ApplicationContextProvider();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() throws IOException {
        PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
        pspc.setIgnoreResourceNotFound(true);
        pspc.setIgnoreUnresolvablePlaceholders(true);
//        pspc.setLocations(new PathMatchingResourcePatternResolver().
//                getResources("classpath:domains/*.properties"));
//        pspc.setLocations(new PathMatchingResourcePatternResolver().
//                getResources("file:${conf.directory}/domains/*.properties"));
        return pspc;
    }
}
