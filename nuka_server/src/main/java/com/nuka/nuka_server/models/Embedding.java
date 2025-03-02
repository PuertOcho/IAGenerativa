package com.nuka.nuka_server.models;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Embedding {

	@JsonProperty("embedding")
	private Double[] embedding;

	@JsonProperty("index")
	private Integer index;

	@JsonProperty("object")
	private String object;

	public Embedding(Double[] embedding, Integer index, String object) {
		super();
		this.embedding = embedding;
		this.index = index;
		this.object = object;
	}

	public Embedding() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Double[] getEmbedding() {
		return embedding;
	}

	public void setEmbedding(Double[] embedding) {
		this.embedding = embedding;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}



}
