package org.example.proyectohospital.Modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Receta {
    private Personal personal; //Una receta tiene un único médico que la prescribe.
    private Paciente paciente; //Una receta tiene un único paciente a quién se le receta.
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
        personal = new Medico(); //Acá se referenciaría con el hotel el medico con el idMedico.
        paciente = new Paciente(); //Acá se referenciaría con el hotel el medico con el idPaciente.
        detalleMedicamentos = new ArrayList<>();
    }

    public Receta() {
        //Inicializar referencias.
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


//    public String mostrarTodosLosDetalles() {
//        StringBuilder sb = new StringBuilder();
//        for (DetalleMedicamento detalle : detalleMedicamentos) {
//            sb.append(detalle.toString()).append("\n");
//        }
//        return sb.toString();
//    }
//
//    public String obtenerNombreEstado(int estado) {
//        switch (estado) {
//            case 1: return "Procesada";
//            case 2: return "Confeccionada";
//            case 3: return "Lista";
//            case 4: return "Entregada";
//            default: return "Desconocido";
//        }
//    }
//
//    @Override
//    public String toString() {
//        String estadoStr = obtenerNombreEstado(1);
//        return "Receta {" + '\n' +
//                "id='" + id + '\n' +
//                ", personal=" + personal.toString() + '\n' +
//                ", paciente=" + paciente.toString() + '\n' +
//                ", fechaPrescripcion=" + fechaPrescripcion + '\n' +
//                ", fechaRetiro=" + fechaRetiro + '\n' +
//                ", estado=" + estadoStr + '\n' +
//                ", detalleMedicamentos=" + this.mostrarTodosLosDetalles() + '\n' +
//                '}';
//    }

}
