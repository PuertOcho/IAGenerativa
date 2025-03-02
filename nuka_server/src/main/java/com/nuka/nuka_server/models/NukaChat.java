package com.nuka.nuka_server.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;


@Document(collection = "NukaChat")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NukaChat {

	@Id
	public String id;
	public String usuarioId;
	public Boolean visible;
	public Boolean favorito;
	public List<String> tags;
	public String summary;
	public Long fechaCreacion;
	public Long fechaUltimoMsj;
	public OpcionesModal opcionesModal;
	
	public NukaChat() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(String usuarioId) {
		this.usuarioId = usuarioId;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public Boolean getFavorito() {
		return favorito;
	}

	public void setFavorito(Boolean favorito) {
		this.favorito = favorito;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Long getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Long fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Long getFechaUltimoMsj() {
		return fechaUltimoMsj;
	}

	public void setFechaUltimoMsj(Long fechaUltimoMsj) {
		this.fechaUltimoMsj = fechaUltimoMsj;
	}

	public OpcionesModal getOpcionesModal() {
		return opcionesModal;
	}

	public void setOpcionesModal(OpcionesModal opcionesModal) {
		this.opcionesModal = opcionesModal;
	}

	
}
