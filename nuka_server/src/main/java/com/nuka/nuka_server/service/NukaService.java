package com.nuka.nuka_server.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.*;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.checkerframework.checker.units.qual.C;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.nuka.nuka_server.Constantes;
import com.nuka.nuka_server.NukaConfiguration;
import com.nuka.nuka_server.models.AccionNuka;
import com.nuka.nuka_server.models.OpcionesModal;
import com.nuka.nuka_server.models.ChatGPTRequest;
import com.nuka.nuka_server.models.ChatGPTResponse;
import com.nuka.nuka_server.models.ContenidoMsj;
import com.nuka.nuka_server.models.ElementoSeleccionado;
import com.nuka.nuka_server.models.EmbeddingResponse;
import com.nuka.nuka_server.models.EstadoMsj;
import com.nuka.nuka_server.models.GrupoOpciones;
import com.nuka.nuka_server.models.NukaChat;
import com.nuka.nuka_server.models.NukaMsj;
import com.nuka.nuka_server.models.OpcionBasica;
import com.nuka.nuka_server.models.Product;
import com.nuka.nuka_server.models.ProductRelation;
import com.nuka.nuka_server.models.ShoppingList;
import com.nuka.nuka_server.models.TipoMsj;
import com.nuka.nuka_server.models.TipoMsjEstado;
import com.nuka.nuka_server.models.User;
import com.nuka.nuka_server.models.WhisperTranscriptionResponse;
import com.nuka.nuka_server.repository.AccionNukaRepository;
import com.nuka.nuka_server.repository.NukaChatRepository;
import com.nuka.nuka_server.repository.NukaMsjRepository;
import com.nuka.nuka_server.repository.ProductRepository;
import com.nuka.nuka_server.repository.ShoppingListRepository;
import com.nuka.nuka_server.repository.UserRepository;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;

@Service
public class NukaService {

	Logger logger = LoggerFactory.getLogger(NukaService.class);
	Map<String, Supplier<NukaMsj>> mapAcciones = new HashMap<>();
	private NukaMsj nukaMsjPeticion;
	private Boolean peticionBloqueada = false;

	// Uso inicioHeader por que en generarConocimiento se usa para detectar el principio de un documento
	String inicioHeader = "[Inicio";
	String header = "\n" + inicioHeader + " Documento:  (Extraído a formato texto)]\n";
	String footer = "\n[Fin Documento: ]\n";
	String contendoresDockerActivos = "\n[Fin Documento: ]\n";

	@Autowired
	UserRepository userRepository;

	@Autowired
	ShoppingListRepository shoppingListRepository;

	@Autowired
	NukaMsjRepository nukaMsjRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	NukaChatRepository nukaChatRepository;

	@Autowired
	AccionNukaRepository accionNukaRepository;
	
	@Autowired
	OpenAIClientService openAIClientService;

	@Autowired
	AzureTextToSpeechService azureTextToSpeechService;

	@Autowired
	UtilsService utilsService;

	@Autowired
	AccionesService accionesService;

	@Autowired
	NukaConfiguration nukaConfiguration;

	public Map<String, Supplier<NukaMsj>> getMapAcciones() {
	return mapAcciones;
	}

	public void setMapAcciones(Map<String, Supplier<NukaMsj>> mapAcciones) {
		this.mapAcciones = mapAcciones;
	}

	public ShoppingList confirmarProductosSeleccionados(ShoppingList shoppingList,
		List<ProductRelation> productsMarcados) {

	Set<String> idsMarcados = productsMarcados.stream().map(product -> product.getProductInfo().getId())
		.collect(Collectors.toSet());

	List<ProductRelation> productsToKeep = shoppingList.getProductRelations().stream()
		.filter(product -> !idsMarcados.contains(product.getProductInfo().getId()))
		.collect(Collectors.toList());

	shoppingList.setIsFinished(true);
	shoppingList.setProductRelations(productsMarcados);
	shoppingList.setDatePurchase(utilsService.timestampNowString());
	shoppingListRepository.save(shoppingList);

	ShoppingList resShoppingList = new ShoppingList(null, false, productsToKeep, null);
	shoppingListRepository.insert(resShoppingList);

	return resShoppingList;
	}

	public List<String> initValorAjustesChat_LIST(NukaChat chat, String ETIQUETA) {
	if (!ObjectUtils.isEmpty(chat)) {
		List<String> aux = obtenerValorOpcionesModal(chat.getOpcionesModal().getOpciones(), ETIQUETA);
		return !ObjectUtils.isEmpty(aux) ? aux : null;
	}
	return null;
	}

	public Integer initValorAjustesChat_INTEGER(NukaChat chat, String ETIQUETA) {
	if (!ObjectUtils.isEmpty(chat)) {
		List<String> aux = obtenerValorOpcionesModal(chat.getOpcionesModal().getOpciones(), ETIQUETA);
		return !ObjectUtils.isEmpty(aux) ? Integer.parseInt(aux.get(0)) : null;
	}
	return null;
	}

	public Boolean initValorAjustesChat_BOOLEAN(NukaChat chat, String ETIQUETA) {
	if (!ObjectUtils.isEmpty(chat)) {
		List<String> aux = obtenerValorOpcionesModal(chat.getOpcionesModal().getOpciones(), ETIQUETA);
		return !ObjectUtils.isEmpty(aux) && !ObjectUtils.isEmpty(aux.get(0)) ? 
				Boolean.valueOf(aux.get(0)) : null;
	}
	return null;
	}
	
