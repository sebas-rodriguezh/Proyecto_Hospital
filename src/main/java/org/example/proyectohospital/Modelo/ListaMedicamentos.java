package org.example.proyectohospital.Modelo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.ArrayList;

public class ListaMedicamentos {
    private ObservableList<Medicamento> medicamentos;

    public ListaMedicamentos(List<Medicamento> medicamentos) {
        this.medicamentos = FXCollections.observableArrayList(medicamentos);
    }

    public ListaMedicamentos() {
        this.medicamentos = FXCollections.observableArrayList();
    }

    public ObservableList<Medicamento> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<Medicamento> medicamentos) {
        this.medicamentos = FXCollections.observableArrayList(medicamentos);
    }

    public Boolean insertarMedicamento(Medicamento medicamento) {
        try {
            if (medicamento == null) {
                throw new IllegalArgumentException("El medicamento no puede ser nulo");
            }

            if (existeMedicamentoConEseCodigo(medicamento.getCodigo())) {
                throw new IllegalArgumentException("Ya existe un medicamento con ese código en el sistema.");
            }

            medicamentos.add(medicamento);
            return true;

        } catch (IllegalArgumentException e) {
            System.err.println("Error al insertar medicamento: " + e.getMessage());
            return false;
        }
    }

    public Boolean eliminar(String codigoMedicamento) {
        if (!existeMedicamentoConEseCodigo(codigoMedicamento)) {
            return false;
        }

        for (Medicamento medicamento : medicamentos) {
            if (medicamento.getCodigo().equals(codigoMedicamento)) {
                medicamentos.remove(medicamento);
                return true;
            }
        }
        return false;
    }

    public Boolean existeMedicamentoConEseCodigo(String codigoMedicamento) {
        for (Medicamento medicamento : medicamentos) {
            if (medicamento.getCodigo().equals(codigoMedicamento)) {
                return true;
            }
        }
        return false;
    }

    public Medicamento getMedicamento(String codigoMedicamento) {
        for (Medicamento medicamento : medicamentos) {
            if (medicamento.getCodigo().equals(codigoMedicamento)) {
                return medicamento;
            }
        }
        return null;
    }

    public Medicamento buscarMedicamentoPorNombre(String nombreMedicamento) {
        for (Medicamento medicamento : medicamentos) {
            if (medicamento.getNombre().equalsIgnoreCase(nombreMedicamento)) {
                return medicamento;
            }
        }
        return null;
    }
    public String mostrarTodosLosMedicamentos() {
        if (medicamentos.isEmpty()) {
            return "No hay medicamentos registrados.";
        }

        StringBuilder sb = new StringBuilder("=== LISTA DE MEDICAMENTOS ===\n");
        for (int i = 0; i < medicamentos.size(); i++) {
            Medicamento med = medicamentos.get(i);
            sb.append(i + 1)
                    .append(". Código: ").append(med.getCodigo())
                    .append(" | Nombre: ").append(med.getNombre())
                    .append(" | Presentación: ").append(med.getPresentacion())
                    .append("\n");
        }
        return sb.toString();
    }

    public String mostrarMedicamentoPorCodigo(String codigoMedicamento) {
        Medicamento medicamento = getMedicamento(codigoMedicamento);

        if (medicamento == null) {
            return "No se encontró ningún medicamento con el código: " + codigoMedicamento;
        }

        return "=== DETALLES DEL MEDICAMENTO ===\n" +
                "Código: " + medicamento.getCodigo() + "\n" +
                "Nombre: " + medicamento.getNombre() + "\n" +
                "Presentación: " + medicamento.getPresentacion();
    }

}