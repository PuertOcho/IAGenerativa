package com.nuka.nuka_server.models.Firebase;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

 
 
 


@JsonIgnoreProperties(ignoreUnknown = true)




public class MensajeFirebase {

    	public String name;
    	
    	public Map<String, String> data;
    	
    	public NotificationFirebase notification;
    	
    	public AndroidConfigFirebase android;
    	
    	public WebpushConfigFirebase webpush;
    	  
    	public FcmOptionsFirebase fcm_options;
    	
// ========== El objetivo de campo de la Unión sólo puede ser uno de los siguientes: ========== 
      	public String token;
    	  
      	public String topic;
    	  
      	public String condition;
// ========== Fin de la lista de tipos posibles para el objetivo de campo de unión. ========== 

		public MensajeFirebase(String name, Map<String, String> data, NotificationFirebase notification,
				AndroidConfigFirebase android, WebpushConfigFirebase webpush, FcmOptionsFirebase fcm_options,
				String token, String topic, String condition) {
			super();
			this.name = name;
			this.data = data;
			this.notification = notification;
			this.android = android;
			this.webpush = webpush;
			this.fcm_options = fcm_options;
			this.token = token;
			this.topic = topic;
			this.condition = condition;
		}

		public MensajeFirebase() {
			super();
			// TODO Auto-generated constructor stub
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Map<String, String> getData() {
			return data;
		}

		public void setData(Map<String, String> data) {
			this.data = data;
		}

		public NotificationFirebase getNotification() {
			return notification;
		}

		public void setNotification(NotificationFirebase notification) {
			this.notification = notification;
		}

		public AndroidConfigFirebase getAndroid() {
			return android;
		}

		public void setAndroid(AndroidConfigFirebase android) {
			this.android = android;
		}

		public WebpushConfigFirebase getWebpush() {
			return webpush;
		}

		public void setWebpush(WebpushConfigFirebase webpush) {
			this.webpush = webpush;
		}

		public FcmOptionsFirebase getFcm_options() {
			return fcm_options;
		}

		public void setFcm_options(FcmOptionsFirebase fcm_options) {
			this.fcm_options = fcm_options;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getTopic() {
			return topic;
		}

		public void setTopic(String topic) {
			this.topic = topic;
		}

		public String getCondition() {
			return condition;
		}

		public void setCondition(String condition) {
			this.condition = condition;
		}
    	
      	
      	
}