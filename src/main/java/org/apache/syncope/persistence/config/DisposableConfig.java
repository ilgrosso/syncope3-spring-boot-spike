package org.apache.syncope.persistence.config;

import org.apache.syncope.dummy.beans.DummyConnectorRegistry;
import org.apache.syncope.dummy.beans.DummyImplementationLookup;
import org.apache.syncope.core.persistence.api.ImplementationLookup;
import org.apache.syncope.core.provisioning.api.ConnectorRegistry;
import org.apache.syncope.core.spring.ApplicationContextProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * This configuration file contains beans necessary to run application standalone and tests without failures.
 * Once integrated in Apache Syncope this configuration must be ignored.
 */
@Configuration
public class DisposableConfig {

    @Bean
    public String adminUser() {
        return "admin";
    }

    @Bean
    public String adminPassword() {
        return "DE088591C00CC98B36F5ADAAF7DA2B004CF7F2FE7BBB45B766B6409876E2F3DB13C7905C6AA59464";
    }

    @Bean
    public String anonymousUser() {
        return "anonymous";
    }

    @Bean
    public String anonymousKey() {
        return "anonymousKey";
    }

    @Bean
    public ConnectorRegistry connectorRegistry() {
        return new DummyConnectorRegistry();
    }

    @Bean
    public ImplementationLookup implementationLookup() {
        return new DummyImplementationLookup();
    }

    @Bean
    public ApplicationContextProvider applicationContextProvider() {
        return new ApplicationContextProvider();
    }
}
