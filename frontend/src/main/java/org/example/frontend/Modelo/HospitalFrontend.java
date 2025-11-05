package org.example.frontend.Modelo;

import org.example.proyectohospital.Modelo.*;

public class HospitalFrontend {
    //Profe, tanto el frontend como el backend utilizan un directorio llamado shared y utilizan ambos el mismo modelo.
    //Ya que como se vió en clase el Modelo existe tanto en el frontend como en el backend.
    //Esta clase solo es para obtener instancias de logueados necesarios para el controller.
    private static HospitalFrontend instance;
    private Medico medicoLogueado;
    private Personal personalLogueado;

    private HospitalFrontend() {
    }

    public static HospitalFrontend getInstance() {
        if (instance == null) {
            instance = new HospitalFrontend();
        }
        return instance;
    }

    public Medico getMedicoLogueado() {
        return medicoLogueado;
    }

    public void setMedicoLogueado(Medico medico) {
        this.medicoLogueado = medico;
        this.personalLogueado = medico;
        System.out.println("Médico establecido en Frontend: " +
                (medico != null ? medico.getNombre() : "null"));
    }

    public void setPersonalLogueado(Personal personal) {
        this.personalLogueado = personal;
        System.out.println("Personal logueado establecido en Frontend: " +
                (personal != null ? personal.getNombre() + " (" + personal.tipo() + ")" : "null"));
    }

    public Personal getPersonalLogueado() {
        return personalLogueado;
    }

    public String getUsuarioLogueadoId() {
        if (personalLogueado != null) {
            return personalLogueado.getId();
        }
        return null;
    }

    public String getUsuarioLogueadoNombre() {
        if (personalLogueado != null) {
            return personalLogueado.getNombre();
        }
        return "Usuario";
    }
}