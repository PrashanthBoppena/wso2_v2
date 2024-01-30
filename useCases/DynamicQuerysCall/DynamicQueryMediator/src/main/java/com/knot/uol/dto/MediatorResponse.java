package com.knot.uol.dto;

import com.google.gson.JsonArray;

public class MediatorResponse {
	private String queryName;
	private String statusCode;
	private String message;
	private JsonArray response;
	private ErrorResponse errorResponse;
	public String getQueryName() {
		return queryName;
	}
	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public JsonArray getResponse() {
		return response;
	}
	public void setResponse(JsonArray response) {
		this.response = response;
	}
	public ErrorResponse getErrorResponse() {
		return errorResponse;
	}
	public void setErrorResponse(ErrorResponse errorResponse) {
		this.errorResponse = errorResponse;
	}
}
