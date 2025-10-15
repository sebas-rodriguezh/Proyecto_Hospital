package org.example.proyectohospital.Logica;

import org.example.proyectohospital.Datos.RecetaDatos;
import org.example.proyectohospital.Modelo.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class GestorRecetas {
    private final RecetaDatos store;

    public GestorRecetas() {
        this.store = new RecetaDatos();
    }

    public List<Receta> findAll() {
        try {
            return store.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Error cargando recetas: " + e.getMessage());
        }
    }

    public List<Receta> findByText(String texto) {
        try {
            if (texto == null || texto.trim().isEmpty()) {
                return store.findAll();
            }
            return store.findByText(texto);
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando recetas: " + e.getMessage());
        }
    }

    public Receta getRecetaPorID(String idReceta) {
        try {
            return store.findById(idReceta);
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo receta: " + e.getMessage());
        }
    }

    public boolean existeRecetaConEseID(String idReceta) {
        try {
            return store.findById(idReceta) != null;
        } catch (SQLException e) {
            throw new RuntimeException("Error verificando existencia: " + e.getMessage());
        }
    }

    public List<Receta> obtenerRecetasPorPaciente(String idPaciente) {
        try {
            if (idPaciente == null || idPaciente.trim().isEmpty()) {
                return store.findAll();
            }
            return store.findByPaciente(idPaciente);
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo recetas por paciente: " + e.getMessage());
        }
    }

    public List<Receta> obtenerRecetasPorMedico(String idMedico) {
        try {
            return store.findByMedico(idMedico);
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo recetas por médico: " + e.getMessage());
        }
    }

    public List<Receta> obtenerRecetasPorEstado(int estado) {
        try {
            return store.findByEstado(estado);
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo recetas por estado: " + e.getMessage());
        }
    }

    public List<Receta> obtenerRecetasPorRangoFechas(LocalDate inicio, LocalDate fin) {
        try {
            List<Receta> todas = store.findAll();
            return todas.stream()
                    .filter(r -> !r.getFechaPrescripcion().isBefore(inicio) &&
                            !r.getFechaPrescripcion().isAfter(fin))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo recetas por rango de fechas: " + e.getMessage());
        }
    }

    public Receta create(Receta nueva) {
        try {
            if (nueva == null) {
                throw new IllegalArgumentException("La receta no puede ser nula");
            }
            if (nueva.getId() == null || nueva.getId().trim().isEmpty()) {
                throw new IllegalArgumentException("El ID de la receta no puede estar vacío");
            }
            if (existeRecetaConEseID(nueva.getId())) {
                throw new IllegalArgumentException("Ya existe una receta con ese ID");
            }

            boolean insertada = store.insert(nueva);
            return insertada ? nueva : null;
        } catch (SQLException e) {
            throw new RuntimeException("Error creando receta: " + e.getMessage());
        }
    }

    public Receta update(Receta actualizada) {
        try {
            if (actualizada == null || actualizada.getId() == null) {
                throw new IllegalArgumentException("Receta o ID no pueden ser nulos");
            }

            boolean actualizadaOk = store.update(actualizada);
            return actualizadaOk ? actualizada : null;
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando receta: " + e.getMessage());
        }
    }

    public Boolean deleteById(String idReceta) {
        try {
            if (idReceta == null || idReceta.trim().isEmpty()) {
                throw new IllegalArgumentException("ID no puede ser nulo o vacío");
            }
            return store.delete(idReceta);
        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando receta: " + e.getMessage());
        }
    }

    public boolean insertarReceta(Receta receta) {
        try {
            if (receta == null || existeRecetaConEseID(receta.getId())) {
                return false;
            }
            create(receta);
            return true;
        } catch (Exception e) {
            System.err.println("Error al insertar receta: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String idReceta) {
        return deleteById(idReceta);
    }

    public boolean actualizarEstadoReceta(String idReceta, int nuevoEstado) {
        try {
            Receta receta = getRecetaPorID(idReceta);
            if (receta != null) {
                receta.setEstado(nuevoEstado);
                update(receta);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error actualizando estado: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarFechaRetiro(String idReceta, LocalDate nuevaFecha) {
        try {
            Receta receta = getRecetaPorID(idReceta);
            if (receta != null) {
                receta.setFechaRetiro(nuevaFecha);
                update(receta);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error actualizando fecha retiro: " + e.getMessage());
            return false;
        }
    }

    public boolean agregarDetalle(String idReceta, DetalleMedicamento detalle) {
        try {
            Receta receta = getRecetaPorID(idReceta);
            if (receta == null) return false;

            for (DetalleMedicamento d : receta.getDetalleMedicamentos()) {
                if (d.getMedicamento().getCodigo().equals(detalle.getMedicamento().getCodigo())) {
                    return false;
                }
            }

            receta.getDetalleMedicamentos().add(detalle);
            update(receta);
            return true;
        } catch (Exception e) {
            System.err.println("Error agregando detalle: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarDetalle(String idReceta, String codigoMedicamento) {
        try {
            Receta receta = getRecetaPorID(idReceta);
            if (receta == null) return false;

            boolean eliminado = receta.getDetalleMedicamentos().removeIf(
                    d -> d.getMedicamento().getCodigo().equals(codigoMedicamento));

            if (eliminado) {
                update(receta);
            }

            return eliminado;
        } catch (Exception e) {
            System.err.println("Error eliminando detalle: " + e.getMessage());
            return false;
        }
    }

    public boolean modificarCantidad(String idReceta, String codigoMedicamento, int nuevaCantidad) {
        try {
            Receta receta = getRecetaPorID(idReceta);
            if (receta == null) return false;

            for (DetalleMedicamento detalle : receta.getDetalleMedicamentos()) {
                if (detalle.getMedicamento().getCodigo().equals(codigoMedicamento)) {
                    detalle.setCantidad(nuevaCantidad);
                    update(receta);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error modificando cantidad: " + e.getMessage());
            return false;
        }
    }

    public boolean modificarDuracion(String idReceta, String codigoMedicamento, int nuevaDuracion) {
        try {
            Receta receta = getRecetaPorID(idReceta);
            if (receta == null) return false;

            for (DetalleMedicamento detalle : receta.getDetalleMedicamentos()) {
                if (detalle.getMedicamento().getCodigo().equals(codigoMedicamento)) {
                    detalle.setDuracion(nuevaDuracion);
                    update(receta);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error modificando duración: " + e.getMessage());
            return false;
        }
    }

    public boolean modificarIndicacion(String idReceta, String codigoMedicamento, String nuevaIndicacion) {
        try {
            Receta receta = getRecetaPorID(idReceta);
            if (receta == null) return false;

            for (DetalleMedicamento detalle : receta.getDetalleMedicamentos()) {
                if (detalle.getMedicamento().getCodigo().equals(codigoMedicamento)) {
                    detalle.setIndicacion(nuevaIndicacion);
                    update(receta);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error modificando indicación: " + e.getMessage());
            return false;
        }
    }

    public DetalleMedicamento getDetalle(String idReceta, String codigoMedicamento) {
        Receta receta = getRecetaPorID(idReceta);
        if (receta == null) return null;

        return receta.getDetalleMedicamentos().stream()
                .filter(d -> d.getMedicamento().getCodigo().equals(codigoMedicamento))
                .findFirst()
                .orElse(null);
    }

    public List<DetalleMedicamento> obtenerDetalles(String idReceta) {
        Receta receta = getRecetaPorID(idReceta);
        if (receta == null) return List.of();
        return receta.getDetalleMedicamentos();
    }

    public boolean hayDetalles(String idReceta) {
        Receta receta = getRecetaPorID(idReceta);
        return receta != null && !receta.getDetalleMedicamentos().isEmpty();
    }

    public String obtenerNombreEstado(int estado) {
        return switch (estado) {
            case 1 -> "Procesada";
            case 2 -> "Confeccionada";
            case 3 -> "Lista";
            case 4 -> "Entregada";
            default -> "Desconocido";
        };
    }

    public List<Receta> getRecetas() {
        return findAll();
    }

    public void setRecetas(List<Receta> recetas) {
        try {
            // Eliminar todas las recetas existentes
            List<Receta> existentes = store.findAll();
            for (Receta receta : existentes) {
                store.delete(receta.getId());
            }

            // Insertar las nuevas
            for (Receta receta : recetas) {
                store.insert(receta);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error estableciendo recetas: " + e.getMessage());
        }
    }

    public String mostrarTodasLasRecetas() {
        List<Receta> recetas = findAll();

        if (recetas.isEmpty()) return "No hay recetas registradas.";

        StringBuilder sb = new StringBuilder("=== LISTA DE TODAS LAS RECETAS ===\n");
        for (Receta r : recetas) {
            sb.append("ID: ").append(r.getId()).append("\n")
                    .append("Paciente: ").append(r.getPaciente().getNombre()).append(" (").append(r.getPaciente().getId()).append(")\n")
                    .append("Médico: ").append(r.getPersonal().getNombre()).append(" (").append(r.getPersonal().getId()).append(")\n")
                    .append("Fecha Prescripción: ").append(r.getFechaPrescripcion()).append("\n")
                    .append("Fecha Retiro: ").append(r.getFechaRetiro()).append("\n")
                    .append("Estado: ").append(obtenerNombreEstado(r.getEstado())).append("\n");

            if (!r.getDetalleMedicamentos().isEmpty()) {
                sb.append("Detalles:\n");
                for (DetalleMedicamento d : r.getDetalleMedicamentos()) {
                    sb.append("- ").append(d.toString()).append("\n");
                }
            } else {
                sb.append("No hay medicamentos en esta receta.\n");
            }

            sb.append("--------------------------------------------------\n");
        }
        return sb.toString();
    }

    public void guardarCambios() {
        // En SQL no necesitamos guardar cambios manualmente, las transacciones son automáticas
        System.out.println("Cambios guardados automáticamente en la base de datos");
    }

    public static String estadoToString(int estado){
        return switch(estado){
            case 1 -> "Procesada";
            case 2 -> "Confeccionada";
            case 3 -> "Lista";
            case 4 -> "Entregada";
            default -> "Desconocido";
        };
    }

    public List<Receta> obtenerRecetasPorMedicamento(String codigoMedicamento) {
        try {
            return store.findByMedicamento(codigoMedicamento);
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo recetas por medicamento: " + e.getMessage());
        }
    }
}