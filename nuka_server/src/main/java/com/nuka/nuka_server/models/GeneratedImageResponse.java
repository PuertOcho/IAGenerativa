package com.nuka.nuka_server.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class GeneratedImageResponse {
    
    @JsonProperty("created")
    private Long created;
    
    @JsonProperty("data")
    private List<GeneratedImage> data;
}
