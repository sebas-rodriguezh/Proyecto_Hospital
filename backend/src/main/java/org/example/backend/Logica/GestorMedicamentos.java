package org.example.backend.Logica;


import org.example.backend.Datos.*;
import org.example.proyectohospital.Modelo.DetalleMedicamento;
import org.example.proyectohospital.Modelo.Medicamento;
import org.example.proyectohospital.Modelo.Receta;

import java.sql.SQLException;
import java.util.List;

public class GestorMedicamentos {

    private final MedicamentoDatos store;

    public GestorMedicamentos() {
        this.store = new MedicamentoDatos();
    }

    public List<Medicamento> findAll() {
        try {
            return store.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Error cargando medicamentos: " + e.getMessage());
        }
    }

    public List<Medicamento> findByText(String texto) {
        try {
            if (texto == null || texto.trim().isEmpty()) {
                return store.findAll();
            }
            return store.findByText(texto);
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando medicamentos: " + e.getMessage());
        }
    }

    public Medicamento getMedicamento(String codigoMedicamento) {
        try {
            return store.findByCodigo(codigoMedicamento);
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo medicamento: " + e.getMessage());
        }
    }

    public Medicamento buscarMedicamentoPorNombre(String nombreMedicamento) {
        try {
            List<Medicamento> medicamentos = store.findAll();
            return medicamentos.stream()
                    .filter(m -> m.getNombre().equalsIgnoreCase(nombreMedicamento))
                    .findFirst()
                    .orElse(null);
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando medicamento por nombre: " + e.getMessage());
        }
    }

    public boolean existeMedicamentoConEseCodigo(String codigoMedicamento) {
        try {
            return store.findByCodigo(codigoMedicamento) != null;
        } catch (SQLException e) {
            throw new RuntimeException("Error verificando existencia: " + e.getMessage());
        }
    }

    public Medicamento create(Medicamento nuevo) {
        try {
            if (nuevo == null) {
                throw new IllegalArgumentException("El medicamento no puede ser nulo");
            }
            if (nuevo.getCodigo() == null || nuevo.getCodigo().trim().isEmpty()) {
                throw new IllegalArgumentException("El código del medicamento no puede estar vacío");
            }
            if (existeMedicamentoConEseCodigo(nuevo.getCodigo())) {
                throw new IllegalArgumentException("Ya existe un medicamento con ese código");
            }

            return store.insert(nuevo);
        } catch (SQLException e) {
            throw new RuntimeException("Error creando medicamento: " + e.getMessage());
        }
    }

    public Medicamento update(Medicamento actualizado) {
        try {
            if (actualizado == null || actualizado.getCodigo() == null) {
                throw new IllegalArgumentException("Medicamento o código no pueden ser nulos");
            }

            Medicamento result = store.update(actualizado);
            if (result != null) {
                actualizarRecetasConMedicamento(actualizado);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando medicamento: " + e.getMessage());
        }
    }

    private void actualizarRecetasConMedicamento(Medicamento medicamentoActualizado) {
        try {
            GestorRecetas gestorRecetas = Hospital.getInstance().getRecetas();
            List<Receta> recetasConMedicamento = gestorRecetas.obtenerRecetasPorMedicamento(medicamentoActualizado.getCodigo());

            for (Receta receta : recetasConMedicamento) {
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

            return update(actualizado);
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando medicamento: " + e.getMessage());
        }
    }

    public Boolean deleteById(String codigo) {
        try {
            if (codigo == null || codigo.trim().isEmpty()) {
                throw new IllegalArgumentException("Código no puede ser nulo o vacío");
            }
            return store.delete(codigo);
        } catch (SQLException e) {
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
            List<Medicamento> existentes = store.findAll();
            for (Medicamento med : existentes) {
                store.delete(med.getCodigo());
            }

            for (Medicamento med : medicamentos) {
                store.insert(med);
            }
        } catch (SQLException e) {
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