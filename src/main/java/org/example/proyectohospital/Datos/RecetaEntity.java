package org.example.proyectohospital.Datos;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class RecetaEntity {
    private String id;
    private String idPersonal; // Solo el ID del personal
    private String idPaciente; // Solo el ID del paciente
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

    public String getIdPersonal() {
        return idPersonal;
    }

    public void setIdPersonal(String idPersonal) {
        this.idPersonal = idPersonal;
    }

    public String getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(String idPaciente) {
        this.idPaciente = idPaciente;
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
