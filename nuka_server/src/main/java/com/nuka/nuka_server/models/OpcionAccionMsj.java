package com.nuka.nuka_server.models;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

 
 
 
 




@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpcionAccionMsj {

    public String endpoint;
    public Map<String, String> datos;
    public Boolean necesitaDatos;
    public Map<String, OpcionAccionDatosMsj> datosNecesarios;
	
    public OpcionAccionMsj(String endpoint, Map<String, String> datos, Boolean necesitaDatos,
			Map<String, OpcionAccionDatosMsj> datosNecesarios) {
		super();
		this.endpoint = endpoint;
		this.datos = datos;
		this.necesitaDatos = necesitaDatos;
		this.datosNecesarios = datosNecesarios;
	}

	public OpcionAccionMsj() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public Map<String, String> getDatos() {
		return datos;
	}

	public void setDatos(Map<String, String> datos) {
		this.datos = datos;
	}

	public Boolean getNecesitaDatos() {
		return necesitaDatos;
	}

	public void setNecesitaDatos(Boolean necesitaDatos) {
		this.necesitaDatos = necesitaDatos;
	}

	public Map<String, OpcionAccionDatosMsj> getDatosNecesarios() {
		return datosNecesarios;
	}

	public void setDatosNecesarios(Map<String, OpcionAccionDatosMsj> datosNecesarios) {
		this.datosNecesarios = datosNecesarios;
	}
    
    
}
