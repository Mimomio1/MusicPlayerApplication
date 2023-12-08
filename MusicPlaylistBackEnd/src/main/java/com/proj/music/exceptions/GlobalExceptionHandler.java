package com.proj.music.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ResourceException> handleResourceNotFoundException(ResourceNotFoundException e) {
		ResourceException resourceException = new ResourceException("Not Found", e.getMessage());
		return new ResponseEntity<>(resourceException, HttpStatus.NOT_FOUND);
	}
}
