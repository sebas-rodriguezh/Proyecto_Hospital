package org.example.proyectohospital.Modelo;
import java.io.Serializable;

public abstract class Personal implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String nombre;
    protected String id;
    protected String clave;

    public Personal(String nombre, String id, String clave) {
        this.nombre = nombre;
        this.id = id;
        this.clave = clave;
    }

    public Personal() {

    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Personal{" + '\n' +
                "nombre='" + nombre + '\'' + '\n' +
                ", id='" + id + '\'' + '\n' +
                ", clave='" + clave + '\'' + '\n' +
                '}';
    }

    //M.V.P
    public abstract String tipo();
}
