package org.example.proyectohospital;

import org.example.proyectohospital.Datos.*;
import org.example.proyectohospital.Logica.*;
import org.example.proyectohospital.Modelo.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import java.time.LocalDate;

class Main {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBA DE CAPA DE DATOS ===\n");

        // === CREAR INSTANCIAS DE DATOS ===
        PacienteDatos pacienteDatos = new PacienteDatos("data/pacientes.xml");
        PersonalDatos personalDatos = new PersonalDatos("data/personal.xml");
        MedicamentoDatos medicamentoDatos = new MedicamentoDatos("data/medicamentos.xml");
        RecetaDatos recetaDatos = new RecetaDatos("data/recetas.xml");

        // === CREAR DATOS DE PRUEBA ===
        System.out.println("1. Creando datos de prueba...");

        // Pacientes
        Paciente paciente1 = new Paciente(70599270, LocalDate.of(2005,11,17), "Alejandro", "119510334");
        Paciente paciente2 = new Paciente(88276784, LocalDate.of(2006,1,15), "Isa", "7465443");
        Paciente paciente3 = new Paciente(70599270, LocalDate.of(2012,4,7), "Gabriel", "486543");

        // Personal
        Medico medico1 = new Medico("Sebas", "11951444", "132456", "Cirujano");
        Medico medico2 = new Medico("Amparo", "15454313", "22222", "Cardiología");
        Medico medico3 = new Medico("Fabiola", "151444", "33333", "Pediatría");
        Administrador admin1 = new Administrador("Roberto", "159951", "4444");
        Farmaceuta farmaceuta1 = new Farmaceuta("Sofia", "147258", "1236547899");

        // Medicamentos
        Medicamento medicamento1 = new Medicamento("Aspirina", "100mg", "MED001");
        Medicamento medicamento2 = new Medicamento("Gravol", "Frasco líquido", "MED002");
        Medicamento medicamento3 = new Medicamento("Ibuprofeno", "Cápsulas 400mg", "MED003");

        // === CONFIGURAR HOSPITAL CON GESTORES ===
        GestorPacientes gestorPacientes = new GestorPacientes();
        GestorPersonal gestorPersonal = new GestorPersonal();
        GestorMedicamentos gestorMedicamentos = new GestorMedicamentos();
        GestorRecetas gestorRecetas = new GestorRecetas();

        // Insertar datos en gestores
        gestorPacientes.insertarPaciente(paciente1, gestorPersonal.existePersonalConEseID(paciente1.getId()));
        gestorPacientes.insertarPaciente(paciente2, gestorPersonal.existePersonalConEseID(paciente2.getId()));
        gestorPacientes.insertarPaciente(paciente3, gestorPersonal.existePersonalConEseID(paciente3.getId()));

        gestorPersonal.insertarPersonal(medico1, gestorPacientes.existeAlguienConEseID(medico1.getId()));
        gestorPersonal.insertarPersonal(medico2, gestorPacientes.existeAlguienConEseID(medico2.getId()));
        gestorPersonal.insertarPersonal(medico3, gestorPacientes.existeAlguienConEseID(medico3.getId()));
        gestorPersonal.insertarPersonal(admin1, gestorPacientes.existeAlguienConEseID(admin1.getId()));
        gestorPersonal.insertarPersonal(farmaceuta1, gestorPacientes.existeAlguienConEseID(farmaceuta1.getId()));

        gestorMedicamentos.insertarMedicamento(medicamento1);
        gestorMedicamentos.insertarMedicamento(medicamento2);
        gestorMedicamentos.insertarMedicamento(medicamento3);

        // Configurar Hospital Singleton
        Hospital hospital = Hospital.getInstance();
        hospital.setPacientes(gestorPacientes);
        hospital.setPersonal(gestorPersonal);
        hospital.setMedicamentos(gestorMedicamentos);
        hospital.setRecetas(gestorRecetas);

        // Crear recetas con detalles
        Receta receta1 = new Receta("REC001", medico1, paciente1, LocalDate.now(), LocalDate.now().plusDays(7), 1);
        Receta receta2 = new Receta("REC002", medico2, paciente2, LocalDate.now(), LocalDate.now().plusDays(5), 2);
        Receta receta3 = new Receta("REC003", medico3, paciente3, LocalDate.now(), LocalDate.now().plusDays(10), 3);

        // Crear detalles de medicamentos
        DetalleMedicamento detalle1 = new DetalleMedicamento(medicamento1, "DET001", 30, 7, "Tomar 1 cada 8 horas");
        DetalleMedicamento detalle2 = new DetalleMedicamento(medicamento2, "DET002", 15, 5, "Tomar cuando hay mareos");
        DetalleMedicamento detalle3 = new DetalleMedicamento(medicamento3, "DET003", 20, 3, "Tomar cada 12 horas");

        // Agregar detalles a recetas
        receta1.getDetalleMedicamentos().add(detalle1);
        receta1.getDetalleMedicamentos().add(detalle2);
        receta2.getDetalleMedicamentos().add(detalle2);
        receta3.getDetalleMedicamentos().add(detalle3);

        gestorRecetas.insertarReceta(receta1);
        gestorRecetas.insertarReceta(receta2);
        gestorRecetas.insertarReceta(receta3);

        System.out.println("✓ Datos de prueba creados\n");

        // === GUARDAR EN XML ===
        System.out.println("2. Guardando datos en XML...");

