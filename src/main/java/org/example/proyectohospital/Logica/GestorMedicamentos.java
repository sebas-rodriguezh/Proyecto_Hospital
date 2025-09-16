package org.example.proyectohospital.Logica;

import org.example.proyectohospital.Modelo.DetalleMedicamento;
import org.example.proyectohospital.Modelo.Medicamento;
import org.example.proyectohospital.Datos.*;
import org.example.proyectohospital.Modelo.Receta;

import java.util.List;
import java.util.stream.Collectors;

public class GestorMedicamentos {

    private final MedicamentoDatos store;

    public GestorMedicamentos(String rutaArchivo) {
        this.store = new MedicamentoDatos(rutaArchivo);
    }

    public List<Medicamento> findAll() {
        MedicamentoConector data = store.load();
        return data.getMedicamentos().stream()
                .map(MedicamentoMapper::toModel)
                .collect(Collectors.toList());
    }

    public List<Medicamento> findByText(String texto) {
        MedicamentoConector data = store.load();
        if (texto == null || texto.trim().isEmpty()) {
            return data.getMedicamentos().stream()
                    .map(MedicamentoMapper::toModel)
                    .collect(Collectors.toList());
        }

        String textoBusqueda = texto.toLowerCase().trim();
        return data.getMedicamentos().stream()
                .filter(m -> m.getNombre().toLowerCase().contains(textoBusqueda) ||
                        m.getCodigo().toLowerCase().contains(textoBusqueda) ||
                        m.getPresentacion().toLowerCase().contains(textoBusqueda))
                .map(MedicamentoMapper::toModel)
                .collect(Collectors.toList());
    }

    public Medicamento getMedicamento(String codigoMedicamento) {
        MedicamentoConector data = store.load();
        return data.getMedicamentos().stream()
                .filter(m -> m.getCodigo().equals(codigoMedicamento))
                .map(MedicamentoMapper::toModel)
                .findFirst()
                .orElse(null);
    }

    public Medicamento buscarMedicamentoPorNombre(String nombreMedicamento) {
        MedicamentoConector data = store.load();
        return data.getMedicamentos().stream()
                .filter(m -> m.getNombre().equalsIgnoreCase(nombreMedicamento))
                .map(MedicamentoMapper::toModel)
                .findFirst()
                .orElse(null);
    }

    public boolean existeMedicamentoConEseCodigo(String codigoMedicamento) {
        MedicamentoConector data = store.load();
        return data.getMedicamentos().stream()
                .anyMatch(m -> m.getCodigo().equals(codigoMedicamento));
    }

    //ESCRITURA

    public Medicamento create(Medicamento nuevo) {
        try {
            if (nuevo == null) {
                throw new IllegalArgumentException("El medicamento no puede ser nulo");
            }

            if (nuevo.getCodigo() == null || nuevo.getCodigo().trim().isEmpty()) {
                throw new IllegalArgumentException("El código del medicamento no puede estar vacío");
            }

            MedicamentoConector data = store.load();

            // Validación usando método existente
            if (existeMedicamentoConEseCodigo(nuevo.getCodigo())) {
                throw new IllegalArgumentException("Ya existe un medicamento con ese código");
            }

            // Agregar al XML
            MedicamentoEntity medicamentoEntity = MedicamentoMapper.toXML(nuevo);
            data.getMedicamentos().add(medicamentoEntity);
            store.save(data);

            return nuevo;
        } catch (Exception e) {
            throw new RuntimeException("Error creando medicamento: " + e.getMessage());
        }
    }

    public Medicamento update(Medicamento actualizado) {
        try {
            if (actualizado == null || actualizado.getCodigo() == null) {
                throw new IllegalArgumentException("Medicamento o código no pueden ser nulos");
            }

            MedicamentoConector data = store.load();
            GestorRecetas gestorRecetas = Hospital.getInstance().getRecetas(); // 🔥 NUEVO

            for (int i = 0; i < data.getMedicamentos().size(); i++) {
                MedicamentoEntity actual = data.getMedicamentos().get(i);
                if (actual.getCodigo().equals(actualizado.getCodigo())) {
                    // Encontramos el medicamento a modificar y aplicamos los cambios
                    data.getMedicamentos().set(i, MedicamentoMapper.toXML(actualizado));
                    store.save(data);
                    actualizarRecetasConMedicamento(actualizado, gestorRecetas);
                    return actualizado;
                }
            }

            throw new IllegalArgumentException("Medicamento no encontrado con código: " + actualizado.getCodigo());
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando medicamento: " + e.getMessage());
        }
    }

