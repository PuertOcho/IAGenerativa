package com.nuka.nuka_server.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.nuka.nuka_server.Constantes;
import com.nuka.nuka_server.NukaConfiguration;
import com.nuka.nuka_server.models.User;
import com.nuka.nuka_server.models.Firebase.AndroidConfigFirebase;
import com.nuka.nuka_server.models.Firebase.AndroidNotification;
import com.nuka.nuka_server.models.Firebase.AndroidNotification.NotificationPriority;
import com.nuka.nuka_server.models.Firebase.MensajeFirebase;
import com.nuka.nuka_server.models.Firebase.NotificationFirebase;
import com.nuka.nuka_server.models.Firebase.AndroidConfigFirebase.AndroidMessagePriority;
import com.nuka.nuka_server.repository.AccionNukaRepository;
import com.nuka.nuka_server.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonInclude;

 
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class NotificacionesService {

    Logger logger = LoggerFactory.getLogger(getClass());
    
	@Autowired
	AccionNukaRepository accionNukaRepository;

	@Autowired
	UtilsService utilsService;
	
	@Autowired
	NukaConfiguration nukaConfiguration;
	
	@Autowired
	UserRepository userRepository;
	
	ObjectMapper objectMapper = new ObjectMapper();
	

	    public String getAccessTokenFirebaseMessaging() throws IOException {
	
		String jsonPath = "C:\\test\\nuka-puertocho-67ec8546120e.json";
	        GoogleCredentials credentials = GoogleCredentials
	            .fromStream(new FileInputStream(jsonPath))
	            .createScoped(Collections.singletonList("https://www.googleapis.com/auth/firebase.messaging"));

	        credentials.refresh();
	        AccessToken token = credentials.getAccessToken();
	        return token.getTokenValue();
	    }
			
		public void enviarNotificacion(String tokenDispositivo, String cabecera, String mensaje, String icono, String imagen, String color, Map<String, String> mapDatos) {
		    try {
			String token = getAccessTokenFirebaseMessaging();
			String url = Constantes.FIREBASE_URL_MESSAGE_SEND;
			
			
			List<String> datosNotificacion = 
				Arrays.asList(
					cabecera, 
					mensaje,
					icono);
			
			// https://i.pinimg.com/originals/31/f8/8d/31f88dd60329078bac90b7f8e59bdd53.png
			// https://i.pinimg.com/originals/08/eb/23/08eb238b9a0424d8869a33911d21a8fc.png
			
			AndroidConfigFirebase androidConfig = new AndroidConfigFirebase();
        			AndroidNotification androidNotification = new AndroidNotification();
        			androidNotification.setTitle(datosNotificacion.get(0));
        			androidNotification.setBody(datosNotificacion.get(1));
        			androidNotification.setIcon(datosNotificacion.get(2));
        			androidNotification.setColor("#66a766");

			androidConfig.setNotification(androidNotification);
			//androidConfig.setData(Map.of("AAAA2","BBBB2"));
			androidConfig.setAndroidMessagePriority(AndroidMessagePriority.HIGH);
			androidConfig.setDirect_boot_ok(true);
			
			MensajeFirebase mensajeFirebase = new MensajeFirebase();
			mensajeFirebase.setAndroid(androidConfig);
			mensajeFirebase.setToken(tokenDispositivo);
			//mensajeFirebase.setData(Map.of("AAAA","BBBB"));
			
			NotificationFirebase notificationFirebase = 
				new NotificationFirebase(datosNotificacion.get(0), datosNotificacion.get(1), datosNotificacion.get(2));
			mensajeFirebase.setNotification(notificationFirebase);
			
			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			String jsonMensajeFirebase = objectMapper.writeValueAsString(mensajeFirebase);
			
			String body = "{\"message\":" + jsonMensajeFirebase + "}";
			HttpResponse<String> response = Unirest.post(url)
					.header("Authorization", "Bearer " + token)
					.header("Content-Type", "application/json")
					.body(body).asString();

			if (200 == response.getStatus()) {
			    String responseString = response.getBody();
			}

		    } catch (Exception e) {
			String traza = "chat : ";
			logger.error(traza + e.getMessage()); 
			//e.printStackTrace();
		    }
		}
		
		// ++++++++++++++++++++++ TEST ++++++++++++++++++++++
		public void testNotificacion2(String usuarioId, String dispositivo) {
		    try {
			
			Optional<User> usuario = userRepository.findById(usuarioId);
			
			String token = getAccessTokenFirebaseMessaging();
			String url = Constantes.FIREBASE_URL_MESSAGE_SEND;
			String cabecera = "cabecera";
			String mensaje = "mensaje";
			String icono = "https://i.pinimg.com/originals/31/f8/8d/31f88dd60329078bac90b7f8e59bdd53.png";
			String tokenDispositivo = usuario.get().getData().getTokenPorDispositivos().get(dispositivo);
			
			List<String> datosNotificacion = 
				Arrays.asList(
					cabecera, 
					mensaje,
					icono);
			
			// https://i.pinimg.com/originals/31/f8/8d/31f88dd60329078bac90b7f8e59bdd53.png
			// https://i.pinimg.com/originals/08/eb/23/08eb238b9a0424d8869a33911d21a8fc.png
			
			AndroidConfigFirebase androidConfig = new AndroidConfigFirebase();
        			AndroidNotification androidNotification = new AndroidNotification();
        			androidNotification.setTitle(datosNotificacion.get(0));
        			androidNotification.setBody(datosNotificacion.get(1));
        			//androidNotification.setIcon(datosNotificacion.get(2));
        			androidNotification.setColor("#66a766");
        			androidNotification.setNotificationPriority(NotificationPriority.PRIORITY_MAX);
        			
			androidConfig.setNotification(androidNotification);
			androidConfig.setData(Map.of("AAAA2","BBBB2"));
			androidConfig.setAndroidMessagePriority(AndroidMessagePriority.HIGH);
			androidConfig.setDirect_boot_ok(true);
			
			MensajeFirebase mensajeFirebase = new MensajeFirebase();
			//mensajeFirebase.setAndroid(androidConfig);
			mensajeFirebase.setToken(tokenDispositivo);
			mensajeFirebase.setData(Map.of("AAAA","BBBB"));
			
			NotificationFirebase notificationFirebase = 
				new NotificationFirebase(datosNotificacion.get(0), datosNotificacion.get(1), null);
			mensajeFirebase.setNotification(notificationFirebase);
			
			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			String jsonMensajeFirebase = objectMapper.writeValueAsString(mensajeFirebase);
			
			String body = "{\"message\":" + jsonMensajeFirebase + "}";
			HttpResponse<String> response = Unirest.post(url)
					.header("Authorization", "Bearer " + token)
					.header("Content-Type", "application/json")
					.body(body).asString();

			if (200 == response.getStatus()) {
			    String responseString = response.getBody();
			}

		    } catch (Exception e) {
			String traza = "chat : ";
			logger.error(traza + e.getMessage()); 
			//e.printStackTrace();
		    }
		}
	
}