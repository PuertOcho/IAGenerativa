package com.nuka.nuka_server.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
 

import java.io.Serializable;

@SuppressWarnings("serial")

@JsonIgnoreProperties(ignoreUnknown = true)
public class Choice implements Serializable {
	
	@JsonProperty("index")
    private Integer index;
	
	@JsonProperty("message")
    private Message message;
    
    @JsonProperty("finish_reason")
    private String finishReason;
    
    @JsonProperty("logprobs")
    private String logprobs;

    		
	public Choice() {
		super();
	}

	public Choice(Integer index, Message message, String finishReason, String logprobs) {
		super();
		this.index = index;
		this.message = message;
		this.finishReason = finishReason;
		this.logprobs = logprobs;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public String getFinishReason() {
		return finishReason;
	}

	public void setFinishReason(String finishReason) {
		this.finishReason = finishReason;
	}

	public String getLogprobs() {
		return logprobs;
	}

	public void setLogprobs(String logprobs) {
		this.logprobs = logprobs;
	}

	@Override
	public String toString() {
		return "Choice [index=" + index + ", message=" + message + ", finishReason=" + finishReason + ", logprobs="
				+ logprobs + "]";
	}
    
    
    
}