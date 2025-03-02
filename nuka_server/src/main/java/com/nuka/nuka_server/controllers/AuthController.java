package com.nuka.nuka_server.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.nuka.nuka_server.Constantes;
import com.nuka.nuka_server.models.ChatGPTRequest;
import com.nuka.nuka_server.models.ChatGPTResponse;
import com.nuka.nuka_server.models.LoginResponse;
import com.nuka.nuka_server.models.NukaChat;
import com.nuka.nuka_server.models.NukaMsj;
import com.nuka.nuka_server.models.TipoMsj;
import com.nuka.nuka_server.models.User;
import com.nuka.nuka_server.models.WhisperTranscriptionResponse;
import com.nuka.nuka_server.repository.NukaChatRepository;
import com.nuka.nuka_server.repository.NukaMsjRepository;
import com.nuka.nuka_server.repository.UserRepository;
import com.nuka.nuka_server.security.JwtUtil;
import com.nuka.nuka_server.service.AzureTextToSpeechService;
import com.nuka.nuka_server.service.NukaService;
import com.nuka.nuka_server.service.OpenAIClientService;
import com.nuka.nuka_server.service.UtilsService;

@Controller
@RequestMapping("/noAuth")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST })
public class AuthController {

    Logger logger = LoggerFactory.getLogger(getClass());

    private final AuthenticationManager authenticationManager;

    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UtilsService utilsService;

    @Autowired
    NukaService nukaService;
    
    @Autowired
    AzureTextToSpeechService azureTextToSpeechService;
    
    @Autowired
    NukaChatRepository nukaChatRepository;
    
    @Autowired
    NukaMsjRepository nukaMsjRepository;
    
	@Autowired
    OpenAIClientService openAIClientService;

