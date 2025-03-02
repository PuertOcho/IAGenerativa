package com.nuka.nuka_server.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;


@SuppressWarnings("serial")
public class WhisperTranscriptionResponse implements Serializable {

	@JsonProperty("text")
	public String text;

	public WhisperTranscriptionResponse(String text) {
		super();
		this.text = text;
	}

	public WhisperTranscriptionResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	
	
}