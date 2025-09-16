package org.example.proyectohospital.Datos;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class RecetaEntity {
    private String id;
    private PersonalEntity personal;
    private PacienteEntity paciente;

    private int estado;

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaPrescripcion;

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaRetiro;

    @XmlElementWrapper(name = "detallesMedicamentos")
    @XmlElement(name = "DetalleMedicamento")
    private List<DetalleMedicamentoEntity> detalleMedicamentos = new ArrayList<>();

    public RecetaEntity() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PersonalEntity getPersonal() {
        return personal;
    }

    public void setPersonal(PersonalEntity personal) {
        this.personal = personal;
    }

    public PacienteEntity getPaciente() {
        return paciente;
    }

    public void setPaciente(PacienteEntity paciente) {
        this.paciente = paciente;
    }

    public LocalDate getFechaPrescripcion() {
        return fechaPrescripcion;
    }

    public void setFechaPrescripcion(LocalDate fechaPrescripcion) {
        this.fechaPrescripcion = fechaPrescripcion;
    }

    public LocalDate getFechaRetiro() {
        return fechaRetiro;
    }

    public void setFechaRetiro(LocalDate fechaRetiro) {
        this.fechaRetiro = fechaRetiro;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public List<DetalleMedicamentoEntity> getDetalleMedicamentos() {
        return detalleMedicamentos;
    }

    public void setDetalleMedicamentos(List<DetalleMedicamentoEntity> detalleMedicamentos) {
        this.detalleMedicamentos = detalleMedicamentos;
    }
}