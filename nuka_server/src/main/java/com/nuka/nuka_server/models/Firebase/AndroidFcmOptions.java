package com.nuka.nuka_server.models.Firebase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

 
 


@JsonIgnoreProperties(ignoreUnknown = true)
	
	
	
	public class AndroidFcmOptions {
	    
		@JsonProperty("analytics_label")
		private String analytics_label;

	}