package org.example.proyectohospital.Modelo;

public abstract class Personal {
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
        return "Personal{" +
                "nombre='" + nombre + '\'' +
                ", id='" + id + '\'' +
                ", clave='" + clave + '\'' +
                '}';
    }

    //M.V.P
    public abstract String tipo();
}
