package org.example.proyectohospital.Datos;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "personalData")
@XmlAccessorType(XmlAccessType.FIELD)
public class PersonalConector {
    @XmlElementWrapper(name = "personal")
    @XmlElement(name = "Personal")
    private List<PersonalEntity> personal = new ArrayList<>();

    public List<PersonalEntity> getPersonal() {
        return personal;
    }

    public void setPersonal(List<PersonalEntity> personal) {
        this.personal = personal;
    }
}
