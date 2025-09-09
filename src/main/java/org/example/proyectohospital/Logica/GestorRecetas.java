package org.example.proyectohospital.Logica;

import org.example.proyectohospital.Datos.*;
import org.example.proyectohospital.Modelo.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class GestorRecetas {
    // ÚNICO atributo - referencia a la fuente de datos
    private final RecetaDatos store;

    public GestorRecetas(String rutaArchivo) {
        this.store = new RecetaDatos(rutaArchivo);
    }

    // === MÉTODOS DE LECTURA ===

    public List<Receta> findAll() {
        RecetaConector data = store.load();
        return data.getRecetas().stream()
                .map(RecetaMapper::toModel)
                .collect(Collectors.toList());
    }

    public List<Receta> findByText(String texto) {
        RecetaConector data = store.load();
        if (texto == null || texto.trim().isEmpty()) {
            return data.getRecetas().stream()
                    .map(RecetaMapper::toModel)
                    .collect(Collectors.toList());
        }

        String textoBusqueda = texto.toLowerCase().trim();
        return data.getRecetas().stream()
                .filter(r -> r.getId().toLowerCase().contains(textoBusqueda) ||
                        r.getPaciente().getId().toLowerCase().contains(textoBusqueda) ||
                        r.getPersonal().getId().toLowerCase().contains(textoBusqueda))
                .map(RecetaMapper::toModel)
                .collect(Collectors.toList());
    }

    public Receta getRecetaPorID(String idReceta) {
        RecetaConector data = store.load();
        return data.getRecetas().stream()
                .filter(r -> r.getId().equals(idReceta))
                .map(RecetaMapper::toModel)
                .findFirst()
                .orElse(null);
    }

    public boolean existeRecetaConEseID(String idReceta) {
        RecetaConector data = store.load();
        return data.getRecetas().stream()
                .anyMatch(r -> r.getId().equals(idReceta));
    }

    public List<Receta> obtenerRecetasPorPaciente(String idPaciente) {
        RecetaConector data = store.load();
        return data.getRecetas().stream()
                .filter(r -> r.getPaciente().getId().equals(idPaciente))
                .map(RecetaMapper::toModel)
                .collect(Collectors.toList());
    }

    public List<Receta> obtenerRecetasPorMedico(String idMedico) {
        RecetaConector data = store.load();
        return data.getRecetas().stream()
                .filter(r -> r.getPersonal().getId().equals(idMedico))
                .map(RecetaMapper::toModel)
                .collect(Collectors.toList());
    }

    public List<Receta> obtenerRecetasPorEstado(int estado) {
        RecetaConector data = store.load();
        return data.getRecetas().stream()
                .filter(r -> r.getEstado() == estado)
                .map(RecetaMapper::toModel)
                .collect(Collectors.toList());
    }

    public List<Receta> obtenerRecetasPorRangoFechas(LocalDate inicio, LocalDate fin) {
        RecetaConector data = store.load();
        return data.getRecetas().stream()
                .filter(r -> !r.getFechaPrescripcion().isBefore(inicio) &&
                        !r.getFechaPrescripcion().isAfter(fin))
                .map(RecetaMapper::toModel)
                .collect(Collectors.toList());
    }

    // === MÉTODOS DE ESCRITURA ===

    public Receta create(Receta nueva) {
        try {
            if (nueva == null) {
                throw new IllegalArgumentException("La receta no puede ser nula");
            }

            if (nueva.getId() == null || nueva.getId().trim().isEmpty()) {
                throw new IllegalArgumentException("El ID de la receta no puede estar vacío");
            }

            RecetaConector data = store.load();

            // Validación usando método existente
            if (existeRecetaConEseID(nueva.getId())) {
                throw new IllegalArgumentException("Ya existe una receta con ese ID");
            }

            // Agregar al XML
            RecetaEntity recetaEntity = RecetaMapper.toXML(nueva);
            data.getRecetas().add(recetaEntity);
            store.save(data);

            return nueva;
        } catch (Exception e) {
            throw new RuntimeException("Error creando receta: " + e.getMessage());
        }
    }

    public Receta update(Receta actualizada) {
        try {
            if (actualizada == null || actualizada.getId() == null) {
                throw new IllegalArgumentException("Receta o ID no pueden ser nulos");
            }

            RecetaConector data = store.load();

            for (int i = 0; i < data.getRecetas().size(); i++) {
                RecetaEntity actual = data.getRecetas().get(i);
                if (actual.getId().equals(actualizada.getId())) {
                    // Encontramos la receta a modificar y aplicamos los cambios
                    data.getRecetas().set(i, RecetaMapper.toXML(actualizada));
                    store.save(data);
                    return actualizada;
                }
            }

            throw new IllegalArgumentException("Receta no encontrada con ID: " + actualizada.getId());
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando receta: " + e.getMessage());
        }
    }

    public Boolean deleteById(String idReceta) {
        try {
            if (idReceta == null || idReceta.trim().isEmpty()) {
                throw new IllegalArgumentException("ID no puede ser nulo o vacío");
            }

            RecetaConector data = store.load();
            boolean eliminado = data.getRecetas().removeIf(receta -> receta.getId().equals(idReceta));

            if (eliminado) {
                store.save(data);
            }

            return eliminado;
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando receta: " + e.getMessage());
        }
    }

    // === MÉTODOS ORIGINALES ADAPTADOS ===

    public boolean insertarReceta(Receta receta) {
        try {
            if (receta == null || existeRecetaConEseID(receta.getId())) {
                return false;
            }

            create(receta);
            return true;
        } catch (Exception e) {
            System.err.println("Error al insertar receta: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String idReceta) {
        return deleteById(idReceta);
    }

    public boolean actualizarEstadoReceta(String idReceta, int nuevoEstado) {
        try {
            Receta receta = getRecetaPorID(idReceta);
            if (receta != null) {
                receta.setEstado(nuevoEstado);
                update(receta);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error actualizando estado: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarFechaRetiro(String idReceta, LocalDate nuevaFecha) {
        try {
            Receta receta = getRecetaPorID(idReceta);
            if (receta != null) {
                receta.setFechaRetiro(nuevaFecha);
                update(receta);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error actualizando fecha retiro: " + e.getMessage());
            return false;
        }
    }

    // === MÉTODOS SOBRE DETALLES DE MEDICAMENTOS ===

    public boolean agregarDetalle(String idReceta, DetalleMedicamento detalle) {
        try {
            Receta receta = getRecetaPorID(idReceta);
            if (receta == null) return false;

            // Verificar si ya existe el medicamento
            for (DetalleMedicamento d : receta.getDetalleMedicamentos()) {
                if (d.getMedicamento().getCodigo().equals(detalle.getMedicamento().getCodigo())) {
                    return false;
                }
            }

            receta.getDetalleMedicamentos().add(detalle);
            update(receta);
            return true;
        } catch (Exception e) {
            System.err.println("Error agregando detalle: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarDetalle(String idReceta, String codigoMedicamento) {
        try {
            Receta receta = getRecetaPorID(idReceta);
            if (receta == null) return false;

            boolean eliminado = receta.getDetalleMedicamentos().removeIf(
                    d -> d.getMedicamento().getCodigo().equals(codigoMedicamento));

            if (eliminado) {
                update(receta);
            }

            return eliminado;
        } catch (Exception e) {
            System.err.println("Error eliminando detalle: " + e.getMessage());
            return false;
        }
    }

    public boolean modificarCantidad(String idReceta, String codigoMedicamento, int nuevaCantidad) {
        try {
            Receta receta = getRecetaPorID(idReceta);
            if (receta == null) return false;

            for (DetalleMedicamento detalle : receta.getDetalleMedicamentos()) {
                if (detalle.getMedicamento().getCodigo().equals(codigoMedicamento)) {
                    detalle.setCantidad(nuevaCantidad);
                    update(receta);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error modificando cantidad: " + e.getMessage());
            return false;
        }
    }

    public boolean modificarDuracion(String idReceta, String codigoMedicamento, int nuevaDuracion) {
        try {
            Receta receta = getRecetaPorID(idReceta);
            if (receta == null) return false;

            for (DetalleMedicamento detalle : receta.getDetalleMedicamentos()) {
                if (detalle.getMedicamento().getCodigo().equals(codigoMedicamento)) {
                    detalle.setDuracion(nuevaDuracion);
                    update(receta);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error modificando duración: " + e.getMessage());
            return false;
        }
    }

    public boolean modificarIndicacion(String idReceta, String codigoMedicamento, String nuevaIndicacion) {
        try {
            Receta receta = getRecetaPorID(idReceta);
            if (receta == null) return false;

            // Buscar el detalle específico dentro de la receta
            for (DetalleMedicamento detalle : receta.getDetalleMedicamentos()) {
                if (detalle.getMedicamento().getCodigo().equals(codigoMedicamento)) {
                    detalle.setIndicacion(nuevaIndicacion);
                    update(receta);  // ✅ Ahora actualizamos la receta correcta
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error modificando indicación: " + e.getMessage());
            return false;
        }
    }

    public DetalleMedicamento getDetalle(String idReceta, String codigoMedicamento) {
        Receta receta = getRecetaPorID(idReceta);
        if (receta == null) return null;

        return receta.getDetalleMedicamentos().stream()
                .filter(d -> d.getMedicamento().getCodigo().equals(codigoMedicamento))
                .findFirst()
                .orElse(null);
    }

    public boolean hayDetalles(String idReceta) {
        Receta receta = getRecetaPorID(idReceta);
        return receta != null && !receta.getDetalleMedicamentos().isEmpty();
    }

    public String obtenerNombreEstado(int estado) {
        return switch (estado) {
            case 1 -> "Procesada";
            case 2 -> "Confeccionada";
            case 3 -> "Lista";
            case 4 -> "Entregada";
            default -> "Desconocido";
        };
    }

    public List<Receta> getRecetas() {
        return findAll();
    }

    public void setRecetas(List<Receta> recetas) {
        try {
            // Reemplazar todo el contenido del XML
            RecetaConector data = store.load();
            List<RecetaEntity> entities = recetas.stream()
                    .map(RecetaMapper::toXML)
                    .collect(Collectors.toList());

            data.setRecetas(entities);
            store.save(data);
        } catch (Exception e) {
            throw new RuntimeException("Error estableciendo recetas: " + e.getMessage());
        }
    }

    public String mostrarTodasLasRecetas() {
        List<Receta> recetas = findAll();

        if (recetas.isEmpty()) return "No hay recetas registradas.";

        StringBuilder sb = new StringBuilder("=== LISTA DE TODAS LAS RECETAS ===\n");
        for (Receta r : recetas) {
            sb.append("ID: ").append(r.getId()).append("\n")
                    .append("Paciente: ").append(r.getPaciente().getNombre()).append(" (").append(r.getPaciente().getId()).append(")\n")
                    .append("Médico: ").append(r.getPersonal().getNombre()).append(" (").append(r.getPersonal().getId()).append(")\n")
                    .append("Fecha Prescripción: ").append(r.getFechaPrescripcion()).append("\n")
                    .append("Fecha Retiro: ").append(r.getFechaRetiro()).append("\n")
                    .append("Estado: ").append(obtenerNombreEstado(r.getEstado())).append("\n");

            if (!r.getDetalleMedicamentos().isEmpty()) {
                sb.append("Detalles:\n");
                for (DetalleMedicamento d : r.getDetalleMedicamentos()) {
                    sb.append("- ").append(d.toString()).append("\n");
                }
            } else {
                sb.append("No hay medicamentos en esta receta.\n");
            }

            sb.append("--------------------------------------------------\n");
        }
        return sb.toString();
    }


}