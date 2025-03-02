package com.nuka.nuka_server.models;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContenidoMsj {

	@Id
	@JsonProperty("id")
	public String id;

	@JsonProperty("tipo")
	public String tipo;

	public ContenidoMsj(String id, String tipo) {
		super();
		this.id = id;
		this.tipo = tipo;
	}

	public ContenidoMsj() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	
	
}
