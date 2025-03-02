package com.nuka.nuka_server.models;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;


public class DataUser {

	@JsonProperty("fechaUltimaActualizacion")
	private Long fechaUltimaActualizacion;

	@JsonProperty("tokenNuka")
	private String tokenNuka;

	@JsonProperty("tokenPorDispositivos")
	private Map<String, String> tokenPorDispositivos;

	public DataUser(Long fechaUltimaActualizacion, String tokenNuka, Map<String, String> tokenPorDispositivos) {
		super();
		this.fechaUltimaActualizacion = fechaUltimaActualizacion;
		this.tokenNuka = tokenNuka;
		this.tokenPorDispositivos = tokenPorDispositivos;
	}
	
	public DataUser() {
		super();
	}

	public Long getFechaUltimaActualizacion() {
		return fechaUltimaActualizacion;
	}

	public void setFechaUltimaActualizacion(Long fechaUltimaActualizacion) {
		this.fechaUltimaActualizacion = fechaUltimaActualizacion;
	}

	public String getTokenNuka() {
		return tokenNuka;
	}

	public void setTokenNuka(String tokenNuka) {
		this.tokenNuka = tokenNuka;
	}

	public Map<String, String> getTokenPorDispositivos() {
		return tokenPorDispositivos;
	}

	public void setTokenPorDispositivos(Map<String, String> tokenPorDispositivos) {
		this.tokenPorDispositivos = tokenPorDispositivos;
	}
	
	
}