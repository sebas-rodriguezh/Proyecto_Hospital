package org.example.proyectohospital.Modelo;

import java.io.Serializable;

public class Farmaceuta extends Personal implements Serializable {
    private static final long serialVersionUID = 1L;

    public Farmaceuta(String nombre, String id, String clave) {
        super(nombre, id, clave);
    }

    public Farmaceuta() {
    }

    @Override
    public String tipo() {
        return "Farmaceuta";
    }

    @Override
    public String toString() {
        return "Farmaceuta{" +
                "nombre='" + nombre + '\'' +
                ", id='" + id + '\'' +
                ", clave='" + clave + '\'' +
                '}';
    }

}
