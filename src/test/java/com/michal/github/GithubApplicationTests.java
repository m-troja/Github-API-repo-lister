package com.michal.github;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.michal.github.dto.BranchDto;
import com.michal.github.dto.GithubRepoDto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

/*
 * * Test checks if:
 * StatusCode of response == 200,
 * returned response of repos is not empty,
 * response returns expected qty of repos,
 * specific repo exists,
 * specific repo contains expected qty of branches,
 * name of two specific branches is as expected.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GithubApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired 
    private TestRestTemplate restTemplate; // http client

    @Test
    void checkBranchesInSpecificRepo() {
    	
    	/* Config */
        final String username = "m-troja";
        final String repoToTest = "Github-API-repo-lister";
        final String nameOfFirstBranch = "main";
        final String nameOfSecondBranch = "test-branch-1";
        final String url = "http://localhost:" + port + "/v1/repos?login=" + username;

        // build and execute GET call, save response 
        ResponseEntity<?> response = restTemplate.exchange(
        	    url,
        	    HttpMethod.GET,
        	    null,
        	    Object.class
        	);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); // check status 200

     // If status == 200, convert responseBody into list of GithubRepoDtos
        ObjectMapper mapper = new ObjectMapper();
        List<GithubRepoDto> repos = mapper.convertValue(response.getBody(), new TypeReference<List<GithubRepoDto>>() {
		}); 
        
        assertThat(repos).isNotEmpty(); 
        assertThat(repos).hasSize(30); // check 30 repos should be returned
        
        // Search for repoToTest
        GithubRepoDto repo = repos.stream()
        		.filter(r -> r.getRepositoryName().equals(repoToTest))
        		.findFirst()
        		.orElse(null);        
        
        assertThat(repo.getRepositoryName()).isEqualTo(repoToTest);
        
        List<BranchDto> branches = repo.getBranches();
        assertThat(branches).hasSize(2);
        
        // Check if one of branches in a list contains branchName equals "main", "test-branch-1"
        boolean hasMainBranch = false, hasTestBranch = false;
        for (BranchDto branch : branches) {
        	if (branch.getBranchName().equals(nameOfFirstBranch)) {
        		hasMainBranch = true;
        	}
        	if (branch.getBranchName().equals(nameOfSecondBranch)) {
          		hasTestBranch = true;
        	} 
        }

        assertThat(hasMainBranch).isTrue();
        assertThat(hasTestBranch).isTrue();
    }
}
