package com.nuka.nuka_server.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.sikuli.script.FindFailed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nuka.nuka_server.service.ScheduledTasksService;
import com.nuka.nuka_server.service.UtilsService;

@Component
public class ScheduledController {

    @Autowired
    ScheduledTasksService scheduledTasksService;

    private static final Logger logger = LoggerFactory.getLogger(ScheduledController.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    @Autowired
    UtilsService utilsService;
    
    // Cada dia a la 4 AM
    @Scheduled(cron = "0 0 4 ? * *")
    public void comprobacionesDiarias() {
    logger.info("comprobacionesDiarias: Ejecucion de tareas programadas {}", dateFormat.format(new Date()));

    scheduledTasksService.eliminarMensajesBorrados();

    scheduledTasksService.crearNotificacionesDeHoyPorCitasCalendario();

    }

    // Cada día 1 de cada mes a las 12 de la noche
    @Scheduled(cron = "0 0 0 1 * ?")
    public void cadaPrimeroDeMes() {
    logger.info("cadaPrimeroDeMes: Ejecucion de tareas programadas {}", dateFormat.format(new Date()));
    scheduledTasksService.copiaSeguridadMongo();
    }

    // Cada dia a la 00:30 AM
    @Scheduled(cron = "0 30 0 ? * *")
    public void comprobacionesDiarias0030AM() {
        scheduledTasksService.actualizarAjustes();
        scheduledTasksService.borrarTMP();
        logger.info("comprobacionesDiarias: Ejecucion de tareas programadas {}", dateFormat.format(new Date()));
    }
    
    // Cada 5 minutos
    @Scheduled(cron = "0 */5 * * * ?")
    public void cada5Minutos() {
    if (scheduledTasksService.enviarNotificaciones(5L, null)) {
        logger.info("cada5Minutos : enviarNotificaciones : Ejecucion de tareas programadas {}",
            dateFormat.format(new Date()));
    }
    }

    // TEST
    @Scheduled(fixedRate = 1000000)
    public void testSched() throws FindFailed, IOException, InterruptedException {
        // scheduledTasksService.procesarArchivoDocumento();
    }

}