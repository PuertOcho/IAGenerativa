package com.nuka.nuka_server.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpcionBasica {
    
    @JsonProperty("id")
    public String id;
    
    @JsonProperty("etiqueta")
    public String etiqueta;
    
    @JsonProperty("tipo")
    public String tipo; // por si es boolean, input text ....
    
    @JsonProperty("datos")
    public List<String> datos;
    
    @JsonProperty("valor")
    public List<String> valor;

    public OpcionBasica(String id, String etiqueta, String tipo, List<String> datos, List<String> valor) {
		this.id = id;
		this.etiqueta = etiqueta;
		this.tipo = tipo;
		this.datos = datos;
		this.valor = valor;
    }
    
    public OpcionBasica() {
    	super();
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

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public List<String> getDatos() {
		return datos;
	}

	public void setDatos(List<String> datos) {
		this.datos = datos;
	}

	public List<String> getValor() {
		return valor;
	}

	public void setValor(List<String> valor) {
		this.valor = valor;
	}
    
    
    
}