    ObjectMapper objectMapper = new ObjectMapper();
    
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
	this.authenticationManager = authenticationManager;
	this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestParam(required = false) String userNick,
	    @RequestParam(required = false) String password) {
	try {
	    String encodedPassword = utilsService.generarHash256(password);

	    Authentication authentication = authenticationManager
		    .authenticate(new UsernamePasswordAuthenticationToken(userNick, encodedPassword));

	    String nickUsuario = authentication.getName();
	    User usuarioCompleto = userRepository.findByUserNick(nickUsuario);

	    User usuarioParaToken = new User(usuarioCompleto.getId(), usuarioCompleto.getUserNick());
	    String token = jwtUtil.createToken(usuarioParaToken);
	    LoginResponse loginRes = new LoginResponse(usuarioCompleto, token);

	    return ResponseEntity.ok(loginRes);
	} catch (BadCredentialsException e) {
	    String traza = "login BadCredentialsException : Invalid userNick or password";
	    logger.error(traza + e.getMessage());
	    return null;
	} catch (Exception e) {
	    String traza = "login Exception : ";
	    logger.error(traza + e.getMessage());
	    return null;
	}
    }

    @PostMapping("/actualizarToken")
    public ResponseEntity actualizarToken(@RequestParam(required = false) String userNick,
	    @RequestParam(required = false) String encodedPassword) {
	try {
	    Authentication authentication = authenticationManager
		    .authenticate(new UsernamePasswordAuthenticationToken(userNick, encodedPassword));

	    String nickUsuario = authentication.getName();
	    User usuarioCompleto = userRepository.findByUserNick(nickUsuario);

	    User usuarioParaToken = new User(usuarioCompleto.getId(), usuarioCompleto.getUserNick());
	    String token = jwtUtil.createToken(usuarioParaToken);
	    LoginResponse loginRes = new LoginResponse(usuarioCompleto, token);

	    return ResponseEntity.ok(loginRes);
	} catch (BadCredentialsException e) {
	    String traza = "login BadCredentialsException : Invalid userNick or password";
	    logger.error(traza + e.getMessage());
	    return null;
	} catch (Exception e) {
	    String traza = "login Exception : ";
	    logger.error(traza + e.getMessage());
	    return null;
	}
    }

    @GetMapping("/imagen/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
	try {
	    Path fileStorageLocation = Paths.get(Constantes.IMAGEN_NOAUTH_PATH).toAbsolutePath().normalize();
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
	    Path fileStorageLocation = Paths.get(Constantes.VIDEO_NOAUTH_PATH).toAbsolutePath().normalize();
	    Path filePath = fileStorageLocation.resolve(filename).normalize();
	    Resource resource = new UrlResource(filePath.toUri());

		logger.info("File path: " + filePath.toString());


		if (!resource.exists()) {
		throw new FileNotFoundException("Could not read file: " + filename);
	    }

	    String contentType = utilsService.determineContentType(filename);
	    return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
	} catch (Exception e) {
	    logger.error("serveVideo : " + e.getMessage());
	    return ResponseEntity.badRequest().build();
	}
    }


    // ++++++++++++++++++++++++++++++++++ BetBot ++++++++++++++++++++++++++++++++++
    
	@GetMapping(value = "/restaurarMongoDB")
    public void restaurarMongoDB(@RequestParam(value = "fecha", required = false) String fecha,
	    @RequestParam(value = "db", required = false) String db) {
	nukaService.restaurarMongoDB(fecha, db);
    }


    @PostMapping("/nukaWatchPeticion")
    public ResponseEntity<String> nukaWatchPeticion(
            @RequestParam(required = false) String nukaWatchPeticionJson,
            @RequestParam(required = false) MultipartFile audioFile) throws UnirestException, IOException {
        
        System.out.println("nukaMsjPeticionJson: " + nukaWatchPeticionJson);

        if (audioFile != null && !audioFile.isEmpty()) {
            System.out.println("Nombre del archivo: " + audioFile.getOriginalFilename());
            System.out.println("Tamaño del archivo: " + audioFile.getSize() + " bytes");
            System.out.println("Tipo de archivo: " + audioFile.getContentType());

    	    String tipo = utilsService.getResourceMapKeyByValue(audioFile.getContentType());
    	    String id = utilsService.generarIdMongo();
    	    String filePath = utilsService.guardarMultipartFile(audioFile, id);
    	    
    		WhisperTranscriptionResponse whisperTranscriptionResponse = openAIClientService
    				.createTranscriptionWithFilePath(filePath);

    		NukaMsj nukaMsj = nukaService.inicializarRespuestaMsj("CHAT");
    		
    		String textoEntrada = null;
    			if (whisperTranscriptionResponse != null) {
    				textoEntrada = whisperTranscriptionResponse.getText();
    			} else if (textoEntrada == null) {
    			throw new IllegalArgumentException("Sin datos de entrada");
    		    }

    			ChatGPTResponse chatGPTResponse = nukaService.preguntaGPT();
				// Cuando recibimos un error al llamar a la API de OpenAI 
				if (Constantes.OPENAI_RESPONSE_ERROR.equals(chatGPTResponse.getChoices().get(0).getFinishReason())) {
					Boolean errorEnRespuesta = true;
				    StringBuilder respuestaGPT = new StringBuilder();
				    respuestaGPT.append("**" + chatGPTResponse.getChoices().get(0).getFinishReason() + "**").append(" → ");
				    respuestaGPT.append("\n");
				    respuestaGPT.append(chatGPTResponse.getChoices().get(0).getMessage().getContent());
				    nukaMsj.texto = respuestaGPT.toString();
				    
				} else {
				    nukaMsj.texto = chatGPTResponse.getChoices().get(0).getMessage().getContent();
				}
				
				Optional<NukaChat> chat = nukaChatRepository.findById("66ed4b7b1a062e5c05b8c6e6");
			    String wavFilename = nukaMsj.id + ".wav";
			    String voz = nukaService.initValorAjustesChat_STRING(chat.get(), "AUDIO_VOZ");
			    
			    
			    String textoPlano = utilsService.markdownToPlainText(nukaMsj.texto);
			    System.out.println("textoPlano: " + textoPlano);
			    
			    Boolean audioGuardado = azureTextToSpeechService.sintetizar(textoPlano, wavFilename, voz, null);
			    
			    if (audioGuardado) {
					nukaMsj.setNeedAudio(true);
					nukaMsj.setAudioResource("audioWav");
		        } else {
		            return ResponseEntity.badRequest().body("No se recibió un archivo de audio.");
		        }

			    System.out.print("nukaMsj.texto: " + nukaMsj.texto);
        }

        return ResponseEntity.ok("Archivo recibido correctamente");
    }

    @PostMapping("/getLastAudio")
    public ResponseEntity<Resource> getLastAudio(@RequestBody String id) throws IOException {
        // Simular búsqueda del chat y mensajes basado en el 'id' recibido en el cuerpo de la solicitud
        Optional<NukaChat> chat = nukaChatRepository.findById(id); // Usa el 'id' recibido en la solicitud
        List<String> chatIds = new ArrayList<>();
        chatIds.add(id);

        Pageable pageable = PageRequest.of(0, 2);
        List<NukaMsj> nukaMsjs = nukaMsjRepository.findTopNByChatIdsAndFecha(chatIds, utilsService.timestampNowLong(), pageable);
        List<NukaMsj> nukaMsjsSoloPeticion = nukaMsjs.stream().filter(m -> m.getTipoMsj().equals(TipoMsj.RESPUESTA)).toList();
        if (!ObjectUtils.isEmpty(nukaMsjsSoloPeticion) && !ObjectUtils.isEmpty(nukaMsjsSoloPeticion.get(0).getAudioResource())) {
            // Obtener la ruta del archivo basado en el ID del mensaje
            String wavFilename = nukaMsjsSoloPeticion.get(0).getId() + ".wav";
            String filePath = Constantes.AUDIO_PATH + wavFilename;

            // Cargar el archivo como recurso desde el sistema de archivos
            Path path = Paths.get(filePath);
            Resource resource = new FileSystemResource(path);

            // Verificar si el recurso existe
            if (resource.exists()) {
                // Configurar los encabezados de la respuesta
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path.getFileName().toString() + "\"");
                headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

                // Devolver el archivo con los encabezados
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(resource.contentLength())
                        .body(resource);
            } else {
                // Si el recurso no existe
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } else {
            // Si no se encontró un mensaje con archivo de audio
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
    }


}