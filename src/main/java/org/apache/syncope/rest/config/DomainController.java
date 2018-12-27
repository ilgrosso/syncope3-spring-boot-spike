package org.apache.syncope.rest.config;

import java.util.Comparator;
import org.apache.syncope.common.lib.SyncopeConstants;
import org.apache.syncope.core.persistence.api.DomainsHolder;
import org.apache.syncope.core.persistence.api.SyncopeSpringBootLoader;
import org.apache.syncope.core.persistence.api.dao.DomainDAO;
import org.apache.syncope.core.persistence.api.entity.Domain;
import org.apache.syncope.core.persistence.api.entity.EntityFactory;
import org.apache.syncope.core.spring.security.AuthContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DomainController {

    private static final Logger LOG = LoggerFactory.getLogger(DomainController.class);

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private DomainConfigurationFactory domainConfigurationFactory;

    @Autowired
    private DomainsHolder domainsHolder;

    @Autowired
    private DomainDAO domainDAO;

    @Autowired
    private EntityFactory entityFactory;

    @Transactional
    @RequestMapping(consumes = "application/json", method = RequestMethod.PUT, value = "/domains")
    public void registerDomain(@RequestBody(required = true) final DomainCR createReq) {
        LOG.debug("Creating domain [{}]", createReq);

        // create the domain entity on the Master domain
        String created = AuthContextUtils.execWithAuthContext(SyncopeConstants.MASTER_DOMAIN, () -> {
            Domain domain = domainDAO.find(createReq.getDomainName());
            if (domain == null) {
                domain = entityFactory.newEntity(Domain.class);
                domain.setKey(createReq.getDomainName());
            }
            domain.setPassword(createReq.getAdminPassword(), createReq.getAdminCipherAlgorithm());
            domain = domainDAO.save(domain);

            return domain.getKey();
        });

        // create JPA configuration
        domainConfigurationFactory.registerDomainConfiguration(createReq);

        // initialize the domain just created as done in SyncopeInitializer
        ctx.getBeansOfType(SyncopeSpringBootLoader.class).values().stream().
                sorted(Comparator.comparing(SyncopeSpringBootLoader::getOrder)).
                forEach(loader -> {
                    LOG.debug("[{}]#[{}] Starting initialization", created, loader.getClass().getName());
                    loader.load(created, domainsHolder.getDomains().get(created));
                    LOG.debug("[{}]#[{}] Initialization completed", created, loader.getClass().getName());
                });
    }
}
