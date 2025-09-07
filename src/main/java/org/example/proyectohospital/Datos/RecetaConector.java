package org.example.proyectohospital.Datos;

import jakarta.xml.bind.*;
import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "recetasData")
@XmlAccessorType(XmlAccessType.FIELD)
public class RecetaConector {
    @XmlElementWrapper(name = "recetas")
    @XmlElement(name = "Receta")

    private List<RecetaEntity> recetas = new ArrayList<>();

    public List<RecetaEntity> getRecetas() {
        return recetas;
    }

    public void setRecetas(List<RecetaEntity> recetas) {
        this.recetas = recetas;
    }
}
