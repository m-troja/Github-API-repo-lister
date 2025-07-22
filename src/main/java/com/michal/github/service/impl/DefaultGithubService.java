package com.michal.github.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.michal.github.dto.GithubRepoDto;
import com.michal.github.dto.cnv.RepoCnv;
import com.michal.github.entity.GithubBranch;
import com.michal.github.entity.GithubRepo;
import com.michal.github.entity.StatusResponse;
import com.michal.github.service.GithubService;

@Service
public class DefaultGithubService implements GithubService {

    @Autowired
    private RepoCnv repoCnv;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CloseableHttpClient httpClient;

    @Value("${github.token}")
    private String githubToken;

    @Value("${github.api.url}")
    private String githubBaseApiUrl;

    @Override
    public String getUserReposWithBranches(String username) throws IOException {
       
    	ReposResult result = fetchUserRepos(username);

        if (result.statusCode() == HttpStatus.SC_NOT_FOUND) {
            return objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(new StatusResponse(404, "User " + username + " was not found"));
        }

        List<GithubRepo> notForked = extractNotForkedRepos(result.responseBody());
        List<GithubRepo> reposWithBranchesAssigned = assignBranchesToRepos(notForked);
        List<GithubRepoDto> repoDtos = repoCnv.convertReposToRepoDtos(reposWithBranchesAssigned);

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(repoDtos);
    }

    private ReposResult fetchUserRepos(String username) throws IOException {
        String url = githubBaseApiUrl + "/users/" + username + "/repos";
        HttpGet request = new HttpGet(url);
        request.addHeader("Accept", "application/json");
        if (githubToken != null) {
            request.addHeader("Authorization", "Bearer " + githubToken);
        }

        HttpResponse response = httpClient.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        return new ReposResult(response.getStatusLine().getStatusCode(), responseBody);
    }

    private List<GithubRepo> extractNotForkedRepos(String responseBody) throws JsonProcessingException {
        List<GithubRepo> repos = objectMapper.readValue(responseBody, new TypeReference<>() {});
        return repos.stream()
                .filter(repo -> !repo.isFork())
                .collect(Collectors.toList());
    }

    private List<GithubRepo> assignBranchesToRepos(List<GithubRepo> repos) throws IOException {
        for (GithubRepo repo : repos) {
            repo.setBranches(fetchBranches(repo));
        }
        return repos;
    }

    private List<GithubBranch> fetchBranches(GithubRepo repo) throws IOException {
        String url = githubBaseApiUrl + "/repos/" + repo.getOwner().getLogin() + "/" + repo.getName() + "/branches";
        HttpGet request = new HttpGet(url);
        request.addHeader("Accept", "application/json");
        if (githubToken != null) {
            request.addHeader("Authorization", "Bearer " + githubToken);
        }

        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        if (entity == null) return List.of();
        String body = EntityUtils.toString(entity);

        return objectMapper.readValue(body, new TypeReference<>() {});
    }

    private record ReposResult(int statusCode, String responseBody) {}
}