	public String initValorAjustesChat_STRING(NukaChat chat, String ETIQUETA) {
	if (!ObjectUtils.isEmpty(chat)) {
		List<String> aux = obtenerValorOpcionesModal(chat.getOpcionesModal().getOpciones(), ETIQUETA);
		return !ObjectUtils.isEmpty(aux) ? aux.get(0) : null;
	}
	return null;
	}

	public ChatGPTResponse preguntaGPT() {
	try {
		NukaMsj nukaMsjPeticion = this.nukaMsjPeticion;
		Optional<NukaChat> chatOptional = nukaChatRepository.findById(nukaMsjPeticion.getChatId());

		String mensaje = nukaMsjPeticion.texto;
		Integer CHAT_NUMERO_CHATS_CONTEXTO = initValorAjustesChat_INTEGER(chatOptional.get(),
			"CHAT_NUMERO_CHATS_CONTEXTO");
		
		Boolean CHAT_CONTEXTO_COMPLETO = initValorAjustesChat_BOOLEAN(chatOptional.get(),
				"CHAT_CONTEXTO_COMPLETO");
		
		String CHAT_ROL = initValorAjustesChat_STRING(chatOptional.get(), "CHAT_ROL");
		String CHAT_ESTRUCTURA_RESPUESTA = initValorAjustesChat_STRING(chatOptional.get(),
			"CHAT_ESTRUCTURA_RESPUESTA");
		String CHAT_MODELO = initValorAjustesChat_STRING(chatOptional.get(), "CHAT_MODELO");

		if (ObjectUtils.isEmpty(CHAT_MODELO)) {
		CHAT_MODELO = Constantes.MODEL_DEFAULT;
		}

		if (!ObjectUtils.isEmpty(CHAT_ROL)) {
		CHAT_ROL = "Rol: " + CHAT_ROL + "\r\n";
		} else {
		CHAT_ROL = Constantes.COMPORTAMIENTO_POR_DEFECTO;
		}

		if (!ObjectUtils.isEmpty(CHAT_ESTRUCTURA_RESPUESTA)) {
		CHAT_ROL += ("Respuesta: " + CHAT_ESTRUCTURA_RESPUESTA + "\r\n");
		}


		if ((!ObjectUtils.isEmpty(CHAT_NUMERO_CHATS_CONTEXTO) && CHAT_NUMERO_CHATS_CONTEXTO > 0) || (CHAT_CONTEXTO_COMPLETO != null && CHAT_CONTEXTO_COMPLETO)) {

		List<String> aux = new ArrayList<>();
		aux.add(chatOptional.get().getId());
		
		Pageable pageable = null;
		if (CHAT_CONTEXTO_COMPLETO != null && CHAT_CONTEXTO_COMPLETO) {
			 Long numeroMensajes = nukaMsjRepository.countByChatIds(Arrays.asList(nukaMsjPeticion.getChatId()));
			 pageable = PageRequest.of(0, numeroMensajes.intValue() + 2);
		} else {
			pageable = PageRequest.of(0, CHAT_NUMERO_CHATS_CONTEXTO);
		}
		
		List<NukaMsj> nukaMsjs = nukaMsjRepository.findTopNByChatIdsAndFecha(aux,
			this.utilsService.timestampNowLong(), pageable);
		Collections.reverse(nukaMsjs);

		String mensajeRes = "";
		for (NukaMsj msj : nukaMsjs) {
			if (msj.tipoMsj == TipoMsj.PETICION && !ObjectUtils.isEmpty(msj.getTexto())) {
			mensajeRes += "user: " + msj.getTexto() + "\r\n";
			}

			if (msj.tipoMsj == TipoMsj.RESPUESTA && !ObjectUtils.isEmpty(msj.getTexto())) {
			mensajeRes += "assistant: " + msj.getTexto() + "\r\n";
			} else if (msj.tipoMsj == TipoMsj.RESPUESTA && !ObjectUtils.isEmpty(msj.getTarea())) {
			mensajeRes += ("assistant: Se ha realizado la tarea " + msj.getTarea() + "\r\n");
			}
		}

		mensajeRes += ("Usuario: " + nukaMsjPeticion.texto + "\r\n");
		mensajeRes += ("Asistente: ");

		if (!ObjectUtils.isEmpty(mensajeRes)) {
			mensaje = mensajeRes;
		}
		}


		ChatGPTRequest chatGPTRequest = new ChatGPTRequest(CHAT_MODELO, procesarLongitudMensaje(mensaje, CHAT_MODELO));
		ChatGPTResponse chatGPTResponse = openAIClientService.chat(chatGPTRequest, CHAT_ROL);

		if (chatGPTResponse != null) {
		//nukaMsjRespuesta.texto = chatGPTResponse.getChoices().get(0).getMessage().getContent();
		return chatGPTResponse;
		}

		return null;
	} catch (Exception e) {
		String traza = "preguntaGPT : ";
		logger.error(traza + e.getMessage());
		// e.printStackTrace();
	}

	return null;
	}

	public String procesarLongitudMensaje(String mensaje, String modelo) {
		Integer limiteModelo = Constantes.LIMITE_TOKENS_POR_MODELO.get(modelo);
		
		// Se devide entre 4 por que 4 caracteres es mas o menos 1 token
		if ((mensaje.length()/4) > limiteModelo) {  
			// Acorta el mensaje si excede el límite de caracteres
			mensaje = mensaje.substring(0, limiteModelo.intValue());

			// error - mandar mensansaje de que no se ha podido procesar toda la entrada de texto 
		}

		// Lógica para procesar el mensaje
		return mensaje;
	}

