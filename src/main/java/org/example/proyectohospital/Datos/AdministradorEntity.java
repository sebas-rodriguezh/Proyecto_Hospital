package org.example.proyectohospital.Datos;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class AdministradorEntity extends PersonalEntity {

    public AdministradorEntity() {}

    public AdministradorEntity(String id, String nombre, String clave) {
        super(id, nombre, clave);
    }

    @Override
    public String getTipo() { return "Administrador"; }
}
