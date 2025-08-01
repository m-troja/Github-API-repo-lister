# Github-API-repo-lister

A Spring Boot application that fetches all public repositories for a given GitHub username and lists their branches (excluding forked repositories).  
It consumes the GitHub REST API and returns the result as JSON.
Please store github.token into /src/main/resources/secrets.properties to increase limit of requests from 60 to 5000 request per hour.

## Features

- Fetch public repositories for any GitHub user
- Filter out forked repositories
- Fetch all branches for each repository
- Return customized data in structured JSON format:
  - respository name
  - owner login
  - branches:
    - branch name
    - last commit sha

## Technologies

- Java 21
- Spring Boot 3.5
- GitHub REST API
- WireMock
- JSONassert

## API Endpoint

`GET /v1/repos?login=${GitHubLogin}`

**Parameters:**

- `GitHubLogin` - GitHub login (required)

**Example request:**

```http
GET http://localhost:8080/v1/repos?login=m-troja
```

## Response:

`200 OK` - Successful fetch of repositories

`404 Not Found` - If GitHub user doesn't exist

Example Succesfull Response:

```json
[
  {
    "repositoryName": "my-repo",
    "ownerLogin": "m-troja",
    "branches": [
      {
        "branchName": "main",
        "lastCommitSha": "a1b2c3d4..."
      },
      {
        "branchName": "dev",
        "lastCommitSha": "e5f6g7h8..."
      }
    ]
  }
]
```

Example Error Response:

```json
{
  "statusCode": 404,
  "message": "User m-troja was not found"
}
```
