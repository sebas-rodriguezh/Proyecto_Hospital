package org.example.proyectohospital.Modelo;

public class DetalleMedicamento {
    private Medicamento medicamento;
    private String idDetalle;
    private int cantidad;
    private int duracion;
    private String indicacion;

    public DetalleMedicamento(Medicamento medicamento, String idDetalle, int cantidad, int duracion, String indicacion) {
        this.medicamento = medicamento;
        this.idDetalle = idDetalle;
        this.cantidad = cantidad;
        this.duracion = duracion;
        this.indicacion = indicacion;
    }


    public DetalleMedicamento(String codigoMedicamento, String idDetalle, int cantidad, int duracion, String indicacion) {
        this.idDetalle = idDetalle;
        this.cantidad = cantidad;
        this.duracion = duracion;
        this.indicacion = indicacion;
        this.medicamento = new Medicamento(); //Acá es donde se llama al hospital.getMedicamentoPorCodigo(). Ya que para la vista se trabaja así.
    }

    public DetalleMedicamento() {

    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    public String getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(String idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
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

    @Override
    public String toString() {
        return "DetalleMedicamento{" +
                "medicamento=" + medicamento.toString() + '\n' +
                ", idDetalle='" + idDetalle + '\'' +
                ", cantidad=" + cantidad +
                ", duracion=" + duracion +
                ", indicacion='" + indicacion + '\'' +
                '}';
    }
}
