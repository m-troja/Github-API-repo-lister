package com.michal.github;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import com.michal.github.entity.GithubBranch;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GithubApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired 
    private TestRestTemplate restTemplate; // http client

    @Test
    void shouldReturnMainAndTestBranch1ForUser_m_troja() {
    	
        String username = "m-troja";
        String url = "http://localhost:" + port + "/v1/repositories?username=" + username;
        String repoNamePlaceholder = "repositoryName";
        String branchNamePlaceholder = "branchName";
        String repoToTest = "Github-API-repo-lister";
        String nameOfFirstBranch = "main";
        String nameOfSecondBranch = "test-branch-1";
        
        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class); // send GET and return response entity
        List<Map<String, Object>> repos = response.getBody(); // extract body of reponse - map of key and value 

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); // check status 200
        assertThat(repos).isNotEmpty(); 
        assertThat(repos).hasSize(30); // check 30 repos should be returned
        System.out.println();
        
        // list to store branches of m-troja's "Github-API-repo-lister" repo
        List<GithubBranch> branches = repos.stream()
          	    .filter(repo -> repoToTest.equals(repo.get(repoNamePlaceholder))) // filter repo by name
        	    .flatMap(repo -> ((List<Map<String, Object>>) repo.get("branches")).stream()) // convert <String, Object> map to Stream<Map<String, Object>>
        	    .map(branch -> {                                                              // convert branch map to GithubBranch object
        	    	String name = (String) branch.get(branchNamePlaceholder); 
        	    	return new GithubBranch(name); 
        	    })
        	    .collect(Collectors.toList());

        assertThat(branches).hasSize(2);
        
        // Check if one of branches in a list contains branch.name equals "main", "test-branch-1"
        int mainFlag = 0, testBranchFlag = 0;
        for (GithubBranch branch : branches) {
        	if (branch.getName().equals(nameOfFirstBranch)) {
        		mainFlag = 1;
        	}
        	if (branch.getName().equals(nameOfSecondBranch)) {
        		testBranchFlag = 1;
        	} 
        }

        assertThat(mainFlag).isEqualTo(1);
        assertThat(testBranchFlag).isEqualTo(1);
    }
}
