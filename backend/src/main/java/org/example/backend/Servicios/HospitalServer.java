package org.example.backend.Servicios;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

public class HospitalServer {
    private static final int PORT_BACKEND = 8080;
    private static final int PORT_CHAT = 7000;
    private static final int MAX_THREADS = 10;
    private final ExecutorService threadPool;
    private final Set<ClientHandler> chatClients = Collections.synchronizedSet(new HashSet<>());
    private static final Logger LOGGER = Logger.getLogger(HospitalServer.class.getName());

    public HospitalServer() {
        this.threadPool = Executors.newFixedThreadPool(MAX_THREADS);
        configureLogger();
    }

    private void configureLogger() {
        try {
            LOGGER.setUseParentHandlers(false);
            var fileHandler = new FileHandler("hospital-server.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.INFO);
        } catch (IOException ex) {
            System.err.println("No se pudo generar la bitácora del servidor.");
        }
    }

    public void start() {
        //Profe, este server es para CRUD y todas las solicitudes del frontend no relacionadas con mensajes.
        new Thread(this::startBackendServer, "Backend-Server").start();

        //Mensajes
        new Thread(this::startChatServer, "Chat-Server").start();

        System.out.println("Servidor Hospitalario iniciado");
        System.out.println("    Backend en puerto: " + PORT_BACKEND);
        System.out.println("    Chat en puerto: " + PORT_CHAT);
    }

    private void startBackendServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT_BACKEND)) {
            LOGGER.info("Backend Hospitalario iniciado en puerto " + PORT_BACKEND);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                LOGGER.info("Nueva conexión backend desde: " + clientSocket.getInetAddress());

                BackendServiceHandler handler = new BackendServiceHandler(clientSocket);
                threadPool.execute(handler);
            }
        } catch (Exception e) {
            LOGGER.severe("Error en servidor backend: " + e.getMessage());
        }
    }

    private void startChatServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT_CHAT)) {
            LOGGER.info("Servidor de Chat iniciado en puerto " + PORT_CHAT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                LOGGER.info("Nueva conexión chat desde: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                chatClients.add(clientHandler);
                clientHandler.start();
            }
        } catch (Exception e) {
            LOGGER.severe("Error en servidor chat: " + e.getMessage());
        }
    }

    public void broadcastMessage(String message, ClientHandler sender) {
        synchronized (chatClients) {
            for (ClientHandler client : chatClients) {
                client.send(message);
            }
        }
    }

    public void sendPrivateMessage(String destinatario, String mensaje, ClientHandler remitente) {
        synchronized (chatClients) {
            for (ClientHandler client : chatClients) {
                if (client.getUsuario().equals(destinatario)) {
                    client.send("[PRIVADO de " + remitente.getUsuario() + "]: " + mensaje);
                    return;
                }
            }
            remitente.send("[SISTEMA] Usuario " + destinatario + " no está conectado");
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        chatClients.remove(clientHandler);
        broadcastSystemMessage(clientHandler.getUsuario() + " salió del chat");
        LOGGER.info("Cliente desconectado del chat: " + clientHandler.getUsuario());
    }

    private void broadcastSystemMessage(String message) {
        synchronized (chatClients) {
            for (ClientHandler client : chatClients) {
                client.send("[SISTEMA] " + message);
            }
        }
        updateUserListForAll();
    }

    private void updateUserListForAll() {
        String userList = getUserListString();
        synchronized (chatClients) {
            for (ClientHandler client : chatClients) {
                client.send("[SISTEMA] Usuarios conectados: " + userList);
            }
        }
    }

    private String getUserListString() {
        StringBuilder usuarios = new StringBuilder();
        synchronized (chatClients) {
            for (ClientHandler client : chatClients) {
                if (usuarios.length() > 0) {
                    usuarios.append(", ");
                }
                usuarios.append(client.getUsuario());
            }
        }
        return usuarios.toString();
    }

    public Set<String> getUsuariosConectados() {
        Set<String> usuarios = new HashSet<>();
        synchronized (chatClients) {
            for (ClientHandler client : chatClients) {
                usuarios.add(client.getUsuario());
            }
        }
        return usuarios;
    }

    public static void main(String[] args) {
        new HospitalServer().start();
    }
}