        try {
            // Convertir y guardar pacientes
            PacienteConector pacienteConector = new PacienteConector();
            List<PacienteEntity> pacientesEntity = new ArrayList<>();
            for (Paciente p : gestorPacientes.getPacientes()) {
                pacientesEntity.add(PacienteMapper.toXML(p));
            }
            pacienteConector.setPacientes(pacientesEntity);
            pacienteDatos.save(pacienteConector);
            System.out.println("✓ Pacientes guardados en: " + pacienteDatos.getXmlPath());

            // Convertir y guardar personal
            PersonalConector personalConector = new PersonalConector();
            List<PersonalEntity> personalEntity = new ArrayList<>();
            for (Personal p : gestorPersonal.getPersonal()) {
                personalEntity.add(PersonalMapper.toXML(p));
            }
            personalConector.setPersonal(personalEntity);
            personalDatos.save(personalConector);
            System.out.println("✓ Personal guardado en: " + personalDatos.getXmlPath());

            // Convertir y guardar medicamentos
            MedicamentoConector medicamentoConector = new MedicamentoConector();
            List<MedicamentoEntity> medicamentosEntity = new ArrayList<>();
            for (Medicamento m : gestorMedicamentos.getMedicamentos()) {
                medicamentosEntity.add(MedicamentoMapper.toXML(m));
            }
            medicamentoConector.setMedicamentos(medicamentosEntity);
            medicamentoDatos.save(medicamentoConector);
            System.out.println("✓ Medicamentos guardados en: " + medicamentoDatos.getXmlPath());

            // Convertir y guardar recetas
            RecetaConector recetaConector = new RecetaConector();
            List<RecetaEntity> recetasEntity = new ArrayList<>();
            for (Receta r : gestorRecetas.getRecetas()) {
                recetasEntity.add(RecetaMapper.toXML(r));
            }
            recetaConector.setRecetas(recetasEntity);
            recetaDatos.save(recetaConector);
            System.out.println("✓ Recetas guardadas en: " + recetaDatos.getXmlPath());

        } catch (Exception e) {
            System.err.println("❌ Error guardando datos: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n3. Archivos XML creados exitosamente!\n");

        // === LIMPIAR HOSPITAL PARA PROBAR CARGA ===
        System.out.println("4. Limpiando datos en memoria para probar carga...");
        Hospital.getInstance().setPacientes(new GestorPacientes());
        Hospital.getInstance().setPersonal(new GestorPersonal());
        Hospital.getInstance().setMedicamentos(new GestorMedicamentos());
        Hospital.getInstance().setRecetas(new GestorRecetas());
        System.out.println("✓ Datos limpiados\n");

        // === CARGAR DESDE XML ===
        System.out.println("5. Cargando datos desde XML...");

        try {
            // Cargar pacientes
            PacienteConector pacientesCargados = pacienteDatos.load();
            GestorPacientes nuevoGestorPacientes = new GestorPacientes();
            for (PacienteEntity pe : pacientesCargados.getPacientes()) {
                nuevoGestorPacientes.insertarPaciente(PacienteMapper.toModel(pe), false);
            }
            Hospital.getInstance().setPacientes(nuevoGestorPacientes);
            System.out.println("✓ Pacientes cargados: " + nuevoGestorPacientes.getPacientes().size());

            // Cargar personal
            PersonalConector personalCargado = personalDatos.load();
            GestorPersonal nuevoGestorPersonal = new GestorPersonal();
            for (PersonalEntity pe : personalCargado.getPersonal()) {
                nuevoGestorPersonal.insertarPersonal(PersonalMapper.toModel(pe), false);
            }
            Hospital.getInstance().setPersonal(nuevoGestorPersonal);
            System.out.println("✓ Personal cargado: " + nuevoGestorPersonal.getPersonal().size());

            // Cargar medicamentos
            MedicamentoConector medicamentosCargados = medicamentoDatos.load();
            GestorMedicamentos nuevoGestorMedicamentos = new GestorMedicamentos();
            for (MedicamentoEntity me : medicamentosCargados.getMedicamentos()) {
                nuevoGestorMedicamentos.insertarMedicamento(MedicamentoMapper.toModel(me));
            }
            Hospital.getInstance().setMedicamentos(nuevoGestorMedicamentos);
            System.out.println("✓ Medicamentos cargados: " + nuevoGestorMedicamentos.getMedicamentos().size());

            // Cargar recetas (IMPORTANTE: cargar después de tener pacientes, personal y medicamentos)
            RecetaConector recetasCargadas = recetaDatos.load();
            GestorRecetas nuevoGestorRecetas = new GestorRecetas();
            for (RecetaEntity re : recetasCargadas.getRecetas()) {
                nuevoGestorRecetas.insertarReceta(RecetaMapper.toModel(re));
            }
            Hospital.getInstance().setRecetas(nuevoGestorRecetas);
            System.out.println("✓ Recetas cargadas: " + nuevoGestorRecetas.getRecetas().size());

        } catch (Exception e) {
            System.err.println("❌ Error cargando datos: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n6. Datos cargados exitosamente desde XML!\n");

        // === MOSTRAR RESULTADOS ===
        System.out.println("=== DATOS CARGADOS DESDE XML ===\n");

        System.out.println("PACIENTES:");
        System.out.println(Hospital.getInstance().getPacientes().mostrarTodosLosPacientes());
        System.out.println("\n" + "=".repeat(50) + "\n");

        System.out.println("PERSONAL:");
        System.out.println(Hospital.getInstance().getPersonal().mostrarTodoElPersonal());
        System.out.println("\n" + "=".repeat(50) + "\n");

        System.out.println("MEDICAMENTOS:");
        System.out.println(Hospital.getInstance().getMedicamentos().mostrarTodosLosMedicamentos());
        System.out.println("\n" + "=".repeat(50) + "\n");

        System.out.println("RECETAS:");
        System.out.println(Hospital.getInstance().getRecetas().mostrarTodasLasRecetas());

        System.out.println("\n=== PRUEBA COMPLETADA EXITOSAMENTE ===");
    }

}

