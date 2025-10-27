package org.example.proyectohospital.Servicios;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.*;

public class ChatServer {
    private final int port;
    private final Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>());
    private static final Logger LOGGER = Logger.getLogger(ChatServer.class.getName());

    public ChatServer(int port) {
        this.port = port;
        configureLogger();
    }

    private void configureLogger() {
        try {
            LOGGER.setUseParentHandlers(false);
            var fileHandler = new FileHandler("hospital-chat-server.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.INFO);
        } catch (IOException ex) {
            System.err.println("No se pudo generar la bitácora del servidor.");
        }
    }

    public void start() {
        LOGGER.info("=== INICIANDO SERVIDOR DE CHAT HOSPITALARIO ===");
        LOGGER.info("Puerto: " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("Servidor listo para aceptar conexiones...");

            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, this);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error en servidor: " + ex.getMessage());
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastSystemMessage(clientHandler.getUsuario() + " salió del chat");
        LOGGER.info("Cliente desconectado: " + clientHandler.getUsuario());
    }

    public void broadcastMessage(String message, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.send(message);
            }
        }
    }

    public void sendPrivateMessage(String destinatario, String mensaje, ClientHandler remitente) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client.getUsuario().equals(destinatario)) {
                    client.send("[PRIVADO de " + remitente.getUsuario() + "]: " + mensaje);
                    return;
                }
            }
            remitente.send("[SISTEMA] Usuario " + destinatario + " no está conectado");
        }
    }

    private void broadcastSystemMessage(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.send("[SISTEMA] " + message);
            }
        }
        updateUserListForAll();
    }

    private void updateUserListForAll() {
        String userList = getUserListString();
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.send("[SISTEMA] Usuarios conectados: " + userList);
            }
        }
    }

    private String getUserListString() {
        List<String> usuarios = new ArrayList<>();
        synchronized (clients) {
            for (ClientHandler client : clients) {
                usuarios.add(client.getUsuario());
            }
        }
        return String.join(", ", usuarios);
    }

    public Set<String> getUsuariosConectados() {
        Set<String> usuarios = new HashSet<>();
        synchronized (clients) {
            for (ClientHandler client : clients) {
                usuarios.add(client.getUsuario());
            }
        }
        return usuarios;
    }

    public static void main(String[] args) {
        new ChatServer(7000).start();
    }
}