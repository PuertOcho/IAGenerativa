package com.nuka.nuka_server.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.nuka.nuka_server.Constantes;
import com.nuka.nuka_server.NukaConfiguration;
import com.nuka.nuka_server.models.AccionNuka;
import com.nuka.nuka_server.models.ChatGPTRequest;
import com.nuka.nuka_server.models.ChatGPTResponse;
import com.nuka.nuka_server.models.Choice;
import com.nuka.nuka_server.models.Embedding;
import com.nuka.nuka_server.models.EmbeddingRequest;
import com.nuka.nuka_server.models.EmbeddingResponse;
import com.nuka.nuka_server.models.GenerateImageRequest;
import com.nuka.nuka_server.models.GeneratedImageResponse;
import com.nuka.nuka_server.models.Message;
import com.nuka.nuka_server.models.NukaChat;
import com.nuka.nuka_server.models.User;
import com.nuka.nuka_server.models.WhisperTranscriptionRequest;
import com.nuka.nuka_server.models.WhisperTranscriptionResponse;
import com.nuka.nuka_server.repository.AccionNukaRepository;

import lombok.RequiredArgsConstructor;
import opennlp.tools.util.StringUtil;

@Service
@RequiredArgsConstructor
public class OpenAIClientService {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	AccionNukaRepository accionNukaRepository;

	@Autowired
	UtilsService utilsService;

	@Autowired
	NukaConfiguration nukaConfiguration;

	ObjectMapper objectMapper = new ObjectMapper();

	public List<AccionNuka> clasificarAccion(String texto, User usuario, NukaChat chat) {
		List<AccionNuka> accionesNuka = new ArrayList<>();
		try {
			ChatGPTResponse chatGPTResponse = clasificarAccionGPT(texto);
			if (!ObjectUtils.isEmpty(chatGPTResponse) && !ObjectUtils.isEmpty(chatGPTResponse.getChoices())
					&& !ObjectUtils.isEmpty(chatGPTResponse.getChoices().get(0).getMessage())) {
				Message mssg = chatGPTResponse.getChoices().get(0).getMessage();
				String idAccion = mssg.getContent().toUpperCase();
				Optional<AccionNuka> accionOptional = accionNukaRepository.findById(idAccion);
				if (!ObjectUtils.isEmpty(accionOptional) && !ObjectUtils.isEmpty(accionOptional.get())) {
					accionesNuka.add(accionOptional.get());
				} else {
					accionesNuka.add(new AccionNuka("CHAT", null, texto, false, "GRUPO_CHAT"));
				}
			}
		} catch (Exception e) {
			String traza = "clasificarAccion : ";
			logger.error(traza + e.getMessage());
			e.printStackTrace();
		}
		return accionesNuka;
	}

	private void auxFuntion(List<AccionNuka> accionesNukaMongo, Embedding embedding) {
		List<List<Object>> resultList = accionesNukaMongo.stream()
				.map(accion -> {
					List<Object> pair = new ArrayList<>();
					pair.add(accion);
					pair.add(utilsService.calculateCosineSimilarity(accion.embedding, embedding.getEmbedding()));
					return pair;
				})
				.sorted((pair1, pair2) -> Double.compare((Double) pair2.get(1), (Double) pair1.get(1))) // Ordena en
																										// orden
																										// descendente
				.collect(Collectors.toList());

		Map<String, Double> aux2 = new HashMap<>();

		for (List<Object> item : resultList) {

			AccionNuka accionNuka = (AccionNuka) item.get(0);
			String name = accionNuka.accionId;
			Double value = (Double) item.get(1);
			aux2.put(name, value);
		}
	}

	private List<String> parsearEnTextos(String texto)
			throws JsonMappingException, JsonProcessingException, UnirestException {
		List<String> contentList = new ArrayList<>();

		ChatGPTRequest chatGPTRequest = new ChatGPTRequest(Constantes.MODEL_DEFAULT, texto);

		ChatGPTResponse chatGPTResponse = chat(chatGPTRequest, Constantes.COMPORTAMIENTO_CLASIFICAR_ACCION);

		if (chatGPTResponse != null) {
			contentList = chatGPTResponse.getChoices().stream().map(choice -> choice.getMessage().getContent())
					.collect(Collectors.toList());
		}

		chatGPTResponse.getChoices().get(0).getMessage().getContent().replace("", "");

		String jsonString = chatGPTResponse.getChoices().get(0).getMessage().getContent();

		Gson gson = new Gson();
		contentList = gson.fromJson(jsonString, new TypeToken<List<String>>() {
		}.getType());

		return contentList;
	}

