package org.apache.syncope.persistence.config;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.ValidationMode;
import javax.validation.Validator;
import org.apache.syncope.core.persistence.api.dao.AnyObjectDAO;
import org.apache.syncope.core.persistence.api.dao.AnySearchDAO;
import org.apache.syncope.core.persistence.api.dao.ConfDAO;
import org.apache.syncope.core.persistence.api.dao.GroupDAO;
import org.apache.syncope.core.persistence.api.dao.PlainAttrDAO;
import org.apache.syncope.core.persistence.api.dao.PlainAttrValueDAO;
import org.apache.syncope.core.persistence.api.dao.PlainSchemaDAO;
import org.apache.syncope.core.persistence.api.dao.UserDAO;
import org.apache.syncope.core.persistence.api.entity.EntityFactory;
import org.apache.syncope.core.persistence.jpa.spring.CommonEntityManagerFactoryConf;
import org.apache.syncope.core.persistence.jpa.spring.MultiJarAwarePersistenceUnitPostProcessor;
import org.apache.syncope.persistence.interceptors.DomainTransactionInterceptorInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class PersistenceContext implements EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceContext.class);

    private Environment env;

    @Override
    public void setEnvironment(final Environment env) {
        this.env = env;
    }

    @Bean
    public static BeanFactoryPostProcessor domainTransactionInterceptorInjector() {
        return new DomainTransactionInterceptorInjector();
    }

    @Bean
    public CommonEntityManagerFactoryConf commonEMFConf() {
        CommonEntityManagerFactoryConf commonEMFConf = new CommonEntityManagerFactoryConf();
        commonEMFConf.setPackagesToScan("org.apache.syncope.core.persistence.jpa.entity");
        commonEMFConf.setValidationMode(ValidationMode.NONE);
        commonEMFConf.setPersistenceUnitPostProcessors(new MultiJarAwarePersistenceUnitPostProcessor());
        Map<String, Object> jpaPropertyMap = new HashMap<>();

        jpaPropertyMap.put("openjpa.Log", "slf4j");
        if (LOG.isDebugEnabled()) {
            jpaPropertyMap.put("openjpa.Log", "SQL=TRACE");
            jpaPropertyMap.put("openjpa.ConnectionFactoryProperties",
                    "PrintParameters=true, PrettyPrint=true, PrettyPrintLineLength=80");
        }

        jpaPropertyMap.put("openjpa.NontransactionalWrite", false);

        jpaPropertyMap.put("openjpa.jdbc.MappingDefaults",
                "ForeignKeyDeleteAction=restrict, JoinForeignKeyDeleteAction=restrict,"
                + "FieldStrategies='"
                + "java.util.Locale=org.apache.syncope.core.persistence.jpa.openjpa.LocaleValueHandler,"
                + "java.lang.Boolean=org.apache.syncope.core.persistence.jpa.openjpa.BooleanValueHandler'");

        jpaPropertyMap.put("openjpa.DataCache", "true");
        jpaPropertyMap.put("openjpa.QueryCache", "true");

        jpaPropertyMap.put("openjpa.RemoteCommitProvider", env.getProperty("${openjpa.RemoteCommitProvider:sjvm}"));

        commonEMFConf.setJpaPropertyMap(jpaPropertyMap);
        return commonEMFConf;
    }

    @Bean
    public EntityFactory entityFactory()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        return (EntityFactory) Class.forName(env.getProperty("entity.factory")).newInstance();
    }

    @Bean
    public PlainSchemaDAO plainSchemaDAO()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        return (PlainSchemaDAO) Class.forName(env.getProperty("plainSchema.dao")).newInstance();
    }

    @Bean
    public PlainAttrDAO plainAttrDAO()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        return (PlainAttrDAO) Class.forName(env.getProperty("plainAttr.dao")).newInstance();
    }

    @Bean
    public PlainAttrValueDAO plainAttrValueDAO()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        return (PlainAttrValueDAO) Class.forName(env.getProperty("plainAttrValue.dao")).newInstance();
    }

    @Bean
    public AnySearchDAO anySearchDAO()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        return (AnySearchDAO) Class.forName(env.getProperty("any.search.dao")).newInstance();
    }

    @Bean
    public UserDAO userDAO()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        return (UserDAO) Class.forName(env.getProperty("user.dao")).newInstance();
    }

    @Bean
    public GroupDAO groupDAO()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        return (GroupDAO) Class.forName(env.getProperty("group.dao")).newInstance();
    }

    @Bean
    public AnyObjectDAO anyObjectDAO()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        return (AnyObjectDAO) Class.forName(env.getProperty("anyObject.dao")).newInstance();
    }

    @Bean
    public ConfDAO confDAO()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        return (ConfDAO) Class.forName(env.getProperty("conf.dao")).newInstance();
    }

    @Bean
    public Validator localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }
}
