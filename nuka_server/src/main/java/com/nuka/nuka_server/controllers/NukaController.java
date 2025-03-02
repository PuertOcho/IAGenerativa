package com.nuka.nuka_server.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.nuka.nuka_server.Constantes;
import com.nuka.nuka_server.NukaConfiguration;
import com.nuka.nuka_server.models.AccionNuka;
import com.nuka.nuka_server.models.OpcionesModal;
import com.nuka.nuka_server.models.ChatGPTRequest;
import com.nuka.nuka_server.models.ChatGPTResponse;
import com.nuka.nuka_server.models.ContenidoMsj;
import com.nuka.nuka_server.models.DataUser;
import com.nuka.nuka_server.models.ElementoSeleccionado;
import com.nuka.nuka_server.models.EstadoMsj;
import com.nuka.nuka_server.models.Filtro;
import com.nuka.nuka_server.models.GenerateImageRequest;
import com.nuka.nuka_server.models.GeneratedImageResponse;
import com.nuka.nuka_server.models.NukaChat;
import com.nuka.nuka_server.models.NukaMsj;
import com.nuka.nuka_server.models.OpcionBasica;
import com.nuka.nuka_server.models.GrupoOpciones;
import com.nuka.nuka_server.models.Product;
import com.nuka.nuka_server.models.ProductRelation;
import com.nuka.nuka_server.models.ProductResponse;
import com.nuka.nuka_server.models.Data;
import com.nuka.nuka_server.models.ShoppingList;
import com.nuka.nuka_server.models.TipoMsj;
import com.nuka.nuka_server.models.TipoMsjEstado;
import com.nuka.nuka_server.models.User;
import com.nuka.nuka_server.models.WhisperTranscriptionRequest;
import com.nuka.nuka_server.models.WhisperTranscriptionResponse;
import com.nuka.nuka_server.repository.AdminRepository;
import com.nuka.nuka_server.repository.EstadoMsjRepository;
import com.nuka.nuka_server.repository.NukaChatRepository;
import com.nuka.nuka_server.repository.NukaMsjRepository;
import com.nuka.nuka_server.repository.ProductRepository;
import com.nuka.nuka_server.repository.ShoppingListRepository;
import com.nuka.nuka_server.repository.UserRepository;
import com.nuka.nuka_server.service.AccionesService;
import com.nuka.nuka_server.service.AzureTextToSpeechService;
import com.nuka.nuka_server.service.NotificacionesService;
import com.nuka.nuka_server.service.NukaService;
import com.nuka.nuka_server.service.OpenAIClientService;
import com.nuka.nuka_server.service.ScheduledTasksService;
import com.nuka.nuka_server.service.UtilsService;

import lombok.RequiredArgsConstructor;

