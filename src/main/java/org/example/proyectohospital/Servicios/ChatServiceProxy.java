package org.example.proyectohospital.Servicios;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ChatServiceProxy {
    private ChatClient chatClient;
    private String usuario;
    private Consumer<String> onMessageReceived;
    private Set<String> usuariosConectados = new HashSet<>();

    public ChatServiceProxy(String usuario, Consumer<String> onMessageReceived) {
        this.usuario = usuario;
        this.onMessageReceived = onMessageReceived;
    }

    public boolean conectar(String host, int port) {
        try {
            chatClient = new ChatClient();
            chatClient.conectar(host, port, usuario, this::procesarMensaje);
            return true;
        } catch (Exception e) {
            onMessageReceived.accept("[ERROR] No se pudo conectar: " + e.getMessage());
            return false;
        }
    }

    private void procesarMensaje(String mensaje) {
        // Actualizar lista de usuarios si viene en mensaje del sistema
        if (mensaje.contains("Usuarios conectados:")) {
            actualizarUsuariosConectados(mensaje);
        }

        // Pasar mensaje al controller
        onMessageReceived.accept(mensaje);
    }

    private void actualizarUsuariosConectados(String mensajeSistema) {
        try {
            String parteUsuarios = mensajeSistema.split("Usuarios conectados: ")[1];
            usuariosConectados.clear();

            for (String usuario : parteUsuarios.split(", ")) {
                if (!usuario.equals(this.usuario)) {
                    usuariosConectados.add(usuario.trim());
                }
            }
        } catch (Exception e) {
            // Ignorar errores de parsing
        }
    }

    public void enviarMensaje(String mensaje) {
        if (chatClient != null) {
            chatClient.enviarMensaje(mensaje);
        }
    }

    public void enviarMensajePrivado(String destinatario, String mensaje) {
        if (chatClient != null) {
            chatClient.enviarMensajePrivado(destinatario, mensaje);
        }
    }

    public void solicitarUsuariosConectados() {
        if (chatClient != null) {
            chatClient.solicitarUsuarios();
        }
    }

    public Set<String> getUsuariosConectados() {
        return new HashSet<>(usuariosConectados);
    }

    public void desconectar() {
        if (chatClient != null) {
            chatClient.desconectar();
        }
    }

    public String getUsuario() {
        return usuario;
    }
}