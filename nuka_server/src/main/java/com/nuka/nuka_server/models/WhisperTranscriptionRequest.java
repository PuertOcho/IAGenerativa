package com.nuka.nuka_server.models;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonInclude;

 
 


@JsonInclude(JsonInclude.Include.NON_NULL)



public class WhisperTranscriptionRequest implements Serializable {

	public String model;
	public MultipartFile file;

	public WhisperTranscriptionRequest(String model, MultipartFile file) {
		this.model = model;
		this.file = file;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

}