package com.nuka.nuka_server.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;

 
 
 
 




@Document(collection = "AccionNuka")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccionNuka {

	@Id
	public String accionId;
	public Double[] embedding;
	public String texto;
	public Boolean necesitaAudio;
	public String grupo;
	
	public AccionNuka(String accionId, Double[] embedding, String texto, Boolean necesitaAudio, String grupo) {
		super();
		this.accionId = accionId;
		this.embedding = embedding;
		this.texto = texto;
		this.necesitaAudio = necesitaAudio;
		this.grupo = grupo;
	}
	public AccionNuka() {
		super();
	}
	public String getAccionId() {
		return accionId;
	}
	public void setAccionId(String accionId) {
		this.accionId = accionId;
	}
	public Double[] getEmbedding() {
		return embedding;
	}
	public void setEmbedding(Double[] embedding) {
		this.embedding = embedding;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	public Boolean getNecesitaAudio() {
		return necesitaAudio;
	}
	public void setNecesitaAudio(Boolean necesitaAudio) {
		this.necesitaAudio = necesitaAudio;
	}
	public String getGrupo() {
		return grupo;
	}
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	
	
}
