package org.apache.syncope;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableAutoConfiguration(
        exclude = { DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class })
@ComponentScan(
        basePackages = {
            "org.apache.syncope.persistence.config",
            "org.apache.syncope.rest",
            "org.apache.syncope.core.persistence.jpa" })
@PropertySource("classpath:domains/Master.properties")
@PropertySource("classpath:domains/Two.properties")
@PropertySource("classpath:persistence.properties")
public class SyncopeCore extends SpringBootServletInitializer {

    protected static final Logger LOG = LoggerFactory.getLogger(SyncopeCore.class);

    public static void main(String[] args) {
        SpringApplication.run(SyncopeCore.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(final ApplicationContext ctx) {
        return args -> {
            LOG.debug("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                LOG.debug(beanName + " -----> " + ctx.getBean(beanName).getClass().getSimpleName());
            }
        };
    }
}