	public ChatGPTResponse clasificarAccionGPT(String texto) {
		ChatGPTRequest chatGPTRequest = new ChatGPTRequest(Constantes.MODEL_GPT4_O_MINI, texto);
		ChatGPTResponse chatGPTResponse = chat(chatGPTRequest,
				Constantes.COMPORTAMIENTO_LISTA_ACCIONES);
		return chatGPTResponse;
	}

	public AccionNuka guardarAccion(String accion, String texto, Boolean necesitaAudio, String grupo)
			throws JsonMappingException, JsonProcessingException, UnirestException {
		EmbeddingResponse embeddingResponse = peticionEmbedding(Arrays.asList(texto));

		for (Embedding embedding : embeddingResponse.getData()) {
			accionNukaRepository
					.save(new AccionNuka(accion, embedding.getEmbedding(), texto, necesitaAudio, grupo));
		}
		return null;
	}

	public EmbeddingResponse peticionEmbedding(List<String> contentList)
			throws UnirestException, JsonMappingException, JsonProcessingException {

		if (!contentList.isEmpty()) {
			EmbeddingRequest embeddingRequest = new EmbeddingRequest("text-embedding-ada-002", contentList);
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String jsonEmbeddingRequest = ow.writeValueAsString(embeddingRequest);

			HttpResponse<String> response = Unirest.post(Constantes.OPENAI_URL_EMBEDDINGS)
					.header("Authorization", "Bearer " + nukaConfiguration.getOPENAI_KEY())
					.header("Content-Type", "application/json")
					.body(jsonEmbeddingRequest).asString();

			ObjectMapper objectMapper = new ObjectMapper();
			EmbeddingResponse embeddingResponse = null;
			if (200 == response.getStatus()) {
				embeddingResponse = objectMapper.readValue(response.getBody(), EmbeddingResponse.class);
			}
			return embeddingResponse;
		}

		return null;
	}

