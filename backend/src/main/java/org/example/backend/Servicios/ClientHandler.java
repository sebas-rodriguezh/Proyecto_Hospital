package org.example.backend.Servicios;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final HospitalServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String usuario = "anonimo";
    private static final Logger LOGGER = Logger.getLogger("ClientHandler");

    public ClientHandler(Socket socket, HospitalServer server) {
        this.socket = socket;
        this.server = server;
    }

    public String getUsuario() {
        return usuario;
    }

    public void send(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

            out.println("[SISTEMA] Bienvenido al chat del Hospital.");

            String userInput = in.readLine();
            if (userInput != null && !userInput.isBlank()) {
                usuario = userInput.trim();
                LOGGER.info("Usuario conectado al chat: " + usuario);
                server.broadcastMessage("[SISTEMA] " + usuario + " se unió al chat", this);
                updateUserList();
            }

            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("/msg ")) {
                    String[] parts = message.substring(5).split(" ", 2);
                    if (parts.length == 2) {
                        server.sendPrivateMessage(parts[0], parts[1], this);
                    }
                } else if (message.equals("/users")) {
                    updateUserList();
                } else {
                    server.broadcastMessage(usuario + ": " + message, this);
                }
            }

        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Error con cliente " + usuario + ": " + ex.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Error cerrando socket: " + ex.getMessage());
            }
            server.removeClient(this);
        }
    }

    private void updateUserList() {
        String userList = String.join(", ", server.getUsuariosConectados());
        send("[SISTEMA] Usuarios conectados: " + userList);
    }
}