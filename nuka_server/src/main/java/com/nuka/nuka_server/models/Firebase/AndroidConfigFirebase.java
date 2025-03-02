package com.nuka.nuka_server.models.Firebase;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

 
 
 


@JsonIgnoreProperties(ignoreUnknown = true)




public class AndroidConfigFirebase {

    // https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages?hl=es#AndroidConfig
    
	@JsonProperty("collapse_key")
	private String collapse_key;
	
	@JsonProperty("priority")
	private AndroidMessagePriority androidMessagePriority;
	
	@JsonProperty("ttl")
	private String ttl;
	
	@JsonProperty("restricted_package_name")
	private String restricted_package_name;
	
	@JsonProperty("data")
	private Map<String, String> data;
	
	@JsonProperty("notification")
	private AndroidNotification notification;
	
	@JsonProperty("fcm_options")
	private AndroidFcmOptions fcm_options;
	
	@JsonProperty("direct_boot_ok")
	private Boolean direct_boot_ok;
    
	public enum AndroidMessagePriority {
	    NORMAL, HIGH
	}

	public AndroidConfigFirebase(String collapse_key, AndroidMessagePriority androidMessagePriority, String ttl,
			String restricted_package_name, Map<String, String> data, AndroidNotification notification,
			AndroidFcmOptions fcm_options, Boolean direct_boot_ok) {
		super();
		this.collapse_key = collapse_key;
		this.androidMessagePriority = androidMessagePriority;
		this.ttl = ttl;
		this.restricted_package_name = restricted_package_name;
		this.data = data;
		this.notification = notification;
		this.fcm_options = fcm_options;
		this.direct_boot_ok = direct_boot_ok;
	}

	public AndroidConfigFirebase() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getCollapse_key() {
		return collapse_key;
	}

	public void setCollapse_key(String collapse_key) {
		this.collapse_key = collapse_key;
	}

	public AndroidMessagePriority getAndroidMessagePriority() {
		return androidMessagePriority;
	}

	public void setAndroidMessagePriority(AndroidMessagePriority androidMessagePriority) {
		this.androidMessagePriority = androidMessagePriority;
	}

	public String getTtl() {
		return ttl;
	}

	public void setTtl(String ttl) {
		this.ttl = ttl;
	}

	public String getRestricted_package_name() {
		return restricted_package_name;
	}

	public void setRestricted_package_name(String restricted_package_name) {
		this.restricted_package_name = restricted_package_name;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public AndroidNotification getNotification() {
		return notification;
	}

	public void setNotification(AndroidNotification notification) {
		this.notification = notification;
	}

	public AndroidFcmOptions getFcm_options() {
		return fcm_options;
	}

	public void setFcm_options(AndroidFcmOptions fcm_options) {
		this.fcm_options = fcm_options;
	}

	public Boolean getDirect_boot_ok() {
		return direct_boot_ok;
	}

	public void setDirect_boot_ok(Boolean direct_boot_ok) {
		this.direct_boot_ok = direct_boot_ok;
	}
      	
	
	
}
