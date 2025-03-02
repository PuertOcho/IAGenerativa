package com.nuka.nuka_server.controllers;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Set;

public class ChatWebSocketHandler extends TextWebSocketHandler {

	ObjectMapper objectMapper = new ObjectMapper();
	
    // Lista de sesiones activas
    private final Set<WebSocketSession> sessions = new HashSet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Almacenar la nueva sesión
        sessions.add(session);
        System.out.println("Nueva conexión: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Log del mensaje recibido
        String payload = message.getPayload();
        System.out.println("Mensaje recibido: " + payload);

        // Difundir el mensaje a todas las sesiones activas
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
            	String jsonPayload = objectMapper.writeValueAsString(payload); 			
                s.sendMessage(new TextMessage(jsonPayload));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        // Eliminar la sesión cerrada
        sessions.remove(session);
        System.out.println("Conexión cerrada: " + session.getId());
    }
}