package org.apache.syncope;

import java.net.URL;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.syncope.persistence.config.XMLContentLoader;
import org.apache.syncope.rest.config.DomainConfiguration;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApplicationTests {

    private URL baseUsersUrl;

    private URL baseDomainsUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @Before
    public void setUp() throws Exception {
        this.baseUsersUrl = new URL("http://localhost:" + port + "/users");
        this.baseDomainsUrl = new URL("http://localhost:" + port + "/domains");
    }

    @Test
    public void getUsers() throws Exception {
        ResponseEntity<List<String>> response = restTemplate.exchange(baseUsersUrl + "?domain=Master", HttpMethod.GET,
                null, new ParameterizedTypeReference<List<String>>() {
        });
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertThat(response.getBody(), Matchers.hasSize(5));

        response = restTemplate.exchange(baseUsersUrl + "?domain=Two", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<String>>() {
        });
        Assert.assertNotNull(response.getBody());
        Assert.assertThat(response.getBody(), Matchers.hasSize(1));
    }

    @Test
    public void saveUser() throws Exception {
        ResponseEntity<String> response = restTemplate.postForEntity(baseUsersUrl + "/save?domain=Master", "masterUser",
                String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertThat(restTemplate.exchange(baseUsersUrl + "?domain=Master", HttpMethod.GET,
                null, new ParameterizedTypeReference<List<String>>() {
        }).getBody(), Matchers.hasSize(6));
        response = restTemplate.postForEntity(baseUsersUrl + "/save?domain=Two", "twoUser", String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertThat(restTemplate.exchange(baseUsersUrl + "?domain=Two", HttpMethod.GET,
                null, new ParameterizedTypeReference<List<String>>() {
        }).getBody(), Matchers.hasSize(2));
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
        Assert.assertTrue(applicationContext.containsBean("localThreeDataSource"));
        Assert.assertTrue(applicationContext.containsBean("ThreeDataSource"));
        Assert.assertTrue(applicationContext.containsBean("ThreeEntityManagerFactory"));
        Assert.assertTrue(applicationContext.containsBean("threeResourceDatabasePopulator"));
        Assert.assertTrue(applicationContext.containsBean("threeDataSourceInitializer"));
        Assert.assertTrue(applicationContext.containsBean("ThreeTransactionManager"));
        Assert.assertTrue(applicationContext.containsBean("ThreeContentXML"));
        // 3. Initialize the just created domain like done in LogicInitializer
        XMLContentLoader loader = applicationContext.getBean(
                org.apache.syncope.persistence.config.XMLContentLoader.class);
        loader.getDomains().clear();
        loader.getDomains().put("Three", applicationContext.getBean("ThreeDataSource", DataSource.class));
        loader.load();
        // 3. check content loaded
        final List<String> users = restTemplate.exchange(baseUsersUrl + "?domain=Three", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<String>>() {
        }).getBody();

        Assert.assertNotNull(users);
        Assert.assertThat(users, Matchers.hasSize(1));
        Assert.assertTrue(StringUtils.contains(users.get(0), "morricone"));
        // 4. Insert an user
        ResponseEntity<String> response = restTemplate.postForEntity(baseUsersUrl + "/save?domain=Three", "monteverdi",
                String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        // 5. Check user has been successfully created
        Assert.assertThat(restTemplate.exchange(baseUsersUrl + "?domain=Three", HttpMethod.GET,
                null, new ParameterizedTypeReference<List<String>>() {
        }).getBody(), Matchers.hasSize(2));
    }

}
