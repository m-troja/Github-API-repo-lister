package com.michal.github.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true) // deserialize only known fields
public class GithubRepo {

	private String name;
	private GithubOwner owner;
	
	@JsonIgnore
	private boolean fork;

	public GithubRepo(String name, GithubOwner owner, boolean fork) {
		super();
		this.name = name;
		this.owner = owner;
		this.fork = fork;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GithubOwner getOwner() {
		return owner;
	}

	public void setOwner(GithubOwner owner) {
		this.owner = owner;
	}

	public boolean isFork() {
		return fork;
	}

	public void setFork(boolean fork) {
		this.fork = fork;
	}


	@Override
	public String toString() {
		return "GithubRepo [name=" + name + ", owner=" + owner + ", fork=" + fork + "]";
	}

	
}
