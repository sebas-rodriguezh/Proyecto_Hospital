package org.example.proyectohospital.Logica;

import java.nio.file.Paths;

public class Hospital {
    private static Hospital instance;
    private GestorMedicamentos medicamentos;
    private GestorPacientes pacientes;
    private GestorPersonal personal;
    private GestorRecetas recetas;

    private static final String RUTA_BASE = Paths.get(System.getProperty("user.dir"), "bd").toString();
    private static final String RUTA_PACIENTES_DEFAULT = Paths.get(RUTA_BASE, "pacientes.xml").toString();
    private static final String RUTA_PERSONAL_DEFAULT = Paths.get(RUTA_BASE, "personal.xml").toString();
    private static final String RUTA_MEDICAMENTOS_DEFAULT = Paths.get(RUTA_BASE, "medicamentos.xml").toString();
    private static final String RUTA_RECETAS_DEFAULT = Paths.get(RUTA_BASE, "recetas.xml").toString();

    private Hospital(String rutaPacientes, String rutaPersonal, String rutaMedicamentos, String rutaRecetas) {
        this.pacientes = new GestorPacientes(rutaPacientes);
        this.personal = new GestorPersonal(rutaPersonal);
        this.medicamentos = new GestorMedicamentos(rutaMedicamentos);
        this.recetas = new GestorRecetas(rutaRecetas);
    }

    private Hospital(GestorPersonal personal, GestorPacientes pacientes,
                     GestorMedicamentos medicamentos, GestorRecetas recetas) {
        this.personal = personal;
        this.pacientes = pacientes;
        this.medicamentos = medicamentos;
        this.recetas = recetas;
    }

    private Hospital() {
        this(RUTA_PACIENTES_DEFAULT, RUTA_PERSONAL_DEFAULT,
                RUTA_MEDICAMENTOS_DEFAULT, RUTA_RECETAS_DEFAULT);
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
        }
    }

    public static void initialize(GestorPersonal personal, GestorPacientes pacientes,
                                  GestorMedicamentos medicamentos, GestorRecetas recetas) {
        if (instance == null) {
            instance = new Hospital(personal, pacientes, medicamentos, recetas);
        }
    }

    public GestorPersonal getPersonal() {
        return personal;
    }

    public void setPersonal(GestorPersonal personal) {
        this.personal = personal;
    }

    public GestorRecetas getRecetas() {
        return recetas;
    }

    public void setRecetas(GestorRecetas recetas) {
        this.recetas = recetas;
    }

    public GestorPacientes getPacientes() {
        return pacientes;
    }

    public void setPacientes(GestorPacientes pacientes) {
        this.pacientes = pacientes;
    }

    public GestorMedicamentos getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(GestorMedicamentos medicamentos) {
        this.medicamentos = medicamentos;
    }

    public static void reset() {
        instance = null;
    }

    public static String getRutaPacientesDefault() { return RUTA_PACIENTES_DEFAULT; }
    public static String getRutaPersonalDefault() { return RUTA_PERSONAL_DEFAULT; }
    public static String getRutaMedicamentosDefault() { return RUTA_MEDICAMENTOS_DEFAULT; }
    public static String getRutaRecetasDefault() { return RUTA_RECETAS_DEFAULT; }
}