package org.example.proyectohospital.Logica;

import org.example.proyectohospital.Datos.*;
import org.example.proyectohospital.Modelo.*;
import java.util.ArrayList;
import java.util.List;

public class RecetaMapper {
    public static RecetaEntity toXML(Receta receta) {
        if (receta == null) {
            return null;
        }

        RecetaEntity recetaEntity = new RecetaEntity();
        recetaEntity.setId(receta.getId());
        recetaEntity.setPersonal(PersonalMapper.toXML(receta.getPersonal()));
        recetaEntity.setPaciente(PacienteMapper.toXML(receta.getPaciente()));

        recetaEntity.setFechaPrescripcion(receta.getFechaPrescripcion());
        recetaEntity.setFechaRetiro(receta.getFechaRetiro());
        recetaEntity.setEstado(receta.getEstado());

        List<DetalleMedicamentoEntity> detallesEntity = new ArrayList<>();
        for (DetalleMedicamento detalle : receta.getDetalleMedicamentos()) {
            DetalleMedicamentoEntity detalleEntity = new DetalleMedicamentoEntity();

            detalleEntity.setMedicamento(MedicamentoMapper.toXML(detalle.getMedicamento()));

            detalleEntity.setIdDetalle(detalle.getIdDetalle());
            detalleEntity.setCantidad(detalle.getCantidad());
            detalleEntity.setDuracion(detalle.getDuracion());
            detalleEntity.setIndicacion(detalle.getIndicacion());
            detallesEntity.add(detalleEntity);
        }
        recetaEntity.setDetalleMedicamentos(detallesEntity);

        return recetaEntity;
    }

    public static Receta toModel(RecetaEntity recetaEntity) {
        if (recetaEntity == null) {
            return null;
        }

        Personal personal = PersonalMapper.toModel(recetaEntity.getPersonal());
        Paciente paciente = PacienteMapper.toModel(recetaEntity.getPaciente());

        Receta receta = new Receta(
                recetaEntity.getId(),
                personal,
                paciente,
                recetaEntity.getFechaPrescripcion(),
                recetaEntity.getFechaRetiro(),
                recetaEntity.getEstado()
        );

        List<DetalleMedicamento> detallesModel = new ArrayList<>();
        for (DetalleMedicamentoEntity detalleEntity : recetaEntity.getDetalleMedicamentos()) {
            Medicamento medicamento = MedicamentoMapper.toModel(detalleEntity.getMedicamento());

            DetalleMedicamento detalle = new DetalleMedicamento(
                    medicamento,
                    detalleEntity.getIdDetalle(),
                    detalleEntity.getCantidad(),
                    detalleEntity.getDuracion(),
                    detalleEntity.getIndicacion()
            );
            detallesModel.add(detalle);
        }
        receta.setDetalleMedicamentos(detallesModel);

        return receta;
    }
}