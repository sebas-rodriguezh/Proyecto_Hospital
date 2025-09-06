package org.example.proyectohospital.Datos;

import jakarta.xml.bind.*;
import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "medicamentosData")
@XmlAccessorType(XmlAccessType.FIELD)
public class MedicamentoConector {
    @XmlElementWrapper(name = "medicamentos")
    @XmlElement(name = "Medicamento")
    private List<MedicamentoEntity> medicamentos = new ArrayList<>();

    public List<MedicamentoEntity> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<MedicamentoEntity> medicamentos) {
        this.medicamentos = medicamentos;
    }
}
