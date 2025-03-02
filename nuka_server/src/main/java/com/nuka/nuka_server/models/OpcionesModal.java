package com.nuka.nuka_server.models;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

 


public class OpcionesModal {
    
    @JsonProperty("opciones")
    public List<GrupoOpciones> opciones;

    public OpcionesModal(List<GrupoOpciones> opciones) {
	this.opciones = opciones;
    }

    public OpcionesModal() {
	super();
    }

	public List<GrupoOpciones> getOpciones() {
		return opciones;
	}

	public void setOpciones(List<GrupoOpciones> opciones) {
		this.opciones = opciones;
	}
    
    
}