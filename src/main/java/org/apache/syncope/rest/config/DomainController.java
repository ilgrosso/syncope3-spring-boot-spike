package org.apache.syncope.rest.config;

import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.syncope.persistence.config.XMLContentLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DomainController {

    private static final Logger LOG = LoggerFactory.getLogger(DomainController.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DomainConfigurationFactory domainConfigurationFactory;

    @RequestMapping(consumes = "application/json", method = RequestMethod.PUT, value = "/domains")
    public void registerDomain(@RequestBody(required = true) final DomainConfiguration domainConfiguration) {
        LOG.debug("Creating domain [{}]", domainConfiguration);

        domainConfigurationFactory.registerDomainConfiguration(domainConfiguration);

        String prefix = StringUtils.capitalize(domainConfiguration.getDomainName());

        // Initialize the domain just created as done in LogicInitializer
        XMLContentLoader loader = applicationContext.getBean(XMLContentLoader.class);
        loader.getDomains().clear();
        loader.getDomains().put(prefix, applicationContext.getBean(prefix + "DataSource", DataSource.class));
        loader.load();
    }
}
