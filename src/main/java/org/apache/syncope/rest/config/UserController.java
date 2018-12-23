package org.apache.syncope.rest.config;

import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.QueryParam;
import org.apache.syncope.core.persistence.api.entity.user.User;
import org.apache.syncope.core.spring.security.AuthContextUtils;
import org.apache.syncope.rest.dao.UserDAOWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserDAOWrapper userDAOWrapper;

    @RequestMapping(produces = "application/json", value = "/users")
    public List<String> list(@QueryParam("domain") final String domain) {
        LOG.debug("Searching users on domain [{}]", domain);
        // set domain with AuthContextUtils
        return AuthContextUtils.execWithAuthContext(domain, () -> userDAOWrapper.list().stream().map(user -> user.
                getKey() + "/" + user.getUsername()).collect(Collectors.toList()));
    }

    @RequestMapping(consumes = "text/plain;charset=UTF-8", produces = "text/plain;charset=UTF-8", method =
            RequestMethod.POST, value = "/users/save")
    public String save(@RequestBody final String username, @QueryParam("domain") final String domain) {
        LOG.debug("Saving user in domain [{}]", domain);
        User user = AuthContextUtils.execWithAuthContext(domain, () -> userDAOWrapper.save(username));
        return user == null ? null : user.getUsername();
    }
}
