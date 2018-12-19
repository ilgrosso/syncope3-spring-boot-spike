package org.apache.syncope.persistence.config;

import org.apache.syncope.core.persistence.api.dao.AnyObjectDAO;
import org.apache.syncope.core.persistence.api.dao.AnySearchDAO;
import org.apache.syncope.core.persistence.api.dao.ConfDAO;
import org.apache.syncope.core.persistence.api.dao.GroupDAO;
import org.apache.syncope.core.persistence.api.dao.PlainAttrDAO;
import org.apache.syncope.core.persistence.api.dao.PlainAttrValueDAO;
import org.apache.syncope.core.persistence.api.dao.PlainSchemaDAO;
import org.apache.syncope.core.persistence.api.dao.UserDAO;
import org.apache.syncope.core.persistence.api.entity.EntityFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/*
 * Replacement of persistenceContext.xml
 */
@Configuration
public class PersistenceContext {

    @Value("${entity.factory}")
    private String entityFactoryClass;

    @Value("${plainSchema.dao}")
    private String plainSchemaDAOClass;

    @Value("${plainAttr.dao}")
    private String plainAttrDAOClass;

    @Value("${plainAttrValue.dao}")
    private String plainAttrValueDAOClass;

    @Value("${any.search.dao}")
    private String anySearchDAOClass;

    @Value("${user.dao}")
    private String userDAOClass;

    @Value("${group.dao}")
    private String groupDAOClass;

    @Value("${anyObject.dao}")
    private String anyObjectDAOClass;

    @Value("${conf.dao}")
    private String confDAOClass;

    @Bean
    public EntityFactory entityFactory() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (EntityFactory) Class.forName(entityFactoryClass).newInstance();
    }

    @Bean
    public PlainSchemaDAO plainSchemaDAO() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (PlainSchemaDAO) Class.forName(plainSchemaDAOClass).newInstance();
    }

    @Bean
    public PlainAttrDAO plainAttrDAO() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (PlainAttrDAO) Class.forName(plainAttrDAOClass).newInstance();
    }

    @Bean
    public PlainAttrValueDAO plainAttrValueDAO() throws ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        return (PlainAttrValueDAO) Class.forName(plainAttrValueDAOClass).newInstance();
    }

    @Bean
    public AnySearchDAO anySearchDAO() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (AnySearchDAO) Class.forName(anySearchDAOClass).newInstance();
    }

    @Bean
    public UserDAO userDAO() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (UserDAO) Class.forName(userDAOClass).newInstance();
    }

    @Bean
    public GroupDAO groupDAO() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (GroupDAO) Class.forName(groupDAOClass).newInstance();
    }

    @Bean
    public AnyObjectDAO anyObjectDAO() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (AnyObjectDAO) Class.forName(anyObjectDAOClass).newInstance();
    }

    @Bean
    public ConfDAO confDAO() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (ConfDAO) Class.forName(confDAOClass).newInstance();
    }

    @Bean
    @Primary
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }

}
