package org.apache.syncope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.openjpa.jdbc.sql.H2Dictionary;
import org.apache.syncope.client.lib.SyncopeClient;
import org.apache.syncope.client.lib.SyncopeClientFactoryBean;
import org.apache.syncope.common.lib.Attr;
import org.apache.syncope.common.lib.SyncopeConstants;
import org.apache.syncope.common.lib.request.UserCR;
import org.apache.syncope.common.lib.to.PagedResult;
import org.apache.syncope.common.lib.to.UserTO;
import org.apache.syncope.common.rest.api.beans.AnyQuery;
import org.apache.syncope.common.rest.api.service.UserService;
import org.apache.syncope.rest.config.DomainCR;
import org.identityconnectors.common.security.Encryptor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

public class SyncopeCoreITCase {

    protected static final Logger LOG = LoggerFactory.getLogger(SyncopeCoreITCase.class);

    private static final String ADMIN_UNAME = "admin";

    private static final String ADMIN_PWD = "password";

    private static final String ADDRESS = "http://localhost:9080/syncope/rest";

    private static String ANONYMOUS_UNAME;

    private static String ANONYMOUS_KEY;

    private static String JWS_KEY;

    private static String JWT_ISSUER;

    private static SyncopeClientFactoryBean clientFactory;

    private static SyncopeClient adminClient;

    private static UserService userService;

    private static URL baseDomainsUrl;

    @BeforeAll
    public static void securitySetup() {
        try (InputStream propStream = Encryptor.class.getResourceAsStream("/security.properties")) {
            Properties props = new Properties();
            props.load(propStream);

            ANONYMOUS_UNAME = props.getProperty("anonymousUser");
            ANONYMOUS_KEY = props.getProperty("anonymousKey");
            JWT_ISSUER = props.getProperty("jwtIssuer");
            JWS_KEY = props.getProperty("jwsKey");
        } catch (Exception e) {
            LOG.error("Could not read secretKey", e);
        }

        assertNotNull(ANONYMOUS_UNAME);
        assertNotNull(ANONYMOUS_KEY);
        assertNotNull(JWS_KEY);
        assertNotNull(JWT_ISSUER);
    }

    @BeforeAll
    public static void restSetup() throws MalformedURLException {
        clientFactory = new SyncopeClientFactoryBean().setAddress(ADDRESS);

        adminClient = clientFactory.create(ADMIN_UNAME, ADMIN_PWD);

        userService = adminClient.getService(UserService.class);

        baseDomainsUrl = new URL("http://localhost:9080/syncope/domains");
    }

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    public void getUsers() throws Exception {
        PagedResult<UserTO> users = userService.search(new AnyQuery.Builder().build());
        assertNotNull(users);
        assertTrue(users.getTotalCount() >= 5);
    }

    @Test
    public void saveUser() throws Exception {
        String email = "masterUser@syncope.apache.org";
        UserCR createReq = new UserCR.Builder(SyncopeConstants.ROOT_REALM, email).
                password("password123").
                plainAttr(new Attr.Builder("fullname").value(email).build()).
                plainAttr(new Attr.Builder("firstname").value(email).build()).
                plainAttr(new Attr.Builder("surname").value("surname").build()).
                plainAttr(new Attr.Builder("ctype").value("a type").build()).
                plainAttr(new Attr.Builder("userId").value(email).build()).
                plainAttr(new Attr.Builder("email").value(email).build()).
                plainAttr(new Attr.Builder("loginDate").
                        value(DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(new Date())).build()).
                build();
        Response response = userService.create(createReq);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatusInfo().getStatusCode());

        PagedResult<UserTO> users = userService.search(new AnyQuery.Builder().build());
        assertNotNull(users);
        assertEquals(6, users.getTotalCount());
    }

    @Test
    public void registerNewDomain() {
        // 1. register new domain through REST service
        restTemplate.put(baseDomainsUrl.toString(), new DomainCR.Builder("Three").
                jdbcDriver("org.h2.Driver").
                jdbcURL("jdbc:h2:mem:syncopethreedb;DB_CLOSE_DELAY=-1").
                dbUsername("sa").
                dbPassword(StringUtils.EMPTY).
                databasePlatform(H2Dictionary.class).
                adminPassword("Password123").
                build());

        // 2. check content loaded
        UserService threeUserService = new SyncopeClientFactoryBean().setAddress(ADDRESS).
                setDomain("Three").
                create(ADMIN_UNAME, "Password123").
                getService(UserService.class);

        PagedResult<UserTO> users = threeUserService.search(new AnyQuery.Builder().build());
        assertNotNull(users);
        assertEquals(1, users.getTotalCount());
        assertEquals("morricone", users.getResult().get(0).getUsername());

        // 3. Insert an user
        UserCR createReq = new UserCR.Builder(SyncopeConstants.ROOT_REALM, "monteverdi").
                password("password123").
                build();
        Response response = threeUserService.create(createReq);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatusInfo().getStatusCode());

        // 4. Check user has been successfully created
        users = threeUserService.search(new AnyQuery.Builder().build());
        assertNotNull(users);
        assertEquals(2, users.getTotalCount());
    }
}
