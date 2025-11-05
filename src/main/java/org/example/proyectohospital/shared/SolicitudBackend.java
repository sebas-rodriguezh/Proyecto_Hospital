package org.example.proyectohospital.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SolicitudBackend implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tipo;
    private Map<String, Object> parametros;

    public SolicitudBackend(String tipo) {
        this.tipo = tipo;
        this.parametros = new HashMap<>();
    }

    public String getTipo() { return tipo; }
    public Map<String, Object> getParametros() { return parametros; }

    public void agregarParametro(String clave, Object valor) {
        parametros.put(clave, valor);
    }
}