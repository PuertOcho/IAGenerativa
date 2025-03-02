package com.nuka.nuka_server.models.Firebase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

 
 
 

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)




public class WebpushConfigFirebase {

    @JsonProperty("headers")
    private Map<String, String> headers;
    
    @JsonProperty("data")
    private Map<String, String> data;
    
    @JsonProperty("notification")
    private ObjectNode notification;
    
    @JsonProperty("fcm_options")
    private WebpushFcmOptions fcmOptions;


}
