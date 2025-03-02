package com.nuka.nuka_server.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class GeneratedImage {
    
    @JsonProperty("revised_prompt")
    private String revised_prompt;
    
    @JsonProperty("url")
    private String url;
    
    @JsonProperty("b64_json")
    private String b64_json;
    
}
