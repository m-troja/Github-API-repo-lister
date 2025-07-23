package com.michal.github.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.michal.github.entity.StatusResponse;
import com.michal.github.exception.UserNotFoundException;
import com.michal.github.service.GithubService;

@RequestMapping("/v1")
@RestController
public class RepositoriesRestController {
	
	@Autowired
	private GithubService githubService;
	
	@GetMapping("/repos")
	public ResponseEntity<?> getUserRepos(@RequestParam String login) throws IOException {
	    try {
	        String json = githubService.getUserReposWithBranches(login);
	        return ResponseEntity.ok()
	                .contentType(MediaType.APPLICATION_JSON)
	                .body(json);
	    } catch (UserNotFoundException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(new StatusResponse(404, ex.getMessage()));
	    }
	}

}
