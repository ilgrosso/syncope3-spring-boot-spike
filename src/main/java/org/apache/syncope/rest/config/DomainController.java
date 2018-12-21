package org.apache.syncope.rest.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DomainController {

    Logger LOG = LoggerFactory.getLogger(DomainController.class);

    @Autowired
    private DomainConfigurationFactory domainConfigurationFactory;

    @RequestMapping(consumes = "application/json", method = RequestMethod.PUT, value = "/domains")
    public void registerDomain(@RequestBody(required = true) final DomainConfiguration domainConfiguration) {
        LOG.debug("Creating domain [{}]", domainConfiguration);
        domainConfigurationFactory.registerDomainConfiguration(domainConfiguration);
    }

}
