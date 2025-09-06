package org.example.proyectohospital.Logica;
import org.example.proyectohospital.Datos.PacienteEntity;
import org.example.proyectohospital.Modelo.Paciente;

public class PacienteMapper {

    public static PacienteEntity toXML (Paciente paciente) {
        if (paciente == null) {
            return null;
        }

        PacienteEntity pacienteEntity = new PacienteEntity();
        pacienteEntity.setId(paciente.getId());
        pacienteEntity.setNombre(paciente.getNombre());
        pacienteEntity.setFechaNacimiento(paciente.getFechaNacimiento());
        pacienteEntity.setTelefono(paciente.getTelefono());

       return pacienteEntity;
    }

    public static Paciente toModel (PacienteEntity pacienteEntity) {
        if (pacienteEntity == null) {
            return null;
        }
        Paciente paciente = new Paciente(pacienteEntity.getTelefono(),pacienteEntity.getFechaNacimiento(), pacienteEntity.getNombre(), pacienteEntity.getId());
        return paciente;
    }
}
