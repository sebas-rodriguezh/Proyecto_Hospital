package org.example.proyectohospital.Modelo;
import java.time.LocalDate;
import java.time.Period;


public class Paciente {
    private String id;
    private String nombre;
    private LocalDate fechaNacimiento;
    private int telefono;

    public Paciente(int telefono, LocalDate fechaNacimiento, String nombre, String id) {
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.nombre = nombre;
        this.id = id;
    }

    public Paciente() {

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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return "Paciente{" + '\n' +
                "id='" + id + '\'' + '\n' +
                ", nombre='" + nombre + '\'' + '\n' +
                ", fechaNacimiento=" + fechaNacimiento + '\n' +
                ", telefono=" + telefono + '\n' +
                '}';
    }
}