	public ChatGPTResponse chat(ChatGPTRequest chatGPTRequest, String comportamiento) {
		try {
			modificarComportamientoChatGPTRequest(chatGPTRequest, comportamiento);

			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String jsonChatGPTRequest = ow.writeValueAsString(chatGPTRequest);

			String url = Constantes.OPENAI_URL_CHAT_COMPLETIONS;
			if (!ObjectUtils.isEmpty(chatGPTRequest)
					&& Constantes.MODEL_GPT35TURBO_INSTRUCT.equals(chatGPTRequest.getModel())) {
				url = Constantes.OPENAI_URL_COMPLETIONS;
			}

			// Configura tiempo de espera de conexión y lectura
			Unirest.setTimeouts(Constantes.UNIREST_CONNECTION_TIMEOUT, Constantes.UNIREST_SOCKET_TIMEOUT);

			HttpResponse<String> response = Unirest.post(url)
					.header("Authorization", "Bearer " + nukaConfiguration.getOPENAI_KEY())
					.header("Content-Type", "application/json")
					.body(jsonChatGPTRequest).asString();

			ObjectMapper objectMapper = new ObjectMapper();
			ChatGPTResponse chatGPTResponse = null;
			if (200 == response.getStatus()) {
				chatGPTResponse = objectMapper.readValue(response.getBody(), ChatGPTResponse.class);
			} else {
				try {
					JsonNode rootNode = objectMapper.readTree(response.getBody());
					JsonNode errorNode = rootNode.path("error");
					String errorMessage = errorNode.path("message").asText();
					String errorRefusal = errorNode.path("refusal").asText();
					String traza = "chat : ";
					logger.error(traza + errorMessage);

					chatGPTResponse = new ChatGPTResponse();
					Choice choice = new Choice();
					choice.setFinishReason(Constantes.OPENAI_RESPONSE_ERROR);
					choice.setMessage(new Message(Constantes.OPENAI_RESPONSE_ERROR, errorMessage, errorRefusal));
					chatGPTResponse.setChoices(Arrays.asList(choice));
				} catch (IOException e) {
					logger.error("Error al parsear la respuesta de error", e);
				}
			}

			return chatGPTResponse;
		} catch (Exception e) {
			String traza = "chat : ";
			logger.error(traza + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public GeneratedImageResponse generarImagen(GenerateImageRequest imageGeneratorRequest) {
		try {

			GeneratedImageResponse generatedImageResponse = null;
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String jsonGPTRequest = ow.writeValueAsString(imageGeneratorRequest);

			String url = Constantes.OPENAI_URL_IMAGES_GENERATION;

			HttpResponse<String> response = Unirest.post(url)
					.header("Authorization", "Bearer " + nukaConfiguration.getOPENAI_KEY())
					.header("Content-Type", "application/json")
					.body(jsonGPTRequest).asString();

			ObjectMapper objectMapper = new ObjectMapper();
			if (200 == response.getStatus()) {
				generatedImageResponse = objectMapper.readValue(response.getBody(), GeneratedImageResponse.class);
			}

			return generatedImageResponse;
		} catch (Exception e) {
			String traza = "generarImagen : ";
			logger.error(traza + e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public void modificarComportamientoChatGPTRequest(ChatGPTRequest chatGPTRequest, String comportamiento) {
		if (comportamiento != null) {
			// Estos modelos NO aceptan el Rol System, por eso se sustituye por User
			Message newMessage;
			if (chatGPTRequest.getModel().equals(Constantes.MODEL_O1_PREVIEW)
					|| chatGPTRequest.getModel().equals(Constantes.MODEL_O1_MINI)) {
				newMessage = new Message(Constantes.ROLE_OPENAI_USER, comportamiento, null);
			} else {
				newMessage = new Message(Constantes.ROLE_OPENAI_SYSTEM, comportamiento, null);
			}
			chatGPTRequest.getMessages().add(0, newMessage);
		}
	}

	public WhisperTranscriptionResponse createTranscriptionWithFilePath(String filePath)
			// ['m4a', 'mp3', 'webm', 'mp4', 'mpga', 'wav', 'mpeg', 'ogg', 'oga', 'flac']"
			throws UnirestException, IOException {

		HttpResponse<String> response = Unirest.post(Constantes.OPENAI_URL_TRANSCRIPTIONS)
				.header("Authorization", "Bearer " + nukaConfiguration.getOPENAI_KEY())
				.field("model", Constantes.MODEL_WHISPER).field("language", "es")
				.field("file", new File(filePath)).asString();

		WhisperTranscriptionResponse whisperTranscriptionResponse = null;
		if (200 == response.getStatus()) {
			ObjectMapper objectMapper = new ObjectMapper();
			whisperTranscriptionResponse = objectMapper.readValue(response.getBody(),
					WhisperTranscriptionResponse.class);
		}

		return whisperTranscriptionResponse;
	}

	public WhisperTranscriptionResponse createTranscriptionWithMultipartFile(MultipartFile file)
			throws UnirestException, IOException {
		WhisperTranscriptionRequest whisperTranscriptionRequest = new WhisperTranscriptionRequest(
				Constantes.MODEL_WHISPER, file);
		String filePath = guardarAudio(whisperTranscriptionRequest);

		setAudioModel(whisperTranscriptionRequest, Constantes.MODEL_WHISPER);

		HttpResponse<String> response = Unirest.post(Constantes.OPENAI_URL_TRANSCRIPTIONS)
				.header("Authorization", "Bearer " + nukaConfiguration.getOPENAI_KEY()).field("model", "whisper-1")
				.field("language", "es")
				.field("file", new File(filePath)).asString();

		WhisperTranscriptionResponse whisperTranscriptionResponse = null;
		if (200 == response.getStatus()) {
			ObjectMapper objectMapper = new ObjectMapper();
			whisperTranscriptionResponse = objectMapper.readValue(response.getBody(),
					WhisperTranscriptionResponse.class);
		}

		return whisperTranscriptionResponse;
	}

	public WhisperTranscriptionResponse createTranscription(WhisperTranscriptionRequest whisperTranscriptionRequest)
			throws UnirestException, IOException {
		String filePath = guardarAudio(whisperTranscriptionRequest);
		whisperTranscriptionRequest.setModel(Constantes.MODEL_WHISPER);

		setAudioModel(whisperTranscriptionRequest, Constantes.MODEL_WHISPER);

		HttpResponse<String> response = Unirest.post(Constantes.OPENAI_URL_TRANSCRIPTIONS)
				.header("Authorization", "Bearer " + nukaConfiguration.getOPENAI_KEY()).field("model", "whisper-1")
				.field("language", "es")
				.field("file", new File(filePath)).asString();

		ObjectMapper objectMapper = new ObjectMapper();
		WhisperTranscriptionResponse whisperTranscriptionResponse = null;
		if (200 == response.getStatus()) {
			whisperTranscriptionResponse = objectMapper.readValue(response.getBody(),
					WhisperTranscriptionResponse.class);
		}

		return whisperTranscriptionResponse;
	}

	private String guardarAudio(WhisperTranscriptionRequest whisperTranscriptionRequest) {
		String filePath = null;

		if (!whisperTranscriptionRequest.getFile().isEmpty()) {
			try {
				String fileName = whisperTranscriptionRequest.getFile().getOriginalFilename();
				String uploadDir = System.getProperty("user.dir") + "\\src\\main\\resources\\audio";

				filePath = uploadDir + File.separator + fileName + ".webm";
				whisperTranscriptionRequest.getFile().transferTo(new File(filePath));
			} catch (IOException e) {
				String traza = "guardarAudio : ";
				logger.error(traza + e.getMessage());
				e.printStackTrace();
			}
		} else {
			String traza = "guardarAudio : ";
			logger.error(traza + "El archivo está vacío");
		}

		return filePath;
	}

	private void setAudioModel(WhisperTranscriptionRequest whisperTranscriptionRequest, String model) {
		if (!ObjectUtils.isEmpty(model)) {
			whisperTranscriptionRequest.setModel(model);
		} else {
			whisperTranscriptionRequest.setModel(Constantes.MODEL_WHISPER);
		}
	}

	/*
	 * DEPRECATED
	 */

	// Se usan embedding en lugar de que la eleccion la haga el propio GPT. No va
	// bien muchas veces, es interesante mantenerlo por el uso de los embeddings
	public List<AccionNuka> clasificarAccion2(String texto, User usuario, NukaChat chat)
			throws JsonMappingException, JsonProcessingException, UnirestException {
		List<String> contentList = new ArrayList<>();
		List<AccionNuka> accionesNuka = new ArrayList<>();

		if (!StringUtil.isEmpty(texto)) {

			// esto lo usaremos para por configuracion porder elegir si se dice mas de un
			// mensaje simultaneamente
			if (chat != null && false) {
				contentList = parsearEnTextos(texto);
			} else {
				contentList = Arrays.asList(texto);
			}

			EmbeddingResponse embeddingResponse = peticionEmbedding(contentList);
			List<AccionNuka> accionesNukaMongo = accionNukaRepository.findAll();

			int icl = 0;
			for (Embedding embedding : embeddingResponse.getData()) {
				Optional<AccionNuka> accionNukaMax = accionesNukaMongo.parallelStream()
						.max((accion1, accion2) -> Double.compare(
								utilsService.calculateCosineSimilarity(accion1.embedding, embedding.getEmbedding()),
								utilsService.calculateCosineSimilarity(accion2.embedding, embedding.getEmbedding())));
				if (accionNukaMax.isPresent()) {

					if (utilsService.calculateCosineSimilarity(accionNukaMax.get().embedding,
							embedding.getEmbedding()) > 0.9) {
						accionesNuka.add(new AccionNuka(accionNukaMax.get().accionId, embedding.getEmbedding(),
								contentList.get(icl), accionNukaMax.get().necesitaAudio, accionNukaMax.get().grupo));
					} else {
						accionesNuka.add(new AccionNuka("CHAT", embedding.getEmbedding(),
								contentList.get(icl), false, "GRUPO_CHAT"));
					}

				}
				icl++;

				auxFuntion(accionesNukaMongo, embedding);
			}
		}
		return accionesNuka;
	}

}