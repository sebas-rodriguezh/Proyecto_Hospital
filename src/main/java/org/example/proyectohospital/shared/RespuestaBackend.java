package org.example.proyectohospital.shared;

import java.io.Serializable;

public class RespuestaBackend implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean exito;
    private String mensaje;
    private Object datos;

    public RespuestaBackend(boolean exito, String mensaje) {
        this.exito = exito;
        this.mensaje = mensaje;
    }

    public RespuestaBackend(boolean exito, String mensaje, Object datos) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.datos = datos;
    }

    public boolean isExito() { return exito; }
    public String getMensaje() { return mensaje; }
    public Object getDatos() { return datos; }
}