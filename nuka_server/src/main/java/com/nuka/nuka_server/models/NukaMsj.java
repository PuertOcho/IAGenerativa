package com.nuka.nuka_server.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "NukaMsj")
public class NukaMsj {
	@Id
	public String id;
	public Long fecha;
	public String texto;
	public String usuarioId;
	public String chatId;
	public TipoMsj tipoMsj;
	public String tarea;
	public Boolean needAudio;
	public String audioResource;
	public String tipoContenidoMsj;
	public List<ContenidoMsj> contenidoMsj;
	public String redireccion;
	public Boolean respondido;
	public boolean visible;

	@Transient
	public Blob audioFile;

	public NukaMsj() {
	    super();
	    this.visible = true;
	}

	public NukaMsj(String id, Long fecha, String texto, String usuarioId, String chatId, TipoMsj tipoMsj, String tarea,
			Boolean needAudio, String audioResource, String tipoContenidoMsj, List<ContenidoMsj> contenidoMsj,
			String redireccion, Boolean respondido, boolean visible, Blob audioFile) {
		super();
		this.id = id;
		this.fecha = fecha;
		this.texto = texto;
		this.usuarioId = usuarioId;
		this.chatId = chatId;
		this.tipoMsj = tipoMsj;
		this.tarea = tarea;
		this.needAudio = needAudio;
		this.audioResource = audioResource;
		this.tipoContenidoMsj = tipoContenidoMsj;
		this.contenidoMsj = contenidoMsj;
		this.redireccion = redireccion;
		this.respondido = respondido;
		this.visible = visible;
		this.audioFile = audioFile;
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

	public String getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(String usuarioId) {
		this.usuarioId = usuarioId;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public TipoMsj getTipoMsj() {
		return tipoMsj;
	}

	public void setTipoMsj(TipoMsj tipoMsj) {
		this.tipoMsj = tipoMsj;
	}

	public String getTarea() {
		return tarea;
	}

	public void setTarea(String tarea) {
		this.tarea = tarea;
	}

	public Boolean getNeedAudio() {
		return needAudio;
	}

	public void setNeedAudio(Boolean needAudio) {
		this.needAudio = needAudio;
	}

	public String getAudioResource() {
		return audioResource;
	}

	public void setAudioResource(String audioResource) {
		this.audioResource = audioResource;
	}

	public String getTipoContenidoMsj() {
		return tipoContenidoMsj;
	}

	public void setTipoContenidoMsj(String tipoContenidoMsj) {
		this.tipoContenidoMsj = tipoContenidoMsj;
	}

	public List<ContenidoMsj> getContenidoMsj() {
		return contenidoMsj;
	}

	public void setContenidoMsj(List<ContenidoMsj> contenidoMsj) {
		this.contenidoMsj = contenidoMsj;
	}

	public String getRedireccion() {
		return redireccion;
	}

	public void setRedireccion(String redireccion) {
		this.redireccion = redireccion;
	}

	public Boolean getRespondido() {
		return respondido;
	}

	public void setRespondido(Boolean respondido) {
		this.respondido = respondido;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Blob getAudioFile() {
		return audioFile;
	}

	public void setAudioFile(Blob audioFile) {
		this.audioFile = audioFile;
	}
	
	
	
	
}
