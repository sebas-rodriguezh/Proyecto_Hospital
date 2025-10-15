package org.example.proyectohospital.Logica;

import org.example.proyectohospital.Datos.PacienteDatos;
import org.example.proyectohospital.Modelo.Paciente;
import org.example.proyectohospital.Modelo.Receta;

import java.sql.SQLException;
import java.util.List;

public class GestorPacientes {
    private final PacienteDatos store;

    public GestorPacientes() {
        this.store = new PacienteDatos();
    }

    public List<Paciente> findAll() {
        try {
            return store.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Error cargando pacientes: " + e.getMessage());
        }
    }

    public List<Paciente> findByText(String texto) {
        try {
            if (texto == null || texto.trim().isEmpty()) {
                return store.findAll();
            }
            return store.findByText(texto);
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando pacientes: " + e.getMessage());
        }
    }

    public Paciente getPaciente(String idPaciente) {
        try {
            return store.findById(idPaciente);
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo paciente: " + e.getMessage());
        }
    }

    public Boolean existeAlguienConEseID(String idPaciente) {
        try {
            return store.findById(idPaciente) != null;
        } catch (SQLException e) {
            throw new RuntimeException("Error verificando existencia: " + e.getMessage());
        }
    }

    public Paciente create(Paciente nuevo) {
        try {
            if (nuevo == null) {
                throw new IllegalArgumentException("El paciente no puede ser nulo");
            }
            if (existeAlguienConEseID(nuevo.getId())) {
                throw new IllegalArgumentException("Ya existe un paciente con ese ID");
            }
            return store.insert(nuevo);
        } catch (SQLException e) {
            throw new RuntimeException("Error creando paciente: " + e.getMessage());
        }
    }

    public Paciente update(Paciente actualizado) {
        try {
            if (actualizado == null) {
                throw new IllegalArgumentException("El paciente no puede ser nulo");
            }

            Paciente result = store.update(actualizado);
            if (result != null) {
                actualizarRecetasConPaciente(actualizado);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando paciente: " + e.getMessage());
        }
    }

    private void actualizarRecetasConPaciente(Paciente pacienteActualizado) {
        try {
            GestorRecetas gestorRecetas = Hospital.getInstance().getRecetas();
            List<Receta> recetasDelPaciente = gestorRecetas.obtenerRecetasPorPaciente(pacienteActualizado.getId());

            for (Receta receta : recetasDelPaciente) {
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

            GestorRecetas gestorRecetas = Hospital.getInstance().getRecetas();
            List<Receta> recetasAsociadas = gestorRecetas.obtenerRecetasPorPaciente(idPaciente);

            if (!recetasAsociadas.isEmpty()) {
                System.out.println("Eliminando " + recetasAsociadas.size() + " recetas asociadas al paciente...");
                for (Receta receta : recetasAsociadas) {
                    gestorRecetas.deleteById(receta.getId());
                }
            }

            boolean eliminado = store.delete(idPaciente);
            if (eliminado) {
                System.out.println("Paciente eliminado correctamente: " + idPaciente);
            }
            return eliminado;
        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando paciente: " + e.getMessage());
        }
    }

    public Boolean insertarPaciente(Paciente paciente, Boolean respuestaListaPersonal) {
        try {
            if (paciente == null) {
                throw new IllegalArgumentException("El paciente no puede ser nulo");
            }
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
            // Eliminar todos los pacientes existentes
            List<Paciente> existentes = store.findAll();
            for (Paciente pac : existentes) {
                store.delete(pac.getId());
            }

            // Insertar los nuevos
            for (Paciente pac : pacientes) {
                store.insert(pac);
            }
        } catch (SQLException e) {
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