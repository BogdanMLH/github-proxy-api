package com.atipera.githubproxyapi;

import com.atipera.githubproxyapi.model.GitHubBranch;
import com.atipera.githubproxyapi.model.GitHubRepo;
import com.atipera.githubproxyapi.model.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class GithubClient {

    private final RestClient restClient;

    GithubClient(@Value("${github.api.url}") String githubApiUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(githubApiUrl)
                .build();
    }

    List<GitHubRepo> getRepositories(String username) {
        try {
            return restClient.get()
                    .uri("/users/{username}/repos", username)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<GitHubRepo>>() {
                    });
        } catch (HttpClientErrorException.NotFound e) {
            throw new UserNotFoundException("User %s not found".formatted(username));
        }
    }

    List<GitHubBranch> getBranches(String username, String repoName){
        return restClient.get()
                .uri("/repos/{username}/{repo}/branches", username, repoName)
                .retrieve()
                .body(new ParameterizedTypeReference<List<GitHubBranch>>() {
                });
    }
}
