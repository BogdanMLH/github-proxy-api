package com.atipera.githubproxyapi.model;

public record GitHubRepo (String name, GitHubOwner owner, boolean fork) {}
