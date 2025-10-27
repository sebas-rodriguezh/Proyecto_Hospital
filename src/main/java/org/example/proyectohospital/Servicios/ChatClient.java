package org.example.proyectohospital.Servicios;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String usuario;
    private Consumer<String> messageHandler;

    public void conectar(String host, int port, String usuario, Consumer<String> onMessage) throws IOException {
        this.usuario = usuario;
        this.messageHandler = onMessage;

        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        new Thread(this::listenForMessages).start();
    }

    private void listenForMessages() {
        try {
            out.println(usuario);

            String message;
            while ((message = in.readLine()) != null) {
                if (messageHandler != null) {
                    messageHandler.accept(message);
                }
            }
        } catch (IOException e) {
            if (messageHandler != null) {
                messageHandler.accept("[SISTEMA] Desconectado del servidor");
            }
        }
    }

    public void enviarMensaje(String mensaje) {
        if (out != null) {
            out.println(mensaje);
        }
    }

    public void enviarMensajePrivado(String destinatario, String mensaje) {
        if (out != null) {
            out.println("/msg " + destinatario + " " + mensaje);
        }
    }

    public void solicitarUsuarios() {
        if (out != null) {
            out.println("/users");
        }
    }

    public void desconectar() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error al desconectar: " + e.getMessage());
        }
    }

    public String getUsuario() {
        return usuario;
    }
}