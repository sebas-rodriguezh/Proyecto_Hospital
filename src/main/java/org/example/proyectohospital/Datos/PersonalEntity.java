package org.example.proyectohospital.Datos;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({MedicoEntity.class, AdministradorEntity.class, FarmaceutaEntity.class})
public abstract class PersonalEntity {
    protected String nombre;
    protected String id;
    protected String clave;

    public PersonalEntity() {}

    public PersonalEntity(String id, String nombre, String clave) {
        this.id = id;
        this.nombre = nombre;
        this.clave = clave;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public abstract String getTipo();
}
