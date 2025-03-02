package com.nuka.nuka_server.models;

public class LoginResponse {

	public User usuario;
	public String token;

    public LoginResponse(User usuario, String token) {
	super();
        this.usuario = usuario;
        this.token = token;
    }
    
    public LoginResponse() {
	super();
    }
    
}