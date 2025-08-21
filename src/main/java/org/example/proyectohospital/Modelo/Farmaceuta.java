package org.example.proyectohospital.Modelo;

public class Farmaceuta extends Personal {
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
