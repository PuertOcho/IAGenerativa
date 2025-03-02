package com.nuka.nuka_server.models;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@Document(collection = "Product")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {

	@Id
	public String id;
	
	@JsonProperty("nombreProducto")
	public String nombreProducto;
	
	@JsonProperty("aliasProducto")
	public List<String> aliasProducto;
	
	@JsonProperty("categoria")
	public String categoria;
	
	@JsonProperty("addedByUserId")
	public String addedByUserId;

	@JsonProperty("embedding")
	public Double[] embedding;
	
	public Product() {
		super();
	}

	public Product(String id, String nombreProducto, List<String> aliasProducto, String categoria, String addedByUserId,
			Double[] embedding) {
		super();
		this.id = id;
		this.nombreProducto = nombreProducto;
		this.aliasProducto = aliasProducto;
		this.categoria = categoria;
		this.addedByUserId = addedByUserId;
		this.embedding = embedding;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombreProducto() {
		return nombreProducto;
	}

	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}

	public List<String> getAliasProducto() {
		return aliasProducto;
	}

	public void setAliasProducto(List<String> aliasProducto) {
		this.aliasProducto = aliasProducto;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getAddedByUserId() {
		return addedByUserId;
	}

	public void setAddedByUserId(String addedByUserId) {
		this.addedByUserId = addedByUserId;
	}

	public Double[] getEmbedding() {
		return embedding;
	}

	public void setEmbedding(Double[] embedding) {
		this.embedding = embedding;
	}

	
	
	
	
}