package org.example.frontend.Servicios;

import org.example.proyectohospital.shared.SolicitudBackend;
import org.example.proyectohospital.shared.RespuestaBackend;
import java.io.*;
import java.net.Socket;

public class HospitalServiceProxy {
    //Profe, el uso de servicios es fundamental en esta arquitectura, y sobre todo para frontend.
    //Ya que al ser proyectos aislados la forma correcta de "comunicarse" con el backend es por medio del proxy y solicitudes al backend.
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public boolean conectar() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
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
            output.writeObject(solicitud);
            output.flush();
            return input.readObject();
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