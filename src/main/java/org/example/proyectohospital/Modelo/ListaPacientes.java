package org.example.proyectohospital.Modelo;

import java.util.ArrayList;
import java.util.List;

public class ListaPacientes {
    private List<Paciente> pacientes;

    public ListaPacientes(List<Paciente> pacientes) {
        this.pacientes = pacientes;
    }
    public ListaPacientes() {
        this.pacientes = new ArrayList<>();
    }

    public List<Paciente> getPacientes() {
        return pacientes;
    }

    public void setPacientes(List<Paciente> pacientes) {
        this.pacientes = pacientes;
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


}
