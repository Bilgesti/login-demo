package com.example.login;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.security.user.name=user",
        "spring.security.user.password=secret"

})
public class ControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void welcomeEndPointTest(){


        ResponseEntity<String> response = testRestTemplate

                .withBasicAuth("user","secret")
                .getForEntity("/", String.class );



        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Welcome to my site!");

    }
}
