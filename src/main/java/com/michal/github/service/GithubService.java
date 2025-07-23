package com.michal.github.service;

import java.io.IOException;

public interface GithubService {
	String getUserReposWithBranches(String username) throws IOException;


}
