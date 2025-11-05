package org.example.proyectohospital.Modelo;

import java.io.Serializable;

public class Medico extends Personal implements Serializable {
    private static final long serialVersionUID = 1L;

    private String especialidad;

    public Medico(String nombre, String id, String clave, String especialidad) {
        super(nombre, id, clave);
        this.especialidad = especialidad;
    }

    public Medico(String especialidad) {
        this.especialidad = especialidad;
    }

    public Medico() {
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public String tipo() {
        return "Medico";
    }

    @Override
    public String toString() {
        return "Medico{" +
                "nombre='" + nombre + '\'' +
                ", especialidad='" + especialidad + '\'' +
                ", id='" + id + '\'' +
                ", clave='" + clave + '\'' +
                '}';
    }
}
