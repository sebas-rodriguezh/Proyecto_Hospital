package org.example.proyectohospital.Datos;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class DetalleMedicamentoEntity {
    // CAMBIO: En lugar de solo el código, guardamos la entidad completa
    private MedicamentoEntity medicamento; // Entidad completa
    private String idDetalle;
    private int cantidad;
    private int duracion;
    private String indicacion;

    public DetalleMedicamentoEntity() {}

    public MedicamentoEntity getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(MedicamentoEntity medicamento) {
        this.medicamento = medicamento;
    }

    public String getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(String idDetalle) {
        this.idDetalle = idDetalle;
    }

    public String getIndicacion() {
        return indicacion;
    }

    public void setIndicacion(String indicacion) {
        this.indicacion = indicacion;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}