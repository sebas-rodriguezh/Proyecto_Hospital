package org.example.proyectohospital.Modelo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.List;

public class ListaPacientes {
    private ObservableList<Paciente> pacientes;

    public ListaPacientes(List<Paciente> pacientes) {
        this.pacientes = FXCollections.observableArrayList(pacientes);
    }

    public ListaPacientes() {
        this.pacientes = FXCollections.observableArrayList();
    }

    public ObservableList<Paciente> getPacientes() {
        return pacientes;
    }

    public void setPacientes(List<Paciente> pacientes) {
        this.pacientes = FXCollections.observableArrayList(pacientes);
    }

    public Boolean insertarPaciente(Paciente paciente, Boolean respuestaListaPersonal) {
        try {
            if (paciente == null) {
                throw new IllegalArgumentException("El paciente no puede ser nulo");
            }

            if (existeAlguienConEseID(paciente.getId()) || (Boolean.TRUE.equals(respuestaListaPersonal))) {
                throw new IllegalArgumentException("Existe una persona con ese ID en el sistema.");
            }

            pacientes.add(paciente);
            return true;

        } catch (IllegalArgumentException e) {
            System.err.println("Error al insertar paciente: " + e.getMessage());
            return false;
        }
    }

    public Boolean eliminar (String idPaciente) {
        if (!existeAlguienConEseID(idPaciente)) {
            return false;
        }

        for (Paciente paciente : pacientes) {
            if (paciente.getId().equals(idPaciente)) {
                pacientes.remove(paciente);
                return true;
            }
        }
        return false;
    }

    public Boolean existeAlguienConEseID (String idPaciente) {
        for (Paciente paciente : pacientes) {
            if (paciente.getId().equals(idPaciente)) {
                return true;
            }
        }
        return false;
    }

    public Paciente getPaciente(String idPaciente) {
        for (Paciente paciente : pacientes) {
            if (paciente.getId().equals(idPaciente)) {
                return paciente;
            }
        }
        return null;
    }

    public String mostrarTodosLosPacientes() {
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
