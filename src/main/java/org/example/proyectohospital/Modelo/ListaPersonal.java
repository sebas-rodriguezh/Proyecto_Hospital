package org.example.proyectohospital.Modelo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.ArrayList;

public class ListaPersonal {
    private ObservableList<Personal> personal;

    public ListaPersonal(List<Personal> personal) {
        this.personal = FXCollections.observableArrayList(personal);
    }

    public ListaPersonal() {
        this.personal = FXCollections.observableArrayList();
    }

    public ObservableList<Personal> getPersonal() {
        return personal;
    }

    public void setPersonal(List<Personal> personal) {
        this.personal = FXCollections.observableArrayList(personal);
    }

    public Boolean insertarPersonal(Personal persona, Boolean respuestaListaPacientes) {
        try {
            if (persona == null) {
                throw new IllegalArgumentException("El personal no puede ser nulo");
            }

            if (existePersonalConEseID(persona.getId()) || (Boolean.TRUE.equals(respuestaListaPacientes))) {
                throw new IllegalArgumentException("Existe una persona con ese ID en el sistema.");
            }

            personal.add(persona);
            return true;

        } catch (IllegalArgumentException e) {
            System.err.println("Error al insertar personal: " + e.getMessage());
            return false;
        }
    }

    public Boolean eliminar(String idPersonal) {
        if (!existePersonalConEseID(idPersonal)) {
            return false;
        }

        for (Personal persona : personal) {
            if (persona.getId().equals(idPersonal)) {
                personal.remove(persona);
                return true;
            }
        }
        return false;
    }

    public Boolean existePersonalConEseID(String idPersonal) {
        for (Personal persona : personal) {
            if (persona.getId().equals(idPersonal)) {
                return true;
            }
        }
        return false;
    }

    public Personal getPersonalPorID(String idPersonal) {
        for (Personal persona : personal) {
            if (persona.getId().equals(idPersonal)) {
                return persona;
            }
        }
        return null;
    }

    public Personal buscarPersonalPorNombre(String nombrePersonal) {
        for (Personal persona : personal) {
            if (persona.getNombre().equalsIgnoreCase(nombrePersonal)) {
                return persona;
            }
        }
        return null;
    }

    public String mostrarTodoElPersonal() {
        if (personal.isEmpty()) {
            return "No hay personal registrado.";
        }

        StringBuilder sb = new StringBuilder("=== LISTA DE PERSONAL ===\n");
        for (int i = 0; i < personal.size(); i++) {
            Personal persona = personal.get(i);
            sb.append(i + 1)
                    .append(". ID: ").append(persona.getId())
                    .append(" | Nombre: ").append(persona.getNombre())
                    .append(" | Tipo: ").append(persona.tipo());

            // Información adicional para Médicos
            if (persona instanceof Medico) {
                Medico medico = (Medico) persona;
                sb.append(" | Especialidad: ").append(medico.getEspecialidad());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String mostrarPersonalPorID(String idPersonal) {
        Personal persona = getPersonalPorID(idPersonal);

        if (persona == null) {
            return "No se encontró ningún personal con el ID: " + idPersonal;
        }

        StringBuilder sb = new StringBuilder("=== DETALLES DEL PERSONAL ===\n");
        sb.append("ID: ").append(persona.getId()).append("\n");
        sb.append("Nombre: ").append(persona.getNombre()).append("\n");
        sb.append("Tipo: ").append(persona.tipo()).append("\n");

        if (persona instanceof Medico) {
            Medico medico = (Medico) persona;
            sb.append("Especialidad: ").append(medico.getEspecialidad()).append("\n");
        } else if (persona instanceof Administrador) {
            sb.append("Rol: Administrador del sistema").append("\n");
        } else if (persona instanceof Farmaceuta) {
            sb.append("Rol: Gestión de medicamentos").append("\n");
        }

        return sb.toString();
    }

    public Personal verificarCredenciales(String id, String clave) {
        for (Personal persona : personal) {
            if (persona.getId().equals(id) && persona.getClave().equals(clave)) {
                return persona;
            }
        }
        return null;
    }

    public ObservableList<Personal> obtenerPersonalPorTipo(String tipo) {
        ObservableList<Personal> resultado = FXCollections.observableArrayList();
        for (Personal persona : personal) {
            if (persona.tipo().equalsIgnoreCase(tipo)) //Para comparar Strings la mejor opción.
            {
                resultado.add(persona);
            }
        }
        return resultado;
    }

    public String mostrarPersonalPorTipo(String tipo) {
        ObservableList<Personal> personalFiltrado = obtenerPersonalPorTipo(tipo);

        if (personalFiltrado.isEmpty()) {
            return "No hay personal del tipo: " + tipo;
        }

        StringBuilder sb = new StringBuilder("=== PERSONAL " + tipo.toUpperCase() + " ===\n");
        for (int i = 0; i < personalFiltrado.size(); i++) {
            Personal persona = personalFiltrado.get(i);
            sb.append(i + 1)
                    .append(". ID: ").append(persona.getId())
                    .append(" | Nombre: ").append(persona.getNombre());

            if (persona instanceof Medico) {
                Medico medico = (Medico) persona;
                sb.append(" | Especialidad: ").append(medico.getEspecialidad());
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}