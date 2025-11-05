package org.example.backend.Logica;

import org.example.backend.Datos.*;
import org.example.proyectohospital.Modelo.*;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class GestorPersonal {
    private final PersonalDatos store;

    public GestorPersonal() {
        this.store = new PersonalDatos();
    }

    public List<Personal> findAll() {
        try {
            return store.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Error cargando personal: " + e.getMessage());
        }
    }

    public List<Personal> findByText(String texto) {
        try {
            if (texto == null || texto.trim().isEmpty()) {
                return store.findAll();
            }
            return store.findByText(texto);
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando personal: " + e.getMessage());
        }
    }

    public Personal getPersonalPorID(String idPersonal) {
        try {
            return store.findById(idPersonal);
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo personal: " + e.getMessage());
        }
    }

    public Personal buscarPersonalPorNombre(String nombrePersonal) {
        try {
            List<Personal> personal = store.findAll();
            return personal.stream()
                    .filter(p -> p.getNombre().equalsIgnoreCase(nombrePersonal))
                    .findFirst()
                    .orElse(null);
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando personal por nombre: " + e.getMessage());
        }
    }

    public Boolean existePersonalConEseID(String idPersonal) {
        try {
            return store.findById(idPersonal) != null;
        } catch (SQLException e) {
            throw new RuntimeException("Error verificando existencia: " + e.getMessage());
        }
    }

    public List<Personal> obtenerPersonalPorTipo(String tipo) {
        try {
            List<Personal> personal = store.findAll();
            return personal.stream()
                    .filter(p -> p.tipo().equalsIgnoreCase(tipo))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error filtrando personal por tipo: " + e.getMessage());
        }
    }

    public Personal verificarCredenciales(String id, String clave) {
        try {
            return store.verificarCredenciales(id, clave);
        } catch (SQLException e) {
            throw new RuntimeException("Error verificando credenciales: " + e.getMessage());
        }
    }

    public Personal create(Personal nuevo) {
        try {
            if (nuevo == null) {
                throw new IllegalArgumentException("El personal no puede ser nulo");
            }
            if (existePersonalConEseID(nuevo.getId())) {
                throw new IllegalArgumentException("Ya existe personal con ese ID");
            }
            return store.insert(nuevo);
        } catch (SQLException e) {
            throw new RuntimeException("Error creando personal: " + e.getMessage());
        }
    }

    public Personal update(Personal actualizado) {
        try {
            if (actualizado == null) {
                throw new IllegalArgumentException("El personal no puede ser nulo");
            }

            Personal result = store.update(actualizado);
            if (result != null) {
                actualizarRecetasConPersonal(actualizado);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando personal: " + e.getMessage());
        }
    }

    public Personal update(Personal actualizado, String idOriginal) {
        try {
            if (actualizado == null || idOriginal == null) {
                throw new IllegalArgumentException("El personal no puede ser nulo");
            }

            return update(actualizado);
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando personal: " + e.getMessage());
        }
    }

    private void actualizarRecetasConPersonal(Personal personalActualizado) {
        try {
            GestorRecetas gestorRecetas = Hospital.getInstance().getRecetas();
            List<Receta> recetasDelPersonal = gestorRecetas.obtenerRecetasPorMedico(personalActualizado.getId());

            for (Receta receta : recetasDelPersonal) {
                receta.setPersonal(personalActualizado);
                gestorRecetas.update(receta);
            }

            System.out.println("Actualizadas " + recetasDelPersonal.size() + " recetas del personal: " + personalActualizado.getNombre());
        } catch (Exception e) {
            System.err.println("Error actualizando recetas: " + e.getMessage());
        }
    }

    public Boolean deleteById(String idPersonal) {
        try {
            if (idPersonal == null || idPersonal.trim().isEmpty()) {
                throw new IllegalArgumentException("ID no puede ser nulo o vacío");
            }

            GestorRecetas gestorRecetas = Hospital.getInstance().getRecetas();
            List<Receta> recetasAsociadas = gestorRecetas.obtenerRecetasPorMedico(idPersonal);

            if (!recetasAsociadas.isEmpty()) {
                System.out.println("Eliminando " + recetasAsociadas.size() + " recetas asociadas al personal...");
                for (Receta receta : recetasAsociadas) {
                    gestorRecetas.deleteById(receta.getId());
                }
            }

            boolean eliminado = store.delete(idPersonal);
            if (eliminado) {
                System.out.println("Personal eliminado correctamente: " + idPersonal);
            }
            return eliminado;
        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando personal: " + e.getMessage());
        }
    }

    public Boolean insertarPersonal(Personal persona, Boolean respuestaListaPacientes) {
        try {
            if (persona == null) {
                throw new IllegalArgumentException("El personal no puede ser nulo");
            }
            if (existePersonalConEseID(persona.getId()) || (Boolean.TRUE.equals(respuestaListaPacientes))) {
                throw new IllegalArgumentException("Existe una persona con ese ID en el sistema.");
            }
            create(persona);
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
        return deleteById(idPersonal);
    }

    public List<Personal> getPersonal() {
        return findAll();
    }

    public void setPersonal(List<Personal> personal) {
        try {
            List<Personal> existentes = store.findAll();
            for (Personal pers : existentes) {
                store.delete(pers.getId());
            }

            for (Personal pers : personal) {
                store.insert(pers);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error estableciendo personal: " + e.getMessage());
        }
    }

    public String mostrarTodoElPersonal() {
        List<Personal> personal = findAll();

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

    public String mostrarPersonalPorTipo(String tipo) {
        List<Personal> personalFiltrado = obtenerPersonalPorTipo(tipo);

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

    public boolean cambiarClave(String idPersonal, String claveActual, String nuevaClave) {
        try {
            if (nuevaClave == null || nuevaClave.trim().isEmpty()) {
                System.err.println("Error: La nueva clave no puede estar vacía");
                return false;
            }

            Personal personal = verificarCredenciales(idPersonal, claveActual);
            if (personal == null) {
                System.err.println("Error: Credenciales actuales incorrectas");
                return false;
            }

            personal.setClave(nuevaClave);
            Personal actualizado = store.update(personal);

            if (actualizado != null) {
                System.out.println("Clave actualizada exitosamente para: " + personal.getNombre());
                return true;
            }

            System.err.println("Error: No se pudo actualizar la clave");
            return false;

        } catch (Exception e) {
            System.err.println("Error cambiando clave: " + e.getMessage());
            return false;
        }
    }
}