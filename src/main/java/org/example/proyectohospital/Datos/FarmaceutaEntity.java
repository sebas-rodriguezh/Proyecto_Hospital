package org.example.proyectohospital.Datos;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class FarmaceutaEntity extends PersonalEntity {

    public FarmaceutaEntity() {}

    public FarmaceutaEntity(String id, String nombre, String clave) {
        super(id, nombre, clave);
    }

    @Override
    public String getTipo() { return "Farmaceuta"; }

}
