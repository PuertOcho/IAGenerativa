package com.nuka.nuka_server.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

 
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message implements Serializable {
	
	@JsonProperty("role")
	private String role;
	
	@JsonProperty("content")
	private String content;

	@JsonProperty("refusal")
	private String refusal;
	
	public Message(String role, String content, String refusal) {
		super();
		this.role = role;
		this.content = content;
		this.refusal = refusal;
	}

	public Message() {
		super();
	}
	
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRefusal() {
		return refusal;
	}

	public void setRefusal(String refusal) {
		this.refusal = refusal;
	}

	@Override
	public String toString() {
		return "Message [role=" + role + ", content=" + content + ", refusal=" + refusal + "]";
	}


	
	
}
