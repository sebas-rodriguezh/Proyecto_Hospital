package org.example.proyectohospital.Logica;

import org.example.proyectohospital.Modelo.Medicamento;

import java.util.ArrayList;
import java.util.List;

public class GestorMedicamentos {
    private List<Medicamento> medicamentos;

    public GestorMedicamentos(List<Medicamento> medicamentos) {
        this.medicamentos = new ArrayList<>(medicamentos);
    }

    public GestorMedicamentos() {
        this.medicamentos = new ArrayList<>();
    }

    public List<Medicamento> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<Medicamento> medicamentos) {
        this.medicamentos = new ArrayList<>(medicamentos);
    }

    public boolean insertarMedicamento(Medicamento medicamento) {
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

    public boolean eliminar(String codigoMedicamento) {
        return medicamentos.removeIf(m -> m.getCodigo().equals(codigoMedicamento));
    }

    public boolean existeMedicamentoConEseCodigo(String codigoMedicamento) {
        return medicamentos.stream().anyMatch(m -> m.getCodigo().equals(codigoMedicamento));
    }

    public Medicamento getMedicamento(String codigoMedicamento) {
        return medicamentos.stream()
                .filter(m -> m.getCodigo().equals(codigoMedicamento))
                .findFirst()
                .orElse(null);
    }

    public Medicamento buscarMedicamentoPorNombre(String nombreMedicamento) {
        return medicamentos.stream()
                .filter(m -> m.getNombre().equalsIgnoreCase(nombreMedicamento))
                .findFirst()
                .orElse(null);
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