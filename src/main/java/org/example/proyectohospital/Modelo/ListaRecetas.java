package org.example.proyectohospital.Modelo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.ArrayList;

public class ListaRecetas {
    private ObservableList<Receta> recetas;

    public ListaRecetas(List<Receta> recetas) {
        this.recetas = FXCollections.observableArrayList(recetas);
    }

    public ListaRecetas() {
        this.recetas = FXCollections.observableArrayList();
    }

    public ObservableList<Receta> getRecetas() {
        return recetas;
    }

    public void setRecetas(List<Receta> recetas) {
        this.recetas = FXCollections.observableArrayList(recetas);
    }

    public Boolean insertarReceta(Receta receta) {
        try {
            if (receta == null) {
                throw new IllegalArgumentException("La receta no puede ser nula");
            }

            if (existeRecetaConEseID(receta.getId())) {
                throw new IllegalArgumentException("Ya existe una receta con ese ID en el sistema.");
            }

            recetas.add(receta);
            return true;

        } catch (IllegalArgumentException e) {
            System.err.println("Error al insertar receta: " + e.getMessage());
            return false;
        }
    }

    public Boolean eliminar(String idReceta) {
        if (!existeRecetaConEseID(idReceta)) {
            return false;
        }

        for (Receta receta : recetas) {
            if (receta.getId().equals(idReceta)) {
                recetas.remove(receta);
                return true;
            }
        }
        return false;
    }

    public Boolean existeRecetaConEseID(String idReceta) {
        for (Receta receta : recetas) {
            if (receta.getId().equals(idReceta)) {
                return true;
            }
        }
        return false;
    }

    public Receta getRecetaPorID(String idReceta) {
        for (Receta receta : recetas) {
            if (receta.getId().equals(idReceta)) {
                return receta;
            }
        }
        return null;
    }

    public ObservableList<Receta> obtenerRecetasPorPaciente(String idPaciente) {
        ObservableList<Receta> resultado = FXCollections.observableArrayList();
        for (Receta receta : recetas) {
            if (receta.getPaciente().getId().equals(idPaciente)) {
                resultado.add(receta);
            }
        }
        return resultado;
    }

    public ObservableList<Receta> obtenerRecetasPorMedico(String idMedico) {
        ObservableList<Receta> resultado = FXCollections.observableArrayList();
        for (Receta receta : recetas) {
            if (receta.getPersonal().getId().equals(idMedico)) {
                resultado.add(receta);
            }
        }
        return resultado;
    }

    public ObservableList<Receta> obtenerRecetasPorEstado(int estado) {
        ObservableList<Receta> resultado = FXCollections.observableArrayList();
        for (Receta receta : recetas) {
            if (receta.getEstado() == estado) {
                resultado.add(receta);
            }
        }
        return resultado;
    }
    public String mostrarTodasLasRecetas() {
        if (recetas.isEmpty()) {
            return "No hay recetas registradas.";
        }

        StringBuilder sb = new StringBuilder("=== LISTA DE RECETAS ===\n");
        for (int i = 0; i < recetas.size(); i++) {
            Receta receta = recetas.get(i);
            sb.append(i + 1)
                    .append(". ID: ").append(receta.getId())
                    .append(" | Paciente: ").append(receta.getPaciente().getNombre())
                    .append(" | Médico: ").append(receta.getPersonal().getNombre())
                    .append(" | Fecha: ").append(receta.getFechaPrescripcion())
                    .append(" | Estado: ").append(receta.obtenerNombreEstado(receta.getEstado())) // Cambiado aquí
                    .append(" | Detalles de medicamentos: ").append(receta.mostrarTodosLosDetalles())
                    .append("\n");
        }
        return sb.toString();
    }

