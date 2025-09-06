package org.example.proyectohospital.Datos;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class MedicoEntity extends PersonalEntity {
    private String especialidad;

    public MedicoEntity() {}

    public MedicoEntity(String id, String nombre, String clave, String especialidad) {
        super(id, nombre, clave);
        this.especialidad = especialidad;
    }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    @Override
    public String getTipo() { return "Medico"; }
}
