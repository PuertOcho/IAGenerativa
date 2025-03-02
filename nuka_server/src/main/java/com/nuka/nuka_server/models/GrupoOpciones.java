package com.nuka.nuka_server.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GrupoOpciones {

    @JsonProperty("etiqueta")
    public String etiqueta;
    
    @JsonProperty("opciones")
    public List<OpcionBasica> opciones;

    public GrupoOpciones(String etiqueta, List<OpcionBasica> opciones) {
		super();
		this.etiqueta = etiqueta;
		this.opciones = opciones;
    }

    public GrupoOpciones() {
    	super();
    }

	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public List<OpcionBasica> getOpciones() {
		return opciones;
	}

	public void setOpciones(List<OpcionBasica> opciones) {
		this.opciones = opciones;
	}
    
    
    
}
