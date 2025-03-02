package com.nuka.nuka_server.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;

 
 
 
 




@Document(collection = "User")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

	@Id
	public String id;
	public String userNick;
	public String username;
	public String password;
	public String email;
	public String phone;
	public String permission;
	public OpcionesModal opcionesModal;
	public DataUser data;
	
	public User() {
		super();
	}

	public User(String id, String userNick) {
		super();
		this.id = id;
		this.userNick = userNick;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserNick() {
		return userNick;
	}

	public void setUserNick(String userNick) {
		this.userNick = userNick;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public OpcionesModal getOpcionesModal() {
		return opcionesModal;
	}

	public void setOpcionesModal(OpcionesModal opcionesModal) {
		this.opcionesModal = opcionesModal;
	}

	public DataUser getData() {
		return data;
	}

	public void setData(DataUser data) {
		this.data = data;
	}
	
	
	
	
}
