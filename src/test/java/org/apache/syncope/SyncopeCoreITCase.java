package org.apache.syncope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.syncope.rest.config.DomainConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
public class SyncopeCoreITCase {

    private URL baseUsersUrl;

    private URL baseDomainsUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @BeforeEach
    public void setUp() throws Exception {
        this.baseUsersUrl = new URL("http://localhost:9080/syncope/users");
        this.baseDomainsUrl = new URL("http://localhost:9080/syncope/domains");
    }

    @Test
    public void getUsers() throws Exception {
        ResponseEntity<List<String>> response = restTemplate.exchange(baseUsersUrl + "?domain=Master", HttpMethod.GET,
                null, new ParameterizedTypeReference<List<String>>() {
        });
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() >= 5);

        response = restTemplate.exchange(baseUsersUrl + "?domain=Two", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<String>>() {
        });
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() >= 1);
    }

    @Test
    public void saveUser() throws Exception {
        ResponseEntity<String> response = restTemplate.postForEntity(baseUsersUrl + "/save?domain=Master", "masterUser",
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(6, restTemplate.exchange(baseUsersUrl + "?domain=Master", HttpMethod.GET,
                null, new ParameterizedTypeReference<List<String>>() {
        }).getBody().size());
        response = restTemplate.postForEntity(baseUsersUrl + "/save?domain=Two", "twoUser", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, restTemplate.exchange(baseUsersUrl + "?domain=Two", HttpMethod.GET,
                null, new ParameterizedTypeReference<List<String>>() {
        }).getBody().size());
    }

    @Test
    public void registerNewDomain() {
        // 1. register new domain through REST service
        restTemplate.put(baseDomainsUrl.toString(), new DomainConfiguration()
                .domainName("three")
                .driverClassName("org.h2.Driver")
                .url("jdbc:h2:mem:syncopethreedb;DB_CLOSE_DELAY=-1")
                .username("sa")
                .password(StringUtils.EMPTY)
                .databasePlatform("org.apache.openjpa.jdbc.sql.H2Dictionary")
                .orm("META-INF/spring-orm.xml")
                .maximumPoolSize("10")
                .minimumIdle("2")
                .auditSql("audit.sql"));

        // 3. check content loaded
        List<String> users = restTemplate.exchange(baseUsersUrl + "?domain=Three", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<String>>() {
        }).getBody();
        assertNotNull(users);
        assertEquals(1, users.size());
        assertTrue(StringUtils.contains(users.get(0), "morricone"));

        // 4. Insert an user
        ResponseEntity<String> response = restTemplate.postForEntity(baseUsersUrl + "/save?domain=Three", "monteverdi",
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // 5. Check user has been successfully created
        assertEquals(2, restTemplate.exchange(baseUsersUrl + "?domain=Three", HttpMethod.GET,
                null, new ParameterizedTypeReference<List<String>>() {
        }).getBody().size());
    }
}
