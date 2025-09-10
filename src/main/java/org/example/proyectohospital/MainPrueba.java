package org.example.proyectohospital;

import org.example.proyectohospital.Logica.*;
import org.example.proyectohospital.Modelo.*;
import java.time.LocalDate;
import java.nio.file.Paths;
import java.util.List;

public class MainPrueba {

    // Rutas de archivos XML
    private static final String RUTA_BASE = Paths.get(System.getProperty("user.dir"), "bd_prueba").toString();
    private static final String RUTA_PACIENTES = Paths.get(RUTA_BASE, "pacientes.xml").toString();
    private static final String RUTA_PERSONAL = Paths.get(RUTA_BASE, "personal.xml").toString();
    private static final String RUTA_MEDICAMENTOS = Paths.get(RUTA_BASE, "medicamentos.xml").toString();
    private static final String RUTA_RECETAS = Paths.get(RUTA_BASE, "recetas.xml").toString();

    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBAS DEL SISTEMA HOSPITAL ===\n");

        try {
            // ✅ INICIALIZAR HOSPITAL CON SINGLETON
            Hospital.initialize(RUTA_PACIENTES, RUTA_PERSONAL, RUTA_MEDICAMENTOS, RUTA_RECETAS);
            Hospital hospital = Hospital.getInstance();

            // Obtener gestores desde el Hospital
            GestorPacientes gestorPacientes = hospital.getPacientes();
            GestorPersonal gestorPersonal = hospital.getPersonal();
            GestorMedicamentos gestorMedicamentos = hospital.getMedicamentos();
            GestorRecetas gestorRecetas = hospital.getRecetas();

            // Limpiar TODOS los datos existentes primero
            System.out.println("LIMPIANDO DATOS EXISTENTES...");
            limpiarDatos(gestorPacientes, gestorPersonal, gestorMedicamentos, gestorRecetas);

            // PASO 1: Probar gestión de medicamentos
            System.out.println("1. PROBANDO GESTIÓN DE MEDICAMENTOS");
            probarMedicamentos(gestorMedicamentos);

            // PASO 2: Probar gestión de pacientes
            System.out.println("\n2. PROBANDO GESTIÓN DE PACIENTES");
            probarPacientes(gestorPacientes);

            // PASO 3: Probar gestión de personal CON ID DUPLICADO
            System.out.println("\n3. PROBANDO GESTIÓN DE PERSONAL CON ID DUPLICADO");
            probarPersonalConDuplicados(gestorPersonal, gestorPacientes);

            // PASO 4: Probar gestión de recetas (requiere datos previos)
            System.out.println("\n4. PROBANDO GESTIÓN DE RECETAS");
            probarRecetas(gestorRecetas, gestorMedicamentos, gestorPersonal, gestorPacientes);

            // PASO 5: Probar actualización en cascada de PERSONAL
            System.out.println("\n5. PROBANDO ACTUALIZACIÓN EN CASCADA - PERSONAL");
            probarActualizacionCascadaPersonal(gestorPersonal, gestorRecetas);

            // PASO 6: Probar actualización en cascada de PACIENTES
            System.out.println("\n6. PROBANDO ACTUALIZACIÓN EN CASCADA - PACIENTES");
            probarActualizacionCascadaPacientes(gestorPacientes, gestorRecetas);

//             🔥 NUEVO PASO 7: Probar ELIMINACIÓN EN CASCADA
            System.out.println("\n7. PROBANDO ELIMINACIÓN EN CASCADA");
            probarEliminacionCascada(gestorPacientes, gestorPersonal, gestorRecetas);

            // PASO 8: Verificar persistencia
            System.out.println("\n8. VERIFICANDO PERSISTENCIA");
            verificarPersistencia();

        } catch (Exception e) {
            System.err.println("Error en las pruebas: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== PRUEBAS COMPLETADAS ===");
    }

