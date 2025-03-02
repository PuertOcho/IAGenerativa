package com.nuka.nuka_server.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.nuka.nuka_server.Constantes;
import com.nuka.nuka_server.NukaConfiguration;
import com.nuka.nuka_server.models.EstadoMsj;

import com.nuka.nuka_server.models.NukaChat;
import com.nuka.nuka_server.models.NukaMsj;

import com.nuka.nuka_server.models.TipoMsjEstado;
import com.nuka.nuka_server.models.User;
import com.nuka.nuka_server.models.Firebase.NotificationFirebase;
import com.nuka.nuka_server.repository.EstadoMsjRepository;
import com.nuka.nuka_server.repository.NukaChatRepository;
import com.nuka.nuka_server.repository.NukaMsjRepository;
import com.nuka.nuka_server.repository.UserRepository;

@Service
public class ScheduledTasksService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    NukaConfiguration nukaConfiguration;

    @Autowired
    NukaMsjRepository nukaMsjRepository;

    @Autowired
    EstadoMsjRepository estadoMsjRepository;

    @Autowired
    NukaChatRepository nukaChatRepository;

    @Autowired
    UtilsService utilsService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NotificacionesService notificacionesService;

    @Autowired
    NukaService nukaService;
    
    
    public void actualizarAjustes () {
    	nukaService.actualizarAjustes(false, true, false);
    }
    
    public void eliminarMensajesBorrados() {
	List<String> mensajeIds = new ArrayList<>();
	List<NukaMsj> mensajesNoVisibles = nukaMsjRepository.findByVisibility(false);
	if (!ObjectUtils.isEmpty(mensajesNoVisibles)) {
	    mensajeIds = mensajesNoVisibles.stream().map(NukaMsj::getId).collect(Collectors.toList());
	    if (!ObjectUtils.isEmpty(mensajeIds)) {
		try {
		    nukaMsjRepository.deleteAllById(mensajeIds);
		} catch (Exception e) {
		    String traza = "eliminarMensajesBorrados : ";
		    logger.error(traza + e.getMessage());
		    // e.printStackTrace();
		}
	    }
	}

	String traza = "eliminarMensajesBorrados : ";
	String texto = "Se han eliminado " + mensajeIds.size() + " mensajes; ids: " + mensajeIds.toString();
	logger.info(traza + texto);
    }

    public void crearNotificacionesDeHoyPorCitasCalendario() {
	LocalDate today = LocalDate.now();
	ZoneId zone = ZoneId.systemDefault();

	LocalDateTime principioDelDia = today.atTime(0, 0, 0, 000_000_000);
	LocalDateTime finDelDia = today.atTime(23, 59, 59, 999_999_999);

	ZonedDateTime zonedDateTimeInicio = principioDelDia.atZone(zone);
	ZonedDateTime zonedDateTimeFin = finDelDia.atZone(zone);

	long principioDelDiaTimestamp = zonedDateTimeInicio.toInstant().toEpochMilli();
	long finDelDiaTimestamp = zonedDateTimeFin.toInstant().toEpochMilli();

	List<EstadoMsj> estadoMsjsCalendario = estadoMsjRepository.findByTipoMsjEstadoAndFechaBetween(
		TipoMsjEstado.CALENDARIO, principioDelDiaTimestamp, finDelDiaTimestamp);

	if (!ObjectUtils.isEmpty(estadoMsjsCalendario)) {
	    List<EstadoMsj> estadoMsjsCalendario2Notificaciones = new ArrayList<>();

	    for (EstadoMsj m : estadoMsjsCalendario) {

		EstadoMsj estadoMsjNotificaciones = new EstadoMsj();
		estadoMsjNotificaciones.setId(utilsService.generarIdMongo());
		estadoMsjNotificaciones.setFecha(m.getFecha());
		estadoMsjNotificaciones.setTexto(m.getTexto());
		estadoMsjNotificaciones.setVisible(true);
		estadoMsjNotificaciones.setUsuariosId(m.getUsuariosId());
		estadoMsjNotificaciones.setVisible(true);
		estadoMsjNotificaciones.setVisible(true);
		estadoMsjNotificaciones.setTipoMsjEstado(TipoMsjEstado.NOTIFICACIONES);
		estadoMsjNotificaciones.setImgMsjPath("assets/icon/0020.png");

		estadoMsjsCalendario2Notificaciones.add(estadoMsjNotificaciones);
	    }

	    estadoMsjRepository.saveAll(estadoMsjsCalendario2Notificaciones);
	    String traza = "crearNotificacionesDeHoyPorCitasCalendario : ";
	    String texto = "Se han agregado " + estadoMsjsCalendario2Notificaciones.size()
		    + " notificaciones como recordatorio del calendario";
	    logger.info(traza + texto);
	}
    }

    public void copiaSeguridadMongo() {
	try {
	    String host = nukaConfiguration.getMONGO_HOST();
	    String port = nukaConfiguration.getMONGO_PUERTO();
	    String database = nukaConfiguration.getMONGO_BD_NAME();
	    String username = nukaConfiguration.getMONGO_USERNAME();
	    String password = nukaConfiguration.getMONGO_PASSWORD();
	    String authenticationDatabase = nukaConfiguration.getMONGO_AUTHENTICATION_DATABASE();

	    LocalDate currentDate = LocalDate.now();
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
	    String formattedDate = currentDate.format(formatter);

	    String outputDirectory = Constantes.MONGODUMP_PATH + formattedDate;

	    System.out.println("outputDirectory: " + outputDirectory);
	    ProcessBuilder processBuilderMkdir = new ProcessBuilder("mkdir", outputDirectory, "&&", "chmod", "777", outputDirectory);
	    processBuilderMkdir.redirectOutput(ProcessBuilder.Redirect.INHERIT);
	    processBuilderMkdir.redirectError(ProcessBuilder.Redirect.INHERIT);

	    Process processMkdir = processBuilderMkdir.start();

	    int exitCodeMkdir = processMkdir.waitFor();

	    if (exitCodeMkdir == 0) {
		logger.info("mkdir : " + outputDirectory);
	    } else {
		logger.error("mkdir :  " + exitCodeMkdir);
	    }
	    
	    logger.info("copiaSeguridadMongo : Comieza la copia de seguridad con fecha " + formattedDate);
	    ProcessBuilder processBuilder = new ProcessBuilder("mongodump", "--host", host, "--port", port,
		    "--username", username, "--password", password, "--authenticationDatabase", authenticationDatabase,
		    "--db", database, "--out", outputDirectory);

	    processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
	    processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

	    Process process = processBuilder.start();

	    int exitCode = process.waitFor();

	    if (exitCode == 0) {
		logger.info("copiaSeguridadMongo : mongodump completado exitosamente.");
	    } else {
		logger.error("copiaSeguridadMongo : Error al ejecutar mongodump. Código de salida: " + exitCode);
	    }

	} catch (IOException | InterruptedException e) {
	    logger.error("copiaSeguridadMongo : Error durante la ejecución de mongodump: " + e.getMessage());
	}
    }


	public Boolean borrarTMP() {
		String outputDirectory = Constantes.TMP_PATH;

		Path directoryPath = Paths.get(outputDirectory);
		if (!Files.exists(directoryPath)) {
			logger.error("El directorio no existe: " + outputDirectory);
			return false;
		}
	
		try {
			Files.walk(directoryPath)
				.filter(Files::isRegularFile)
				.forEach(file -> {
					try {
						Files.delete(file);
					} catch (IOException e) {
						logger.error("Error eliminando archivo: " + file, e);
					}
				});
	
			Files.walk(directoryPath)
				.filter(Files::isDirectory)
				.filter(path -> !path.equals(directoryPath)) // Evita borrar TMP_PATH en sí mismo
				.forEach(subdir -> {
					try {
						Files.delete(subdir);
					} catch (IOException e) {
						logger.error("Error eliminando subdirectorio: " + subdir, e);
					}
				});
	
			return true;
		} catch (IOException e) {
			logger.error("Error al limpiar el directorio: " + outputDirectory, e);
			return false;
		}
	}


    public Boolean enviarNotificaciones(Long tiempoLimiteEnMinutos, Long tiempoMinimoParaIgnorarEnMinutos) {
	List<NotificationFirebase> notificaciones = new ArrayList<>();
	ZoneId zone = ZoneId.systemDefault();

	LocalDateTime inicio = null;
	if (!ObjectUtils.isEmpty(tiempoMinimoParaIgnorarEnMinutos)) {
	    inicio = LocalDateTime.now().plusMinutes(tiempoMinimoParaIgnorarEnMinutos);
	} else {
	    inicio = LocalDateTime.now();
	}
	LocalDateTime fin = inicio.plusMinutes(tiempoLimiteEnMinutos);

	ZonedDateTime zonedDateTimeInicio = inicio.atZone(zone);
	ZonedDateTime zonedDateTimeFin = fin.atZone(zone);

	long principioTimestamp = zonedDateTimeInicio.toInstant().toEpochMilli();
	long finTimestamp = zonedDateTimeFin.toInstant().toEpochMilli() - 1;

	List<EstadoMsj> estadoMsjsNotificaciones = estadoMsjRepository
		.findByTipoMsjEstadoAndFechaBetween(TipoMsjEstado.NOTIFICACIONES, principioTimestamp, finTimestamp);

	if (!ObjectUtils.isEmpty(estadoMsjsNotificaciones)) {

	    Map<String, List<EstadoMsj>> mapaPorIdUsuario = estadoMsjsNotificaciones.stream().flatMap(
		    msj -> msj.getUsuariosId().stream().map(idUsuario -> new AbstractMap.SimpleEntry<>(idUsuario, msj)))
		    .collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey,
			    Collectors.mapping(AbstractMap.SimpleEntry::getValue, Collectors.toList())));

	    for (String idUsuario : mapaPorIdUsuario.keySet()) {
		Optional<User> usuario = userRepository.findById(idUsuario);

		if (!ObjectUtils.isEmpty(usuario.get()) && !ObjectUtils.isEmpty(usuario.get().getData())
			&& !ObjectUtils.isEmpty(usuario.get().getData().getTokenPorDispositivos())
			&& !ObjectUtils.isEmpty(mapaPorIdUsuario.get(idUsuario))) {

		    for (EstadoMsj notificacion : mapaPorIdUsuario.get(idUsuario)) {
			for (String dispositivo : usuario.get().getData().getTokenPorDispositivos().keySet()) {
			    /*
			     * String tokenDispositivos, String cabecera, String mensaje, String icono,
			     * String imagen, String color, Map<String, String> mapDatos
			     */
			    notificacionesService.enviarNotificacion(
				    usuario.get().getData().getTokenPorDispositivos().get(dispositivo),
				    String.format("Recordatorio → %s",
					    utilsService.getFormattedDateFromTimestamp("HH:mm", notificacion.fecha)),
				    notificacion.texto,
				    utilsService.getImagenPublicUrlByNotificacion(notificacion.imgMsjPath), null,
				    Constantes.COLOR_NOTIFICACION_PRINCIPAL, null);
			}
		    }

		}
		usuario.get().getData().getTokenPorDispositivos();

	    }

	    System.out.print(mapaPorIdUsuario);
	}

	return notificaciones.size() > 0;
    }

    // ++++++++++++++++++++++++++++++++++ DEPRECATED
    // ++++++++++++++++++++++++++++++++++

    // Ahora se usa 'eliminarMensajesBorrados' por que no se hace distinciones de si
    // el chat es visible o no
    public void eliminarMensajesBorradosChatNoVisibles() {
	List<String> ids;
	List<NukaMsj> mensajesDeChatsNoVisibles;
	List<String> mensajeIds = new ArrayList<>();
	List<NukaChat> ChatsNoVisibles = nukaChatRepository.findByVisible(false);

	if (!ObjectUtils.isEmpty(ChatsNoVisibles)) {
	    ids = ChatsNoVisibles.stream().map(NukaChat::getId).collect(Collectors.toList());
	    if (!ObjectUtils.isEmpty(ids)) {
		mensajesDeChatsNoVisibles = nukaMsjRepository.findByChatIds(ids);
		if (!ObjectUtils.isEmpty(mensajesDeChatsNoVisibles)) {
		    try {
			mensajeIds = mensajesDeChatsNoVisibles.stream().map(NukaMsj::getId)
				.collect(Collectors.toList());
			nukaMsjRepository.deleteAllById(mensajeIds);
		    } catch (Exception e) {
			String traza = "eliminarMensajesBorradosChatNoVisibles : ";
			logger.error(traza + e.getMessage());
			// e.printStackTrace();
		    }
		}
	    }
	}

	String traza = "eliminarMensajesBorradosChatNoVisibles : ";
	String texto = "Se han eliminado " + mensajeIds.size() + " mensajes";
	logger.info(traza + texto);
    }
    
}
