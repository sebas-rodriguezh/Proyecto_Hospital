package org.example.proyectohospital.Logica;

import org.example.proyectohospital.Modelo.Medico;

import java.nio.file.Paths;

public class Hospital {
    private static Hospital instance;
    private GestorMedicamentos gestorMedicamentos;
    private GestorPacientes gestorPacientes;
    private GestorPersonal gestorPersonal;
    private GestorRecetas gestorRecetas;
    private Medico medicoLogueado;

    private static final String RUTA_BASE = Paths.get(System.getProperty("user.dir"), "bd").toString();
    private static final String RUTA_PACIENTES_DEFAULT = Paths.get(RUTA_BASE, "pacientes.xml").toString();
    private static final String RUTA_PERSONAL_DEFAULT = Paths.get(RUTA_BASE, "personal.xml").toString();
    private static final String RUTA_MEDICAMENTOS_DEFAULT = Paths.get(RUTA_BASE, "medicamentos.xml").toString();
    private static final String RUTA_RECETAS_DEFAULT = Paths.get(RUTA_BASE, "recetas.xml").toString();

    private Hospital(String rutaPacientes, String rutaPersonal, String rutaMedicamentos, String rutaRecetas) {
        this.gestorPacientes = new GestorPacientes(rutaPacientes);
        this.gestorPersonal = new GestorPersonal(rutaPersonal);
        this.gestorMedicamentos = new GestorMedicamentos(rutaMedicamentos);
        this.gestorRecetas = new GestorRecetas(rutaRecetas);

        System.out.println("Hospital inicializado con rutas personalizadas");
    }

    private Hospital(GestorPersonal personal, GestorPacientes pacientes,
                     GestorMedicamentos medicamentos, GestorRecetas recetas) {
        this.gestorPersonal = personal;
        this.gestorPacientes = pacientes;
        this.gestorMedicamentos = medicamentos;
        this.gestorRecetas = recetas;

        System.out.println("Hospital inicializado con gestores existentes");
    }

    private Hospital () {
        this.gestorPacientes = new GestorPacientes(RUTA_PACIENTES_DEFAULT);
        this.gestorPersonal = new GestorPersonal(RUTA_PERSONAL_DEFAULT);
        this.gestorMedicamentos = new GestorMedicamentos(RUTA_MEDICAMENTOS_DEFAULT);
        this.gestorRecetas = new GestorRecetas(RUTA_RECETAS_DEFAULT);
    }

    public static Hospital getInstance() {
        if (instance == null) {
            instance = new Hospital();
        }
        return instance;
    }

    public static void initialize(String rutaPacientes, String rutaPersonal,
                                  String rutaMedicamentos, String rutaRecetas) {
        if (instance == null) {
            instance = new Hospital(rutaPacientes, rutaPersonal, rutaMedicamentos, rutaRecetas);
        } else {
            System.out.println("Hospital ya está inicializado. No se puede reinicializar.");
        }
    }

    public static void initialize(GestorPersonal personal, GestorPacientes pacientes,
                                  GestorMedicamentos medicamentos, GestorRecetas recetas) {
        if (instance == null) {
            instance = new Hospital(personal, pacientes, medicamentos, recetas);
        } else {
            System.out.println("Hospital ya está inicializado. No se puede reinicializar.");
        }
    }

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


    public static String getRutaPacientesDefault() { return RUTA_PACIENTES_DEFAULT; }
    public static String getRutaPersonalDefault() { return RUTA_PERSONAL_DEFAULT; }
    public static String getRutaMedicamentosDefault() { return RUTA_MEDICAMENTOS_DEFAULT; }
    public static String getRutaRecetasDefault() { return RUTA_RECETAS_DEFAULT; }
}