@EnableConfigurationProperties
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST })
public class NukaController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    NukaService nukaService;

    @Autowired
    NukaConfiguration nukaConfiguration;

    @Autowired
    ShoppingListRepository shoppingListRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OpenAIClientService openAIClientService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NukaMsjRepository nukaMsjRepository;

    @Autowired
    NukaChatRepository nukaChatRepository;

    @Autowired
    EstadoMsjRepository estadoMsjRepository;

    @Autowired
    AdminRepository adminRepository;
    
    @Autowired
    AccionesService accionesService;

    @Autowired
    AzureTextToSpeechService azureTextToSpeechService;

    @Value("${nuka.DEBUG_MODE}")
    public Boolean DEBUG_MODE;

    @Autowired
    UtilsService utilsService;

    @Autowired
    NotificacionesService notificacionesService;

    @Autowired
    ScheduledTasksService scheduledTasksService;

    public ObjectMapper objectMapper = new ObjectMapper();

    // API
    @GetMapping("/getResource")
    public ResponseEntity<InputStreamResource> getResource(@RequestParam(required = false) String tipo,
	    @RequestParam(required = false) String id) {
	try {
	    // MAP_RESOURCE -> 0:carpeta; 1:extension; 2:mimeType
	    List<String> resourceCaracteristicas = Constantes.MAP_RESOURCE.get(tipo);
	    if (resourceCaracteristicas != null) {
		String filePath = Constantes.DATOS_PATH + File.separator + resourceCaracteristicas.get(0)
			+ File.separator + id + resourceCaracteristicas.get(1);
		Resource resource = new UrlResource(Paths.get(filePath).toUri());
		InputStreamResource is = new InputStreamResource(resource.getInputStream());
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(resourceCaracteristicas.get(2)))
			.body(is);
	    }
	    return ResponseEntity.accepted().build();
	} catch (Exception e) {
	    String traza = "getResource : ";
	    logger.error(traza + e.getMessage());
	    // e.printStackTrace();
	    return null;
	}

    }

    @PostMapping("/nukaMsjPeticion")
    public List<NukaMsj> nukaMsjPeticion(@RequestParam(required = false) String nukaMsjPeticionJson,
	    @RequestParam(required = false) MultipartFile audioFile,
	    @RequestParam(required = false) MultipartFile contenidoFile0,
	    @RequestParam(required = false) MultipartFile contenidoFile1,
	    @RequestParam(required = false) MultipartFile contenidoFile2) throws UnirestException, InterruptedException,
	    ExecutionException, IOException, UnsupportedAudioFileException {
	try {
	    NukaMsj nukaMsjPeticion = objectMapper.readValue(nukaMsjPeticionJson, NukaMsj.class);
	    nukaMsjPeticion.setFecha(utilsService.timestampNowLong());
	    
	    List<MultipartFile> contenidoMsj = new ArrayList<MultipartFile>();
	    if (!ObjectUtils.isEmpty(contenidoFile0)) {
		contenidoMsj.add(contenidoFile0);
		if (!ObjectUtils.isEmpty(contenidoFile1)) {
		    contenidoMsj.add(contenidoFile1);
		    if (!ObjectUtils.isEmpty(contenidoFile2)) {
			contenidoMsj.add(contenidoFile2);
		    }
		}
	    }

	    if (!ObjectUtils.isEmpty(contenidoMsj)) {
		nukaService.guardarContenidoMsj(nukaMsjPeticion, contenidoMsj);
	    }

	    List<NukaMsj> nukaMsjRespuesta = nukaService.nukaMsjPeticion(nukaMsjPeticion, audioFile, contenidoMsj);

	    nukaMsjRepository.saveAll(nukaMsjRespuesta);

	    return nukaMsjRespuesta;
	} catch (Exception e) {
	    // Maneja errores de análisis JSON
	    throw new IllegalArgumentException("Error al procesar el JSON nukaMsjPeticionJson: ", e);
	}
    }

    @GetMapping(value = "/autonombrarChat")
    public ResponseEntity<String> autonombrarChat(@RequestParam String chatId) {
        Optional<NukaChat> chat = nukaChatRepository.findById(chatId);
        if (chat.isPresent()) {
            OpcionBasica ob = utilsService.getOpcionBasica(chat.get(), "GENERAL", "GENERAL_AUTONOMBRAR_CHAT");
            if (chat.get().getSummary() != null && 
                chat.get().getSummary().equals("Chat sin titulo") &&
                Boolean.parseBoolean(ob.getValor().get(0))) {
                
                List<NukaMsj> mensajes = nukaMsjRepository.findByChatIds(Arrays.asList(chatId));
                String titulo = nukaService.generarTitulo(mensajes);
                chat.get().setSummary(titulo.replace("Titulo:", "").trim());
                nukaChatRepository.save(chat.get());
                return ResponseEntity.ok("{\"summary\": \"" + chat.get().getSummary() + "\"}"); // Asegúrate de que sea JSON
            }
        }
        // En caso de error, devuelve un JSON con un mensaje
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Chat no encontrado o condiciones no cumplidas\"}");
    }

    @GetMapping("/getShoppingCart")
    public ShoppingList getShoppingCart() {

	List<ShoppingList> shoppingLists = shoppingListRepository.findByIsFinished(false, null);

	if (shoppingLists.isEmpty()) {
	    return new ShoppingList(null, false, null, null);
	} else {
	    ShoppingList resShoppingList = shoppingLists.get(0);
	    return resShoppingList;
	}
    }

    @GetMapping("/getShoppingHistory")
    public List<ShoppingList> getShoppingHistory(
	    @RequestParam(value = "paginaActual", required = false) int paginaActual,
	    @RequestParam(value = "itemPorPagina", required = false) int itemPorPagina) {

	Pageable pageable = PageRequest.of(paginaActual, itemPorPagina, Sort.by("datePurchase").descending());
	List<ShoppingList> shoppingLists = shoppingListRepository.findByIsFinished(true, pageable);

	if (shoppingLists.isEmpty()) {
	    return new ArrayList<>();
	} else {
	    return shoppingLists;
	}
    }

    @GetMapping("/getNumShoppingHistory")
    public long getNumShoppingHistory() {
	return shoppingListRepository.countByIsFinished(true);
    }

    @GetMapping("/confirmarProductosSeleccionados")
    public ShoppingList confirmarProductosSeleccionados(
	    @RequestParam(value = "shoppingListId", required = false) String shoppingListId,
	    @RequestParam(value = "productsMarcadosJson", required = false) String productsMarcadosJson)
	    throws JsonMappingException, JsonProcessingException {

	ObjectMapper objectMapper = new ObjectMapper();
	List<ProductRelation> productsMarcados = objectMapper.readValue(productsMarcadosJson,
		new TypeReference<List<ProductRelation>>() {
		});

	Optional<ShoppingList> shoppingList = shoppingListRepository.findById(shoppingListId);

	if (shoppingList.isEmpty()) {
	    return new ShoppingList(null, false, null, null);
	} else {
	    ShoppingList resShoppingList = nukaService.confirmarProductosSeleccionados(shoppingList.get(),
		    productsMarcados);
	    return resShoppingList;
	}

    }

    @PostMapping("/getNukaChats")
    public List<NukaChat> getNukaChats(@RequestParam(required = false) String usuarioId,
	    @RequestParam(required = false) String filtroJson) throws JsonMappingException, JsonProcessingException {

	Optional<User> user = userRepository.findById(usuarioId);
	List<NukaChat> nukaChats = new ArrayList<>();
	List<Filtro> filtros = new ArrayList<>();
	if (!ObjectUtils.isEmpty(filtroJson)) {
	    filtros = objectMapper.readValue(filtroJson, new TypeReference<List<Filtro>>() {
	    });
	}

	if (!ObjectUtils.isEmpty(user) && !ObjectUtils.isEmpty(user.get())) {
	    if (!ObjectUtils.isEmpty(filtros)) {
		nukaChats = nukaChatRepository.findByVisibleAndUsuarioIdAndFilter(true, user.get().getId(),
			filtros.get(0).getValores());

	    } else {
		nukaChats = nukaChatRepository.findByVisibleAndUsuarioId(true, user.get().getId());
		if (nukaChats.isEmpty()) {
		    NukaChat nukaChat = new NukaChat();
		    nukaChat.setVisible(true);
		    nukaChat.setUsuarioId(user.get().getId());
		    nukaChat.setTags(null);
		    nukaChat.summary = "GENERAL";
		    nukaChat.setFechaCreacion(utilsService.timestampNowLong());
		    nukaChat.setFechaUltimoMsj(nukaChat.getFechaCreacion());
		    nukaChat.setOpcionesModal(nukaService.initNukaChatConfig(nukaChat));

		    nukaChatRepository.insert(nukaChat);
		    nukaChats.add(nukaChat);
		}
	    }
	}
	return nukaChats;
    }

    @PostMapping("/getNukaChatsByIds")
    public List<NukaChat> getNukaChatsByIds(@RequestBody(required = false) List<String> chatIds) {
	List<NukaChat> nukaChats = nukaChatRepository.findByIds(chatIds);
	return nukaChats;
    }

    @PostMapping("/comprobarMensajes")
    public List<NukaMsj> comprobarMensajes(@RequestBody(required = false) List<String> mensajes) {
	List<NukaMsj> nukaMsjs = null;// nukaService.comprobarMensajes(mensajes);
	return nukaMsjs;
    }

    @PostMapping("/getMessages")
    public List<NukaMsj> getMessages(@RequestParam(required = false) String chatIds,
	    @RequestParam(required = false) String fecha, @RequestParam(required = false) String numMensajes) {
		List<String> aux = new ArrayList<>();
		aux.add(chatIds);
		Pageable pageable = PageRequest.of(0, Integer.valueOf(numMensajes));
		List<NukaMsj> nukaMsjs = nukaMsjRepository.findTopNByChatIdsAndFecha(aux, Long.valueOf(fecha), pageable);
		Collections.reverse(nukaMsjs);
		return nukaMsjs;
    }

    @GetMapping("/getScripts")
    public List<Data> getScripts(@RequestParam(required = false) String id) {
    	System.out.println("getScripts " + id);
		List<Data> scripts= adminRepository.findScripts();
		System.out.println("num: " + scripts.size());
		return scripts;
    }
    
    @PostMapping("/getMessagesBetweenDates")
    public List<NukaMsj> getMessagesBetweenDates(@RequestParam(required = false) String chatId,
	    @RequestParam(required = false) String messageIdStart, @RequestParam(required = false) String messageIdEnd,
	    @RequestParam(required = false) String fechaStart, @RequestParam(required = false) String fechaEnd) {

	List<NukaMsj> nukaMsjs = new ArrayList<>();
	Long fechaStartLong = null;
	Long fechaEndLong = null;

	if (messageIdStart != null && messageIdEnd != null) {
	    Optional<NukaMsj> msjStart = nukaMsjRepository.findById(messageIdStart);
	    Optional<NukaMsj> msjEnd = nukaMsjRepository.findById(messageIdEnd);

	    if (msjStart.get() != null && msjEnd.get() != null) {
		fechaStartLong = msjStart.get().getFecha();
		fechaEndLong = msjEnd.get().getFecha();
	    }

	} else {
	    fechaStartLong = Long.valueOf(fechaStart);
	    fechaEndLong = Long.valueOf(fechaEnd);
	}

	List<String> aux = new ArrayList<>();
	aux.add(chatId);
	nukaMsjs = nukaMsjRepository.findMessagesByChatIdsAndDateRange(aux, fechaEndLong, fechaStartLong);

	Collections.reverse(nukaMsjs);
	return nukaMsjs;
    }

    @GetMapping("/agregarChat")
    public NukaChat agregarChat(@RequestParam(value = "summary", required = false) String summary,
	    @RequestParam(value = "usuarioId", required = false) String usuarioId) {
	return nukaService.agregarChat(usuarioId, summary);
    }

    public ChatGPTResponse chat(@RequestParam(value = "texto", required = false) String texto)
	    throws JsonMappingException, JsonProcessingException, UnirestException {
	ChatGPTRequest chatGPTRequest = new ChatGPTRequest(Constantes.MODEL_DEFAULT, texto);
	return openAIClientService.chat(chatGPTRequest, null);
    }

    @GetMapping(value = "/guardarAjustesChat")
    public OpcionesModal guardarAjustesChat(
    		@RequestParam(value = "params", required = false) String params,
    		@RequestParam(value = "id", required = false) String id)
	    throws JsonMappingException, JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, List<String>> opcionesModificadas = objectMapper.readValue(params,
			new TypeReference<Map<String, List<String>>>() {
			});
		OpcionesModal opcionesModal = nukaService.guardarAjustesChat(opcionesModificadas, id);
	
		return opcionesModal;
    }
    
    @GetMapping(value = "/guardarAjustesUsuario")
    public User guardarAjustesUsuario(@RequestParam(value = "params", required = false) String params,
	    @RequestParam(value = "id", required = false) String id)
	    throws JsonMappingException, JsonProcessingException {

	ObjectMapper objectMapper = new ObjectMapper();
	Map<String, List<String>> opcionesModificadas = objectMapper.readValue(params,
		new TypeReference<Map<String, List<String>>>() {
		});
	User userSaved = nukaService.guardarAjustesUsuario(opcionesModificadas, id);

	return userSaved;
    }

    @GetMapping(value = "/añadirOpcionAjustesChat")
    public List<NukaChat> añadirOpcionAjustesChat(@RequestParam(value = "id", required = false) String id,
	    @RequestParam(value = "etiqueta", required = false) String etiqueta,
	    @RequestParam(value = "etiquetaHeader", required = false) String etiquetaHeader,
	    @RequestParam(value = "tipo", required = false) String tipo,
	    @RequestParam(value = "datos", required = false) List<String> datos,
	    @RequestParam(value = "valor", required = false) List<String> valor) throws Exception {

	List<NukaChat> nukaChats = nukaChatRepository.findAll();

	for (NukaChat nukaChat : nukaChats) {
	    OpcionBasica opcionBasica = new OpcionBasica(id, etiqueta, tipo, datos, valor);

	    if (ObjectUtils.isEmpty(nukaChat.getOpcionesModal())) {
		nukaChat.setOpcionesModal(new OpcionesModal(new ArrayList<>()));
	    }

	    if (nukaChat.getOpcionesModal().getOpciones().stream()
		    .anyMatch(ncoe -> ncoe.etiqueta.equals(etiquetaHeader))) {
		GrupoOpciones GrupoOpciones = nukaChat.getOpcionesModal().getOpciones().stream()
			.filter(nco -> nco.etiqueta.equals(etiquetaHeader)).findFirst().get();
		GrupoOpciones.opciones.add(opcionBasica);
	    } else {
		GrupoOpciones GrupoOpciones = new GrupoOpciones(etiquetaHeader, (List<OpcionBasica>) opcionBasica);
		nukaChat.getOpcionesModal().getOpciones().add(GrupoOpciones);
	    }

	}

	List<NukaChat> nukaChatsRes = nukaChatRepository.saveAll(nukaChats);

	return nukaChatsRes;
    }

    @GetMapping(value = "/añadirOpcionAjustesUsuario")
    public User añadirOpcionAjustesUsuario(@RequestParam(value = "id", required = false) String id,
	    @RequestParam(value = "etiqueta", required = false) String etiqueta,
	    @RequestParam(value = "etiquetaHeader", required = false) String etiquetaHeader,
	    @RequestParam(value = "tipo", required = false) String tipo,
	    @RequestParam(value = "datos", required = false) List<String> datos,
	    @RequestParam(value = "valor", required = false) List<String> valor) throws Exception {

	Optional<User> usuario = userRepository.findById(id);

	if (!ObjectUtils.isEmpty(usuario.get())) {

	    OpcionBasica opcionBasica = new OpcionBasica(id, etiqueta, tipo, datos, valor);

	    if (ObjectUtils.isEmpty(usuario.get().getOpcionesModal())) {
		usuario.get().setOpcionesModal(new OpcionesModal(new ArrayList<>()));
	    }

	    if (usuario.get().getOpcionesModal().getOpciones().stream()
		    .anyMatch(ncoe -> ncoe.etiqueta.equals(etiquetaHeader))) {
		GrupoOpciones GrupoOpciones = usuario.get().getOpcionesModal().getOpciones().stream()
			.filter(nco -> nco.etiqueta.equals(etiquetaHeader)).findFirst().get();
		GrupoOpciones.opciones.add(opcionBasica);
	    } else {
		GrupoOpciones GrupoOpciones = new GrupoOpciones(etiquetaHeader, (List<OpcionBasica>) opcionBasica);
		usuario.get().getOpcionesModal().getOpciones().add(GrupoOpciones);
	    }

	}

	return usuario.get();
    }

    @GetMapping(value = "/cerrarNotificacion")
    public Boolean cerrarNotificacion(@RequestParam(value = "estadoMsjId", required = false) String estadoMsjId) {
	Optional<EstadoMsj> estadoMsj = estadoMsjRepository.findById(estadoMsjId);
	if (!ObjectUtils.isEmpty(estadoMsj.get())) {
	    estadoMsj.get().setVisible(false);
	    EstadoMsj estadoMsjRes = estadoMsjRepository.save(estadoMsj.get());
	    return !ObjectUtils.isEmpty(estadoMsjRes);
	}
	return false;
    }

    @PostMapping(value = "/agregarTareaPorHacer")
    public EstadoMsj agregarTareaPorHacer(@RequestParam(value = "usuarioId", required = false) String usuarioId,
	    @RequestParam(value = "msjEstadoId", required = false) String msjEstadoId) {
	Optional<EstadoMsj> msjEstado = estadoMsjRepository.findById(msjEstadoId);
	if (!ObjectUtils.isEmpty(msjEstado.get())) {
	    EstadoMsj newMsjEstado = msjEstado.get();
	    newMsjEstado.setId(utilsService.generarIdMongo());
	    newMsjEstado.setEstado("POR_HACER");
	    newMsjEstado.setFecha(utilsService.timestampNowLong());
	    return estadoMsjRepository.save(newMsjEstado);
	}
	return null;
    }

    @PostMapping(value = "/terminarTareaPorHacer")
    public EstadoMsj terminarTareaPorHacer(@RequestParam(value = "usuarioId", required = false) String usuarioId,
	    @RequestParam(value = "msjEstadoId", required = false) String msjEstadoId) {
	Optional<EstadoMsj> msjEstado = estadoMsjRepository.findById(msjEstadoId);
	if (!ObjectUtils.isEmpty(msjEstado.get())) {

	    msjEstado.get().setVisible(false);
	    msjEstado.get().setEstado("TERMINADA");
	    List<String> listUser = new ArrayList<String>();
	    listUser.add(usuarioId);
	    msjEstado.get().setUsuariosId(listUser);
	    msjEstado.get().setFecha(utilsService.timestampNowLong());
	    return estadoMsjRepository.save(msjEstado.get());
	}
	return null;
    }

    @PostMapping(value = "/añadirEstadoMsj")
    public EstadoMsj añadirEstadoMsj(@RequestParam(value = "usuarioId", required = false) String usuarioId,
	    @RequestParam(value = "usuariosIdsUsername", required = false) String usuariosIdsUsername,
	    @RequestParam(value = "tipoMsjEstado", required = false) String tipoMsjEstado,
	    @RequestParam(value = "fecha", required = false) String fecha,
	    @RequestParam(value = "texto", required = false) String texto,
	    @RequestParam(value = "estado", required = false) String estado,
	    @RequestParam(value = "imgMsjPath", required = false) String imgMsjPath,
	    @RequestParam(value = "redireccion", required = false) String redireccion,
	    @RequestParam(value = "tiempoRepeticion", required = false) String tiempoRepeticion) {
	try {
	    Optional<User> usuario = null;
	    if (!ObjectUtils.isEmpty(usuarioId)) {
		usuario = userRepository.findById(usuarioId);
	    }
	    List<String> usuariosIds = null;
	    if (usuariosIdsUsername != null) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
		    List<String> listUsuariosUsernames = objectMapper.readValue(usuariosIdsUsername, List.class);
		    List<User> usuarios = userRepository.findByUsers(listUsuariosUsernames);
		    usuariosIds = usuarios.stream().map(u -> u.getId()).collect(Collectors.toList());
		} catch (IOException e) {
		    String traza = "añadirEstadoMsj 1 : ";
		    logger.error(traza + e.getMessage());
		    // e.printStackTrace();
		    return null;
		}
	    }

	    List<String> ids = new ArrayList<>();
	    if (usuariosIds != null) {
		ids.addAll(usuariosIds);
	    }
	    if (usuarioId != null && ids.stream().noneMatch(e -> e.equals(usuarioId))) {
		ids.add(usuarioId);
	    }

	    Integer tiempoRepeticionRes = null;
	    if (tiempoRepeticion != null) {
		tiempoRepeticionRes = Integer.valueOf(tiempoRepeticion);
	    }

	    EstadoMsj estadoMsj = nukaService.agregarEstadoMsj(ids,
		    ObjectUtils.isEmpty(fecha) ? null : Long.valueOf(fecha), TipoMsjEstado.valueOf(tipoMsjEstado),
		    texto, estado, imgMsjPath, redireccion, tiempoRepeticionRes);
	    estadoMsjRepository.save(estadoMsj);
	    return estadoMsj;
	} catch (Exception e) {
	    String traza = "añadirEstadoMsj 2 : ";
	    logger.error(traza + e.getMessage());
	    // e.printStackTrace();
	}
	return null;
    }

    @GetMapping(value = "/getAllEstadoMsj")
    public List<EstadoMsj> getAllEstadoMsj(@RequestParam(value = "usuarioId", required = false) String usuarioId) {
	try {
	    ArrayList<TipoMsjEstado> estados = new ArrayList<>();
	    estados.add(TipoMsjEstado.NOTIFICACIONES);
	    estados.add(TipoMsjEstado.CALENDARIO);
	    estados.add(TipoMsjEstado.TAREAS);

	    List<EstadoMsj> msjs = estadoMsjRepository.findByUsuarioIdAndTipoMsjEstadoInList(usuarioId, estados);

	    return msjs;
	} catch (Exception e) {
	    String traza = "getAllEstadoMsj : ";
	    logger.error(traza + e.getMessage());
	    // e.printStackTrace();
	    return new ArrayList<>();
	}

    }

    @GetMapping(value = "/getNotificaciones")
    public List<EstadoMsj> getNotificaciones(@RequestParam(value = "usuarioId", required = false) String usuarioId) {
	try {
	    List<EstadoMsj> msjs = estadoMsjRepository.findByUsuarioIdAndTipoMsjEstado(usuarioId,
		    TipoMsjEstado.NOTIFICACIONES);
	    return msjs;
	} catch (Exception e) {
	    String traza = "getNotificaciones : ";
	    logger.error(traza + e.getMessage());
	    // e.printStackTrace();
	    return new ArrayList<>();
	}

    }

    @GetMapping(value = "/getEventosYCitas")
    public List<EstadoMsj> getEventosYCitas(@RequestParam(value = "usuarioId", required = false) String usuarioId,
	    @RequestParam int anyo, @RequestParam int mes) {
	try {

	    LocalDate startDate = LocalDate.of(anyo, mes, 1);
	    ZoneId zoneId = ZoneId.systemDefault();
	    LocalDate endDate = startDate.plusMonths(1).minusDays(1);

	    long startDateEpoch = startDate.atStartOfDay(zoneId).toEpochSecond() * 1000;
	    long endDateEpoch = endDate.atStartOfDay(zoneId).toEpochSecond() * 1000;

	    List<EstadoMsj> msjs = estadoMsjRepository.findByUsuarioIdAndTipoMsjEstadoAndFechaBetween(usuarioId,
		    TipoMsjEstado.CALENDARIO, startDateEpoch, endDateEpoch);
	    return msjs;
	} catch (Exception e) {
	    String traza = "getEventosYCitas : ";
	    logger.error(traza + e.getMessage());
	    // e.printStackTrace();
	    return new ArrayList<>();
	}
    }

    @GetMapping(value = "/getEventosYCitasAll")
    public List<EstadoMsj> getEventosYCitasAll(@RequestParam(value = "usuarioId", required = false) String usuarioId) {
		try {
			List<EstadoMsj> msjs = estadoMsjRepository.findByUsuarioIdAndTipoMsjEstado(usuarioId,
				TipoMsjEstado.CALENDARIO);
			return msjs;
		} catch (Exception e) {
			String traza = "getEventosYCitasAll : ";
			logger.error(traza + e.getMessage());
			// e.printStackTrace();
			return new ArrayList<>();
		}
    }

    @GetMapping(value = "/getTareas")
    public List<EstadoMsj> getTareas(@RequestParam(value = "usuarioId", required = false) String usuarioId) {
	try {
	    List<EstadoMsj> msjs = estadoMsjRepository.findByUsuarioIdAndTipoMsjEstado(usuarioId, TipoMsjEstado.TAREAS);
	    return msjs;
	} catch (Exception e) {
	    String traza = "getTareas : ";
	    logger.error(traza + e.getMessage());
	    // e.printStackTrace();
	    return new ArrayList<>();
	}
    }

    @PostMapping(value = "/eliminarNukaMensajes")
    public Boolean eliminarNukaMensajes(
	    @RequestBody(required = false) List<String> nukaMsjIds) {
	Boolean res = false;
	if (!ObjectUtils.isEmpty(nukaMsjIds)) {
	    
	    //Optional<NukaMsj> nukaMsj = nukaMsjRepository.findById(nukaMsjId);
	    List<NukaMsj> nukaMsjs = nukaMsjRepository.findByIds(nukaMsjIds);
	    
	    if (!ObjectUtils.isEmpty(nukaMsjs)) {
		nukaMsjs.parallelStream().forEach(m -> { m.setVisible(false); });
		List<NukaMsj> savedMsjs = nukaMsjRepository.saveAll(nukaMsjs);
		return !ObjectUtils.isEmpty(savedMsjs);
	    } else {
		return res;
	    }
	}
	return res;
    }

    @GetMapping(value = "/eliminarNukaChat")
    public Boolean eliminarNukaChat(@RequestParam(value = "nukaChatId", required = false) String nukaChatId) {
	Boolean res = false;
	if (!ObjectUtils.isEmpty(nukaChatId)) {
	    Optional<NukaChat> nukaChat = nukaChatRepository.findById(nukaChatId);
	    if (!ObjectUtils.isEmpty(nukaChat.get())) {
		nukaChat.get().setVisible(false);
		NukaChat savedChat = nukaChatRepository.save(nukaChat.get());
		return !ObjectUtils.isEmpty(savedChat);
	    } else {
		return res;
	    }
	}
	return res;
    }

    @GetMapping(value = "/toogleFavoriteStatus")
    public Boolean toogleFavoriteStatus(@RequestParam(value = "nukaChatId", required = false) String nukaChatId) {
	Boolean res = false;
	if (!ObjectUtils.isEmpty(nukaChatId)) {
	    Optional<NukaChat> nukaChat = nukaChatRepository.findById(nukaChatId);
	    if (!ObjectUtils.isEmpty(nukaChat.get())) {
		Boolean esFavorito = nukaChat.get() != null && nukaChat.get().getFavorito() != null
			? nukaChat.get().getFavorito()
			: false;
		nukaChat.get().setFavorito(!esFavorito);
		NukaChat savedChat = nukaChatRepository.save(nukaChat.get());
		return !ObjectUtils.isEmpty(savedChat);
	    } else {
		return res;
	    }
	}
	return res;
    }

    @GetMapping(value = "/getBusquedaChat")
    public List<NukaMsj> getBusquedaChat(@RequestParam(value = "texto", required = false) String texto,
	    @RequestParam(value = "usuarioId", required = false) String usuarioId) {
	List<NukaChat> chats = nukaChatRepository.findByVisibleAndUsuarioId(true, usuarioId);
	List<String> chatIds = chats.stream().map(c -> c.getId()).collect(Collectors.toList());
	Pageable pageable = PageRequest.of(0, 5);
	List<NukaMsj> nukaMsjs = nukaMsjRepository.findByTextoLikeVisibleOrderByFechaDesc(texto, chatIds, pageable);

	return nukaMsjs;
    }

    @GetMapping(value = "/getVersionServidor")
    public ResponseEntity getVersionServidor() {
	Map<String, String> response = new HashMap<>();
	response.put("version", Constantes.NUKA_VERSION);
	return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/actualizarTokenDispositivo")
    public Boolean actualizarTokenDispositivo(@RequestParam(value = "usuarioId", required = false) String usuarioId,
	    @RequestParam(value = "dispositivo", required = false) String dispositivo,
	    @RequestParam(value = "token", required = false) String token) {
	return nukaService.actualizarTokenDispositivo(usuarioId, dispositivo, token);
    }

    @GetMapping("/imagen/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
	try {
	    Path fileStorageLocation = Paths.get(Constantes.IMAGEN_PATH).toAbsolutePath().normalize();
	    Path filePath = fileStorageLocation.resolve(filename).normalize();
	    Resource resource = new UrlResource(filePath.toUri());

	    if (resource.exists() || resource.isReadable()) {
		return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(resource);
	    } else {
		throw new FileNotFoundException("Could not read file: " + filename);
	    }
	} catch (Exception e) {
	    logger.error("serveFile : " + e.getMessage());
	    return ResponseEntity.badRequest().build();
	}
    }

    @GetMapping("/video/{filename:.+}")
    public ResponseEntity<Resource> serveVideo(@PathVariable String filename) {
	try {
	    Path fileStorageLocation = Paths.get(Constantes.VIDEO_PATH).toAbsolutePath().normalize();
	    Path filePath = fileStorageLocation.resolve(filename).normalize();
	    Resource resource = new UrlResource(filePath.toUri());

	    if (!resource.exists() || !resource.isReadable()) {
		throw new FileNotFoundException("Could not read file: " + filename);
	    }

	    String contentType = utilsService.determineContentType(filename);
	    return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
	} catch (Exception e) {
	    logger.error("serveVideo : " + e.getMessage());
	    return ResponseEntity.badRequest().build();
	}
    }

    @PostMapping(value = "/generarImagen")
    public GeneratedImageResponse generarImagen(@RequestParam(value = "model", required = false) String model,
	    @RequestParam(value = "prompt", required = true) String prompt,
	    @RequestParam(value = "size", required = false) String size,
	    @RequestParam(value = "numImages", required = false) int numImages) {
	GenerateImageRequest imagenResquest = new GenerateImageRequest("dall-e-2", prompt, "1024x1024", 1);
	if (!ObjectUtils.isEmpty(size)) {
	    imagenResquest.setSize(size);
	}

	if (!ObjectUtils.isEmpty(model)) {
	    imagenResquest.setModel(model);
	}

	if (!ObjectUtils.isEmpty(numImages)) {
	    imagenResquest.setNumImages(numImages);
	}

	return openAIClientService.generarImagen(imagenResquest);
    }

    @PostMapping("/modificarCitaOTarea")
    public EstadoMsj modificarCitaOTarea(@RequestParam(required = false) String modificarCitaOTareaJson) throws UnirestException, InterruptedException,
	    ExecutionException, IOException, UnsupportedAudioFileException {
	try {
	    EstadoMsj estadoMsj = objectMapper.readValue(modificarCitaOTareaJson, EstadoMsj.class);
	    Optional<EstadoMsj> estadoMsjGuardado = estadoMsjRepository.findById(estadoMsj.getId());
	    if (!ObjectUtils.isEmpty(estadoMsjGuardado.get())) {
			if (!ObjectUtils.isEmpty(estadoMsj.getImgMsjPath())) {
				estadoMsjGuardado.get().setImgMsjPath(estadoMsj.getImgMsjPath());
			}
			
			if (!ObjectUtils.isEmpty(estadoMsj.getTexto())) {
				estadoMsjGuardado.get().setTexto(estadoMsj.getTexto());
			}
			
			if (!ObjectUtils.isEmpty(estadoMsj.getFecha())) {
				estadoMsjGuardado.get().setFecha(estadoMsj.getFecha());
			}
			
			EstadoMsj estadoMsjModificado = estadoMsjRepository.save(estadoMsjGuardado.get());
			return estadoMsjModificado;
	    }
	    
	    return null;
	} catch (Exception e) {
	    // Maneja errores de análisis JSON
	    throw new IllegalArgumentException("Error al procesar el JSON nukaMsjPeticionJson: ", e);
	}
    }
    

    @PostMapping("/guardarArchivoTemporal")
    public ResponseEntity<Map<String, String>> guardarArchivoTemporal(
        @RequestParam String usuarioId,
        @RequestParam String carpeta,
        @RequestParam MultipartFile file) {
        try {
            System.out.println("Usuario ID: " + usuarioId);
            System.out.println("Archivo Nombre: " + file.getOriginalFilename());
            System.out.println("Archivo Tamaño: " + file.getSize());
            
            String path = nukaService.guardarArchivoTemporal(usuarioId, carpeta, file);            
            Map<String, String> response = new HashMap<>();
            response.put("path", path);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error guardando archivo temporal: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    


    // ++++++++++++++++++++++++++++++++++ BASE DE DATOS ++++++++++++++++++++++++++++++++++

    /***********************************************************************************************************************
     * Function Name: guardarAccion
     ************************************************************************************************************************
     * @brief Guardamos una accion en el repositorio de accionNukaRepository
     * @param[in] accion: aaa
     * @param[in] texto: aaa
     * @param[in] necesitaAudio: aaa
     * @param[in] grupo: aaa
     * @retval true Successful; CMT closed
     * @retval false Invalid settings
     * @details This function frees the CMT channel by clearing its assignment and
     *          disabling the associated interrupt. The CMT channel cannot be used
     *          again until it has been reopened with either the
     *          R_CMT_CreatePeriodic or the R_CMT_CreateOneShot function. If the CMT
     *          channel is already used as RTOS system timer, a call to this
     *          function with this CMT channel as channel, will result in FALSE
     *          being returned
     */
    @GetMapping(value = "/guardarAccion")
    public void guardarAccion(@RequestParam(value = "accion", required = false) String accion,
	    @RequestParam(value = "texto", required = false) String texto,
	    @RequestParam(value = "necesitaAudio", required = false) Boolean necesitaAudio,
	    @RequestParam(value = "grupo", required = false) String grupo) throws Exception {
	openAIClientService.guardarAccion(accion, texto, necesitaAudio, grupo);
    }

    @GetMapping(value = "/guardarUsuario")
    public void guardarUsuario(@RequestParam(value = "userNick", required = false) String userNick,
	    @RequestParam(value = "username", required = false) String username,
	    @RequestParam(value = "password", required = false) String password,
	    @RequestParam(value = "email", required = false) String email,
	    @RequestParam(value = "phone", required = false) String phone,
	    @RequestParam(value = "permission", required = false) String permission) throws Exception {

	User usuario = new User();
	usuario.setUserNick(userNick);
	usuario.setUsername(username);
	usuario.setPassword(this.utilsService.generarHash256(password));
	usuario.setEmail(email);
	usuario.setPhone(phone);
	usuario.setPermission(permission);

	DataUser dataUser = new DataUser();
	dataUser.setFechaUltimaActualizacion(utilsService.timestampNowLong());
	usuario.setData(dataUser);

	OpcionesModal opcionesModal = nukaService.inicializarAjustesUsuario();
	usuario.setOpcionesModal(opcionesModal);

	userRepository.insert(usuario);
    }

    @GetMapping(value = "/guardarProducto")
    public void guardarProducto(@RequestParam(value = "nombreProducto", required = false) String nombreProducto,
	    @RequestParam(value = "aliasProducto", required = false) List<String> aliasProducto,
	    @RequestParam(value = "categoria", required = false) String categoria) throws Exception {
	nukaService.guardarProducto(nombreProducto, aliasProducto, categoria);
    }

    @GetMapping(value = "/guardarProductoOpcionModal")
    public NukaMsj guardarProductoOpcionModal(
	    @RequestParam(value = "nombreProducto", required = false) String nombreProducto,
	    @RequestParam(value = "aliasProducto", required = false) List<String> aliasProducto,
	    @RequestParam(value = "categoria", required = false) String categoria,
	    @RequestParam(value = "chatId", required = false) String chatId,
	    @RequestParam(value = "productId", required = false) String productId) throws Exception {

	NukaMsj nukaMsj = null;
	List<String> aliasProductoLowerCase = aliasProducto.stream().map(String::toLowerCase)
		.collect(Collectors.toList());
	List<Product> products = productRepository.findByAliasProducto(aliasProductoLowerCase);
	if (ObjectUtils.isEmpty(products)) {
	    Product product = nukaService.guardarProducto(nombreProducto, aliasProducto, categoria);
	    nukaMsj = new NukaMsj(utilsService.generarIdMongo(), utilsService.timestampNowLong(),
		    "Se ha guardado un nuevo producto \"" + product.nombreProducto + "\"", productId, chatId,
		    TipoMsj.RESPUESTA, Constantes.AÑADIR_PRODUCTO_NUEVO, null, null, null, null, null, null, true,
		    null);
	    ProductResponse productResponse = new ProductResponse(1, product.getNombreProducto());
	    String jsonProducts = objectMapper.writeValueAsString(productResponse);
	    List<Product> productosAñadidos = new ArrayList<>();
	    List<String> productosNoReconocidos = new ArrayList<>();

	    accionesService.añadirProductosShopingList(jsonProducts, productosAñadidos, productosNoReconocidos);

	    if (ObjectUtils.isEmpty(productosAñadidos) && productosNoReconocidos.size() == 0) {
		String textoRes = nukaMsj.getTexto() + "\n Se ha añadido "
			+ productosAñadidos.toString().replace("[", "").replace("]", "") + " a la cesta de la compra";
		nukaMsj.setTexto(textoRes);
	    }
	} else {
	    Product product = products.get(0);
	    nukaMsj = new NukaMsj(utilsService.generarIdMongo(), utilsService.timestampNowLong(),
		    "El producto \"" + product.nombreProducto + "\" ya existe y no se ha guardado", productId, chatId,
		    TipoMsj.RESPUESTA, Constantes.AÑADIR_PRODUCTO_NUEVO, null, null, null, null, null, null, true,
		    null);
	}

	return nukaMsjRepository.save(nukaMsj);
    }

    @GetMapping(value = "/agregarAliasProducto")
    public NukaMsj agregarAliasProducto(@RequestParam(value = "usuarioId", required = false) String usuarioId,
	    @RequestParam(value = "chatId", required = false) String chatId,
	    @RequestParam(value = "productId", required = false) String productId,
	    @RequestParam(value = "aliasProducto", required = false) String aliasProducto) throws Exception {

	NukaMsj nukaMsj = nukaService.agregarAliasProducto(usuarioId, chatId, productId, aliasProducto);

	List<Product> products = productRepository.findByAliasProducto(Arrays.asList(aliasProducto));
	List<Product> productosAñadidos = new ArrayList<>();
	List<String> productosNoReconocidos = new ArrayList<>();

	if (products != null && products.size() > 0) {
	    ProductResponse productResponse = new ProductResponse(1, products.get(0).getNombreProducto());
	    String jsonProducts = objectMapper.writeValueAsString(productResponse);
	    accionesService.añadirProductosShopingList(jsonProducts, productosAñadidos, productosNoReconocidos);
	}

	if (!ObjectUtils.isEmpty(productosAñadidos) && productosNoReconocidos.size() == 0) {
	    String listaNombresProductos = null;
	    if (productosAñadidos.size() == 1) {
		listaNombresProductos = productosAñadidos.get(0).getNombreProducto();
	    } else {
		listaNombresProductos = productosAñadidos.stream().map(Product::getNombreProducto)
			.collect(Collectors.joining(", "));
	    }
	    String textoRes = nukaMsj.getTexto() + "\n Se ha añadido " + listaNombresProductos
		    + " a la cesta de la compra";
	    nukaMsj.setTexto(textoRes);
	}

	return nukaMsjRepository.save(nukaMsj);
    }

    @GetMapping(value = "/restaurarMongoDB")
    public void restaurarMongoDB(
	    @RequestParam(value = "fecha", required = false) String fecha,
	    @RequestParam(value = "db", required = false) String db) {
	nukaService.restaurarMongoDB(fecha, db);
    }

    @GetMapping(value = "/actualizarAjustes")
    public void actualizarAjustes(
	    @RequestParam(value = "actualizarUsuarios", required = false) Boolean actualizarUsuarios,
	    @RequestParam(value = "actualizarChats", required = false) Boolean actualizarChats,
	    @RequestParam(value = "resetearAjustes", required = false) Boolean resetearAjustes) {
	nukaService.actualizarAjustes(actualizarUsuarios, actualizarChats, resetearAjustes);
    }

    // ++++++++++++++++++++++++++++++++++ TEST ++++++++++++++++++++++++++++++++++

    @GetMapping(value = "/testNotificacion2")
    public void testNotificacion2(@RequestParam(value = "dispositivo", required = false) String dispositivo,
	    @RequestParam(value = "usuarioId", required = false) String usuarioId) {
	notificacionesService.testNotificacion2(usuarioId, dispositivo);
    }

    public int testUsuariosBBDD() {
	List<User> usuarios = new ArrayList<>();
	try {
	    usuarios = userRepository.findAll();
	} catch (Exception e) {
	    String traza = "testUsuariosBBDD : ";
	    logger.error(traza + e.getMessage());
	    // e.printStackTrace();
	}
	return usuarios.size();
    }

    @GetMapping(value = "/testChatsBBDD")
    public int testChatsBBDD() {
	List<NukaChat> chats = new ArrayList<>();
	try {
	    chats = nukaChatRepository.findAll();
	} catch (Exception e) {
	    String traza = "testUsuariosBBDD : ";
	    logger.error(traza + e.getMessage());
	    // e.printStackTrace();
	}
	return chats.size();
    }

    @GetMapping(value = "/test")
    public User test() {
	Optional<User> usuario = null;
	try {
	    usuario = userRepository.findById("6495ea1c3c484b05a173bb56");
	    OpcionesModal opcionesModal = nukaService.inicializarAjustesUsuario();
	    usuario.get().setOpcionesModal(opcionesModal);
	} catch (Exception e) {
	    String traza = "test : ";
	    logger.error(traza + e.getMessage());
	    // e.printStackTrace();
	}
	userRepository.save(usuario.get());
	return usuario.get();
    }

    @GetMapping(value = "/copiaSeguridadMongo")
    public void copiaSeguridadMongo() {
	scheduledTasksService.copiaSeguridadMongo();
    }

    @GetMapping(value = "/test2")
    public List<NukaChat> test2() {
	List<NukaChat> chats = nukaChatRepository.findByVisibleAndUsuarioId(true, "6495ea1c3c484b05a173bb56");
	chats.stream().forEach(c -> {
	    try {
		OpcionesModal opcionesModal = nukaService.inicializarAjustesChat();

		if ("GENERAL".equals(c.getSummary())) {
		    nukaService.eliminarOpcionPorId(opcionesModal, "GENERAL_NOMBRE_CHAT");
		}

		c.setOpcionesModal(opcionesModal);
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		String traza = "test2 : ";
		logger.error(traza + e.getMessage());
		// e.printStackTrace();
	    }
	});
	List<NukaChat> chatsRes = nukaChatRepository.saveAll(chats);
	return chatsRes;
    }

    @GetMapping(value = "/test3")
    public List<NukaChat> test3() {
	List<NukaChat> chats = nukaChatRepository.findByVisibleAndUsuarioId(true, "6495ea1c3c484b05a173bb56");
	for (NukaChat chat : chats) {
	    List<GrupoOpciones> grupoOpciones = chat.getOpcionesModal().getOpciones();
	    GrupoOpciones go = grupoOpciones.stream().filter(go1 -> "GENERAL".equals(go1.getEtiqueta())).toList()
		    .get(0);
	    OpcionBasica opcionBasica = go.getOpciones().stream().filter(ob -> "GENERAL_TAGS".equals(ob.getId()))
		    .toList().get(0);
	    List<String> valores = opcionBasica.getValor();
	    chat.setTags(valores);
	}
	List<NukaChat> chatsRes = nukaChatRepository.saveAll(chats);
	return chatsRes;
    }

    @GetMapping(value = "/hi")
    @Secured("ROLE_ADMIN")
    public String hi() {
	logger.info("----------------------- hi test -----------------------");
	return "Hi!";
    }

    @GetMapping(value = "/tts")
    public Boolean tts(@RequestParam(value = "texto", required = false) String texto)
	    throws InterruptedException, ExecutionException, IOException {

	Instant timestamp = Instant.now();
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
		.withZone(ZoneId.systemDefault());
	String timestampString = formatter.format(timestamp).replace(" ", "_").replace(":", "-");
	String wavFilename = timestampString + "_" + "USER" + ".wav";
	Boolean res = azureTextToSpeechService.sintetizar(texto, wavFilename, null, null);
	return res;
    }

    @GetMapping(value = "/clasificador2")
    public List<AccionNuka> clasificador2(@RequestParam(value = "texto", required = false) String texto)
	    throws Exception {
	Optional<User> usuario = userRepository.findById("6495ea1c3c484b05a173bb56");
	Optional<NukaChat> chat = nukaChatRepository.findById("6509d14c98e1c446d2a06988");
	return openAIClientService.clasificarAccion2(texto, usuario.get(), chat.get());
    }

    @GetMapping(value = "/transcription")
    public WhisperTranscriptionResponse createTranscription(
	    @RequestParam(value = "audioFile", required = false) MultipartFile audioFile)
	    throws UnirestException, IOException {
	WhisperTranscriptionRequest whisperTranscriptionRequest = new WhisperTranscriptionRequest(null, audioFile);
	return openAIClientService.createTranscription(whisperTranscriptionRequest);
    }


}
