package com.nuka.nuka_server;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class NukaConfiguration {
    
    public String NUKA_PUERTO;
    public String NUKA_HOST;
    public String NUKA_DEBUG_MODE;
    
    public String OPENAI_KEY;
    
    public String MONGO_USERNAME;
    public String MONGO_PASSWORD;
    public String MONGO_AUTHENTICATION_DATABASE = "admin";
    public String MONGO_BD_NAME;
    public String MONGO_PUERTO;
    public String MONGO_HOST;
    
    public String MICROSOFT_COG_KEY;
    public String MICROSOFT_COG_REGION;
	
    public String SERVELET_MULTIPART_OPTION_MAX_FILE_SIZE = "512MB";
    public String SERVELET_MULTIPART_OPTION_MAX_REQUEST_SIZE = "512MB";
    public String SERVELET_MULTIPART_OPTION_ENABLE_MULTIPART = "true";

	public NukaConfiguration(Environment environment) {

		this.NUKA_PUERTO = environment.getProperty("NUKA_PUERTO");
		this.NUKA_HOST = environment.getProperty("NUKA_HOST");
		this.NUKA_DEBUG_MODE = environment.getProperty("NUKA_DEBUG_MODE");
			
		this.OPENAI_KEY = environment.getProperty("NUKA_OPENAI_KEY");
		
		this.MONGO_USERNAME = environment.getProperty("NUKA_MONGO_USERNAME");
		this.MONGO_PASSWORD = environment.getProperty("NUKA_MONGO_PASSWORD");
		this.MONGO_PUERTO = environment.getProperty("NUKA_MONGO_PUERTO");
		this.MONGO_HOST = environment.getProperty("NUKA_MONGO_HOST");
		this.MONGO_BD_NAME = environment.getProperty("NUKA_MONGO_BD_NAME");
		
		this.MICROSOFT_COG_KEY = environment.getProperty("NUKA_MICROSOFT_COG_KEY");
		this.MICROSOFT_COG_REGION = environment.getProperty("NUKA_MICROSOFT_COG_REGION");

		// Puerto de la aplicacion
		System.setProperty("server.port", "9898"); 
		   
		// Configuración para habilitar el manejo de archivos multipartes (subidas de archivos)
		System.setProperty("spring.servlet.multipart.enabled", this.SERVELET_MULTIPART_OPTION_ENABLE_MULTIPART);
		System.setProperty("spring.servlet.multipart.max-request-size", this.SERVELET_MULTIPART_OPTION_MAX_REQUEST_SIZE);
		System.setProperty("spring.servlet.multipart.max-file-size", this.SERVELET_MULTIPART_OPTION_MAX_FILE_SIZE);
	
		// Configuración de MongoDB
		System.setProperty("spring.data.mongodb.authentication-database", this.MONGO_AUTHENTICATION_DATABASE);
		System.setProperty("spring.data.mongodb.database", this.MONGO_BD_NAME);
		System.setProperty("spring.data.mongodb.port", this.MONGO_PUERTO);
		System.setProperty("spring.data.mongodb.host", this.MONGO_HOST);
		System.setProperty("spring.data.mongodb.username", this.MONGO_USERNAME);
		System.setProperty("spring.data.mongodb.password", this.MONGO_PASSWORD);
	
    }

	public String getNUKA_PUERTO() {
		return NUKA_PUERTO;
	}

	public void setNUKA_PUERTO(String nUKA_PUERTO) {
		NUKA_PUERTO = nUKA_PUERTO;
	}

	public String getNUKA_HOST() {
		return NUKA_HOST;
	}

	public void setNUKA_HOST(String nUKA_HOST) {
		NUKA_HOST = nUKA_HOST;
	}

	public String getNUKA_DEBUG_MODE() {
		return NUKA_DEBUG_MODE;
	}

	public void setNUKA_DEBUG_MODE(String nUKA_DEBUG_MODE) {
		NUKA_DEBUG_MODE = nUKA_DEBUG_MODE;
	}

	public String getOPENAI_KEY() {
		return OPENAI_KEY;
	}

	public void setOPENAI_KEY(String oPENAI_KEY) {
		OPENAI_KEY = oPENAI_KEY;
	}

	public String getMONGO_BD_NAME() {
		return MONGO_BD_NAME;
	}

	public void setMONGO_BD_NAME(String mONGO_BD_NAME) {
		MONGO_BD_NAME = mONGO_BD_NAME;
	}

	public String getMONGO_PUERTO() {
		return MONGO_PUERTO;
	}

	public void setMONGO_PUERTO(String mONGO_PUERTO) {
		MONGO_PUERTO = mONGO_PUERTO;
	}

	public String getMONGO_HOST() {
		return MONGO_HOST;
	}

	public void setMONGO_HOST(String mONGO_HOST) {
		MONGO_HOST = mONGO_HOST;
	}

	public String getMICROSOFT_COG_KEY() {
		return MICROSOFT_COG_KEY;
	}

	public void setMICROSOFT_COG_KEY(String mICROSOFT_COG_KEY) {
		MICROSOFT_COG_KEY = mICROSOFT_COG_KEY;
	}

	public String getMICROSOFT_COG_REGION() {
		return MICROSOFT_COG_REGION;
	}

	public void setMICROSOFT_COG_REGION(String mICROSOFT_COG_REGION) {
		MICROSOFT_COG_REGION = mICROSOFT_COG_REGION;
	}

	public String getMONGO_USERNAME() {
		return MONGO_USERNAME;
	}

	public void setMONGO_USERNAME(String mONGO_USERNAME) {
		MONGO_USERNAME = mONGO_USERNAME;
	}

	public String getMONGO_PASSWORD() {
		return MONGO_PASSWORD;
	}

	public void setMONGO_PASSWORD(String mONGO_PASSWORD) {
		MONGO_PASSWORD = mONGO_PASSWORD;
	}

	public String getMONGO_AUTHENTICATION_DATABASE() {
		return MONGO_AUTHENTICATION_DATABASE;
	}

	public void setMONGO_AUTHENTICATION_DATABASE(String mONGO_AUTHENTICATION_DATABASE) {
		MONGO_AUTHENTICATION_DATABASE = mONGO_AUTHENTICATION_DATABASE;
	}

	public String getSERVELET_MULTIPART_OPTION_MAX_FILE_SIZE() {
		return SERVELET_MULTIPART_OPTION_MAX_FILE_SIZE;
	}

	public void setSERVELET_MULTIPART_OPTION_MAX_FILE_SIZE(String sERVELET_MULTIPART_OPTION_MAX_FILE_SIZE) {
		SERVELET_MULTIPART_OPTION_MAX_FILE_SIZE = sERVELET_MULTIPART_OPTION_MAX_FILE_SIZE;
	}

	public String getSERVELET_MULTIPART_OPTION_MAX_REQUEST_SIZE() {
		return SERVELET_MULTIPART_OPTION_MAX_REQUEST_SIZE;
	}

	public void setSERVELET_MULTIPART_OPTION_MAX_REQUEST_SIZE(String sERVELET_MULTIPART_OPTION_MAX_REQUEST_SIZE) {
		SERVELET_MULTIPART_OPTION_MAX_REQUEST_SIZE = sERVELET_MULTIPART_OPTION_MAX_REQUEST_SIZE;
	}

	public String getSERVELET_MULTIPART_OPTION_ENABLE_MULTIPART() {
		return SERVELET_MULTIPART_OPTION_ENABLE_MULTIPART;
	}

	public void setSERVELET_MULTIPART_OPTION_ENABLE_MULTIPART(String sERVELET_MULTIPART_OPTION_ENABLE_MULTIPART) {
		SERVELET_MULTIPART_OPTION_ENABLE_MULTIPART = sERVELET_MULTIPART_OPTION_ENABLE_MULTIPART;
	}
    
}
