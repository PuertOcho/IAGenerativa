package com.nuka.nuka_server.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
 

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Usage implements Serializable {

    @JsonProperty("prompt_tokens")
    private String promptTokens;

    @JsonProperty("completion_tokens")
    private String completionTokens;

    @JsonProperty("total_tokens")
    private String totalTokens;
}
