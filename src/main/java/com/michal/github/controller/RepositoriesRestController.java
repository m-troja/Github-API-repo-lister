package com.michal.github.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.michal.github.dto.GithubRepoDto;
import com.michal.github.service.GithubService;

@RequestMapping("/v1")
@RestController
public class RepositoriesRestController {
	
	@Autowired
	private GithubService githubService;
	
	@GetMapping("/repos")
	public  List<GithubRepoDto> getUserRepos(@RequestParam String login) throws IOException {
	 
		return githubService.getUserReposWithBranches(login);
	}
}
