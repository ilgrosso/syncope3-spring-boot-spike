package org.apache.syncope.persistence.config;

import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn({ "applicationContextProvider", "MasterContentXML", "commonEMFConf", "XMLContentLoaderCustom" })
public class PersistenceInitializer implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceInitializer.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        XMLContentLoader loader = applicationContext.getBean(XMLContentLoader.class);
        LOG.debug("Starting initialization...");
        applicationContext.getBeansOfType(DataSource.class).entrySet().stream().
                filter(entry -> (!entry.getKey().startsWith("local"))).
                forEach(entry -> {
                    loader.getDomains().put(
                            StringUtils.substringBefore(entry.getKey(), DataSource.class.getSimpleName()),
                            entry.getValue());
                });
        LOG.debug("Invoking {} with priority {}", AopUtils.getTargetClass(loader).getName(), loader.getPriority());
        loader.load();
        LOG.debug("Initialization completed");
    }
}
