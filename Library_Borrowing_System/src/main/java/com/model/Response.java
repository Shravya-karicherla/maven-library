package com.model;
public class Response {
    private boolean status;
    private String responseType;
    private Object response; // Made this generic to accommodate different data types

    public Response(boolean status, String responseType, Object response) {
        this.status = status;
        this.responseType = responseType;
        this.response = response;
    }

    public Response() {
	}

	public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }
}

