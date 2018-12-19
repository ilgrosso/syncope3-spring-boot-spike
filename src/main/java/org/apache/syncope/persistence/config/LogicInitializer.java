package org.apache.syncope.persistence.config;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.syncope.core.persistence.api.SyncopeLoader;
import org.apache.syncope.core.spring.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn({ "MasterContentXML", "commonEMFConf", "XMLContentLoaderCustom" })
public class LogicInitializer implements InitializingBean, BeanFactoryAware {

    private Logger LOG = LoggerFactory.getLogger(LogicInitializer.class);

    @Autowired
    private ApplicationContext applicationContext;

    private DefaultListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplicationContextProvider.setBeanFactory(beanFactory);
        List<SyncopeLoader> loaders = new ArrayList<>();
        org.apache.syncope.persistence.config.XMLContentLoader loader = beanFactory.getBean(
                org.apache.syncope.persistence.config.XMLContentLoader.class);
        LOG.debug("Starting initialization...");
        applicationContext.getBeansOfType(DataSource.class).
                entrySet().stream().
                filter((entry) -> (!entry.getKey().startsWith("local"))).
                forEachOrdered((entry) -> {
                    loader.getDomains().put(
                            StringUtils.substringBefore(entry.getKey(), DataSource.class.getSimpleName()),
                            entry.
                                    getValue());
                });
        LOG.debug("Invoking {} with priority {}", AopUtils.getTargetClass(loader).getName(), loader.getPriority());
        loader.load();
        LOG.debug("Initialization completed");
    }
}
