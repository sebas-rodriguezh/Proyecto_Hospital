package org.example.proyectohospital.Modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Receta implements Serializable {
    private static final long serialVersionUID = 1L;

    private Personal personal;
    private Paciente paciente;
    private LocalDate fechaPrescripcion;
    private LocalDate fechaRetiro;
    private int estado;// 1: Procesada - 2: Confeccionada - 3: Lista - 4: Entregada
    private List<DetalleMedicamento> detalleMedicamentos;
    private String id;

    public Receta(String id, Personal personal, Paciente paciente, LocalDate fechaPrescripcion, LocalDate fechaRetiro, int estado) {
        this.id = id;
        this.personal = personal;
        this.paciente = paciente;
        this.fechaPrescripcion = fechaPrescripcion;
        this.fechaRetiro = fechaRetiro;
        this.estado = estado;
        this.detalleMedicamentos = new ArrayList<>();
    }

    public Receta(String id, LocalDate fechaPrescripcion, LocalDate fechaRetiro, int estado) {
        this.id = id;
        this.fechaPrescripcion = fechaPrescripcion;
        this.fechaRetiro = fechaRetiro;
        this.estado = estado;
        personal = new Medico();
        paciente = new Paciente();
        detalleMedicamentos = new ArrayList<>();
    }

    public Receta(String id, String idPaciente, String idMedico, LocalDate fechaPrescripcion, LocalDate fechaRetiro, int estado) {
        this.id = id;
        this.fechaPrescripcion = fechaPrescripcion;
        this.fechaRetiro = fechaRetiro;
        this.estado = estado;
        personal = new Medico();
        paciente = new Paciente();
        detalleMedicamentos = new ArrayList<>();
    }

    public Receta() {
        personal = new Medico();
        paciente = new Paciente();
        detalleMedicamentos = new ArrayList<>();
    }


    public Personal getPersonal() {
        return personal;
    }

    public void setPersonal(Personal personal) {
        this.personal = personal;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getNombrePaciente() {return (paciente != null) ? paciente.getNombre() : "" ;}

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

    public List<DetalleMedicamento> getDetalleMedicamentos() {
        return detalleMedicamentos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDetalleMedicamentos(List<DetalleMedicamento> detalleMedicamentos) {
        this.detalleMedicamentos = detalleMedicamentos;
    }

    public void agregarDetalleMedicamento(DetalleMedicamento detalle) {
        if (this.detalleMedicamentos == null) {
            this.detalleMedicamentos = new ArrayList<>();
        }
        this.detalleMedicamentos.add(detalle);
    }

    public List<DetalleMedicamento> getDetallesMedicamentos() {
        return detalleMedicamentos != null ? detalleMedicamentos : new ArrayList<>();
    }
    public LocalDate getFechaConfeccion() {
        return this.fechaPrescripcion;
    }

    public String getFechaNacimiento() {
        return (paciente != null && paciente.getFechaNacimiento() != null) ?
                paciente.getFechaNacimiento().toString() : "";
    }

    public String getNombreEstado(int estado) {
        switch (estado) {
            case 1: return "Confeccionada";
            case 2: return "Procesada";
            case 3: return "Lista";
            case 4: return "Entregada";
            default: return "Desconocido";
        }
    }

    public String getNombreEstado() {
        return getNombreEstado(this.estado);
    }




}
