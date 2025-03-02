package com.nuka.nuka_server.models;

 
 
 
 




public class EventoDeportivo {

    Integer minuto;
    TipoEvento evento;
    String jugador;
    LocalOVisitante equipo;
    
    public enum LocalOVisitante {
	LOCAL, VISITANTE
    }

    public enum TipoEvento {
	TARJETA_AMARILLA, TARJETA_ROJA,
	GOL, GOL_ANULADO, GOL_PENALTI, GOL_PROPIA,
	SUSTITUCION_IN, SUSTITUCION_OUT,
	INTENTOS_TOTALES, INTENTOS_A_PUERTA, INTENTOS_FUERA, 
	CORNERS, POSESION, FALTAS_COMETIDAS, FUERA_DE_JUEGO, GOLES_SALVADOS
    }
}