    public String mostrarRecetaPorID(String idReceta) {
        Receta receta = getRecetaPorID(idReceta);

        if (receta == null) {
            return "No se encontró ninguna receta con el ID: " + idReceta;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== DETALLES DE LA RECETA ===\n");
        sb.append("ID: ").append(receta.getId()).append("\n");
        sb.append("Paciente: ").append(receta.getPaciente().getNombre())
                .append(" (").append(receta.getPaciente().getId()).append(")\n");
        sb.append("Médico: ").append(receta.getPersonal().getNombre())
                .append(" (").append(receta.getPersonal().getId()).append(")\n");
        sb.append("Fecha Prescripción: ").append(receta.getFechaPrescripcion()).append("\n");
        sb.append("Fecha Retiro: ").append(receta.getFechaRetiro()).append("\n");
        sb.append("Estado: ").append(receta.obtenerNombreEstado(receta.getEstado())).append("\n"); // Cambiado aquí

        // Mostrar detalles de medicamentos
        if (receta.hayMedicamentosEnLaReceta()) {
            sb.append("\n=== MEDICAMENTOS RECETADOS ===\n");
            sb.append(receta.mostrarTodosLosDetalles());
        } else {
            sb.append("\nNo hay medicamentos en esta receta.\n");
        }

        return sb.toString();
    }

    public String mostrarRecetasPorPaciente(String idPaciente) {
        ObservableList<Receta> recetasPaciente = obtenerRecetasPorPaciente(idPaciente);

        if (recetasPaciente.isEmpty()) {
            return "No hay recetas para el paciente con ID: " + idPaciente;
        }

        StringBuilder sb = new StringBuilder("=== RECETAS DEL PACIENTE ===\n");
        for (int i = 0; i < recetasPaciente.size(); i++) {
            Receta receta = recetasPaciente.get(i);
            sb.append(i + 1)
                    .append(". ID Receta: ").append(receta.getId())
                    .append(" | Médico: ").append(receta.getPersonal().getNombre())
                    .append(" | Fecha: ").append(receta.getFechaPrescripcion())
                    .append(" | Estado: ").append(receta.obtenerNombreEstado(receta.getEstado())) // Cambiado aquí
                    .append("\n");
        }
        return sb.toString();
    }

    public String mostrarRecetasPorEstado(int estado) {
        ObservableList<Receta> recetasEstado = obtenerRecetasPorEstado(estado);

        if (recetasEstado.isEmpty()) {
            return "No hay recetas con ese estado: ";
        }

        StringBuilder sb = new StringBuilder("=== RECETAS  ===\n"); // Cambiado aquí
        for (int i = 0; i < recetasEstado.size(); i++) {
            Receta receta = recetasEstado.get(i);
            sb.append(i + 1)
                    .append(". ID: ").append(receta.getId())
                    .append(" | Paciente: ").append(receta.getPaciente().getNombre())
                    .append(" | Médico: ").append(receta.getPersonal().getNombre())
                    .append(" | Fecha: ").append(receta.getFechaPrescripcion())
                    .append("\n");
        }
        return sb.toString();
    }

    public Boolean actualizarEstadoReceta(String idReceta, int nuevoEstado) {
        Receta receta = getRecetaPorID(idReceta);
        if (receta != null) {
            receta.setEstado(nuevoEstado);
            return true;
        }
        return false;
    }

    public Boolean actualizarFechaRetiro(String idReceta, java.time.LocalDate nuevaFecha) {
        Receta receta = getRecetaPorID(idReceta);
        if (receta != null) {
            receta.setFechaRetiro(nuevaFecha);
            return true;
        }
        return false;
    }

    public ObservableList<Receta> obtenerRecetasPorRangoFechas(java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin) {
        ObservableList<Receta> resultado = FXCollections.observableArrayList();
        for (Receta receta : recetas) {
            if (!receta.getFechaPrescripcion().isBefore(fechaInicio) &&
                    !receta.getFechaPrescripcion().isAfter(fechaFin)) {
                resultado.add(receta);
            }
        }
        return resultado;
    }
}