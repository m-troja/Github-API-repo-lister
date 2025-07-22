package com.michal.github.controller;

import java.io.IOException;

import org.apache.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.michal.github.service.GithubService;

@RequestMapping("/v1")
@RestController
public class RepositoriesRestController {
	
	@Autowired
	private GithubService githubService;
	
	@RequestMapping("/repositories")
	public String doGet(@RequestParam String username) throws ParseException, IOException
	{
		return githubService.respondToGithubController(username);
	}

}
