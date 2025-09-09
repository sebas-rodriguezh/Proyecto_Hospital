package org.example.proyectohospital.Logica;

import java.nio.file.Paths;

public class Hospital {
    private static Hospital instance;
    private GestorMedicamentos gestorMedicamentos;
    private GestorPacientes gestorPacientes;
    private GestorPersonal gestorPersonal;
    private GestorRecetas gestorRecetas;

    private static final String RUTA_BASE = Paths.get(System.getProperty("user.dir"), "bd").toString();
    private static final String RUTA_PACIENTES_DEFAULT = Paths.get(RUTA_BASE, "pacientes.xml").toString();
    private static final String RUTA_PERSONAL_DEFAULT = Paths.get(RUTA_BASE, "personal.xml").toString();
    private static final String RUTA_MEDICAMENTOS_DEFAULT = Paths.get(RUTA_BASE, "medicamentos.xml").toString();
    private static final String RUTA_RECETAS_DEFAULT = Paths.get(RUTA_BASE, "recetas.xml").toString();

    // Constructor privado con rutas personalizadas
    private Hospital(String rutaPacientes, String rutaPersonal, String rutaMedicamentos, String rutaRecetas) {
        this.gestorPacientes = new GestorPacientes(rutaPacientes);
        this.gestorPersonal = new GestorPersonal(rutaPersonal);
        this.gestorMedicamentos = new GestorMedicamentos(rutaMedicamentos);
        this.gestorRecetas = new GestorRecetas(rutaRecetas);

        System.out.println("Hospital inicializado con rutas personalizadas");
    }

    // Constructor privado con gestores ya instanciados
    private Hospital(GestorPersonal personal, GestorPacientes pacientes,
                     GestorMedicamentos medicamentos, GestorRecetas recetas) {
        this.gestorPersonal = personal;
        this.gestorPacientes = pacientes;
        this.gestorMedicamentos = medicamentos;
        this.gestorRecetas = recetas;

        System.out.println("Hospital inicializado con gestores existentes");
    }

    // Constructor privado por defecto
//    private Hospital() {
//        this(RUTA_PACIENTES_DEFAULT, RUTA_PERSONAL_DEFAULT,
//                RUTA_MEDICAMENTOS_DEFAULT, RUTA_RECETAS_DEFAULT);
//
//    }

    private Hospital () {
        this.gestorPacientes = new GestorPacientes(RUTA_PACIENTES_DEFAULT);
        this.gestorPersonal = new GestorPersonal(RUTA_PERSONAL_DEFAULT);
        this.gestorMedicamentos = new GestorMedicamentos(RUTA_MEDICAMENTOS_DEFAULT);
        this.gestorRecetas = new GestorRecetas(RUTA_RECETAS_DEFAULT);
    }

    // Método para obtener la instancia (con inicialización automática)
    public static Hospital getInstance() {
        if (instance == null) {
            instance = new Hospital();
        }
        return instance;
    }

    // Método para inicializar con rutas personalizadas (opcional)
    public static void initialize(String rutaPacientes, String rutaPersonal,
                                  String rutaMedicamentos, String rutaRecetas) {
        if (instance == null) {
            instance = new Hospital(rutaPacientes, rutaPersonal, rutaMedicamentos, rutaRecetas);
        } else {
            System.out.println("Hospital ya está inicializado. No se puede reinicializar.");
        }
    }

    // Método para inicializar con gestores existentes (opcional)
    public static void initialize(GestorPersonal personal, GestorPacientes pacientes,
                                  GestorMedicamentos medicamentos, GestorRecetas recetas) {
        if (instance == null) {
            instance = new Hospital(personal, pacientes, medicamentos, recetas);
        } else {
            System.out.println("Hospital ya está inicializado. No se puede reinicializar.");
        }
    }

    // ======== GETTERS CON NOMBRES QUE COINCIDEN CON TU NECESIDAD ========

    // Método que buscas para usar en controladores
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

    // ======== GETTERS COMPATIBLES CON CÓDIGO EXISTENTE ========

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

    // ======== SETTERS (para casos especiales) ========

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

    // Setters compatibles con código existente
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

    // ======== MÉTODOS UTILITARIOS ========

    public static void reset() {
        instance = null;
        System.out.println("Hospital reseteado - se creará nueva instancia en próximo getInstance()");
    }

    // Verificar si el hospital está inicializado
    public static boolean isInitialized() {
        return instance != null;
    }

    // Información del estado del hospital
    public void mostrarEstado() {
        System.out.println("=== ESTADO DEL HOSPITAL ===");
        System.out.println("Gestor de Pacientes: " + (gestorPacientes != null ? "✓ Inicializado" : "✗ No inicializado"));
        System.out.println("Gestor de Personal: " + (gestorPersonal != null ? "✓ Inicializado" : "✗ No inicializado"));
        System.out.println("Gestor de Medicamentos: " + (gestorMedicamentos != null ? "✓ Inicializado" : "✗ No inicializado"));
        System.out.println("Gestor de Recetas: " + (gestorRecetas != null ? "✓ Inicializado" : "✗ No inicializado"));
    }

    // ======== GETTERS DE RUTAS (para información) ========

    public static String getRutaPacientesDefault() { return RUTA_PACIENTES_DEFAULT; }
    public static String getRutaPersonalDefault() { return RUTA_PERSONAL_DEFAULT; }
    public static String getRutaMedicamentosDefault() { return RUTA_MEDICAMENTOS_DEFAULT; }
    public static String getRutaRecetasDefault() { return RUTA_RECETAS_DEFAULT; }
}