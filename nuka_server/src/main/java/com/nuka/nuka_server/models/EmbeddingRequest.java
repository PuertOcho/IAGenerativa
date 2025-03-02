package com.nuka.nuka_server.models;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

 
 





public class EmbeddingRequest implements Serializable {

	@JsonProperty("model")
	private String model;

	@JsonProperty("input")
	private List<String> input;

	public EmbeddingRequest(String model, List<String> input) {
		this.model = model;
		this.input = input;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public List<String> getInput() {
		return input;
	}

	public void setInput(List<String> input) {
		this.input = input;
	}

}
