package com.nuka.nuka_server.models;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;

 
 
 
 




@Document(collection = "ShoppingList")
public class ShoppingList {

	@Id
	public String id;
	public Boolean isFinished;
	public List<ProductRelation> productRelations;
	public String datePurchase;
	
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getIsFinished() {
		return isFinished;
	}

	public void setIsFinished(Boolean isFinished) {
		this.isFinished = isFinished;
	}

	public List<ProductRelation> getProductRelations() {
		return productRelations;
	}

	public void setProductRelations(List<ProductRelation> productRelations) {
		this.productRelations = productRelations;
	}

	public String getDatePurchase() {
		return datePurchase;
	}

	public void setDatePurchase(String datePurchase) {
		this.datePurchase = datePurchase;
	}

	public ShoppingList(String id, Boolean isFinished, List<ProductRelation> productRelations, String datePurchase) {
		super();
		this.id = id;
		this.isFinished = isFinished;
		this.productRelations = productRelations;
		this.datePurchase = datePurchase;
	}

	public ShoppingList() {
		super();
		// TODO Auto-generated constructor stub
	}
	

	
	
}
