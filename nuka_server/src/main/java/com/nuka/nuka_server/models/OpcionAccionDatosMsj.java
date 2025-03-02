package com.nuka.nuka_server.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpcionAccionDatosMsj {

    public String etiqueta;
    public String tipo; //imagen, texto, seleccion, checkbox, toogle, rango, listString
    public List<String> auxiliar;
	public OpcionAccionDatosMsj(String etiqueta, String tipo, List<String> auxiliar) {
		super();
		this.etiqueta = etiqueta;
		this.tipo = tipo;
		this.auxiliar = auxiliar;
	}
	public OpcionAccionDatosMsj() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public List<String> getAuxiliar() {
		return auxiliar;
	}
	public void setAuxiliar(List<String> auxiliar) {
		this.auxiliar = auxiliar;
	}
    
    
    
}
