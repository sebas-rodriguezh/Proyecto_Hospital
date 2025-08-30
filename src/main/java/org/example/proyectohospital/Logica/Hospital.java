package org.example.proyectohospital.Logica;

public class Hospital {
    private static Hospital instance;
    private GestorMedicamentos medicamentos;
    private GestorPacientes pacientes;
    private GestorPersonal personal;
    private GestorRecetas recetas;

    //Singleton.
    private Hospital(GestorPersonal personal, GestorPacientes pacientes,
                     GestorMedicamentos medicamentos, GestorRecetas recetas) {
        this.personal = personal;
        this.pacientes = pacientes;
        this.medicamentos = medicamentos;
        this.recetas = recetas;
    }

    private Hospital() {
        this.personal = new GestorPersonal();
        this.pacientes = new GestorPacientes();
        this.medicamentos = new GestorMedicamentos();
        this.recetas = new GestorRecetas();
    }

    public static Hospital getInstance() {
        if (instance == null) {
            instance = new Hospital();
        }
        return instance;
    }

    //Metodo Opcional: Inicializar con datos específicos
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
}
