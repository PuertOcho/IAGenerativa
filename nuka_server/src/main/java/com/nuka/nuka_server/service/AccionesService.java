package com.nuka.nuka_server.service;

import java.io.FileWriter;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.nuka.nuka_server.Constantes;
import com.nuka.nuka_server.NukaConfiguration;
import com.nuka.nuka_server.models.AccionNuka;
import com.nuka.nuka_server.models.ChatGPTRequest;
import com.nuka.nuka_server.models.ChatGPTResponse;
import com.nuka.nuka_server.models.ContenidoMsj;
import com.nuka.nuka_server.models.Embedding;
import com.nuka.nuka_server.models.EmbeddingResponse;
import com.nuka.nuka_server.models.EstadoMsj;
import com.nuka.nuka_server.models.NukaMsj;
import com.nuka.nuka_server.models.OpcionAccionDatosMsj;
import com.nuka.nuka_server.models.OpcionAccionMsj;
import com.nuka.nuka_server.models.OpcionMsj;
import com.nuka.nuka_server.models.OpcionesMsj;
import com.nuka.nuka_server.models.Product;
import com.nuka.nuka_server.models.ProductRelation;
import com.nuka.nuka_server.models.ProductResponse;
import com.nuka.nuka_server.models.ShoppingList;
import com.nuka.nuka_server.models.TipoMsjEstado;
import com.nuka.nuka_server.repository.AccionNukaRepository;
import com.nuka.nuka_server.repository.EstadoMsjRepository;
import com.nuka.nuka_server.repository.NukaMsjRepository;
import com.nuka.nuka_server.repository.ProductRepository;
import com.nuka.nuka_server.repository.ShoppingListRepository;
import com.nuka.nuka_server.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import opennlp.tools.util.StringUtil;

