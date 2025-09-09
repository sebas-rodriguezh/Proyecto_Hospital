package org.example.proyectohospital.Logica;

import org.example.proyectohospital.Datos.*;
import org.example.proyectohospital.Modelo.Paciente;
import org.example.proyectohospital.Modelo.Receta;

import java.util.List;
import java.util.stream.Collectors;

public class GestorPacientes {
    private final PacienteDatos store;

    public GestorPacientes(String rutaArchivo) {
        this.store = new PacienteDatos(rutaArchivo);
    }

    // === MÉTODOS DE LECTURA ===

    public List<Paciente> findAll() {
        PacienteConector data = store.load();
        return data.getPacientes().stream()
                .map(PacienteMapper::toModel)
                .collect(Collectors.toList());
    }

    public List<Paciente> findByText(String texto) {
        PacienteConector data = store.load();
        if (texto == null || texto.trim().isEmpty()) {
            return data.getPacientes().stream()
                    .map(PacienteMapper::toModel)
                    .collect(Collectors.toList());
        }

        String textoBusqueda = texto.toLowerCase().trim();
        return data.getPacientes().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(textoBusqueda) ||
                        p.getId().toLowerCase().contains(textoBusqueda))
                .map(PacienteMapper::toModel)
                .collect(Collectors.toList());
    }

    public Paciente getPaciente(String idPaciente) {
        PacienteConector data = store.load();
        return data.getPacientes().stream()
                .filter(p -> p.getId().equals(idPaciente))
                .map(PacienteMapper::toModel)
                .findFirst()
                .orElse(null);
    }

    public Boolean existeAlguienConEseID(String idPaciente) {
        PacienteConector data = store.load();
        return data.getPacientes().stream()
                .anyMatch(p -> p.getId().equals(idPaciente));
    }

    // === MÉTODOS DE ESCRITURA ===

    public Paciente create(Paciente nuevo) {
        try {
            if (nuevo == null) {
                throw new IllegalArgumentException("El paciente no puede ser nulo");
            }

            PacienteConector data = store.load();

            // Solo validación local - sin referencias a Hospital
            if (existeAlguienConEseID(nuevo.getId())) {
                throw new IllegalArgumentException("Ya existe un paciente con ese ID");
            }

            // Agregar al XML
            PacienteEntity pacienteEntity = PacienteMapper.toXML(nuevo);
            data.getPacientes().add(pacienteEntity);
            store.save(data);

            return nuevo;
        } catch (Exception e) {
            throw new RuntimeException("Error creando paciente: " + e.getMessage());
        }
    }

    public Paciente update(Paciente actualizado) {
        try {
            if (actualizado == null) {
                throw new IllegalArgumentException("El paciente no puede ser nulo");
            }

            PacienteConector data = store.load();
            GestorRecetas gestorRecetas = Hospital.getInstance().getRecetas(); // 🔥 NUEVO

            for (int i = 0; i < data.getPacientes().size(); i++) {
                PacienteEntity actual = data.getPacientes().get(i);
                if (actual.getId().equals(actualizado.getId())) {
                    data.getPacientes().set(i, PacienteMapper.toXML(actualizado));
                    store.save(data);

                    // 🔥 ACTUALIZAR RECETAS que usan este paciente
                    actualizarRecetasConPaciente(actualizado, gestorRecetas);

                    return actualizado;
                }
            }

            throw new IllegalArgumentException("Paciente no encontrado con ID: " + actualizado.getId());
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando paciente: " + e.getMessage());
        }
    }

    // 🔥 MÉTODO NUEVO para actualizar recetas con paciente modificado
    private void actualizarRecetasConPaciente(Paciente pacienteActualizado, GestorRecetas gestorRecetas) {
        try {
            List<Receta> recetasDelPaciente = gestorRecetas.obtenerRecetasPorPaciente(pacienteActualizado.getId());

            for (Receta receta : recetasDelPaciente) {
                // Reemplazar el objeto paciente viejo con el actualizado
                receta.setPaciente(pacienteActualizado);
                gestorRecetas.update(receta);
            }

            System.out.println("Actualizadas " + recetasDelPaciente.size() + " recetas del paciente: " + pacienteActualizado.getNombre());

        } catch (Exception e) {
            System.err.println("Error actualizando recetas del paciente: " + e.getMessage());
        }
    }

    public Boolean deleteById(String idPaciente) {
        try {
            if (idPaciente == null || idPaciente.trim().isEmpty()) {
                throw new IllegalArgumentException("ID no puede ser nulo o vacío");
            }

            // 🔥 NUEVO: Eliminar recetas asociadas en cascada
            GestorRecetas gestorRecetas = Hospital.getInstance().getRecetas();
            List<Receta> recetasAsociadas = gestorRecetas.obtenerRecetasPorPaciente(idPaciente);

            if (!recetasAsociadas.isEmpty()) {
                System.out.println("Eliminando " + recetasAsociadas.size() + " recetas asociadas al paciente...");
                for (Receta receta : recetasAsociadas) {
                    gestorRecetas.deleteById(receta.getId());
                }
            }

            PacienteConector data = store.load();
            boolean eliminado = data.getPacientes().removeIf(paciente -> paciente.getId().equals(idPaciente));

            if (eliminado) {
                store.save(data);
                System.out.println("Paciente eliminado correctamente: " + idPaciente);
            }

            return eliminado;
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando paciente: " + e.getMessage());
        }
    }

    // === MÉTODOS ORIGINALES ADAPTADOS ===

    public Boolean insertarPaciente(Paciente paciente, Boolean respuestaListaPersonal) {
        try {
            if (paciente == null) {
                throw new IllegalArgumentException("El paciente no puede ser nulo");
            }

            // CAMBIO: Validación simplificada sin Hospital
            if (existeAlguienConEseID(paciente.getId()) || (Boolean.TRUE.equals(respuestaListaPersonal))) {
                throw new IllegalArgumentException("Existe una persona con ese ID en el sistema.");
            }

            create(paciente);
            return true;

        } catch (IllegalArgumentException e) {
            System.err.println("Error al insertar paciente: " + e.getMessage());
            return false;
        }
    }

    public Boolean eliminar(String idPaciente) {
        if (!existeAlguienConEseID(idPaciente)) {
            return false;
        }
        return deleteById(idPaciente);
    }

    public List<Paciente> getPacientes() {
        return findAll();
    }

    public void setPacientes(List<Paciente> pacientes) {
        try {
            PacienteConector data = store.load();
            List<PacienteEntity> entities = pacientes.stream()
                    .map(PacienteMapper::toXML)
                    .collect(Collectors.toList());

            data.setPacientes(entities);
            store.save(data);
        } catch (Exception e) {
            throw new RuntimeException("Error estableciendo pacientes: " + e.getMessage());
        }
    }

    public String mostrarTodosLosPacientes() {
        List<Paciente> pacientes = findAll();

        if (pacientes.isEmpty()) {
            return "No hay pacientes registrados en el sistema.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== LISTA DE TODOS LOS PACIENTES ===\n");
        sb.append("Total de pacientes: ").append(pacientes.size()).append("\n\n");

        for (int i = 0; i < pacientes.size(); i++) {
            Paciente paciente = pacientes.get(i);
            sb.append("Paciente #").append(i + 1).append(":\n");
            sb.append(paciente.toString()).append("\n");
            sb.append("---------------------------------\n");
        }

        return sb.toString();
    }
}