	public Product guardarProducto(String nombreProducto, List<String> aliasProducto, String categoria)
		throws IOException, UnirestException {
	Product product = new Product();
	product.setNombreProducto(nombreProducto);
	product.setAliasProducto(aliasProducto);
	product.setCategoria(categoria);
	EmbeddingResponse embbeding = openAIClientService.peticionEmbedding(aliasProducto);
	product.setEmbedding(embbeding.getData().get(0).getEmbedding());
	Product productResponse = productRepository.save(product);
	return productResponse;
	}

	private TipoMsj checkNukaPeticion(NukaMsj nukaMsjPeticion) {
	if (nukaMsjPeticion.texto != null || nukaMsjPeticion.audioFile != null) {
		return nukaMsjPeticion.tipoMsj;
	}
	return null;
	}

	public void guardarContenidoMsj(NukaMsj nukaMsjPeticion, List<MultipartFile> contenidoMsj) {
	List<ContenidoMsj> res = new ArrayList<>();
	for (MultipartFile c : contenidoMsj) {
		String tipo = utilsService.getResourceMapKeyByValue(c.getContentType());
		String id = utilsService.generarIdMongo();
		ContenidoMsj contenido = new ContenidoMsj(id, tipo);
		utilsService.guardarMultipartFile(c, id);
		res.add(contenido);
	}

	nukaMsjPeticion.setContenidoMsj(res);
	}

	public void segmentarAudio(String filePath) throws UnirestException, IOException, InterruptedException {
		
		ProcessBuilder processBuilder = new ProcessBuilder(
			"ffmpeg", "-i", filePath, "-f", "segment", "-segment_time", "120",
			"-c", "copy", Constantes.TMP_PATH + "%03d.wav"
		);
		
		processBuilder.redirectErrorStream(true);
		Process process = processBuilder.start();
		int exitCode = process.waitFor();

		if (exitCode == 0) {
			// Se han creado los archivos segmentados
		} else {
			// Manejo de errores
		}
		

		List<String> filePaths = new ArrayList<>();

		String stringRes = "";
		if (exitCode == 0) {
			File dir = new File(Constantes.TMP_PATH);
			File[] files = dir.listFiles((d, name) -> name.endsWith(".wav"));
			
			if (files != null) {
				for (File file : files) {
					filePaths.add(file.getAbsolutePath());
				}
			}
			
			Collections.sort(filePaths, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					String[] parts1 = o1.split("/");
					String[] parts2 = o2.split("/");
					return parts1[parts1.length - 1].compareTo(parts2[parts2.length - 1]);
				}
			});
			
