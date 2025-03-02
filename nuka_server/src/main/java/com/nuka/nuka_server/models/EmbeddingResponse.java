package com.nuka.nuka_server.models;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;



@SuppressWarnings("serial")
public class EmbeddingResponse implements Serializable {

	@JsonProperty("data")
	private List<Embedding> data;

	@JsonProperty("model")
	private String model;

	@JsonProperty("object")
	private String object;

	@JsonProperty("usage")
	private Usage usage;

	public EmbeddingResponse(List<Embedding> data, String model, String object, Usage usage) {
		super();
		this.data = data;
		this.model = model;
		this.object = object;
		this.usage = usage;
	}

	public EmbeddingResponse() {
		super();
	}

	public List<Embedding> getData() {
		return data;
	}

	public void setData(List<Embedding> data) {
		this.data = data;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public Usage getUsage() {
		return usage;
	}

	public void setUsage(Usage usage) {
		this.usage = usage;
	}

	
	
}