package com.nuka.nuka_server.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {

	public Integer quantity;
	public String productName;
	
	public ProductResponse() {
		super();
	}

	public ProductResponse(Integer quantity, String productName) {
		super();
		this.quantity = quantity;
		this.productName = productName;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	

}