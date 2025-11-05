package org.example.proyectohospital.Modelo;

import java.io.Serializable;

public class Medicamento implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String codigo;
    private String presentacion;

    public Medicamento(String nombre, String presentacion, String codigo) {
        this.nombre = nombre;
        this.presentacion = presentacion;
        this.codigo = codigo;
    }

    public Medicamento() {

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    @Override
    public String toString() {
        return "Medicamento{" +
                "nombre='" + nombre + '\'' +
                ", codigo='" + codigo + '\'' +
                ", presentacion='" + presentacion + '\'' +
                '}';
    }

}