			// Ahora puedes procesar cada archivo
			for (String path : filePaths) {
				
				WhisperTranscriptionResponse whisperTranscriptionResponse = openAIClientService.createTranscriptionWithFilePath(path);
				if (whisperTranscriptionResponse != null) {
					
					stringRes += (" " + whisperTranscriptionResponse.getText());
				} else {
					stringRes = null;
					break;
				}
			}
		}
	}

	public List<NukaMsj> nukaMsjPeticion(NukaMsj nukaMsjPeticion, MultipartFile audioFile,
		List<MultipartFile> contenidoMsj) throws UnirestException, InterruptedException, ExecutionException,
		IOException, UnsupportedAudioFileException {
	List<NukaMsj> NukaMsjRespuesta = new ArrayList<>();
	Optional<User> usuario = userRepository.findById(nukaMsjPeticion.usuarioId);
	Optional<NukaChat> chat = nukaChatRepository.findById(nukaMsjPeticion.getChatId());
	Boolean errorEnRespuesta = false;
	
	if (nukaMsjPeticion.tipoMsj == TipoMsj.PETICION) {
		if (!ObjectUtils.isEmpty(audioFile)) {

		String filePath = null;
		if (nukaMsjPeticion.audioResource.equals("audioWebm")) {
			filePath = utilsService.guardarAudio(audioFile, nukaMsjPeticion.id + ".webm");
		} else if (nukaMsjPeticion.audioResource.equals("audioAac")) {
			String filePathAAC = utilsService.guardarAudio(audioFile, nukaMsjPeticion.id + ".aac");
			filePath = utilsService.convertToWav(filePathAAC);
			if (utilsService.borrarArchivo(filePathAAC)) {
				nukaMsjPeticion.setAudioResource("audioWav");
			}
		}

		ProcessBuilder processBuilder = new ProcessBuilder(
			"ffmpeg", "-i", filePath, "-f", "segment", "-segment_time", "120",
			"-c", "copy", Constantes.TMP_PATH + "%03d.wav"
		);
		
		
		processBuilder.redirectErrorStream(true);
		Process process = processBuilder.start();
		int exitCode = process.waitFor();

		if (exitCode == 0) {
			// Se han creado los archivos segmentados
		} else {
			// Manejo de errores
		}
		

		List<String> filePaths = new ArrayList<>();

		String stringRes = "";
		if (exitCode == 0) {
			File dir = new File(Constantes.TMP_PATH);
			File[] files = dir.listFiles((d, name) -> name.endsWith(".wav"));
			
			if (files != null) {
				for (File file : files) {
					filePaths.add(file.getAbsolutePath());
				}
			}
			
			Collections.sort(filePaths, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					String[] parts1 = o1.split("/");
					String[] parts2 = o2.split("/");
					return parts1[parts1.length - 1].compareTo(parts2[parts2.length - 1]);
				}
			});
			
			// Ahora puedes procesar cada archivo
			for (String path : filePaths) {
				
				WhisperTranscriptionResponse whisperTranscriptionResponse = openAIClientService.createTranscriptionWithFilePath(path);
				if (whisperTranscriptionResponse != null) {
					
					stringRes += (" " + whisperTranscriptionResponse.getText());
				} else {
					stringRes = null;
					break;
				}
			}
			

			for (File file : files) {
				utilsService.borrarArchivo(file.getAbsolutePath());
			}
		} else {
			// Manejo de errores
		}
		
		if (stringRes != null) {
			nukaMsjPeticion.texto = stringRes;
		}

		} else if (nukaMsjPeticion.texto == null) {
		throw new IllegalArgumentException("Sin datos de entrada");
		}

		List<AccionNuka> accionesNuka = new ArrayList<>();
		if (nukaMsjPeticion.tarea != null) {
		Optional<AccionNuka> accionNuka = accionNukaRepository.findById(nukaMsjPeticion.tarea);
		if (accionNuka.get() != null) {
			accionesNuka.add(accionNuka.get());
		} else {
			throw new IllegalArgumentException("Error al obtener accionNukaMongo");
		}
		} else {
		accionesNuka
			.addAll(openAIClientService.clasificarAccion(nukaMsjPeticion.texto, usuario.get(), chat.get()));
		}

		for (AccionNuka accion : accionesNuka) {
			NukaMsj nukaMsj = new NukaMsj();
	
			int contadorPB = 0; // Este codigo es para reintentar por si this.nukaPeticion estas bloqueada
			while (contadorPB < 10) {
				if (!this.peticionBloqueada) {
				this.peticionBloqueada = true;
				this.nukaMsjPeticion = nukaMsjPeticion;
				if (mapAcciones.containsKey(accion.accionId)) {
					Supplier<NukaMsj> metodo = mapAcciones.get(accion.accionId);
					nukaMsj = metodo.get();
					break;
				} else {
					ChatGPTResponse chatGPTResponse = preguntaGPT();
					if (chatGPTResponse != null) {
					nukaMsj = inicializarRespuestaMsj("CHAT");
					
					// Cuando recibimos un error al llamar a la API de OpenAI 
					if (Constantes.OPENAI_RESPONSE_ERROR.equals(chatGPTResponse.getChoices().get(0).getFinishReason())) {
						errorEnRespuesta = true;
						StringBuilder respuestaGPT = new StringBuilder();
						respuestaGPT.append("**" + chatGPTResponse.getChoices().get(0).getFinishReason() + "**").append(" → ");
						respuestaGPT.append("\n");
						respuestaGPT.append(chatGPTResponse.getChoices().get(0).getMessage().getContent());
						nukaMsj.texto = respuestaGPT.toString();
						
					} else {
						// Cuando la respuesta de la API es buena
						nukaMsj.texto = chatGPTResponse.getChoices().get(0).getMessage().getContent();
					}
					} 
					break;
				}
				}
	
				try {
				Thread.sleep(50);
				contadorPB++;
				} catch (InterruptedException e) {
				// Manejar la excepción si es necesario
				}
			}
	
			Boolean forzarAudio = Boolean
				.valueOf(initValorAjustesChat_STRING(chat.get(), "AUDIO_FORZAR_GENERAR_AUDIO"));
			if (!errorEnRespuesta && (forzarAudio || nukaMsj.texto != null && (Boolean.TRUE.equals(accion.necesitaAudio)))) {
				String wavFilename = nukaMsj.id + ".wav";
				String voz = initValorAjustesChat_STRING(chat.get(), "AUDIO_VOZ");
				Boolean audioGuardado = azureTextToSpeechService.sintetizar(nukaMsj.texto, wavFilename, voz, null);
				if (audioGuardado) {
				nukaMsj.setNeedAudio(true);
				nukaMsj.setAudioResource("audioWav");
				}
			}
	
			NukaMsjRespuesta.add(nukaMsj);
			}
	
			if (!errorEnRespuesta) {
			nukaMsjPeticion.setRespondido(true);
			} else {
			nukaMsjPeticion.setRespondido(false);
			}
			
		} else if (checkNukaPeticion(nukaMsjPeticion) == TipoMsj.RESPUESTA) {
			// Algun codigo que se le tengas que añadir contexto
		}
	
		// Se utiliza para en Chats ver cual es la fecha del ultimo mensaje (Se podria hacer asincronamente)
		if (NukaMsjRespuesta.size() > 0) {
			actualizarChatUltimoMsj(NukaMsjRespuesta.get(0));
		}
		
		NukaMsjRespuesta.add(0, nukaMsjPeticion);
		
		return NukaMsjRespuesta;
	}

	public String generarTitulo(String mensaje) {
		String res = null;
		
		String CHAT_ROL = Constantes.COMPORTAMIENTO_AUTONOMBRAR_CHAT;
		ChatGPTRequest chatGPTRequest = new ChatGPTRequest(Constantes.MODEL_DEFAULT, mensaje);
		ChatGPTResponse chatGPTResponse = openAIClientService.chat(chatGPTRequest, CHAT_ROL);
		
		if (Constantes.OPENAI_RESPONSE_ERROR.equals(chatGPTResponse.getChoices().get(0).getFinishReason())) {
			return "Error al generar titulo";
		}
		
		res = chatGPTResponse.getChoices().get(0).getMessage().getContent();
		
		return res;
	}

	public String generarTitulo(List<NukaMsj> mensajes) {
		String res = null;
		String mensaje = null;
		String mensajeRes = "";
		for (NukaMsj msj : mensajes) {
			if (msj.tipoMsj == TipoMsj.PETICION && !ObjectUtils.isEmpty(msj.getTexto())) {
			mensajeRes += "user: " + msj.getTexto() + "\r\n";
			}
	
			if (msj.tipoMsj == TipoMsj.RESPUESTA && !ObjectUtils.isEmpty(msj.getTexto())) {
			mensajeRes += "assistant: " + msj.getTexto() + "\r\n";
			} else if (msj.tipoMsj == TipoMsj.RESPUESTA && !ObjectUtils.isEmpty(msj.getTarea())) {
			mensajeRes += ("assistant: Se ha realizado la tarea " + msj.getTarea() + "\r\n");
			}
		}
	
		mensajeRes += ("Usuario: " + nukaMsjPeticion.texto + "\r\n");
		mensajeRes += ("Asistente: ");
	
		if (!ObjectUtils.isEmpty(mensajeRes)) {
			mensaje = mensajeRes;
		}
		
		String CHAT_ROL = Constantes.COMPORTAMIENTO_AUTONOMBRAR_CHAT;
		ChatGPTRequest chatGPTRequest = new ChatGPTRequest(Constantes.MODEL_GPT4_O_MINI, mensaje);
		ChatGPTResponse chatGPTResponse = openAIClientService.chat(chatGPTRequest, CHAT_ROL);
		
		if (Constantes.OPENAI_RESPONSE_ERROR.equals(chatGPTResponse.getChoices().get(0).getFinishReason())) {
			return "Error al generar titulo";
		}
		
		res = chatGPTResponse.getChoices().get(0).getMessage().getContent();
		
		return res;
	}
	
	public void actualizarChatUltimoMsj(NukaMsj nukaRespuesta) {
		Optional<NukaChat> chat = nukaChatRepository.findById(nukaRespuesta.chatId);
		if (!chat.isEmpty()) {
			chat.get().setFechaUltimoMsj(nukaRespuesta.fecha);
			nukaChatRepository.save(chat.get());
		}
	}

	public NukaMsj inicializarRespuestaMsj(String tarea) {
	this.peticionBloqueada = false;
	NukaMsj nukaMsj = new NukaMsj();
	Instant timestamp = Instant.now();
	nukaMsj.setChatId(nukaMsjPeticion.getChatId());
	nukaMsj.setUsuarioId(nukaMsjPeticion.getUsuarioId());
	nukaMsj.setFecha(timestamp.toEpochMilli());
	nukaMsj.setId(nukaMsj.getFecha() + "_" + utilsService.generarIdAleatorio(24));
	nukaMsj.setTarea(tarea);
	nukaMsj.setTipoMsj(TipoMsj.RESPUESTA);
	return nukaMsj;
	}
	
	public OpcionesModal guardarAjustesChat(Map<String, List<String>> opcionesModificadas, String chatId) {
	if (!ObjectUtils.isEmpty(opcionesModificadas)) {
		Optional<NukaChat> nukaChat = nukaChatRepository.findById(chatId);

		if (!ObjectUtils.isEmpty(nukaChat.get())) {
		OpcionesModal opcionesModalOriginal = nukaChat.get().getOpcionesModal();

		opcionesModalOriginal.getOpciones().forEach(opcion -> {
			opcion.getOpciones().stream().forEach(o -> {
			opcionesModificadas.keySet().stream().forEach(d -> {
				if (d.equals(o.id)) {
				o.setValor(opcionesModificadas.get(o.id));
				}
			});
			});
		});

		datosParaActualizarVista(nukaChat.get(), opcionesModalOriginal);

		nukaChat.get().setOpcionesModal(opcionesModalOriginal);
		nukaChatRepository.save(nukaChat.get());

		return opcionesModalOriginal;
		}
	}

	return null;
	}

	public User guardarAjustesUsuario(Map<String, List<String>> opcionesModificadas, String userId) {
	if (!ObjectUtils.isEmpty(opcionesModificadas)) {
		Optional<User> user = userRepository.findById(userId);

		if (!ObjectUtils.isEmpty(user.get())) {
		OpcionesModal opcionesModalOriginal = user.get().getOpcionesModal();

		opcionesModalOriginal.getOpciones().forEach(opcion -> {
			opcion.getOpciones().stream().forEach(o -> {
			opcionesModificadas.keySet().stream().forEach(d -> {
				if (d.equals(o.id)) {
				o.setValor(opcionesModificadas.get(o.id));
				}
			});
			});
		});

		user.get().setOpcionesModal(opcionesModalOriginal);
		User userSaved = userRepository.save(user.get());
		return userSaved;
		}
	}
	return null;
	}

	public void datosParaActualizarVista(NukaChat nukaChat, OpcionesModal opcionesModal) {
	if (opcionesModal != null && opcionesModal.getOpciones() != null) {
		// Modificar titulo
		if (!"GENERAL".equals(nukaChat.summary)) {
		nukaChat.setSummary(
			obtenerValorOpcionesModal(opcionesModal.getOpciones(), "GENERAL_NOMBRE_CHAT").get(0));
		}

		// Modificar tags
		OpcionBasica obTags = utilsService.getOpcionBasica(nukaChat, "GENERAL", "GENERAL_TAGS");
		if (!ObjectUtils.isEmpty(obTags)) {
		if (!ObjectUtils.isEmpty(obTags)) {
			nukaChat.setTags(obTags.getValor());
		}
		}

	}
	}

	public List<String> obtenerValorOpcionesModal(List<GrupoOpciones> opciones, String id) {
	for (GrupoOpciones opcion : opciones) {
		if (opcion.getOpciones() != null) {
		for (OpcionBasica subOpcion : opcion.getOpciones()) {
			if (subOpcion.getId().equals(id) && subOpcion.getValor() != null) {
			return subOpcion.getValor();
			}
		}
		}
	}
	return null;
	}

	public void eliminarOpcionPorId(OpcionesModal opcionesModal, String idAEliminar) {
	List<GrupoOpciones> opciones = opcionesModal.getOpciones();
	for (GrupoOpciones grupoOpcion : opciones) {
		int i = 0;
		for (OpcionBasica ob : grupoOpcion.getOpciones()) {
		if (ob.getId().equals(idAEliminar)) {
			grupoOpcion.getOpciones().remove(i);
			break;
		}
		i++;
		}
	}
	}

	public OpcionesModal initNukaChatConfig(NukaChat nukaChat) {
	OpcionesModal OpcionesModal = null;
	try {
		OpcionesModal = inicializarAjustesChat();
		Map<String, List<String>> opcionesPorDefecto = new HashMap<>();
		opcionesPorDefecto.put(Constantes.GENERAL_NOMBRE_CHAT, Arrays.asList(nukaChat.summary));
		OpcionesModal.getOpciones().forEach(opcion -> {
		opcion.getOpciones().stream().forEach(o -> {
			opcionesPorDefecto.keySet().stream().forEach(d -> {
			if (d.equals(o.id)) {
				o.setValor(opcionesPorDefecto.get(o.id));
			}
			});
		});
		});

		if ("GENERAL".equals(nukaChat.summary)) {
		eliminarOpcionPorId(OpcionesModal, "GENERAL_NOMBRE_CHAT");
		}

	} catch (Exception e) {
		e.printStackTrace();
	}

	return OpcionesModal;
	}

	public NukaChat agregarChat(String usuarioId, String summary) {
	NukaChat nukaChat = new NukaChat();
	nukaChat.setVisible(true);
	nukaChat.setUsuarioId(usuarioId);
	nukaChat.setTags(new ArrayList<>());
	nukaChat.summary = summary;
	nukaChat.setFechaCreacion(utilsService.timestampNowLong());
	nukaChat.setFechaUltimoMsj(utilsService.timestampNowLong());
	nukaChat.setOpcionesModal(initNukaChatConfig(nukaChat));

	NukaChat nukaChatResponse = nukaChatRepository.insert(nukaChat);
	return nukaChatResponse;
	}

	public OpcionesModal inicializarAjustesChat() throws Exception {
	ObjectMapper objectMapper = new ObjectMapper();
	String jsonFilePath = Constantes.OPCIONES_AJUSTES_CHAT_PATH;
	Resource resource = new UrlResource(Paths.get(jsonFilePath).toUri());
	if (resource.exists()) {
		OpcionesModal myData = objectMapper.readValue(resource.getInputStream(), OpcionesModal.class);
		return myData;
	} else {
		throw new Exception("El archivo JSON no existe en " + jsonFilePath);
	}
	}

	public OpcionesModal inicializarAjustesUsuario() throws Exception {
	ObjectMapper objectMapper = new ObjectMapper();

	String jsonFilePath = Constantes.OPCIONES_AJUSTES_USUARIO_PATH;
	Resource resource = new UrlResource(Paths.get(jsonFilePath).toUri());

	// Verificamos si el archivo existe
	if (resource.exists()) {
		// Leemos y parseamos el contenido del archivo JSON en un objeto Java
		OpcionesModal myData = objectMapper.readValue(resource.getInputStream(), OpcionesModal.class);
		return myData;
	} else {
		throw new Exception("El archivo JSON no existe en " + jsonFilePath);
	}
	}

	public NukaMsj agregarAliasProducto(String usuarioId, String chatId, String productId, String aliasProducto) {
	NukaMsj nukaMsj = new NukaMsj();
	List<Product> product = productRepository.findByAliasProducto(Arrays.asList(aliasProducto));
	if (!ObjectUtils.isEmpty(product)) {
		nukaMsj.setTexto("No se ha podido asignar el alias \"" + aliasProducto
			+ "\" por que ya esta asignado al producto " + product.get(0).nombreProducto);
	} else {
		try {
		Product product2 = productRepository.findById(productId).get();
		product2.getAliasProducto().add(aliasProducto);
		productRepository.save(product2);
		nukaMsj.setTexto(" Se le ha asignado el alias \"" + aliasProducto + "\" al producto "
			+ product2.getNombreProducto());
		} catch (Exception e) {
		nukaMsj.setTexto(
			"Parece que hay algun problema con el producto seleccionado, sera mejor que contacte con el administrados para echar un vistazo a la base de datos");
		}
	}
	nukaMsj.setFecha(utilsService.timestampNowLong());
	nukaMsj.setId(utilsService.generarIdMongo());
	nukaMsj.setUsuarioId(usuarioId);
	nukaMsj.setChatId(chatId);
	nukaMsj.setTipoMsj(TipoMsj.RESPUESTA);
	nukaMsj.setTarea(Constantes.AÑADIR_ALIAS_LISTA_COMPRA);
	nukaMsj.setRedireccion("shopping-list");
	return nukaMsj;
	}

	public EstadoMsj agregarEstadoMsj(List<String> ids, Long fecha, TipoMsjEstado tipoMsjEstado, String texto,
		String estado, String imgMsjPath, String redireccion, Integer tiempoRepeticion) {
	Long fechaEstado = fecha == null ? utilsService.timestampNowLong() : fecha;
	EstadoMsj estadoMsj = new EstadoMsj(utilsService.generarIdMongo(), fechaEstado, texto, ids, tipoMsjEstado,
		estado, imgMsjPath, redireccion, true, tiempoRepeticion);
	return estadoMsj;
	}

	public long count() {
	return userRepository.count();
	}

	public void restaurarMongoDB(String fecha, String db) {
	// fecha: tiene este formato yyyy_MM_dd
	// db: base datos que vamos a restaurar
	if (true && !ObjectUtils.isEmpty(db) && !ObjectUtils.isEmpty(fecha)) {
		try {
		String host = nukaConfiguration.getMONGO_HOST();
		String port = nukaConfiguration.getMONGO_PUERTO();
		String database = db;
		String username = nukaConfiguration.getMONGO_USERNAME();
		String password = nukaConfiguration.getMONGO_PASSWORD();
		String authenticationDatabase = nukaConfiguration.getMONGO_AUTHENTICATION_DATABASE();
		String inputDirectory = Constantes.MONGODUMP_PATH + fecha + File.separator + db;

		logger.info("restaurarMongoDB : Comieza la restauracion de bd (" + db + ") con fecha " + fecha);
		ProcessBuilder processBuilder = new ProcessBuilder("mongorestore", "--host", host, "--port", port,
			"--username", username, "--password", password, "--authenticationDatabase",
			authenticationDatabase, "--db", database, "--dir", inputDirectory);

		processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

		Process process = processBuilder.start();

		int exitCode = process.waitFor();

		if (exitCode == 0) {
			logger.info("restaurarMongoDB : mongorestore completado exitosamente.");
		} else {
			logger.error("restaurarMongoDB : Error al ejecutar mongorestore. Código de salida: " + exitCode);
		}

		} catch (IOException | InterruptedException e) {
		logger.error("restaurarMongoDB : Error durante la ejecución de mongorestore: " + e.getMessage());
		}
	} else {
		if (ObjectUtils.isEmpty(db) || ObjectUtils.isEmpty(fecha)) {
		String mensaje = "restaurarMongoDB : db o fecha vacia";
		logger.error(mensaje + " - db: " + db + ", fecha: " + fecha);
		} else {
		logger.error("restaurarMongoDB : Error con el codigo de autentificacion");
		}
	}
	}

	public void actualizarAjustes(Boolean actualizarUsuarios, Boolean actualizarChats, Boolean resetearAjustes) {
	if (actualizarUsuarios) {
		try {
		List<User> usuarios = userRepository.findAll();
		List<User> usuariosModificados = new ArrayList<>();

		for (User usuario : usuarios) {
			OpcionesModal opcionesModalNuevo = inicializarAjustesUsuario();

			if (resetearAjustes) {
			usuario.setOpcionesModal(opcionesModalNuevo);
			} else {
			Map<String, List<String>> idValorMap = new HashMap<>();
			usuario.getOpcionesModal().getOpciones().parallelStream().forEach(o -> {
				o.getOpciones().parallelStream().forEach(ob -> {
				idValorMap.put(ob.getId(), ob.getValor());
				});
			});

			for (GrupoOpciones grupoOpciones : opcionesModalNuevo.getOpciones()) {
				for (OpcionBasica ob : grupoOpciones.getOpciones()) {
				if (!ObjectUtils.isEmpty(idValorMap.get(ob.getId()))) {
					ob.setValor(idValorMap.get(ob.getId()));
				}
				}
			}
			}
			usuario.setOpcionesModal(opcionesModalNuevo);
			usuariosModificados.add(usuario);
		}
		logger.info("Se han modificado los ajustes de " + usuariosModificados.size() + " usuarios");
		userRepository.saveAll(usuariosModificados);

		} catch (Exception e) {
		logger.error("actuactualizarAjustes : Error al actualizar usuarios");
		}
	}

	if (actualizarChats) {
		try {
		List<NukaChat> chatsVisibles = nukaChatRepository.findByVisible(true);
		List<NukaChat> chatsModificados = new ArrayList<>();

		for (NukaChat chat : chatsVisibles) {
			OpcionesModal opcionesModalNuevo = inicializarAjustesChat();
			if (resetearAjustes) {
			chat.setOpcionesModal(opcionesModalNuevo);
			} else {
			Map<String, List<String>> idValorMap = new HashMap<>();
			chat.getOpcionesModal().getOpciones().parallelStream().forEach(o -> {
				o.getOpciones().parallelStream().forEach(ob -> {
				idValorMap.put(ob.getId(), ob.getValor());
				});
			});

			for (GrupoOpciones grupoOpciones : opcionesModalNuevo.getOpciones()) {
				for (OpcionBasica ob : grupoOpciones.getOpciones()) {
				if (!ObjectUtils.isEmpty(idValorMap.get(ob.getId()))) {
					ob.setValor(idValorMap.get(ob.getId()));
				}
				}
			}
			}
			chat.setOpcionesModal(opcionesModalNuevo);
			chatsModificados.add(chat);
		}

		logger.info("Se han modificado los ajustes de " + chatsModificados.size() + " chats");
		nukaChatRepository.saveAll(chatsModificados);

		} catch (Exception e) {
		logger.error("actuactualizarAjustes : Error al actualizar usuarios");
		}
	}
	}

	public Boolean actualizarTokenDispositivo(String usuarioId, String dispositivo, String token) {
	try {
		Optional<User> usuario = userRepository.findById(usuarioId);

		if (!ObjectUtils.isEmpty(usuario) && !ObjectUtils.isEmpty(usuario.get())
			&& usuario.get().getData().getTokenPorDispositivos() != null) {
		String tokenDelDispositivoGuardado = usuario.get().getData().getTokenPorDispositivos().get(dispositivo);
		if (tokenDelDispositivoGuardado == null) {
			usuario.get().getData().getTokenPorDispositivos().put(dispositivo, token);
			logger.info("actualizarTokenDispositivo : Se ha añadido " + dispositivo + " al usuario "
				+ usuario.get().userNick + " con token " + token);
			userRepository.save(usuario.get());
			return true;
		} else if (!token.equals(tokenDelDispositivoGuardado)) {
			usuario.get().getData().getTokenPorDispositivos().put(dispositivo, token);
			logger.info("actualizarTokenDispositivo : Se ha modificado el token de dispositivo " + dispositivo
				+ " del usuario " + usuario.get().userNick);
			userRepository.save(usuario.get());
			return true;
		}

		} else if (!ObjectUtils.isEmpty(usuario.get())
			&& usuario.get().getData().getTokenPorDispositivos() == null) {
		usuario.get().getData().setTokenPorDispositivos(new HashMap<>());
		usuario.get().getData().getTokenPorDispositivos().put(dispositivo, token);
		logger.info("actualizarTokenDispositivo : Se ha añadido " + dispositivo + " al usuario "
			+ usuario.get().userNick + " con token " + token);
		userRepository.save(usuario.get());
		return true;
		}

	} catch (Exception e) {
		logger.error(String.format("actualizarTokenDispositivo : Error al actual)izar el token de %s", usuarioId));
	}
	return false;
	}

	public String guardarArchivoTemporal(
			String usuarioId,
			String carpeta, 
			MultipartFile file) {
		String idUnico = utilsService.generarIdMongo();
		// Podria usar carpeta pero de momento es mejor asi
		String path = utilsService.guardarArchivoTemporal(file, idUnico, idUnico);
		System.out.println("path:" + path);
		return path;
	}
			

    /*
     * ACCIONES
     */

	public NukaService() {
        mapAcciones.put("AÑADIR_COMPRA", wrapAction(this::accionAñadirCompra));
        mapAcciones.put("RECORDATORIO", wrapAction(this::accionRecordatorio));
        mapAcciones.put("CALENDARIO_CITA", wrapAction(this::accionCalendarioCita));
        mapAcciones.put("LISTA_ACCIONES", wrapAction(this::accionListaAcciones));
    }
    private Supplier<NukaMsj> wrapAction(ActionWithException action) {
        return () -> {
            try {
                return action.execute();
            } catch (JsonProcessingException | UnirestException e) {
                e.printStackTrace();
                return this.nukaMsjPeticion;
            }
        };
    }
	// Interface funcional que permite lanzar excepciones
	@FunctionalInterface
	private interface ActionWithException {
		NukaMsj execute() throws JsonProcessingException, UnirestException;
	}
	private NukaMsj accionAñadirCompra() throws JsonMappingException, JsonProcessingException, UnirestException {
		NukaMsj nukaMsjPeticion = this.nukaMsjPeticion;
		NukaMsj nukaMsjRespuesta = inicializarRespuestaMsj("AÑADIR_COMPRA");
		accionesService.accionAñadirCompra(nukaMsjPeticion, nukaMsjRespuesta, null);
		return nukaMsjRespuesta;
		}
	
		private NukaMsj accionRecordatorio() throws JsonMappingException, JsonProcessingException, UnirestException {
		NukaMsj nukaMsjPeticion = this.nukaMsjPeticion;
		NukaMsj nukaMsjRespuesta = inicializarRespuestaMsj("RECORDATORIO");
		accionesService.accionRecordatorio(nukaMsjPeticion, nukaMsjRespuesta);
		return nukaMsjRespuesta;
		}
	
		private NukaMsj accionCalendarioCita() throws JsonMappingException, JsonProcessingException, UnirestException {
		NukaMsj nukaMsjPeticion = this.nukaMsjPeticion;
		NukaMsj nukaMsjRespuesta = inicializarRespuestaMsj("CALENDARIO_CITA");
		accionesService.accionCalendarioCita(nukaMsjPeticion, nukaMsjRespuesta);
		return nukaMsjRespuesta;
		}
	
		private NukaMsj accionListaAcciones() throws JsonMappingException, JsonProcessingException, UnirestException {
		NukaMsj nukaMsjPeticion = this.nukaMsjPeticion;
		NukaMsj nukaMsjRespuesta = inicializarRespuestaMsj("LISTA_ACCIONES");
		accionesService.accionListaAcciones(nukaMsjPeticion, nukaMsjRespuesta);
		return nukaMsjRespuesta;
		}
	
		private NukaMsj accionConversionArchivos() throws JsonMappingException, JsonProcessingException, UnirestException {
			NukaMsj nukaMsjPeticion = this.nukaMsjPeticion;
			NukaMsj nukaMsjRespuesta = inicializarRespuestaMsj("CONVERTIR_ARCHIVOS");
			accionesService.accionConversionArchivos(nukaMsjPeticion, nukaMsjRespuesta);
			return nukaMsjRespuesta;
		}
}
