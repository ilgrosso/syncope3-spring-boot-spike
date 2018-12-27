package org.apache.syncope.core.logic.init;

import java.util.Comparator;
import org.apache.syncope.core.persistence.api.SyncopeSpringBootLoader;
import org.apache.syncope.core.persistence.jpa.spring.CommonEntityManagerFactoryConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class SyncopeInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(SyncopeInitializer.class);

    @Autowired
    private CommonEntityManagerFactoryConf domainsHolder;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        event.getApplicationContext().getBeansOfType(SyncopeSpringBootLoader.class).values().stream().
                sorted(Comparator.comparing(SyncopeSpringBootLoader::getOrder)).
                forEach(loader -> {
                    domainsHolder.getDomains().forEach((domain, datasource) -> {
                        LOG.debug("[{}]#[{}] Starting initialization", domain, loader.getClass().getName());
                        loader.load(domain, datasource);
                        LOG.debug("[{}]#[{}] Initialization completed", domain, loader.getClass().getName());
                    });
                });
    }
}