@RequiredArgsConstructor
@Service
public class AccionesService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UserRepository userRepository;

    @Autowired
    ShoppingListRepository shoppingListRepository;

    @Autowired
    NukaMsjRepository nukaMsjRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AccionNukaRepository accionNukaRepository;

    @Autowired
    OpenAIClientService openAIClientService;

    @Autowired
    AzureTextToSpeechService azureTextToSpeechService;

    @Autowired
    UtilsService utilsService;

    @Autowired
    NukaConfiguration nukaConfiguration;

    @Autowired
    EstadoMsjRepository estadoMsjRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    // Acciones

    public void accionAñadirCompra(NukaMsj nukaMsjPeticion, NukaMsj nukaMsjRespuesta, Product producto)
	    throws JsonMappingException, JsonProcessingException, UnirestException {
	String jsonProducts = null;

	if (!ObjectUtils.isEmpty(nukaMsjPeticion)) {
	    ChatGPTRequest chatGPTRequest = new ChatGPTRequest(Constantes.MODEL_DEFAULT, nukaMsjPeticion.texto, 0.2);
	    ChatGPTResponse chatGPTResponse = openAIClientService.chat(chatGPTRequest,
		    Constantes.COMPORTAMIENTO_ACCION_AÑADIR_COMPRA);
	    if (chatGPTResponse != null) {
		jsonProducts = chatGPTResponse.getChoices().get(0).getMessage().getContent();
	    }
	} else if (!ObjectUtils.isEmpty(producto)) {
	    ProductResponse productResponse = new ProductResponse(1, producto.getNombreProducto());
	    jsonProducts = objectMapper.writeValueAsString(productResponse);
	}

	try {
	    List<Product> productosAñadidos = new ArrayList<>();
	    List<String> productosNoReconocidos = new ArrayList<>();
	    añadirProductosShopingList(jsonProducts, productosAñadidos, productosNoReconocidos);

	    nukaMsjRespuesta.setRedireccion("shopping-list");
	    if (!ObjectUtils.isEmpty(productosAñadidos)) {
		String stringRes = formatProductListInResponse(productosAñadidos);
		nukaMsjRespuesta.setTexto("Se ha añadido " + stringRes + " a la cesta de la compra\n");
	    }

	    if (productosNoReconocidos.size() > 0) {
		productosNoReconocidos.stream().forEach(p -> {
		    try {
			List<Product> opcionesProductos = darOpcionesProducto(p);
			String opcionesJson = null;
			nukaMsjRespuesta.setTexto("Es necesario que especifique la siguiente acción. \n");

			// opciones de posibles productos
			OpcionesMsj opcionesMsj = new OpcionesMsj("No se reconoce el alias de \"" + p
				+ "\", agregalo a alguno de los siguiente productos", new ArrayList<>());
			opcionesProductos.stream().forEach(op -> {

			    Map<String, String> datos = new HashMap<>();
			    datos.put("productId", op.id);
			    datos.put("aliasProducto", p);

			    OpcionAccionMsj opcionAccionMsj = new OpcionAccionMsj("agregarAliasProducto", datos, false,
				    null);

			    OpcionMsj opcionMsj = new OpcionMsj(op.id, op.nombreProducto, opcionAccionMsj);
			    opcionesMsj.getOpciones().add(opcionMsj);

			});

			// opcion de agregar un nuevo producto
			Map<String, OpcionAccionDatosMsj> datosNecesarios = new HashMap<>();

			OpcionAccionDatosMsj opcionAccionDatosMsj_1 = new OpcionAccionDatosMsj("Nombre de producto",
				"texto", null);
			OpcionAccionDatosMsj opcionAccionDatosMsj_2 = new OpcionAccionDatosMsj("Alias de producto",
				"texto", Arrays.asList(p));
			OpcionAccionDatosMsj opcionAccionDatosMsj_3 = new OpcionAccionDatosMsj("Categoria de producto",
				"seleccion", Constantes.CATEGORIAS_PRODUCTO);
			datosNecesarios.put("nombreProducto", opcionAccionDatosMsj_1);
			datosNecesarios.put("aliasProducto", opcionAccionDatosMsj_2);
			datosNecesarios.put("categoria", opcionAccionDatosMsj_3);

			OpcionAccionMsj opcionAccionMsj = new OpcionAccionMsj("guardarProductoOpcionModal", null, true,
				datosNecesarios);

			OpcionMsj opcionMsj = new OpcionMsj(null, "Nuevo producto", opcionAccionMsj);
			opcionesMsj.getOpciones().add(opcionMsj);

			opcionesJson = objectMapper.writeValueAsString(opcionesMsj);

			String idOpciones = utilsService.generarIdMongo();
			try (FileWriter fileWriter = new FileWriter(Constantes.OPCION_PATH + idOpciones + ".json")) {
			    fileWriter.write(opcionesJson);
			    if (nukaMsjRespuesta.contenidoMsj == null) {
				nukaMsjRespuesta.setContenidoMsj(new ArrayList<>());
			    }
			    nukaMsjRespuesta.contenidoMsj.add(new ContenidoMsj(idOpciones, "OPCIONES"));
			} catch (IOException e) {
			    logger.error(e.getMessage());
			    e.printStackTrace();
			}

		    } catch (JsonProcessingException | UnirestException e) {
			String traza = "accionAñadirCompra : ";
			logger.error(traza + e.getMessage());
			// e.printStackTrace();
		    }

		});

	    }

	    if (ObjectUtils.isEmpty(productosAñadidos) && productosNoReconocidos.size() == 0) {
		nukaMsjRespuesta.setTexto("No se han añadido productos a la lista de la compra\n");
	    }

	} catch (Exception e) {
	    logger.error(e.getMessage());
	    e.printStackTrace();
	}

    }

    public void accionCalendarioCita(NukaMsj nukaMsjPeticion, NukaMsj nukaMsjRespuesta)
	    throws JsonMappingException, JsonProcessingException, UnirestException {
	int intentosMaximos = 3;
	for (int intento = 1; intento <= intentosMaximos; intento++) {
	    if (!ObjectUtils.isEmpty(nukaMsjPeticion)) {
		String json = null;
		try {
		    ChatGPTRequest chatGPTRequest = new ChatGPTRequest(Constantes.MODEL_DEFAULT,
			    nukaMsjPeticion.texto, 0.2 * intento);
		    String comportamientoAccionRecordatorio = utilsService.cambiarDatosUsuarioDeTexto(
			    nukaMsjPeticion.getUsuarioId(), Constantes.COMPORTAMIENTO_ACCION_CALENDARIO_CITA);
		    ChatGPTResponse chatGPTResponse = openAIClientService.chat(chatGPTRequest,
			    comportamientoAccionRecordatorio);
		    if (chatGPTResponse != null) {
			json = chatGPTResponse.getChoices().get(0).getMessage().getContent();
			Map<String, String> jsonResponse = objectMapper.readValue(json,
				new TypeReference<Map<String, String>>() {
				});
			long fechaEvento = utilsService.timestampWithFormat("yyyy-MM-dd HH:mm:ss",
				jsonResponse.get("fecha"));
			EstadoMsj estadoMsj = new EstadoMsj();
			estadoMsj.setId(utilsService.generarIdMongo());
			estadoMsj.setFecha(fechaEvento);
			estadoMsj.setTexto(jsonResponse.get("etiqueta"));
			estadoMsj.setTiempoRepeticion(1);
			estadoMsj.setVisible(true);
			estadoMsj.setUsuariosId(Arrays.asList(nukaMsjPeticion.getUsuarioId()));
			estadoMsj.setVisible(true);
			estadoMsj.setTipoMsjEstado(TipoMsjEstado.CALENDARIO); // y notificaciones
			estadoMsj.setRedireccion("CALENDARIO");

			nukaMsjRespuesta
				.setTexto("Se ha añadido \"" + jsonResponse.get("etiqueta") + "\" al calendario");
			nukaMsjRespuesta.setRedireccion("status:CALENDARIO");

			estadoMsjRepository.save(estadoMsj);
			break;
		    }
		} catch (Exception e) {
		    // En el último intento, relanzar la excepción para que sea manejada fuera del
		    // bucle
		    if (intento == intentosMaximos) {
			logger.error(e.getMessage());
			e.printStackTrace();
		    }
		}
	    }
	}
    }

    public void accionListaAcciones(NukaMsj nukaMsjPeticion, NukaMsj nukaMsjRespuesta) {
		StringBuilder respuesta = new StringBuilder();
		respuesta.append("<p>")
			.append("Las acciones disponibles en el asistente son:")
			.append("</p>");

		List<AccionNuka> acciones = accionNukaRepository.findAll();
		for (AccionNuka accion : acciones) {
			respuesta
				.append("<p>")
				.append("<strong>" + accion.getAccionId() + "</strong>").append(" → ").append(accion.getTexto())
			.append("</p>");
		}

		if (!ObjectUtils.isEmpty(respuesta)) {
			nukaMsjRespuesta.setTexto(respuesta.toString());
		} else {
			String mensajeError = "accionListaAcciones : Error al convertir lista de acciones";
			logger.error(mensajeError);
			nukaMsjRespuesta.setTexto(mensajeError);
		}
    }

    public void accionRecordatorio(NukaMsj nukaMsjPeticion, NukaMsj nukaMsjRespuesta)
	    throws JsonMappingException, JsonProcessingException, UnirestException {
	int intentosMaximos = 3;
	for (int intento = 1; intento <= intentosMaximos; intento++) {
	    if (!ObjectUtils.isEmpty(nukaMsjPeticion)) {
		String json = null;
		try {
		    ChatGPTRequest chatGPTRequest = new ChatGPTRequest(Constantes.MODEL_DEFAULT,
			    nukaMsjPeticion.texto, 0.2 * intento);
		    String comportamientoAccionRecordatorio = utilsService.cambiarDatosUsuarioDeTexto(
			    nukaMsjPeticion.getUsuarioId(), Constantes.COMPORTAMIENTO_ACCION_RECORDATORIO);
		    ChatGPTResponse chatGPTResponse = openAIClientService.chat(chatGPTRequest,
			    comportamientoAccionRecordatorio);
		    if (chatGPTResponse != null) {
			json = chatGPTResponse.getChoices().get(0).getMessage().getContent();
			Map<String, String> jsonResponse = objectMapper.readValue(json,
				new TypeReference<Map<String, String>>() {
				});
			long fechaEvento = utilsService.timestampWithFormat("yyyy-MM-dd HH:mm:ss",
				jsonResponse.get("fecha"));
			EstadoMsj estadoMsj = new EstadoMsj();
			estadoMsj.setId(utilsService.generarIdMongo());
			estadoMsj.setFecha(fechaEvento);
			estadoMsj.setTexto(jsonResponse.get("etiqueta"));
			estadoMsj.setTiempoRepeticion(1);
			estadoMsj.setVisible(true);
			estadoMsj.setUsuariosId(Arrays.asList(nukaMsjPeticion.getUsuarioId()));
			estadoMsj.setVisible(true);
			estadoMsj.setTipoMsjEstado(TipoMsjEstado.NOTIFICACIONES);
			estadoMsj.setImgMsjPath("assets/icon/0058.png");

			nukaMsjRespuesta
				.setTexto("Se ha añadido \"" + jsonResponse.get("etiqueta") + "\" a notificaciones");
			nukaMsjRespuesta.setRedireccion("status:NOTIFICACIONES");

			estadoMsjRepository.save(estadoMsj);
			break;
		    }
		} catch (Exception e) {
		    // En el último intento, relanzar la excepción para que sea manejada fuera del
		    // bucle
		    if (intento == intentosMaximos) {
			throw new RuntimeException("Se ha excedido el número máximo de intentos", e);
		    }
		}
	    }
	}
    }

    public void accionConversionArchivos(NukaMsj nukaMsjPeticion, NukaMsj nukaMsjRespuesta) throws JsonMappingException, JsonProcessingException, UnirestException {

	
	}
	
    // segundas llamadas

    public void añadirProductosShopingList(String jsonProducts, List<Product> productosAñadidos,
	    List<String> productosNoReconocidos) throws JsonMappingException, JsonProcessingException {
	// Con el json de productos, que corresponde al alias. Buscamos en Mongo que
	// producto es
	List<ProductResponse> productResponse = objectMapper.readValue("[" + jsonProducts + "]",
		new TypeReference<List<ProductResponse>>() {
		});

	// obtenemos la lista de la compra actual y añadimos el producto y si no tenemos
	// creamos una
	List<ShoppingList> shoppingLists = shoppingListRepository.findByIsFinished(false, null);
	ShoppingList resShoppingList = null;

	List<ProductRelation> productRelations = new ArrayList<>();

	if (shoppingLists.isEmpty()) {
	    resShoppingList = new ShoppingList(null, false, null, null);

	    productResponse.forEach(p -> {
		List<Product> product = productRepository
			.findByAliasProducto(Arrays.asList(p.productName.toLowerCase()));
		if (!ObjectUtils.isEmpty(product)) {
		    productosAñadidos.add(product.get(0));
		    productRelations.add(new ProductRelation(p.getQuantity(), product.get(0), false));
		} else {
		    productosNoReconocidos.add(p.productName.toLowerCase());
		}
	    });

	    if (!ObjectUtils.isEmpty(resShoppingList) && !ObjectUtils.isEmpty(productRelations)) {
		resShoppingList.setProductRelations(productRelations);
		shoppingListRepository.insert(resShoppingList);
	    }

	} else {
	    resShoppingList = shoppingLists.get(0);
	    List<ProductRelation> productRelationsAux = resShoppingList.getProductRelations();

	    productResponse.forEach(p -> {
		List<Product> product = productRepository
			.findByAliasProducto(Arrays.asList(p.productName.toLowerCase()));
		if (!ObjectUtils.isEmpty(product)) {
		    productosAñadidos.add(product.get(0));
		    ProductRelation relacion = productRelationsAux.parallelStream()
			    .filter(pr -> pr.getProductInfo().getId().equals(product.get(0).getId())).findFirst()
			    .orElse(null);

		    if (!ObjectUtils.isEmpty(relacion)) {
			relacion.setQuantity(relacion.getQuantity() + p.getQuantity());
		    } else {
			productRelationsAux.add(new ProductRelation(p.getQuantity(), product.get(0), false));
		    }
		} else {
		    productosNoReconocidos.add(p.productName.toLowerCase());
		}

	    });

	    resShoppingList.setProductRelations(productRelationsAux);
	    shoppingListRepository.save(resShoppingList);
	}
    }

    // privados

    private List<Product> darOpcionesProducto(String aliasProducto)
	    throws JsonMappingException, JsonProcessingException, UnirestException {
	List<Product> productsRes = new ArrayList<>();

	if (!StringUtil.isEmpty(aliasProducto)) {

	    EmbeddingResponse embeddingResponse = openAIClientService.peticionEmbedding(Arrays.asList(aliasProducto));
	    List<Product> productsMongo = productRepository.findAll();

	    for (Embedding embedding : embeddingResponse.getData()) {
		List<Product> productsList = productsMongo.parallelStream()
			.sorted((p1, p2) -> Double.compare(
				utilsService.calculateCosineSimilarity(p2.embedding, embedding.getEmbedding()),
				utilsService.calculateCosineSimilarity(p1.embedding, embedding.getEmbedding())))
			.limit(3).collect(Collectors.toList());
		productsRes = productsList;
	    }

	}
	return productsRes;
    }

    private static String formatProductListInResponse(List<Product> productList) {
	int size = productList.size();
	if (size == 0) {
	    return "";
	} else if (size == 1) {
	    return productList.get(0).getNombreProducto();
	} else {
	    StringBuilder stringBuilder = new StringBuilder();
	    for (int i = 0; i < size - 1; i++) {
		if (size - 1 > i + 1) {
		    stringBuilder.append(productList.get(i).getNombreProducto());
		    stringBuilder.append(", ");
		} else {
		    stringBuilder.append(productList.get(i).getNombreProducto());
		    stringBuilder.append(" ");
		}
	    }
	    stringBuilder.append("y ");
	    stringBuilder.append(productList.get(size - 1).getNombreProducto());
	    return stringBuilder.toString();
	}
    }

}
