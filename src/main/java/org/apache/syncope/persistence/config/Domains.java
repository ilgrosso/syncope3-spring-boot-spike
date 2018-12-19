package org.apache.syncope.persistence.config;

import org.apache.syncope.persistence.interceptors.DomainTransactionInterceptorInjector;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.ValidationMode;
import org.apache.syncope.core.persistence.jpa.spring.CommonEntityManagerFactoryConf;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Replacement of domains.xml
 */
@Configuration
public class Domains {

    @Value("${openjpa.RemoteCommitProvider:sjvm}")
    private String remoteCommitProvider;

    // use this in order to inject customized transaction interceptor
    @Bean
    public static BeanFactoryPostProcessor domainTransactionInterceptorInjector() {
        return new DomainTransactionInterceptorInjector();
    }

    @Bean
    public CommonEntityManagerFactoryConf commonEMFConf() {
        CommonEntityManagerFactoryConf commonEMFConf = new CommonEntityManagerFactoryConf();
        commonEMFConf.setPackagesToScan("org.apache.syncope.core.persistence.jpa.entity");
        commonEMFConf.setValidationMode(ValidationMode.NONE);
        commonEMFConf.setPersistenceUnitPostProcessors(
                new org.apache.syncope.core.persistence.jpa.spring.MultiJarAwarePersistenceUnitPostProcessor());
        Map<String, Object> jpaPropertyMap = new HashMap<>();
        jpaPropertyMap.put("openjpa.Log", "slf4j");
        jpaPropertyMap.put("openjpa.NontransactionalWrite", false);
        jpaPropertyMap.put("openjpa.jdbc.MappingDefaults",
                "ForeignKeyDeleteAction=restrict, JoinForeignKeyDeleteAction=restrict,"
                + "FieldStrategies='java.util.Locale=org.apache.syncope.core.persistence.jpa.openjpa.LocaleValueHandler,"
                + "java.lang.Boolean=org.apache.syncope.core.persistence.jpa.openjpa.BooleanValueHandler'");
        jpaPropertyMap.put("openjpa.DataCache", "true");
        jpaPropertyMap.put("openjpa.QueryCache", "true");
        jpaPropertyMap.put("openjpa.RemoteCommitProvider", remoteCommitProvider);
        commonEMFConf.setJpaPropertyMap(jpaPropertyMap);
        return commonEMFConf;
    }

}
