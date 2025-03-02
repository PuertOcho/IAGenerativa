package com.nuka.nuka_server.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nuka.nuka_server.Constantes;

 
public class ChatGPTRequest implements Serializable {

	@JsonProperty("model")
	private String model;

	@JsonProperty("messages")
	private List<Message> messages;

	@JsonProperty("temperature")
	private Double temperature;

	public ChatGPTRequest(String model, String prompt) {
		this.model = model;
		this.temperature = 1.;
		this.messages = new ArrayList<>();
		this.messages.add(new Message(Constantes.ROLE_OPENAI_USER, prompt, null));
	}

	public ChatGPTRequest(String model, String prompt, Double temperatura) {
		this.model = model;
		this.temperature = temperatura;
		this.messages = new ArrayList<>();
		this.messages.add(new Message(Constantes.ROLE_OPENAI_USER, prompt, null));
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	@Override
	public String toString() {
		return "ChatGPTRequest [model=" + model + ", messages=" + messages + ", temperature=" + temperature + "]";
	}

	
}
