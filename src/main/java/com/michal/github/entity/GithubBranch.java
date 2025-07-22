package com.michal.github.entity;

public class GithubBranch {

	String name;
	String commitSha;
	
	public GithubBranch() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCommitSha() {
		return commitSha;
	}

	public void setCommitSha(String commitSha) {
		this.commitSha = commitSha;
	}
	
	
}
