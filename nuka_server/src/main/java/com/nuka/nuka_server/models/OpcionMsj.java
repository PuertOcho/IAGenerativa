package com.nuka.nuka_server.models;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

 
 
 
 




@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpcionMsj {
	public String id;
	public String etiqueta;
	public OpcionAccionMsj accion;
	public OpcionMsj(String id, String etiqueta, OpcionAccionMsj accion) {
		super();
		this.id = id;
		this.etiqueta = etiqueta;
		this.accion = accion;
	}
	public OpcionMsj() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	public OpcionAccionMsj getAccion() {
		return accion;
	}
	public void setAccion(OpcionAccionMsj accion) {
		this.accion = accion;
	}
	
	
	
	
}

