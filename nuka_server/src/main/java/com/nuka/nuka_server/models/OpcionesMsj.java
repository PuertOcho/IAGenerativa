package com.nuka.nuka_server.models;

import java.util.List;


import com.fasterxml.jackson.annotation.JsonInclude;



@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpcionesMsj {
	public String etiqueta;
	public List<OpcionMsj> opciones;
	public OpcionesMsj(String etiqueta, List<OpcionMsj> opciones) {
		super();
		this.etiqueta = etiqueta;
		this.opciones = opciones;
	}
	public OpcionesMsj() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	public List<OpcionMsj> getOpciones() {
		return opciones;
	}
	public void setOpciones(List<OpcionMsj> opciones) {
		this.opciones = opciones;
	}
	
	
	
	
}
