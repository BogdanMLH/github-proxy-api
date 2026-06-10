# GitHub Proxy API

A simple proxy REST API that wraps the [GitHub REST API (v3)](https://developer.github.com/v3) to list a user's non-fork repositories together with their branches and the latest commit SHA for each branch.

## Tech Stack

- **Java 25**
- **Spring Boot 4**
- **Gradle (Kotlin DSL)**
- **WireMock** (for integration tests)

## Architecture

The application follows a simple `Controller -> Service -> Client` architecture:

- **ApiController** – exposes the REST endpoint
- **ApiService** – business logic (filtering forks, mapping to DTOs)
- **GithubClient** – communicates with the GitHub API
- **GlobalExceptionHandler** – handles errors and maps them to a unified error response format

## API Endpoints

### Get non-fork repositories of a user

```
GET /api/{username}/repos
```

**Description:**
Returns a list of repositories owned by the given GitHub user, excluding forks. For each repository, all branches are returned along with the SHA of their latest commit.

**Example request:**

```
GET /api/BogdanMLH/repos
```

**Example response (200 OK):**

```json
[
  {
    "repositoryName": "Hello-World",
    "ownerLogin": "octocat",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "7fd1a60b01f91b314f59955a4e4d4e80d8edf11d"
      },
      {
        "name": "develop",
        "lastCommitSha": "c3d0be41ecbe669545ee3e94d31ed9a4bc91ee3c"
      }
    ]
  }
]
```

### Error response

If the requested GitHub user does not exist, the API responds with `404 Not Found`:

**Example response (404 Not Found):**

```json
{
  "status": 404,
  "message": "User octocat123456789 not found"
}
```

## Running the Application

### Prerequisites

- JDK 25
- Internet connection (the application calls the public GitHub API)

### Run

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`.

### Example usage

```bash
curl http://localhost:8080/api/BogdanMLH/repos
```

## Running Tests

The project contains integration tests only. Instead of mocking, a [WireMock](https://wiremock.org/) server is started during the tests to emulate the GitHub API, so the full request/response flow is verified end-to-end.

```bash
./gradlew test
```

## Configuration

The base URL of the GitHub API is configurable via `application.properties`:

```properties
github.api.url=https://api.github.com
```

This allows the integration tests to point the client to the local WireMock server instead of the real GitHub API.