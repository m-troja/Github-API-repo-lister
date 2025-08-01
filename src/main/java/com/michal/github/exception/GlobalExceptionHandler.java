package com.michal.github.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.michal.github.exception.ErrorMessage;
import com.michal.github.exception.UserNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler  {
	

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleUserNotFound(UserNotFoundException ex) {
        
    	ErrorMessage error = new ErrorMessage(404, ex.getMessage());
        
    	 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);    }
	
}
