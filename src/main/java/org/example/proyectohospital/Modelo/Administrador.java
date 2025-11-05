package org.example.proyectohospital.Modelo;

import java.io.Serializable;

public class Administrador extends Personal implements Serializable {
    private static final long serialVersionUID = 1L;
    public Administrador(String nombre, String id, String clave) {
        super(nombre, id, clave);
    }

    public Administrador() {
    }


    @Override
    public String toString() {
        return "Administrador{" +
                "nombre='" + nombre + '\'' +
                ", id='" + id + '\'' +
                ", clave='" + clave + '\'' +
                '}';
    }

    @Override
    public String tipo() {
        return "Administrador";
    }
}
