package com.michal.github.exception;

public class UserNotFoundException extends RuntimeException {
	
	public UserNotFoundException(String message) {
		super(message); //  parse message to Throwable
	}
	
	
}
