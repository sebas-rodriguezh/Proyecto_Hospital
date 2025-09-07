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
        recetaEntity.setIdPersonal(receta.getPersonal().getId());
        recetaEntity.setIdPaciente(receta.getPaciente().getId());
        recetaEntity.setFechaPrescripcion(receta.getFechaPrescripcion());
        recetaEntity.setFechaRetiro(receta.getFechaRetiro());
        recetaEntity.setEstado(receta.getEstado());

        // Convertir lista de detalles
        List<DetalleMedicamentoEntity> detallesEntity = new ArrayList<>();
        for (DetalleMedicamento detalle : receta.getDetalleMedicamentos()) {
            DetalleMedicamentoEntity detalleEntity = new DetalleMedicamentoEntity();
            detalleEntity.setCodigoMedicamento(detalle.getMedicamento().getCodigo());
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

        // Obtenemos el hospital para acceder a los gestores
        Hospital hospital = Hospital.getInstance();

        // Recuperamos las entidades completas usando los IDs
        Personal personal = hospital.getPersonal().getPersonalPorID(recetaEntity.getIdPersonal());
        Paciente paciente = hospital.getPacientes().getPaciente(recetaEntity.getIdPaciente());

        Receta receta = new Receta(
                recetaEntity.getId(),
                personal,
                paciente,
                recetaEntity.getFechaPrescripcion(),
                recetaEntity.getFechaRetiro(),
                recetaEntity.getEstado()
        );

        // Convertir lista de detalles
        List<DetalleMedicamento> detallesModel = new ArrayList<>();
        for (DetalleMedicamentoEntity detalleEntity : recetaEntity.getDetalleMedicamentos()) {
            // Recuperamos el medicamento completo usando el código
            Medicamento medicamento = hospital.getMedicamentos().getMedicamento(detalleEntity.getCodigoMedicamento());

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
