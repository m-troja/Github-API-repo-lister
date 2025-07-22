package com.michal.github.service;

import java.io.IOException;

import org.apache.http.ParseException;

public interface GithubService {
	
	String respondToGithubController(String username) throws ParseException, IOException;
}
