package org.example.proyectohospital.Logica;

import org.example.proyectohospital.Datos.*;
import org.example.proyectohospital.Modelo.*;

public class PersonalMapper {
    public static PersonalEntity toXML(Personal personal) {
        if (personal == null) {
            return null;
        }

        if (personal instanceof Medico) {
            Medico medico = (Medico) personal;
            MedicoEntity medicoEntity = new MedicoEntity();
            medicoEntity.setId(medico.getId());
            medicoEntity.setNombre(medico.getNombre());
            medicoEntity.setClave(medico.getClave());
            medicoEntity.setEspecialidad(medico.getEspecialidad());
            return medicoEntity;

        } else if (personal instanceof Administrador) {
            Administrador admin = (Administrador) personal;
            AdministradorEntity adminEntity = new AdministradorEntity();
            adminEntity.setId(admin.getId());
            adminEntity.setNombre(admin.getNombre());
            adminEntity.setClave(admin.getClave());
            return adminEntity;

        } else if (personal instanceof Farmaceuta) {
            Farmaceuta farmaceuta = (Farmaceuta) personal;
            FarmaceutaEntity farmaceutaEntity = new FarmaceutaEntity();
            farmaceutaEntity.setId(farmaceuta.getId());
            farmaceutaEntity.setNombre(farmaceuta.getNombre());
            farmaceutaEntity.setClave(farmaceuta.getClave());
            return farmaceutaEntity;
        }

        throw new IllegalArgumentException("Tipo de personal desconocido: " + personal.getClass().getSimpleName());
    }

    public static Personal toModel(PersonalEntity personalEntity) {
        if (personalEntity == null) {
            return null;
        }

        if (personalEntity instanceof MedicoEntity) {
            MedicoEntity medicoEntity = (MedicoEntity) personalEntity;
            return new Medico(
                    medicoEntity.getNombre(),
                    medicoEntity.getId(),
                    medicoEntity.getClave(),
                    medicoEntity.getEspecialidad()
            );

        } else if (personalEntity instanceof AdministradorEntity) {
            AdministradorEntity adminEntity = (AdministradorEntity) personalEntity;
            return new Administrador(
                    adminEntity.getNombre(),
                    adminEntity.getId(),
                    adminEntity.getClave()
            );

        } else if (personalEntity instanceof FarmaceutaEntity) {
            FarmaceutaEntity farmaceutaEntity = (FarmaceutaEntity) personalEntity;
            return new Farmaceuta(
                    farmaceutaEntity.getNombre(),
                    farmaceutaEntity.getId(),
                    farmaceutaEntity.getClave()
            );
        }

        throw new IllegalArgumentException("Tipo de PersonalEntity desconocido: " + personalEntity.getClass().getSimpleName());
    }
}
