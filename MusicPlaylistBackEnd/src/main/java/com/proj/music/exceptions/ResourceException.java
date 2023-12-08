package com.proj.music.exceptions;

public class ResourceException {

	private String error;
	private String message;

	public ResourceException(String error, String message) {
		super();
		this.error = error;
		this.message = message;
	}

}
