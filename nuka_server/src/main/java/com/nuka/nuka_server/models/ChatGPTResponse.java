package com.nuka.nuka_server.models;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

 

@JsonIgnoreProperties(ignoreUnknown = true)

public class ChatGPTResponse implements Serializable {

	@JsonProperty("id")
	private String id;

	@JsonProperty("object")
	private String object;

	@JsonProperty("model")
	private String model;

	@JsonProperty("created")
	private long created;

	@JsonProperty("choices")
	private List<Choice> choices;

	@JsonProperty("usage")
	private Usage usage;
	
	@JsonProperty("system_fingerprint")
	private String system_fingerprint;

	public ChatGPTResponse(String id, String object, String model, long created, List<Choice> choices, Usage usage, String system_fingerprint) {
		super();
		this.id = id;
		this.object = object;
		this.model = model;
		this.created = created;
		this.choices = choices;
		this.usage = usage;
		this.system_fingerprint = system_fingerprint;
	}

	public ChatGPTResponse() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public List<Choice> getChoices() {
		return choices;
	}

	public void setChoices(List<Choice> choices) {
		this.choices = choices;
	}

	public Usage getUsage() {
		return usage;
	}

	public void setUsage(Usage usage) {
		this.usage = usage;
	}

	public String getSystem_fingerprint() {
		return system_fingerprint;
	}

	public void setSystem_fingerprint(String system_fingerprint) {
		this.system_fingerprint = system_fingerprint;
	}

	@Override
	public String toString() {
		return "ChatGPTResponse [id=" + id + ", object=" + object + ", model=" + model + ", created=" + created
				+ ", choices=" + choices + ", usage=" + usage + ", system_fingerprint=" + system_fingerprint + "]";
	}

	
	
}