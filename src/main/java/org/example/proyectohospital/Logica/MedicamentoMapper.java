package org.example.proyectohospital.Logica;
import org.example.proyectohospital.Datos.MedicamentoEntity;
import org.example.proyectohospital.Modelo.Medicamento;

public class MedicamentoMapper {

    public static MedicamentoEntity toXML(Medicamento medicamento) {
        if (medicamento == null) {
            return null;
        }

        MedicamentoEntity medicamentoEntity = new MedicamentoEntity();
        medicamentoEntity.setCodigo(medicamento.getCodigo());
        medicamentoEntity.setNombre(medicamento.getNombre());
        medicamentoEntity.setPresentacion(medicamento.getPresentacion());

        return medicamentoEntity;
    }

    public static Medicamento toModel(MedicamentoEntity medicamentoEntity) {
        if (medicamentoEntity == null) {
            return null;
        }

        return new Medicamento(
                medicamentoEntity.getNombre(),
                medicamentoEntity.getPresentacion(),
                medicamentoEntity.getCodigo()
        );
    }
}
