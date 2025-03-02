package com.nuka.nuka_server.models;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

 


@Document(collection = "EstadoMsj")
public class EstadoMsj {
	@Id
	public String id;
	public Long fecha;
	public String texto;
	public List<String> usuariosId;
	public TipoMsjEstado tipoMsjEstado;
	
	// estado -> POR_HACER: Tareas que se tiene que hacer
	// 	  -> TERMINADA: Tareas que se han realizado
	//	  -> INFO: Solo muestra info de la tarea, pero no guarda quien la ha hecho
	public String estado; 
	
	// imgMsjPath -> Notificaciones: path de img de la notificacion
	// 	      -> Calendario: color del evento en el calendario
	public String imgMsjPath; 
	public String redireccion;
	public boolean visible;
	public Integer tiempoRepeticion; // En dias


	public EstadoMsj(String id, Long fecha, String texto, List<String> usuariosId, TipoMsjEstado tipoMsjEstado,
			String estado, String imgMsjPath, String redireccion, boolean visible, Integer tiempoRepeticion) {
		super();
		this.id = id;
		this.fecha = fecha;
		this.texto = texto;
		this.usuariosId = usuariosId;
		this.tipoMsjEstado = tipoMsjEstado;
		this.estado = estado;
		this.imgMsjPath = imgMsjPath;
		this.redireccion = redireccion;
		this.visible = visible;
		this.tiempoRepeticion = tiempoRepeticion;
	}

	public EstadoMsj() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getFecha() {
		return fecha;
	}

	public void setFecha(Long fecha) {
		this.fecha = fecha;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public List<String> getUsuariosId() {
		return usuariosId;
	}

	public void setUsuariosId(List<String> usuariosId) {
		this.usuariosId = usuariosId;
	}

	public TipoMsjEstado getTipoMsjEstado() {
		return tipoMsjEstado;
	}

	public void setTipoMsjEstado(TipoMsjEstado tipoMsjEstado) {
		this.tipoMsjEstado = tipoMsjEstado;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getImgMsjPath() {
		return imgMsjPath;
	}

	public void setImgMsjPath(String imgMsjPath) {
		this.imgMsjPath = imgMsjPath;
	}

	public String getRedireccion() {
		return redireccion;
	}

	public void setRedireccion(String redireccion) {
		this.redireccion = redireccion;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Integer getTiempoRepeticion() {
		return tiempoRepeticion;
	}

	public void setTiempoRepeticion(Integer tiempoRepeticion) {
		this.tiempoRepeticion = tiempoRepeticion;
	}
	
	
}
