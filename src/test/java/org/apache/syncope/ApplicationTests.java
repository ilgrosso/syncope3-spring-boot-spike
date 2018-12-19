package org.apache.syncope;

import java.net.URL;
import java.util.List;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApplicationTests {

    private URL baseUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp() throws Exception {
        this.baseUrl = new URL("http://localhost:" + port + "/");
    }

    @Test
    public void getUsers() throws Exception {
        ResponseEntity<List<String>> response = restTemplate.exchange(baseUrl + "users?domain=Master", HttpMethod.GET,
                null, new ParameterizedTypeReference<List<String>>() {
        });
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertThat(response.getBody(), Matchers.hasSize(5));

        response = restTemplate.exchange(baseUrl + "users?domain=Two", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<String>>() {
        });
        Assert.assertNotNull(response.getBody());
        Assert.assertThat(response.getBody(), Matchers.hasSize(1));
    }

    @Test
    public void saveUser() throws Exception {
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "save?domain=Master", "masterUser",
                String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertThat(restTemplate.exchange(baseUrl + "users?domain=Master", HttpMethod.GET,
                null, new ParameterizedTypeReference<List<String>>() {
        }).getBody(), Matchers.hasSize(6));
        response = restTemplate.postForEntity(baseUrl + "save?domain=Two", "twoUser", String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertThat(restTemplate.exchange(baseUrl + "users?domain=Two", HttpMethod.GET,
                null, new ParameterizedTypeReference<List<String>>() {
        }).getBody(), Matchers.hasSize(2));
    }

}
