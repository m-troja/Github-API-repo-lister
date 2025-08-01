package com.michal.github.service;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.michal.github.dto.GithubRepoDto;

public interface GithubService {
	List<GithubRepoDto> getUserReposWithBranches(String username) throws IOException ;


}
