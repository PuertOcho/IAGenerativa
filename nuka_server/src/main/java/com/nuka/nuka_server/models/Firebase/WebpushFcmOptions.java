package com.nuka.nuka_server.models.Firebase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

 
 


@JsonIgnoreProperties(ignoreUnknown = true)



public class WebpushFcmOptions {
    // Define the fields for FCM options for webpush
    // For example:
    @JsonProperty("link")
    private String link;

    // Add other fields here
}