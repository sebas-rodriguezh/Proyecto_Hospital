package org.example.proyectohospital.Logica;

import org.example.proyectohospital.Datos.*;
import org.example.proyectohospital.Modelo.*;
import java.util.List;
import java.util.stream.Collectors;

public class GestorPersonal {
    private final PersonalDatos store;

    public GestorPersonal(String rutaArchivo) {
        this.store = new PersonalDatos(rutaArchivo);
    }

    public List<Personal> findAll() {
        PersonalConector data = store.load();
        return data.getPersonal().stream()
                .map(PersonalMapper::toModel)
                .collect(Collectors.toList());
    }

    public List<Personal> findByText(String texto) {
        PersonalConector data = store.load();
        if (texto == null || texto.trim().isEmpty()) {
            return data.getPersonal().stream()
                    .map(PersonalMapper::toModel)
                    .collect(Collectors.toList());
        }

        String textoBusqueda = texto.toLowerCase().trim();
        return data.getPersonal().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(textoBusqueda) ||
                        p.getId().toLowerCase().contains(textoBusqueda) ||
                        p.getTipo().toLowerCase().contains(textoBusqueda))
                .map(PersonalMapper::toModel)
                .collect(Collectors.toList());
    }

    public Personal getPersonalPorID(String idPersonal) {
        PersonalConector data = store.load();
        return data.getPersonal().stream()
                .filter(p -> p.getId().equals(idPersonal))
                .map(PersonalMapper::toModel)
                .findFirst()
                .orElse(null);
    }

    public Personal buscarPersonalPorNombre(String nombrePersonal) {
        PersonalConector data = store.load();
        return data.getPersonal().stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombrePersonal))
                .map(PersonalMapper::toModel)
                .findFirst()
                .orElse(null);
    }

    public Boolean existePersonalConEseID(String idPersonal) {
        PersonalConector data = store.load();
        return data.getPersonal().stream()
                .anyMatch(p -> p.getId().equals(idPersonal));
    }

    public List<Personal> obtenerPersonalPorTipo(String tipo) {
        PersonalConector data = store.load();
        return data.getPersonal().stream()
                .filter(p -> p.getTipo().equalsIgnoreCase(tipo))
                .map(PersonalMapper::toModel)
                .collect(Collectors.toList());
    }

    public Personal verificarCredenciales(String id, String clave) {
        PersonalConector data = store.load();
        return data.getPersonal().stream()
                .filter(p -> p.getId().equals(id) && p.getClave().equals(clave))
                .map(PersonalMapper::toModel)
                .findFirst()
                .orElse(null);
    }

    public Personal create(Personal nuevo) {
        try {
            if (nuevo == null) {
                throw new IllegalArgumentException("El personal no puede ser nulo");
            }

            PersonalConector data = store.load();

            if (existePersonalConEseID(nuevo.getId())) {
                throw new IllegalArgumentException("Ya existe personal con ese ID");
            }

            PersonalEntity personalEntity = PersonalMapper.toXML(nuevo);
            data.getPersonal().add(personalEntity);
            store.save(data);

            return nuevo;
        } catch (Exception e) {
            throw new RuntimeException("Error creando personal: " + e.getMessage());
        }
    }

    public Personal update(Personal actualizado) {
        try {
            if (actualizado == null) {
                throw new IllegalArgumentException("El personal no puede ser nulo");
            }

            PersonalConector data = store.load();
            GestorRecetas gestorRecetas = Hospital.getInstance().getRecetas();

            for (int i = 0; i < data.getPersonal().size(); i++) {
                PersonalEntity actual = data.getPersonal().get(i);
                if (actual.getId().equals(actualizado.getId())) {
                    data.getPersonal().set(i, PersonalMapper.toXML(actualizado));
                    store.save(data);
                    actualizarRecetasConPersonal(actualizado, gestorRecetas);
                    return actualizado;
                }
            }

            throw new IllegalArgumentException("Personal no encontrado con ID: " + actualizado.getId());
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando personal: " + e.getMessage());
        }
    }

    public Personal update(Personal actualizado, String idOriginal) {
        try {
            if (actualizado == null || idOriginal == null) {
                throw new IllegalArgumentException("El personal no puede ser nulo");
            }

            PersonalConector data = store.load();
            GestorRecetas gestorRecetas = Hospital.getInstance().getRecetas();

            for (int i = 0; i < data.getPersonal().size(); i++) {
                PersonalEntity actual = data.getPersonal().get(i);
                if (actual.getId().equals(idOriginal)) {
                    data.getPersonal().set(i, PersonalMapper.toXML(actualizado));
                    store.save(data);
                    actualizarRecetasConPersonal(actualizado, gestorRecetas);
                    return actualizado;
                }
            }

            throw new IllegalArgumentException("Personal no encontrado con ID: " + actualizado.getId());
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando personal: " + e.getMessage());
        }
    }





    private void actualizarRecetasConPersonal(Personal personalActualizado, GestorRecetas gestorRecetas) {
        try {
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

            PersonalConector data = store.load();
            boolean eliminado = data.getPersonal().removeIf(personal -> personal.getId().equals(idPersonal));

            if (eliminado) {
                store.save(data);
                System.out.println("Personal eliminado correctamente: " + idPersonal);
            }

            return eliminado;
        } catch (Exception e) {
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
            PersonalConector data = store.load();
            List<PersonalEntity> entities = personal.stream()
                    .map(PersonalMapper::toXML)
                    .collect(Collectors.toList());

            data.setPersonal(entities);
            store.save(data);
        } catch (Exception e) {
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

            PersonalConector data = store.load();
            for (int i = 0; i < data.getPersonal().size(); i++) {
                PersonalEntity actual = data.getPersonal().get(i);
                if (actual.getId().equals(idPersonal)) {
                    data.getPersonal().set(i, PersonalMapper.toXML(personal));
                    store.save(data);
                    System.out.println("Clave actualizada exitosamente para: " + personal.getNombre());
                    return true;
                }
            }

            System.err.println("Error: No se encontró el personal con ID: " + idPersonal);
            return false;

        } catch (Exception e) {
            System.err.println("Error cambiando clave: " + e.getMessage());
            return false;
        }
    }
}