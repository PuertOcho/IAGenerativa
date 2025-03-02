package com.nuka.nuka_server.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductRelation {

	public Integer quantity;
	public Product productInfo;
	public Boolean marcado;
	
	public ProductRelation() {
		super();
	}

	public ProductRelation(Integer quantity, Product productInfo, Boolean marcado) {
		super();
		this.quantity = quantity;
		this.productInfo = productInfo;
		this.marcado = marcado;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Product getProductInfo() {
		return productInfo;
	}

	public void setProductInfo(Product productInfo) {
		this.productInfo = productInfo;
	}

	public Boolean getMarcado() {
		return marcado;
	}

	public void setMarcado(Boolean marcado) {
		this.marcado = marcado;
	}

	
	
}