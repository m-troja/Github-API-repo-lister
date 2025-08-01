package com.michal.github.exception;

public class UserNotFoundException extends RuntimeException {
	
	public UserNotFoundException(String username) {
		super("Login " + username + " was not found" ); //  parse message to Throwable
	}
	
	
}
