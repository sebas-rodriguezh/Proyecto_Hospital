package org.example.frontend.Servicios;

import org.example.proyectohospital.shared.SolicitudBackend;
import org.example.proyectohospital.shared.RespuestaBackend;
import java.io.*;
import java.net.Socket;

public class HospitalServiceProxy {
    //Profe, el uso de servicios es fundamental en esta arquitectura, y sobre todo para frontend.
    //Ya que al ser proyectos aislados la forma correcta de "comunicarse" con el backend es por medio del proxy y solicitudes al backend.

    private static final String SERVER_HOST = "localhost"; // IP del backend
    private static final int SERVER_PORT = 8080; // Puerto del backend

    private Socket socket; // Socket TCP real
    private ObjectOutputStream output; // Para enviar objetos
    private ObjectInputStream input;  // Para recibir objetos

    public boolean conectar() {
        try {
            // === CREACIÓN DEL SOCKET TCP ===
            // Esto es UNA CONEXIÓN REAL DE RED
            // - El sistema operativo asigna un puerto local aleatorio (ej: 54321)
            // - Se conecta a localhost:8080
            socket = new Socket(SERVER_HOST, SERVER_PORT);

            // === PREPARAR CANALES DE COMUNICACIÓN ===
            // outputStream: para ESCRIBIR bytes al backend
            // inputStream: para LEER bytes del backend
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            return true;
        } catch (IOException e) {
            System.err.println("No se pudo conectar al backend: " + e.getMessage());
            return false;
        }
    }

    public Object enviarSolicitud(SolicitudBackend solicitud) {
        try {
            // === SERIALIZACIÓN Y ENVÍO ===
            // 1. El objeto SolicitudBackend se convierte a bytes
            // 2. Los bytes viajan por la red TCP
            // 3. El backend los recibe y reconstruye el objeto
            output.writeObject(solicitud); //Envío
            output.flush(); // Forzar envío inmediato


            // === ESPERA Y RECEPCIÓN ===
            // Este método se BLOQUEA hasta que llegue la respuesta
            // El backend procesa y envía la respuesta
            return input.readObject(); //Recepción
        } catch (Exception e) {
            System.err.println("Error enviando solicitud: " + e.getMessage());
            return new RespuestaBackend(false, "Error de comunicación: " + e.getMessage());
        }
    }

    public void desconectar() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error desconectando: " + e.getMessage());
        }
    }
}