    private static void limpiarDatos(GestorPacientes gp, GestorPersonal gper,
                                     GestorMedicamentos gm, GestorRecetas gr) {
        try {
            // Limpiar pacientes
            List<Paciente> pacientes = gp.findAll();
            for (Paciente p : pacientes) {
                gp.deleteById(p.getId());
            }

            // Limpiar personal
            List<Personal> personal = gper.findAll();
            for (Personal p : personal) {
                gper.deleteById(p.getId());
            }

            // Limpiar medicamentos
            List<Medicamento> medicamentos = gm.findAll();
            for (Medicamento m : medicamentos) {
                gm.deleteById(m.getCodigo());
            }

            // Limpiar recetas
            List<Receta> recetas = gr.findAll();
            for (Receta r : recetas) {
                gr.deleteById(r.getId());
            }

            System.out.println("Datos limpiados exitosamente");

        } catch (Exception e) {
            System.err.println("Error limpiando datos: " + e.getMessage());
        }
    }

    private static void probarMedicamentos(GestorMedicamentos gestor) {
        try {
            // Crear medicamentos de prueba
            Medicamento med1 = new Medicamento("Paracetamol", "Tabletas 500mg", "MED001");
            Medicamento med2 = new Medicamento("Ibuprofeno", "Cápsulas 400mg", "MED002");
            Medicamento med3 = new Medicamento("Amoxicilina", "Jarabe 250mg/5ml", "MED003");

            // Insertar medicamentos
            System.out.println("Insertando medicamentos...");
            gestor.create(med1);
            gestor.create(med2);
            gestor.create(med3);

            // Mostrar todos los medicamentos
            System.out.println("Medicamentos en el sistema:");
            System.out.println(gestor.mostrarTodosLosMedicamentos());

        } catch (Exception e) {
            System.err.println("Error en pruebas de medicamentos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void probarPacientes(GestorPacientes gestor) {
        try {
            // Crear pacientes de prueba - NOTA: Usamos ID "DUPLICADO001" que luego intentaremos duplicar en personal
            Paciente pac1 = new Paciente(88887777, LocalDate.of(1990, 5, 15), "José Martínez", "PAC001");
            Paciente pac2 = new Paciente(99998888, LocalDate.of(1985, 3, 22), "Laura Fernández", "PAC002");
            Paciente pac3 = new Paciente(77776666, LocalDate.of(2000, 8, 10), "Pedro Sánchez", "DUPLICADO001"); // ⚠️ ID DUPLICADO

            // Insertar pacientes
            System.out.println("Insertando pacientes...");
            gestor.create(pac1);
            gestor.create(pac2);
            gestor.create(pac3);

            // Mostrar todos los pacientes
            System.out.println("Pacientes en el sistema:");
            System.out.println(gestor.mostrarTodosLosPacientes());

        } catch (Exception e) {
            System.err.println("Error en pruebas de pacientes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void probarPersonalConDuplicados(GestorPersonal gestor, GestorPacientes gestorPacientes) {
        try {
            // Crear personal de prueba - Intentaremos duplicar el ID "DUPLICADO001"
            Medico medico1 = new Medico("Dr. Juan Pérez", "MED001", "clave123", "Cardiología");
            Medico medico2 = new Medico("Dra. Ana García", "MED002", "clave456", "Pediatría");
            Administrador admin = new Administrador("Carlos López", "ADM001", "admin123");
            Farmaceuta farmaceuta = new Farmaceuta("María Rodríguez", "DUPLICADO001", "farm123"); // ⚠️ Mismo ID que paciente

            // Insertar personal - Probamos la validación de duplicados
            System.out.println("Insertando personal con validación de duplicados...");

            System.out.println("\nIntentando insertar médico 1...");
            boolean exito1 = gestor.insertarPersonal(medico1, gestorPacientes.existeAlguienConEseID(medico1.getId()));
            System.out.println("Médico 1 insertado: " + exito1);

            System.out.println("\nIntentando insertar médico 2...");
            boolean exito2 = gestor.insertarPersonal(medico2, gestorPacientes.existeAlguienConEseID(medico2.getId()));
            System.out.println("Médico 2 insertado: " + exito2);

            System.out.println("\nIntentando insertar administrador...");
            boolean exito3 = gestor.insertarPersonal(admin, gestorPacientes.existeAlguienConEseID(admin.getId()));
            System.out.println("Administrador insertado: " + exito3);

            System.out.println("\n⚠️ Intentando insertar farmaceuta con ID DUPLICADO...");
            boolean exito4 = gestor.insertarPersonal(farmaceuta, gestorPacientes.existeAlguienConEseID(farmaceuta.getId()));
            System.out.println("Farmaceuta con ID duplicado insertado: " + exito4 + " (debería ser false)");

            // Probar cambio de clave
            System.out.println("\nProbando cambio de clave...");
            boolean cambioClave = gestor.cambiarClave("MED001", "clave123", "nuevaClaveSegura");
            System.out.println("Cambio de clave exitoso: " + cambioClave);

            // Mostrar todo el personal
            System.out.println("\nPersonal en el sistema:");
            System.out.println(gestor.mostrarTodoElPersonal());

        } catch (Exception e) {
            System.err.println("Error en pruebas de personal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void probarRecetas(GestorRecetas gestorRecetas, GestorMedicamentos gestorMed,
                                      GestorPersonal gestorPer, GestorPacientes gestorPac) {
        try {
            // Obtener datos necesarios para las recetas
            Medico medico = (Medico) gestorPer.getPersonalPorID("MED001");
            Paciente paciente = gestorPac.getPaciente("PAC001");
            Medicamento medicamento = gestorMed.getMedicamento("MED001");
            Medicamento medicamento2 = gestorMed.getMedicamento("MED002");

            if (medico == null || paciente == null || medicamento == null) {
                System.err.println("No se pudieron obtener los datos necesarios para las recetas");
                return;
            }

            Medico medico2 = (Medico) gestorPer.getPersonalPorID("MED002");
            Paciente paciente2 = gestorPac.getPaciente("PAC002");

            Receta receta2 = new Receta("QEKE34",medico2, paciente2, LocalDate.now(), LocalDate.now().plusDays(7), 1);
            gestorRecetas.insertarReceta(receta2);

            DetalleMedicamento detalle1 = new DetalleMedicamento(
                    medicamento,
                    "DET001",
                    10,
                    7,
                    "Tomar 1 tableta cada 8 horas"
            );



            gestorRecetas.agregarDetalle(receta2.getId(), detalle1);



            // Crear receta
            Receta receta1 = new Receta(
                    "REC001",
                    medico,
                    paciente,
                    LocalDate.now(),
                    LocalDate.now().plusDays(7),
                    1 // Procesada
            );

            // Crear detalles de medicamento


            DetalleMedicamento detalle2 = new DetalleMedicamento(
                    medicamento2,
                    "DET002",
                    15,
                    2,
                    "Tomar en ayunas"
            );

            // Insertar receta
            System.out.println("Insertando receta...");
            gestorRecetas.create(receta1);

            // Agregar detalles a la receta
            System.out.println("Agregando detalles de medicamento...");
            gestorRecetas.agregarDetalle("REC001", detalle1);
            gestorRecetas.agregarDetalle("REC001", detalle2);

            // Probar modificaciones
            System.out.println("Modificando indicación...");
            gestorRecetas.modificarIndicacion("REC001", "MED002", "Tomar después de la segunda comida");

            System.out.println("Modificando duración...");
            gestorRecetas.modificarDuracion("REC001", "MED002", 28);

            System.out.println("Modificando cantidad...");
            gestorRecetas.modificarCantidad("REC001", "MED002", 28);

            // Actualizar estado
            System.out.println("Actualizando estado de receta...");
            gestorRecetas.actualizarEstadoReceta("REC001", 2); // Confeccionada

            // Mostrar recetas
            System.out.println("Recetas en el sistema:");
            System.out.println(gestorRecetas.mostrarTodasLasRecetas());

        } catch (Exception e) {
            System.err.println("Error en pruebas de recetas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void probarActualizacionCascadaPersonal(GestorPersonal gestorPersonal, GestorRecetas gestorRecetas) {
        try {
            System.out.println("=== PRUEBA DE ACTUALIZACIÓN EN CASCADA - PERSONAL ===");

            // 1. Obtener médico existente
            Medico medico = (Medico) gestorPersonal.getPersonalPorID("MED001");
            System.out.println("Nombre ANTES: " + medico.getNombre());
            System.out.println("Especialidad ANTES: " + medico.getEspecialidad());

            // 2. Ver recetas ANTES
            List<Receta> recetasAntes = gestorRecetas.obtenerRecetasPorMedico("MED001");
            System.out.println("Recetas del médico ANTES: " + recetasAntes.size());
            if (!recetasAntes.isEmpty()) {
                System.out.println("Nombre en receta ANTES: " + recetasAntes.get(0).getPersonal().getNombre());
                System.out.println("Especialidad en receta ANTES: " +
                        ((Medico)recetasAntes.get(0).getPersonal()).getEspecialidad());
            }

            // 3. Modificar el médico
            System.out.println("\n🔧 Modificando médico...");
            medico.setNombre("Dr. Juan Pérez modelele");
            medico.setEspecialidad("CambiosDos");
            gestorPersonal.update(medico);

            // 4. Verificar cambios en el médico
            Medico medicoActualizado = (Medico) gestorPersonal.getPersonalPorID("MED001");
            System.out.println("Nombre DESPUÉS (directo): " + medicoActualizado.getNombre());
            System.out.println("Especialidad DESPUÉS (directo): " + medicoActualizado.getEspecialidad());

            // 5. Ver recetas DESPUÉS (deberían estar actualizadas por cascada)
            System.out.println("\n📋 Verificando actualización en recetas...");
            List<Receta> recetasDespues = gestorRecetas.obtenerRecetasPorMedico("MED001");
            System.out.println("Recetas del médico DESPUÉS: " + recetasDespues.size());

            if (!recetasDespues.isEmpty()) {
                Personal personalEnReceta = recetasDespues.get(0).getPersonal();
                System.out.println("Nombre en receta DESPUÉS: " + personalEnReceta.getNombre());

                if (personalEnReceta instanceof Medico) {
                    Medico medicoEnReceta = (Medico) personalEnReceta;
                    System.out.println("Especialidad en receta DESPUÉS: " + medicoEnReceta.getEspecialidad());
                }

                // Verificación
                if (personalEnReceta.getNombre().equals("Dr. Juan Pérez MODIFICADO")) {
                    System.out.println("✅ ✅ ✅ ACTUALIZACIÓN EN CASCADA EXITOSA");
                } else {
                    System.out.println("❌ ❌ ❌ ACTUALIZACIÓN EN CASCADA FALLÓ");
                }
            }

        } catch (Exception e) {
            System.err.println("Error en prueba de cascada personal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void probarActualizacionCascadaPacientes(GestorPacientes gestorPacientes, GestorRecetas gestorRecetas) {
        try {
            System.out.println("=== PRUEBA DE ACTUALIZACIÓN EN CASCADA - PACIENTES ===");

            // 1. Obtener paciente existente
            Paciente paciente = gestorPacientes.getPaciente("PAC001");
            System.out.println("Nombre paciente ANTES: " + paciente.getNombre());
            System.out.println("Teléfono paciente ANTES: " + paciente.getTelefono());

            // 2. Ver recetas ANTES
            List<Receta> recetasAntes = gestorRecetas.obtenerRecetasPorPaciente("PAC001");
            System.out.println("Recetas del paciente ANTES: " + recetasAntes.size());
            if (!recetasAntes.isEmpty()) {
                System.out.println("Nombre en receta ANTES: " + recetasAntes.get(0).getPaciente().getNombre());
                System.out.println("Teléfono en receta ANTES: " + recetasAntes.get(0).getPaciente().getTelefono());
            }

            // 3. Modificar el paciente
            System.out.println("\n🔧 Modificando paciente...");
            paciente.setNombre("Majid");
            paciente.setTelefono(43504543);
            gestorPacientes.update(paciente);

            // 4. Verificar cambios en el paciente
            Paciente pacienteActualizado = gestorPacientes.getPaciente("PAC001");
            System.out.println("Nombre DESPUÉS (directo): " + pacienteActualizado.getNombre());
            System.out.println("Teléfono DESPUÉS (directo): " + pacienteActualizado.getTelefono());

            // 5. Ver recetas DESPUÉS (deberían estar actualizadas por cascada)
            System.out.println("\n📋 Verificando actualización en recetas...");
            List<Receta> recetasDespues = gestorRecetas.obtenerRecetasPorPaciente("PAC001");
            System.out.println("Recetas del paciente DESPUÉS: " + recetasDespues.size());

            if (!recetasDespues.isEmpty()) {
                Paciente pacienteEnReceta = recetasDespues.get(0).getPaciente();
                System.out.println("Nombre en receta DESPUÉS: " + pacienteEnReceta.getNombre());
                System.out.println("Teléfono en receta DESPUÉS: " + pacienteEnReceta.getTelefono());

                // Verificación CORREGIDA
                if (pacienteEnReceta.getNombre().equals("Emilio") &&
                        pacienteEnReceta.getTelefono() == 3443443) {
                    System.out.println("✅ ✅ ✅ ACTUALIZACIÓN EN CASCADA EXITOSA");
                } else {
                    System.out.println("❌ ❌ ❌ ACTUALIZACIÓN EN CASCADA FALLÓ");
                    System.out.println("Esperado: Emilio, 3443443");
                    System.out.println("Obtenido: " + pacienteEnReceta.getNombre() + ", " + pacienteEnReceta.getTelefono());
                }
            }

        } catch (Exception e) {
            System.err.println("Error en prueba de cascada pacientes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void probarEliminacionCascada(GestorPacientes gestorPacientes,
                                                 GestorPersonal gestorPersonal,
                                                 GestorRecetas gestorRecetas) {
        try {
            System.out.println("=== PRUEBA DE ELIMINACIÓN EN CASCADA ===");

            // 1. Verificar datos existentes antes de eliminar
            System.out.println("\n📊 ESTADO INICIAL:");
            System.out.println("Pacientes: " + gestorPacientes.findAll().size());
            System.out.println("Personal: " + gestorPersonal.findAll().size());
            System.out.println("Recetas: " + gestorRecetas.findAll().size());

            // 2. Ver recetas asociadas al paciente PAC001
            List<Receta> recetasPaciente = gestorRecetas.obtenerRecetasPorPaciente("PAC002");
            System.out.println("Recetas del paciente PAC001: " + recetasPaciente.size());

            // 3. Ver recetas asociadas al médico MED001
            List<Receta> recetasMedico = gestorRecetas.obtenerRecetasPorMedico("MED001");
            System.out.println("Recetas del médico MED001: " + recetasMedico.size());

            // 4. ELIMINAR PACIENTE EN CASCADA
            System.out.println("\n🔥 ELIMINANDO PACIENTE PAC001 (debería eliminar sus recetas)");
            //boolean pacienteEliminado = gestorPacientes.deleteById("PAC001");
            //System.out.println("Paciente eliminado: " + pacienteEliminado);

            // 5. Verificar que las recetas del paciente fueron eliminadas
            System.out.println("\n📊 DESPUÉS DE ELIMINAR PACIENTE:");
            System.out.println("Pacientes: " + gestorPacientes.findAll().size());
            System.out.println("Recetas totales: " + gestorRecetas.findAll().size());

            List<Receta> recetasPacienteDespues = gestorRecetas.obtenerRecetasPorPaciente("PAC001");
            System.out.println("Recetas del paciente PAC001 después: " + recetasPacienteDespues.size());

            // 6. ELIMINAR MÉDICO EN CASCADA
            System.out.println("\n🔥 ELIMINANDO MÉDICO MED001 (debería eliminar sus recetas)");
            boolean medicoEliminado = gestorPersonal.deleteById("MED002");
            System.out.println("Médico eliminado: " + medicoEliminado);

            // 7. Verificar que las recetas del médico fueron eliminadas
            System.out.println("\n📊 DESPUÉS DE ELIMINAR MÉDICO:");
            System.out.println("Personal: " + gestorPersonal.findAll().size());
            System.out.println("Recetas totales: " + gestorRecetas.findAll().size());

            List<Receta> recetasMedicoDespues = gestorRecetas.obtenerRecetasPorMedico("MED001");
            System.out.println("Recetas del médico MED001 después: " + recetasMedicoDespues.size());

            // 8. Verificación final
            if (recetasPacienteDespues.isEmpty() && recetasMedicoDespues.isEmpty()) {
                System.out.println("\n✅ ✅ ✅ ELIMINACIÓN EN CASCADA EXITOSA");
            } else {
                System.out.println("\n❌ ❌ ❌ ELIMINACIÓN EN CASCADA FALLÓ");
            }

        } catch (Exception e) {
            System.err.println("Error en prueba de eliminación en cascada: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void verificarPersistencia() {
        try {
            System.out.println("=== VERIFICACIÓN DE PERSISTENCIA ===");

            // Crear nuevas instancias (deberían cargar los datos del XML)
            GestorPacientes nuevoGestorPacientes = new GestorPacientes(RUTA_PACIENTES);
            GestorPersonal nuevoGestorPersonal = new GestorPersonal(RUTA_PERSONAL);
            GestorMedicamentos nuevoGestorMed = new GestorMedicamentos(RUTA_MEDICAMENTOS);
            GestorRecetas nuevoGestorRecetas = new GestorRecetas(RUTA_RECETAS);

            System.out.println("Pacientes cargados desde XML: " + nuevoGestorPacientes.findAll().size());
            System.out.println("Personal cargados desde XML: " + nuevoGestorPersonal.findAll().size());
            System.out.println("Medicamentos cargados desde XML: " + nuevoGestorMed.findAll().size());
            System.out.println("Recetas cargadas desde XML: " + nuevoGestorRecetas.findAll().size());

            // Verificar que el ID duplicado fue rechazado correctamente
            boolean existeDuplicadoEnPersonal = nuevoGestorPersonal.existePersonalConEseID("DUPLICADO001");
            boolean existeDuplicadoEnPacientes = nuevoGestorPacientes.existeAlguienConEseID("DUPLICADO001");

            System.out.println("\n=== VERIFICACIÓN DE DUPLICADOS ===");
            System.out.println("ID 'DUPLICADO001' en personal: " + existeDuplicadoEnPersonal + " (debería ser false)");
            System.out.println("ID 'DUPLICADO001' en pacientes: " + existeDuplicadoEnPacientes + " (debería ser true)");

            if (!existeDuplicadoEnPersonal && existeDuplicadoEnPacientes) {
                System.out.println("✅ Validación de duplicados FUNCIONA CORRECTAMENTE");
            } else {
                System.out.println("❌ Validación de duplicados FALLÓ");
            }

            // Verificar que la eliminación en cascada persistió
            System.out.println("\n=== VERIFICACIÓN ELIMINACIÓN EN CASCADA ===");

            // Verificar que PAC001 fue eliminado
            boolean existePAC001 = nuevoGestorPacientes.existeAlguienConEseID("PAC001");
            System.out.println("Paciente PAC001 existe: " + existePAC001 + " (debería ser false)");

            // Verificar que MED001 fue eliminado
            boolean existeMED001 = nuevoGestorPersonal.existePersonalConEseID("MED001");
            System.out.println("Personal MED001 existe: " + existeMED001 + " (debería ser false)");

            // Verificar que las recetas asociadas fueron eliminadas
            List<Receta> recetasPAC001 = nuevoGestorRecetas.obtenerRecetasPorPaciente("PAC001");
            List<Receta> recetasMED001 = nuevoGestorRecetas.obtenerRecetasPorMedico("MED001");

            System.out.println("Recetas de PAC001: " + recetasPAC001.size() + " (debería ser 0)");
            System.out.println("Recetas de MED001: " + recetasMED001.size() + " (debería ser 0)");

            if (!existePAC001 && !existeMED001 && recetasPAC001.isEmpty() && recetasMED001.isEmpty()) {
                System.out.println("✅ ELIMINACIÓN EN CASCADA PERSISTIÓ CORRECTAMENTE");
            } else {
                System.out.println("❌ ELIMINACIÓN EN CASCADA NO PERSISTIÓ");
            }

        } catch (Exception e) {
            System.err.println("Error verificando persistencia: " + e.getMessage());
            e.printStackTrace();
        }
    }
}