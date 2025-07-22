package com.michal.github.entity;

public class GithubOwner {
	
	String login;
	
	public GithubOwner(String login) {
		this.login = login;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Override
	public String toString() {
		return "GithubOwner [login=" + login + "]";
	}

}
