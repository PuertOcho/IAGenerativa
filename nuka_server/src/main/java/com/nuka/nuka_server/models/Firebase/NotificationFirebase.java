package com.nuka.nuka_server.models.Firebase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationFirebase {
    
	@JsonProperty("title")
	private String title;
    
	@JsonProperty("body")
	private String body;
	
	@JsonProperty("image")
	private String image;

	public NotificationFirebase(String title, String body, String image) {
		super();
		this.title = title;
		this.body = body;
		this.image = image;
	}

	public NotificationFirebase() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	
	
	
	
	
}
