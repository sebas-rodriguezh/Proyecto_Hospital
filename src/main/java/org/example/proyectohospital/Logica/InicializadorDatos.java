package org.example.proyectohospital.Logica;

import org.example.proyectohospital.Logica.Hospital;
import org.example.proyectohospital.Modelo.*;
import java.time.LocalDate;

public class InicializadorDatos {

    public static void inicializarSiEsNecesario() {
        Hospital hospital = Hospital.getInstance();

        // Verificar si ya hay datos cargados
        if (!hospital.getGestorPersonal().getPersonal().isEmpty()) {
            System.out.println("Ya existen datos en el sistema. No se cargan datos iniciales.");
            return;
        }

        System.out.println("No se encontraron datos. Cargando datos iniciales...");
        cargarDatosIniciales();
    }

    public static void cargarDatosIniciales() {
        try {
            Hospital hospital = Hospital.getInstance();

            System.out.println("Cargando personal inicial...");

            // ===== CREAR PERSONAL INICIAL =====

            // Administrador
            Administrador admin = new Administrador("Administrador Principal", "admin", "1234");
            boolean adminCreado = hospital.getGestorPersonal().insertarPersonal(admin, false);
            System.out.println("Administrador creado: " + adminCreado);

            // Médicos
            Medico medico1 = new Medico("Dr. Juan Pérez", "12345", "1234", "Cardiología");
            Medico medico2 = new Medico("Dra. María González", "67890", "1234", "Pediatría");
            boolean medico1Creado = hospital.getGestorPersonal().insertarPersonal(medico1, false);
            boolean medico2Creado = hospital.getGestorPersonal().insertarPersonal(medico2, false);
            System.out.println("Médico 1 creado: " + medico1Creado);
            System.out.println("Médico 2 creado: " + medico2Creado);

            // Farmaceutas
            Farmaceuta farmaceuta1 = new Farmaceuta("Carlos Rodríguez", "11111", "1234");
            Farmaceuta farmaceuta2 = new Farmaceuta("Ana López", "22222", "1234");
            boolean farm1Creado = hospital.getGestorPersonal().insertarPersonal(farmaceuta1, false);
            boolean farm2Creado = hospital.getGestorPersonal().insertarPersonal(farmaceuta2, false);
            System.out.println("Farmaceuta 1 creado: " + farm1Creado);
            System.out.println("Farmaceuta 2 creado: " + farm2Creado);

            System.out.println("Cargando medicamentos iniciales...");

            // ===== CREAR MEDICAMENTOS INICIALES =====

            Medicamento med1 = new Medicamento("Paracetamol", "Tableta 500mg", "PAR001");
            Medicamento med2 = new Medicamento("Ibuprofeno", "Tableta 400mg", "IBU002");
            Medicamento med3 = new Medicamento("Amoxicilina", "Cápsula 250mg", "AMO003");
            Medicamento med4 = new Medicamento("Aspirina", "Tableta 100mg", "ASP004");
            Medicamento med5 = new Medicamento("Loratadina", "Tableta 10mg", "LOR005");

            hospital.getGestorMedicamentos().insertarMedicamento(med1);
            hospital.getGestorMedicamentos().insertarMedicamento(med2);
            hospital.getGestorMedicamentos().insertarMedicamento(med3);
            hospital.getGestorMedicamentos().insertarMedicamento(med4);
            hospital.getGestorMedicamentos().insertarMedicamento(med5);

            System.out.println("Cargando pacientes iniciales...");

            // ===== CREAR PACIENTES INICIALES =====

            Paciente paciente1 = new Paciente(12345678, LocalDate.of(1990, 5, 15), "Pedro Martínez", "P001");
            Paciente paciente2 = new Paciente(87654321, LocalDate.of(1985, 10, 20), "Laura Sánchez", "P002");
            Paciente paciente3 = new Paciente(11223344, LocalDate.of(1995, 3, 10), "Carlos López", "P003");

            hospital.getGestorPacientes().insertarPaciente(paciente1, false);
            hospital.getGestorPacientes().insertarPaciente(paciente2, false);
            hospital.getGestorPacientes().insertarPaciente(paciente3, false);

            // ===== MOSTRAR RESUMEN =====

            System.out.println("\n=== DATOS INICIALES CARGADOS ===");
            System.out.println("Personal: " + hospital.getGestorPersonal().getPersonal().size() + " registros");
            System.out.println("Medicamentos: " + hospital.getGestorMedicamentos().getMedicamentos().size() + " registros");
            System.out.println("Pacientes: " + hospital.getGestorPacientes().getPacientes().size() + " registros");

            System.out.println("\n=== CREDENCIALES DE ACCESO ===");
            System.out.println("👤 Administrador:");
            System.out.println("   Usuario: admin | Clave: 1234");
            System.out.println("👨‍⚕️ Médicos:");
            System.out.println("   Usuario: 12345 | Clave: 1234 (Dr. Juan Pérez - Cardiología)");
            System.out.println("   Usuario: 67890 | Clave: 1234 (Dra. María González - Pediatría)");
            System.out.println("💊 Farmaceutas:");
            System.out.println("   Usuario: 11111 | Clave: 1234 (Carlos Rodríguez)");
            System.out.println("   Usuario: 22222 | Clave: 1234 (Ana López)");

            // Verificar que los datos se guardaron correctamente
            Personal testAdmin = hospital.getGestorPersonal().getPersonalPorID("admin");
            System.out.println("\n=== VERIFICACIÓN ===");
            System.out.println("Admin encontrado: " + (testAdmin != null ? testAdmin.getNombre() : "NO ENCONTRADO"));

        } catch (Exception e) {
            System.err.println("Error al cargar datos iniciales: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void mostrarPersonalExistente() {
        try {
            Hospital hospital = Hospital.getInstance();
            System.out.println("\n=== PERSONAL EXISTENTE EN EL SISTEMA ===");

            var personal = hospital.getGestorPersonal().getPersonal();
            if (personal.isEmpty()) {
                System.out.println("No hay personal registrado.");
            } else {
                for (Personal p : personal) {
                    System.out.println("- ID: " + p.getId() + " | Nombre: " + p.getNombre() + " | Tipo: " + p.tipo());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al mostrar personal: " + e.getMessage());
        }
    }
}