    private void actualizarRecetasConMedicamento(Medicamento medicamentoActualizado, GestorRecetas gestorRecetas) {
        try {
            List<Receta> recetasConMedicamento = gestorRecetas.obtenerRecetasPorMedicamento(medicamentoActualizado.getCodigo());

            for (Receta receta : recetasConMedicamento) {
                // Buscar y actualizar el detalle que contiene este medicamento
                for (DetalleMedicamento detalle : receta.getDetallesMedicamentos()) {
                    if (detalle.getMedicamento().getCodigo().equals(medicamentoActualizado.getCodigo())) {
                        detalle.setMedicamento(medicamentoActualizado);
                    }
                }
                gestorRecetas.update(receta);
            }

            System.out.println("Actualizadas " + recetasConMedicamento.size() +
                    " recetas con el medicamento: " + medicamentoActualizado.getNombre());

        } catch (Exception e) {
            System.err.println("Error actualizando recetas con medicamento: " + e.getMessage());
        }
    }


    public Medicamento update(Medicamento actualizado, String codigoOriginal) {
        try {
            if (actualizado == null || codigoOriginal == null) {
                throw new IllegalArgumentException("Medicamento o código original no pueden ser nulos");
            }

            MedicamentoConector data = store.load();

            for (int i = 0; i < data.getMedicamentos().size(); i++) {
                MedicamentoEntity actual = data.getMedicamentos().get(i);

                if (actual.getCodigo().equals(codigoOriginal)) {
                    // Encontramos el medicamento a modificar
                    data.getMedicamentos().set(i, MedicamentoMapper.toXML(actualizado));
                    store.save(data);
                    return actualizado;
                }
            }

            throw new IllegalArgumentException("Medicamento no encontrado con código: " + codigoOriginal);

        } catch (Exception e) {
            throw new RuntimeException("Error actualizando medicamento: " + e.getMessage());
        }
    }





    public Boolean deleteById(String codigo) {
        try {
            if (codigo == null || codigo.trim().isEmpty()) {
                throw new IllegalArgumentException("Código no puede ser nulo o vacío");
            }

            MedicamentoConector data = store.load();
            boolean eliminado = data.getMedicamentos().removeIf(medicamento -> medicamento.getCodigo().equals(codigo));

            if (eliminado) {
                store.save(data);
            }

            return eliminado;
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando medicamento: " + e.getMessage());
        }
    }

    public boolean insertarMedicamento(Medicamento medicamento) {
        try {
            if (medicamento == null) {
                throw new IllegalArgumentException("El medicamento no puede ser nulo");
            }

            if (existeMedicamentoConEseCodigo(medicamento.getCodigo())) {
                throw new IllegalArgumentException("Ya existe un medicamento con ese código en el sistema.");
            }

            create(medicamento);
            return true;

        } catch (IllegalArgumentException e) {
            System.err.println("Error al insertar medicamento: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String codigoMedicamento) {
        return deleteById(codigoMedicamento);
    }

    public List<Medicamento> getMedicamentos() {
        return findAll();
    }

    public void setMedicamentos(List<Medicamento> medicamentos) {
        try {
            // Reemplazar todo el contenido del XML
            MedicamentoConector data = store.load();
            List<MedicamentoEntity> entities = medicamentos.stream()
                    .map(MedicamentoMapper::toXML)
                    .collect(Collectors.toList());

            data.setMedicamentos(entities);
            store.save(data);
        } catch (Exception e) {
            throw new RuntimeException("Error estableciendo medicamentos: " + e.getMessage());
        }
    }

    public String mostrarTodosLosMedicamentos() {
        List<Medicamento> medicamentos = findAll();

        if (medicamentos.isEmpty()) {
            return "No hay medicamentos registrados.";
        }

        StringBuilder sb = new StringBuilder("=== LISTA DE MEDICAMENTOS ===\n");
        for (int i = 0; i < medicamentos.size(); i++) {
            Medicamento med = medicamentos.get(i);
            sb.append(i + 1)
                    .append(". Código: ").append(med.getCodigo())
                    .append(" | Nombre: ").append(med.getNombre())
                    .append(" | Presentación: ").append(med.getPresentacion())
                    .append("\n");
        }
        return sb.toString();
    }


}
