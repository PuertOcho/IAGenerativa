package com.nuka.nuka_server.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Blob {

	private byte[] content;
	private String originalFilename;
	private String contentType;

}