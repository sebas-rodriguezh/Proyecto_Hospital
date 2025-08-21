package org.example.proyectohospital.Modelo;
import java.util.List;
import java.util.ArrayList;

public class Hospital {
    private ListaMedicamentos medicamentos;
    private ListaPacientes pacientes;
    private ListaPersonal personal;
    private ListaRecetas recetas;

    public Hospital(ListaPersonal personal, ListaPacientes pacientes, ListaMedicamentos medicamentos, ListaRecetas recetas) {
        this.personal = personal;
        this.pacientes = pacientes;
        this.medicamentos = medicamentos;
        this.recetas = recetas;
    }

    public Hospital() {
        this.personal = new ListaPersonal();
        this.pacientes = new ListaPacientes();
        this.medicamentos = new ListaMedicamentos();
        this.recetas = new ListaRecetas();
    }

    public ListaPersonal getPersonal() {
        return personal;
    }

    public void setPersonal(ListaPersonal personal) {
        this.personal = personal;
    }

    public ListaRecetas getRecetas() {
        return recetas;
    }

    public void setRecetas(ListaRecetas recetas) {
        this.recetas = recetas;
    }

    public ListaPacientes getPacientes() {
        return pacientes;
    }

    public void setPacientes(ListaPacientes pacientes) {
        this.pacientes = pacientes;
    }

    public ListaMedicamentos getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(ListaMedicamentos medicamentos) {
        this.medicamentos = medicamentos;
    }
}
