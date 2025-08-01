package com.michal.github.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.michal.github.dto.GithubRepoDto;
import com.michal.github.dto.cnv.RepoCnv;
import com.michal.github.entity.GithubBranch;
import com.michal.github.entity.GithubRepo;
import com.michal.github.exception.UserNotFoundException;
import com.michal.github.service.GithubService;

@Service
public class DefaultGithubService implements GithubService {

    @Autowired
    private RepoCnv repoCnv;

    @Autowired
    private ObjectMapper objectMapper;


    private final RestClient githubRestClient;
    
    private record ReposResult(HttpStatus status, String responseBody) {}

    @Override
    public String getUserReposWithBranches(String username) throws IOException {
       
    	ReposResult result = fetchUserRepos(username);
        List<GithubRepo> notForked = extractNotForkedRepos(result.responseBody());
        List<GithubRepo> reposWithBranchesAssigned = assignBranchesToRepos(notForked);
        List<GithubRepoDto> repoDtos = repoCnv.convertReposToRepoDtos(reposWithBranchesAssigned);

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(repoDtos);
    }

    private ReposResult fetchUserRepos(String username)  {
        String uri = "/users/" + username + "/repos";

        try {  
            String body = githubRestClient.get()
        			.uri(uri)
	        		.retrieve() 
	        		.body(String.class); 
            
            return new ReposResult(HttpStatus.OK, body);

        }
        catch(HttpClientErrorException e)
        {
        	if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        		throw new UserNotFoundException(username);
        	}
        	throw new RuntimeException("Github Api error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        }
        
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
        String uri = "/repos/" + repo.getOwner().getLogin() + "/" + repo.getName() + "/branches";
       
        List<GithubBranch> branches = githubRestClient.get()
    			.uri(uri)
        		.retrieve() 
        		.body(new ParameterizedTypeReference<List<GithubBranch>>() {});

        return branches;
    }

	public DefaultGithubService(@Qualifier("githubRestClient") RestClient githubRestClient) {
		this.githubRestClient = githubRestClient;
	}

}
