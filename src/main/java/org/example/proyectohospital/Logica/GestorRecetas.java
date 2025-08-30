package org.example.proyectohospital.Logica;

import org.example.proyectohospital.Modelo.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorRecetas {
    private List<Receta> recetas;

    public GestorRecetas() {
        this.recetas = new ArrayList<>();
    }

    public GestorRecetas(List<Receta> recetas) {
        this.recetas = recetas;
    }

    public boolean insertarReceta(Receta receta) {
        if (receta == null || existeRecetaConEseID(receta.getId())) return false;
        recetas.add(receta);
        return true;
    }

    public boolean eliminar(String idReceta) {
        return recetas.removeIf(r -> r.getId().equals(idReceta));
    }

    public boolean existeRecetaConEseID(String idReceta) {
        return recetas.stream().anyMatch(r -> r.getId().equals(idReceta));
    }

    public Receta getRecetaPorID(String idReceta) {
        return recetas.stream()
                .filter(r -> r.getId().equals(idReceta))
                .findFirst()
                .orElse(null);
    }

    public List<Receta> obtenerRecetasPorPaciente(String idPaciente) {
        List<Receta> resultado = new ArrayList<>();
        for (Receta receta : recetas) {
            if (receta.getPaciente().getId().equals(idPaciente)) {
                resultado.add(receta);
            }
        }
        return resultado;
    }

    public List<Receta> obtenerRecetasPorMedico(String idMedico) {
        List<Receta> resultado = new ArrayList<>();
        for (Receta receta : recetas) {
            if (receta.getPersonal().getId().equals(idMedico)) {
                resultado.add(receta);
            }
        }
        return resultado;
    }

    public List<Receta> obtenerRecetasPorEstado(int estado) {
        List<Receta> resultado = new ArrayList<>();
        for (Receta receta : recetas) {
            if (receta.getEstado() == estado) {
                resultado.add(receta);
            }
        }
        return resultado;
    }

    public boolean actualizarEstadoReceta(String idReceta, int nuevoEstado) {
        Receta receta = getRecetaPorID(idReceta);
        if (receta != null) {
            receta.setEstado(nuevoEstado);
            return true;
        }
        return false;
    }

    public boolean actualizarFechaRetiro(String idReceta, LocalDate nuevaFecha) {
        Receta receta = getRecetaPorID(idReceta);
        if (receta != null) {
            receta.setFechaRetiro(nuevaFecha);
            return true;
        }
        return false;
    }

    public List<Receta> obtenerRecetasPorRangoFechas(LocalDate inicio, LocalDate fin) {
        List<Receta> resultado = new ArrayList<>();
        for (Receta receta : recetas) {
            if (!receta.getFechaPrescripcion().isBefore(inicio) &&
                    !receta.getFechaPrescripcion().isAfter(fin)) {
                resultado.add(receta);
            }
        }
        return resultado;
    }

    // === MÉTODOS SOBRE DETALLES DE MEDICAMENTOS ===

    public boolean agregarDetalle(String idReceta, DetalleMedicamento detalle) {
        Receta receta = getRecetaPorID(idReceta);
        if (receta == null) return false;

        for (DetalleMedicamento d : receta.getDetalleMedicamentos()) {
            if (d.getMedicamento().getCodigo().equals(detalle.getMedicamento().getCodigo())) {
                return false;
            }
        }
        receta.getDetalleMedicamentos().add(detalle);
        return true;
    }

    public boolean eliminarDetalle(String idReceta, String codigoMedicamento) {
        Receta receta = getRecetaPorID(idReceta);
        if (receta == null) return false;

        return receta.getDetalleMedicamentos().removeIf(
                d -> d.getMedicamento().getCodigo().equals(codigoMedicamento));
    }

    public boolean modificarCantidad(String idReceta, String codigoMedicamento, int nuevaCantidad) {
        DetalleMedicamento detalle = getDetalle(idReceta, codigoMedicamento);
        if (detalle != null) {
            detalle.setCantidad(nuevaCantidad);
            return true;
        }
        return false;
    }

    public boolean modificarDuracion(String idReceta, String codigoMedicamento, int nuevaDuracion) {
        DetalleMedicamento detalle = getDetalle(idReceta, codigoMedicamento);
        if (detalle != null) {
            detalle.setDuracion(nuevaDuracion);
            return true;
        }
        return false;
    }

    public boolean modificarIndicacion(String idReceta, String codigoMedicamento, String nuevaIndicacion) {
        DetalleMedicamento detalle = getDetalle(idReceta, codigoMedicamento);
        if (detalle != null) {
            detalle.setIndicacion(nuevaIndicacion);
            return true;
        }
        return false;
    }

    public DetalleMedicamento getDetalle(String idReceta, String codigoMedicamento) {
        Receta receta = getRecetaPorID(idReceta);
        if (receta == null) return null;

        for (DetalleMedicamento d : receta.getDetalleMedicamentos()) {
            if (d.getMedicamento().getCodigo().equals(codigoMedicamento)) {
                return d;
            }
        }
        return null;
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
        return recetas;
    }

    public void setRecetas(List<Receta> recetas) {
        this.recetas = recetas;
    }

    // === MÉTODOS DE VISUALIZACIÓN ===

    public String mostrarTodasLasRecetas() {
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
}
