package com.atipera.githubproxyapi;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.atipera.githubproxyapi.model.RepositoryDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiControllerIntegrationTest {

    static WireMockServer wireMockServer = new WireMockServer(0);

    static {
        wireMockServer.start();
    }

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @AfterAll
    static void teardown() {
        wireMockServer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("github.api.url", () -> wireMockServer.baseUrl());
    }

    @Test
    void shouldReturnRepositoriesExcludingForks() {
        wireMockServer.stubFor(get(urlEqualTo("/users/testuser/repos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                  {"name": "repo1", "fork": false, "owner": {"login": "testuser"}},
                                  {"name": "repo2", "fork": true, "owner": {"login": "testuser"}}
                                ]
                                """)));

        wireMockServer.stubFor(get(urlEqualTo("/repos/testuser/repo1/branches"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                  {"name": "main", "commit": {"sha": "abc123"}}
                                ]
                                """)));

        ResponseEntity<RepositoryDto[]> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/testuser/repos",
                RepositoryDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].name()).isEqualTo("repo1");
        assertThat(response.getBody()[0].ownerLogin()).isEqualTo("testuser");
        assertThat(response.getBody()[0].branches()).hasSize(1);
        assertThat(response.getBody()[0].branches().get(0).name()).isEqualTo("main");
        assertThat(response.getBody()[0].branches().get(0).lastCommitedSha()).isEqualTo("abc123");
    }

    @Test
    void shouldReturn404WhenUserNotFound() {
        wireMockServer.stubFor(get(urlEqualTo("/users/unknownuser/repos"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {"message": "Not Found"}
                                """)));

        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/unknownuser/repos",
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("\"status\":404");
        assertThat(response.getBody()).contains("not found");
    }
}