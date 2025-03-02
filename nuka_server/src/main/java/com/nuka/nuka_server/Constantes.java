package com.nuka.nuka_server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constantes {
    
    	static Logger logger = LoggerFactory.getLogger(Constantes.class);
    	
    	// Constantes del sistema
    	public static String NUKA_VERSION = "0.4-24.01.15";
    	public static String MICROSOFT_URL_TTS = "https://MICROSOFT_COG_REGION.tts.speech.microsoft.com/cognitiveservices/v1";
    	
        public static String MODEL_GPT35TURBO = "gpt-3.5-turbo";
        public static String MODEL_GPT4_O_MINI = "gpt-4o-mini";
        public static String MODEL_GPT4_O = "gpt-4o";
        public static String MODEL_O1_PREVIEW = "o1-preview";
        public static String MODEL_O1_MINI = "o1-mini";
        public static String MODEL_O1 = "o1";
        public static String MODEL_O3_MINI = "o3-mini";
        public static String MODEL_CHATGPT_4_O = "chatgpt-4o-latest";

		public static String MODEL_DEFAULT = MODEL_GPT4_O_MINI;
		public static String MODEL_DEFAULT_PLUS = MODEL_GPT4_O;
		public static String MODEL_RAZONAMIENTO_DEFAULT = MODEL_O1_MINI;
		public static String MODEL_RAZONAMIENTO_DEFAULT_PLUS = MODEL_O1_PREVIEW;
		
        public static String MODEL_GPT35TURBO_INSTRUCT = "gpt-3.5-turbo-instruct";
        public static String MODEL_WHISPER = "whisper-1";
        
        public static String OPENAI_URL_CHAT_COMPLETIONS = "https://api.openai.com/v1/chat/completions";
        public static String OPENAI_URL_COMPLETIONS = "https://api.openai.com/v1/completions";
        public static String OPENAI_URL_ASSISTANTS = "https://api.openai.com/v1/assistants";
        public static String OPENAI_URL_IMAGE_ = "https://api.openai.com/v1/images/generations";
        public static String OPENAI_RESPONSE_ERROR = "OPENAI_RESPONSE_ERROR";
        public static String OPENAI_RESPONSE_FINISH_REASON_LENGTH = "length";
        public static String OPENAI_RESPONSE_FINISH_REASON_STOP = "stop";

        public static String OPENAI_URL_TRANSCRIPTIONS = "https://api.openai.com/v1/audio/transcriptions";
        public static String OPENAI_URL_EMBEDDINGS = "https://api.openai.com/v1/embeddings";
        public static String OPENAI_URL_IMAGES_GENERATION = "https://api.openai.com/v1/images/generations";
    
        public static String FIREBASE_URL_MESSAGE_SEND = "https://fcm.googleapis.com/v1/projects/nuka-puertocho/messages:send";
        
        public static String BETBOT_ENDPOINT = "http://192.168.1.88:9900/";
        public static String BETBOT_API = BETBOT_ENDPOINT + "api/";
                
		public static String ID_USUARIO_DEFAULT = "6495ea1c3c484b05a173bb56";
		public static String USERNICK_USUARIO_DEFAULT = "puertocho";

		// OLLAMA
		public static final String LOCAL_ENDPOINT_EMBEDDINGS = "http://localhost:11434/api/embeddings";
		public static final String LOCAL_ENDPOINT_LLM = "http://localhost:11434/api/generate";

   	
		// Modelos de Embeddings
		public static final String MODEL_LOCAL_EMBEDDINGS_NOMIC_EMBED_TEXT = "nomic-embed-text";
		public static final String MODEL_LOCAL_EMBEDDINGS_MXBAI_EMBED_LARGE = "mxbai-embed-large";
		
		// Modelos LLM
		public static final String MODEL_LOCAL_LLM_PHI4 = "phi4";
		public static final String MODEL_LOCAL_LLM_LLAMA3_2_VISION = "llama3.2-vision";
		public static final String MODEL_LOCAL_LLM_LLAVA_7B = "llava:7b";
		public static final String MODEL_LOCAL_LLM_LLAMA3_2 = "llama3.2";
		public static final String MODEL_LOCAL_LLM_DEEPSEEK_R1_8B = "deepseek-r1:8b";
		public static final String MODEL_LOCAL_LLM_MISTRAL = "mistral";
		
		// Modelos default
		public static final String MODEL_LOCAL_EMBEDDINGS_DEFAULT = MODEL_LOCAL_EMBEDDINGS_MXBAI_EMBED_LARGE;
		public static final String MODEL_LOCAL_LLM_DEFAULT = MODEL_LOCAL_LLM_DEEPSEEK_R1_8B;

    	// carpeta, extension, mimeType -> ("audio", ".wav", "audio/wav")
	public static Map<String, List<String>> MAP_RESOURCE = Map.of(
		"audioWav", Arrays.asList("audio", ".wav", "audio/wav"), 
		"audioWebm", Arrays.asList("audio", ".webm", "audio/webm"),
		"audioAac", Arrays.asList("audio", ".aac", "audio/aac"),
		"pdf", Arrays.asList("documento", ".pdf", "application/pdf"), 
		"imagenPng", Arrays.asList("imagen", ".png", "image/png"), 
		"imagenJpg", Arrays.asList("imagen", ".jpg", "image/jpeg"),
		"gif", Arrays.asList("imagen", ".gif", "image/gif"),
		"jsonOpciones", Arrays.asList("opcion", ".json", "application/json")
	);

	// URLs Nuka
	public static String NUKA_URL_NOAUTH = "https://URL/noAuth/";
	public static String NUKA_URL_NOAUTH_IMAGEN = NUKA_URL_NOAUTH + "imagen/";
	public static String NUKA_URL_NOAUTH_VIDEO = NUKA_URL_NOAUTH + "video/";
	
	// PATHs
	public static String DATOS_PATH = File.separator + "var" + File.separator + "datos";
	public static String DATOS_NOAUTH_PATH = File.separator + "var" + File.separator + "datosNoAuth";
	public static String OPCION_PATH = DATOS_PATH + File.separator + "opcion" + File.separator;
	public static String AUDIO_PATH = DATOS_PATH + File.separator + "audio" + File.separator;
	public static String IMAGEN_PATH = DATOS_PATH + File.separator + "imagen" + File.separator;
	public static String IMAGEN_NOAUTH_PATH = DATOS_NOAUTH_PATH + File.separator + "imagen" + File.separator;
	public static String VIDEO_PATH = DATOS_PATH + File.separator + "video" + File.separator;
	public static String VIDEO_NOAUTH_PATH = DATOS_NOAUTH_PATH + File.separator + "video" + File.separator;
	public static String IMAGEN_BETBOT_PATH = DATOS_PATH + File.separator + "static" + File.separator + "imagen_betbot" + File.separator;
	public static String TMP_PATH = DATOS_PATH + File.separator + "tmp" + File.separator;
	
	public static String OPCIONES_AJUSTES_CHAT_PATH = DATOS_PATH + File.separator + "static" + File.separator + "AjustesChat.json";
	public static String OPCIONES_AJUSTES_USUARIO_PATH = DATOS_PATH + File.separator + "static" + File.separator + "AjustesUsuario.json";
	public static String MONGODUMP_PATH = DATOS_PATH + File.separator + "mongodump" + File.separator;
	
	// ROLES OPEN AI
	public static String ROLE_OPENAI_USER = "user";
	public static String ROLE_OPENAI_SYSTEM = "system";
	public static String ROLE_OPENAI_ASSISTANT = "assistant";
	
	// ROLES APP
	public static String ROLE_APP_USER = "USER";
	public static String ROLE_APP_ADMIN = "ADMIN";

	// COLORES
	public static String COLOR_NOTIFICACION_PRINCIPAL = "#21b866";
	
	// COMPORTAMIENTO
	public static String COMPORTAMIENTO_PATH = DATOS_PATH + File.separator + "comportamientos" + File.separator;
	public static String COMPORTAMIENTO_CLASIFICAR_ACCION = convertFileToString(COMPORTAMIENTO_PATH + "COMPORTAMIENTO_CLASIFICAR_ACCION");
	public static String COMPORTAMIENTO_LISTA_ACCIONES = convertFileToString(COMPORTAMIENTO_PATH + "COMPORTAMIENTO_LISTA_ACCIONES");
	public static String COMPORTAMIENTO_POR_DEFECTO = "Responde brevemente como si se tratara de una conversación. ";
	public static String COMPORTAMIENTO_ACCION_RECORDATORIO = convertFileToString(COMPORTAMIENTO_PATH + "COMPORTAMIENTO_ACCION_RECORDATORIO");
	public static String COMPORTAMIENTO_ACCION_CALENDARIO_CITA = convertFileToString(COMPORTAMIENTO_PATH + "COMPORTAMIENTO_ACCION_CALENDARIO_CITA");
	public static String COMPORTAMIENTO_ACCION_AÑADIR_COMPRA = convertFileToString(COMPORTAMIENTO_PATH + "COMPORTAMIENTO_ACCION_AÑADIR_COMPRA");
	
	public static String COMPORTAMIENTO_AUTONOMBRAR_CHAT = convertFileToString(COMPORTAMIENTO_PATH + "COMPORTAMIENTO_AUTONOMBRAR_CHAT");

	
	// Tipos 
	public static List<String> CATEGORIAS_PRODUCTO = Arrays.asList(
		"Lacteos", "Bebidas", "Carne", 
		"Congelados", "Higiene", "Limpieza", 
		"Snacks", "Otros", "Verduras", "Frutas");
	
	// Tareas
	public static String AÑADIR_PRODUCTO_NUEVO = "AÑADIR_PRODUCTO_NUEVO";
	public static String AÑADIR_ALIAS_LISTA_COMPRA = "AÑADIR_ALIAS_LISTA_COMPRA";
	
	// Unirest
	public static long UNIREST_CONNECTION_TIMEOUT = 60000; // 60 segundos
	public static long UNIREST_SOCKET_TIMEOUT = 1800000;	// 30 mint, por algunas peticiones a OpenAi

	// Ajustes de usuario
	public static String ESTADO_MOSTRAR_AVISOS_Y_NOTIFICACIONES = "ESTADO_MOSTRAR_AVISOS_Y_NOTIFICACIONES";
	public static String ESTADO_MOSTRAR_CALENDARIO = "ESTADO_MOSTRAR_CALENDARIO";
	public static String ESTADO_MOSTRAR_DISPOSITIVOS = "ESTADO_MOSTRAR_DISPOSITIVOS";
	public static String CHATS_REDIRIGIR_CHAT_GENERAL = "CHATS_REDIRIGIR_CHAT_GENERAL";
	
	// Ajustes de chat
	public static String AUDIO_REPRODUCCION_AUTOMATICA = "AUDIO_REPRODUCCION_AUTOMATICA";
	public static String GENERAL_NOMBRE_CHAT = "GENERAL_NOMBRE_CHAT";
	public static String GENERAL_GUARDAR_AJUSTES_CHAT = "GENERAL_GUARDAR_AJUSTES_CHAT";
	
	// Acciones sincronizacion Obsidian
	public static String CREATE_OBSIDIAN = "CREATE";
	public static String DELETE_OBSIDIAN = "DELETE";
		
	
	public static Map<String, Integer> LIMITE_TOKENS_POR_MODELO = Map.of(
			"gpt-3.5-turbo", 16385,
			"gpt-4", 8192,
	        "gpt-4-turbo", 128000,
	        "gpt-4o", 128000,
	        "gpt-4o-mini", 128000,
	        "o1-preview", 128000,
	        "o1-mini", 128000);
	
	// Caracteres especiales para SSML
        public static Map<String, String> MAP_CARACTERES_ESPECIALES_XML = Stream.of(new Object[][] {
            {"á", "&#225;"},            {"Á", "&#193;"},
            {"é", "&#233;"},            {"É", "&#201;"},
            {"í", "&#237;"},            {"Í", "&#205;"},
            {"ó", "&#243;"},            {"Ó", "&#211;"},
            {"ú", "&#250;"},            {"Ú", "&#218;"},
            {"ï", "&#239;"},            {"Ï", "&#207;"},
            {"ü", "&#252;"},            {"Ü", "&#220;"},
            {"ñ", "&#241;"},            {"Ñ", "&#209;"},
            {"ç", "&#231;"},            {"Ç", "&#199;"},
            {"ª", "&#170;"},            {"º", "&#186;"},
            {"¿", "&#191;"},            {"¡", "&#161;"}
        }).collect(Collectors.toMap(data -> (String) data[0], data -> (String) data[1])); 
	
        public static String convertFileToString(String pathFile) {  
			try {
				return new String(Files.readAllBytes(Paths.get(pathFile)));
			} catch (IOException e) {
				String traza = "convertFileToString : ";
				logger.error(traza + e.getMessage()); 
				//e.printStackTrace();
			} 
            return null;
        }
        
}
