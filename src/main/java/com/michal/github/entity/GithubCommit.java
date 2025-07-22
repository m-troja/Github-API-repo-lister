package com.michal.github.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class GithubCommit {
	
	String sha;
	
	@JsonIgnoreProperties
	String url;
	
	public GithubCommit(String sha) {
		this.sha = sha;
	}

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

	public GithubCommit() {
		super();
	}

	@Override
	public String toString() {
		return "GithubCommit [sha=" + sha + ", url=" + url + "]";
	}
	
	
}

