package com.pos.retailer.component;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseWrapper<T> {
	private String message;
	private HttpStatus status;
	private T data;

	public ResponseWrapper() {
		super();
	}

	public ResponseWrapper(String message, HttpStatus status, T data) {
		this.message = message;
		this.status = status;
		this.data = data;
	}
	
	public ResponseWrapper(HttpStatus status, T data) {
		this(null, status, data);
	}

	public ResponseWrapper(String message, HttpStatus status) {
		this(message, status, null);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@SuppressWarnings("unchecked")
	public ResponseEntity<T> sendResponse() {
		return new ResponseEntity<T>((T) this, status);

	}

}
