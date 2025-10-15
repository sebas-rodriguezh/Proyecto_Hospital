package org.example.proyectohospital.Logica;

import org.example.proyectohospital.Modelo.Medico;

public class Hospital {
    private static Hospital instance;
    private GestorMedicamentos gestorMedicamentos;
    private GestorPacientes gestorPacientes;
    private GestorPersonal gestorPersonal;
    private GestorRecetas gestorRecetas;
    private Medico medicoLogueado;

    private Hospital() {
        this.gestorPacientes = new GestorPacientes();
        this.gestorPersonal = new GestorPersonal();
        this.gestorMedicamentos = new GestorMedicamentos();
        this.gestorRecetas = new GestorRecetas();

        System.out.println("Hospital inicializado con base de datos SQL");
    }

    public static Hospital getInstance() {
        if (instance == null) {
            instance = new Hospital();
        }
        return instance;
    }

    // Métodos de acceso (se mantienen igual)
    public GestorPacientes getGestorPacientes() {
        return gestorPacientes;
    }

    public GestorPersonal getGestorPersonal() {
        return gestorPersonal;
    }

    public GestorMedicamentos getGestorMedicamentos() {
        return gestorMedicamentos;
    }

    public GestorRecetas getGestorRecetas() {
        return gestorRecetas;
    }

    public GestorPersonal getPersonal() {
        return gestorPersonal;
    }

    public GestorRecetas getRecetas() {
        return gestorRecetas;
    }

    public GestorPacientes getPacientes() {
        return gestorPacientes;
    }

    public GestorMedicamentos getMedicamentos() {
        return gestorMedicamentos;
    }

    // Setters (se mantienen igual)
    public void setGestorPersonal(GestorPersonal gestorPersonal) {
        this.gestorPersonal = gestorPersonal;
    }

    public void setGestorRecetas(GestorRecetas gestorRecetas) {
        this.gestorRecetas = gestorRecetas;
    }

    public void setGestorPacientes(GestorPacientes gestorPacientes) {
        this.gestorPacientes = gestorPacientes;
    }

    public void setGestorMedicamentos(GestorMedicamentos gestorMedicamentos) {
        this.gestorMedicamentos = gestorMedicamentos;
    }

    public void setPersonal(GestorPersonal personal) {
        this.gestorPersonal = personal;
    }

    public void setRecetas(GestorRecetas recetas) {
        this.gestorRecetas = recetas;
    }

    public void setPacientes(GestorPacientes pacientes) {
        this.gestorPacientes = pacientes;
    }

    public void setMedicamentos(GestorMedicamentos medicamentos) {
        this.gestorMedicamentos = medicamentos;
    }

    public void setMedicoLogueado(Medico medico) {
        this.medicoLogueado = medico;
        System.out.println("Médico establecido en Hospital: " +
                (medico != null ? medico.getNombre() : "null"));
    }

    public Medico getMedicoLogueado() {
        return medicoLogueado;
    }

}