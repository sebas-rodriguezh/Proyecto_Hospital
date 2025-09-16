package org.example.proyectohospital.Datos;

import jakarta.xml.bind.*;
import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "pacientesData")
@XmlAccessorType(XmlAccessType.FIELD)
public class PacienteConector {
    @XmlElementWrapper (name = "pacientes")
    @XmlElement(name = "Paciente")

    private List<PacienteEntity> pacientes = new ArrayList<>();

    public List<PacienteEntity> getPacientes() {
        return pacientes;
    }

    public void setPacientes(List<PacienteEntity> pacientes) {
        this.pacientes = pacientes;
    }
}
