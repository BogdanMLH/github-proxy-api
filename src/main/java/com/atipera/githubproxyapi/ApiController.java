package com.atipera.githubproxyapi;

import com.atipera.githubproxyapi.model.RepositoryDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final ApiService apiService;

    ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/{username}/repos")
    List<RepositoryDto> getRepos(@PathVariable("username") String username) {
        return apiService.getRepositories(username);
    }
}