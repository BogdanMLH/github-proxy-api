package com.atipera.githubproxyapi;

import com.atipera.githubproxyapi.model.BranchDto;
import com.atipera.githubproxyapi.model.RepositoryDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiService {

    private final GithubClient githubClient;

    ApiService(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    List<RepositoryDto> getRepositories(String username) {
        return githubClient.getRepositories(username).stream()
                .filter(gitHubRepo -> !gitHubRepo.fork())
                .map(repo -> new RepositoryDto(repo.name(), repo.owner().login(), getBranches(username, repo.name())))
                .toList();
    }

    private List<BranchDto> getBranches(String username, String repo) {
        return githubClient.getBranches(username, repo).stream()
                .map(branch -> new BranchDto(branch.name(), branch.commit().sha()))
                .toList();
    }
}
