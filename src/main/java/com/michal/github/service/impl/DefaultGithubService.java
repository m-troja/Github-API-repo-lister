package com.michal.github.service.impl;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.michal.github.entity.GithubResponse;
import com.michal.github.dto.GithubRepoDto;
import com.michal.github.dto.cnv.RepoCnv;
import com.michal.github.entity.GithubBranch;
import com.michal.github.entity.GithubOwner;
import com.michal.github.entity.GithubRepo;
import com.michal.github.service.GithubService;

@Service
public class DefaultGithubService implements GithubService {
	
	@Autowired
	RepoCnv cnv;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	CloseableHttpClient httpClient;

	@Value("${github.token}")
	private String githubToken;
	
	@Value("${github.api.url}")
	private String githubBaseApiUrl;
	
	
	@Override
	public String respondToGithubController(String username) throws ParseException, IOException {
		
		HttpGet getRequest = prepareGetRequestToGetReposForUser(username);
		String responseBody = getResponseBodyOfReposFromGithub(getRequest);
		List<GithubRepo> notForkedRepos= extractListOfNotForkedRepos(responseBody);
		List<GithubRepo> reposWithBranchesAssigned = assignBranchesToRepo(notForkedRepos);
		List<GithubRepoDto> repoDtos = cnv.convertReposToRepoDtos(reposWithBranchesAssigned);
		
		return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(repoDtos);
	
	}
	
	/* Get entity of first response - repos */
	private List<GithubRepo> assignBranchesToRepo(List<GithubRepo> repos) throws ParseException, IOException
	{
		List<GithubRepo> reposWithBranchesAssigned = new ArrayList<>();
		
		for (GithubRepo repo : repos) {
			repo.setBranches(getBranchesForRepo(repo));
			reposWithBranchesAssigned.add(repo);
		}
		
		return reposWithBranchesAssigned;
	}
	
	/* Get list of branches for single repo */
	private List<GithubBranch> getBranchesForRepo(GithubRepo repo) throws ParseException, IOException
	{
		HttpGet getRequest = prepareGetRequestToGetBranchesForRepo(repo);	
	
		HttpResponse httpResponse = httpClient.execute(getRequest);
		
		HttpEntity entity = httpResponse.getEntity();
		
		if ( entity != null)
		{
			 String	responseBody = EntityUtils.toString(entity);
			 
			 List<GithubBranch> branches = objectMapper.readValue(responseBody,  new TypeReference<List<GithubBranch>>() {});
			
			return branches;
		}
		
		return null;
	}	
		
	
	private HttpGet prepareGetRequestToGetBranchesForRepo(GithubRepo repo)
	{
		String urlToGet = githubBaseApiUrl + "/repos/" + repo.getOwner().getLogin() + "/" + repo.getName() + "/branches";
		HttpGet getRequest = new HttpGet(urlToGet);
		getRequest.addHeader("Accept", "application/json");
		
		if ( githubToken != null) {
			getRequest.addHeader("Authorization", "Bearer " + githubToken);
		}

		return getRequest;
	}
	
	private HttpGet prepareGetRequestToGetReposForUser(String username)
	{
		String urlToGet = githubBaseApiUrl + "/users/" + username + "/repos";

		HttpGet getRequest = new HttpGet(urlToGet);
		getRequest.addHeader("Accept", "application/json");
		
		if ( githubToken != null) {
			getRequest.addHeader("Authorization", "Bearer " + githubToken);
		}

		return getRequest;
	}
	
	/* Get entity of first response - repos */
	private String getResponseBodyOfReposFromGithub(HttpGet getRequest) throws ParseException, IOException
	{
		HttpResponse httpResponse = httpClient.execute(getRequest);
		
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		
		HttpEntity entity = httpResponse.getEntity();
		
		if ( entity != null)
		{
			 String	responseBody = EntityUtils.toString(entity);
			
			if ( statusCode != HttpStatus.SC_OK ) { // If status != 200, return responseBody
				return responseBody;
			}
			
			return responseBody;
			
		}	
		return "error in getResponseBodyFromGithub";
	}

	/* Extract list of not forked repos from first Github response */
	private List<GithubRepo> extractListOfNotForkedRepos(String responseBody) throws JsonMappingException, JsonProcessingException
	{
		// extract all repos into a list
		List<GithubRepo> repos = objectMapper.readValue(responseBody, new TypeReference<List<GithubRepo>>() {});
        
		// keep only not-forked repos
        List<GithubRepo> notForkedRepos = repos.stream().filter(repo -> !repo.isFork()).collect(Collectors.toList());
  
        
        return notForkedRepos;
	}
}
