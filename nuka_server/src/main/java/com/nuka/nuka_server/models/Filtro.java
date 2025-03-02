package com.nuka.nuka_server.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Filtro {
    
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("datos")
	private List<String> datos;

	@JsonProperty("valores")
	private List<String> valores;
	
	
	public Filtro() {
		super();
	}

	public Filtro(String id, List<String> datos, List<String> valores) {
	    super();
	    this.id = id;
	    this.datos = datos;
	    this.valores = valores;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getDatos() {
		return datos;
	}

	public void setDatos(List<String> datos) {
		this.datos = datos;
	}

	public List<String> getValores() {
		return valores;
	}

	public void setValores(List<String> valores) {
		this.valores = valores;
	}

	
	
}
