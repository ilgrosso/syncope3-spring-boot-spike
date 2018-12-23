package org.apache.syncope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.syncope.persistence.config.XMLContentLoader;
import org.apache.syncope.rest.config.DomainConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { SyncopeCore.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class SyncopeCoreITCase {

    private URL baseUsersUrl;

    private URL baseDomainsUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    public void setUp() throws Exception {
        this.baseUsersUrl = new URL("http://localhost:" + port + "/syncope/users");
        this.baseDomainsUrl = new URL("http://localhost:" + port + "/syncope/domains");
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
        // 2. Check that new beans are registered in the ApplicationContext
        assertTrue(applicationContext.containsBean("localThreeDataSource"));
        assertTrue(applicationContext.containsBean("ThreeDataSource"));
        assertTrue(applicationContext.containsBean("ThreeEntityManagerFactory"));
        assertTrue(applicationContext.containsBean("threeResourceDatabasePopulator"));
        assertTrue(applicationContext.containsBean("threeDataSourceInitializer"));
        assertTrue(applicationContext.containsBean("ThreeTransactionManager"));
        assertTrue(applicationContext.containsBean("ThreeContentXML"));
        // 3. Initialize the just created domain like done in LogicInitializer
        XMLContentLoader loader = applicationContext.getBean(
                org.apache.syncope.persistence.config.XMLContentLoader.class);
        loader.getDomains().clear();
        loader.getDomains().put("Three", applicationContext.getBean("ThreeDataSource", DataSource.class));
        loader.load();
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
