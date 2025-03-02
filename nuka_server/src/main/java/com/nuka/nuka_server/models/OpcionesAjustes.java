package com.nuka.nuka_server.models;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

 
 




public class OpcionesAjustes {
    
    @JsonProperty("opciones")
    public List<GrupoOpciones> opciones;

    public OpcionesAjustes(List<GrupoOpciones> opciones) {
	this.opciones = opciones;
    }

    public OpcionesAjustes() {
	super();
    }
    
    
}