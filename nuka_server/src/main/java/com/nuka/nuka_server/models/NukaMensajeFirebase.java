package com.nuka.nuka_server.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nuka.nuka_server.models.Firebase.MensajeFirebase;

 
 
 
 




@Document(collection = "NukaMensajeFirebase")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NukaMensajeFirebase {

	@Id
	public String mensajeId;
	public MensajeFirebase mensajeFirebase;

}