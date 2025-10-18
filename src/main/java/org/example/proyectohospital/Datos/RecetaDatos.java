package org.example.proyectohospital.Datos;

import org.example.proyectohospital.Modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecetaDatos {

    public List<Receta> findAll() throws SQLException {
        String sql = "SELECT id FROM recetas ORDER BY fecha_prescripcion DESC";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Receta> list = new ArrayList<>();
            while (rs.next()) {
                Receta receta = findById(rs.getString("id"));
                if (receta != null) {
                    list.add(receta);
                }
            }
            return list;
        }
    }

    public Receta findById(String id) throws SQLException {
        String sqlReceta = """
        SELECT r.*, 
               p.nombre as paciente_nombre, p.telefono, p.fecha_nacimiento,
               per.nombre as personal_nombre, per.tipo, per.especialidad
        FROM recetas r
        JOIN pacientes p ON r.paciente_id = p.id
        JOIN personal per ON r.personal_id = per.id
        WHERE r.id = ?
        """;

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sqlReceta)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Receta receta = new Receta(
                        rs.getString("id"),
                        rs.getDate("fecha_prescripcion").toLocalDate(),
                        rs.getDate("fecha_retiro").toLocalDate(),
                        Integer.parseInt(rs.getString("estado"))
                );

                Paciente paciente = new Paciente();
                paciente.setId(rs.getString("paciente_id"));
                paciente.setNombre(rs.getString("paciente_nombre"));
                paciente.setTelefono(rs.getInt("telefono"));
                paciente.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
                receta.setPaciente(paciente);

                String tipoPersonal = rs.getString("tipo");
                String personalId = rs.getString("personal_id");
                String personalNombre = rs.getString("personal_nombre");

                if ("Medico".equals(tipoPersonal)) {
                    Medico medico = new Medico();
                    medico.setId(personalId);
                    medico.setNombre(personalNombre);
                    medico.setEspecialidad(rs.getString("especialidad"));
                    receta.setPersonal(medico);
                } else {
                    throw new RuntimeException("Error: Solo un médico puede crear recetas.");
                }

                List<DetalleMedicamento> detalles = cargarDetallesReceta(id);
                receta.setDetalleMedicamentos(detalles);

                return receta;
            }
            return null;
        }
    }

    private List<DetalleMedicamento> cargarDetallesReceta(String recetaId) throws SQLException {
        String sql = """
        SELECT dm.*, m.nombre as medicamento_nombre, m.presentacion
        FROM detalle_medicamentos dm
        JOIN medicamentos m ON dm.medicamento_codigo = m.codigo
        WHERE dm.receta_id = ?
        """;

        List<DetalleMedicamento> detalles = new ArrayList<>();

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, recetaId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Medicamento medicamento = new Medicamento(
                        rs.getString("medicamento_nombre"),
                        rs.getString("presentacion"),
                        rs.getString("medicamento_codigo")
                );

                DetalleMedicamento detalle = new DetalleMedicamento(
                        medicamento,
                        rs.getString("id_detalle"),
                        rs.getInt("cantidad"),
                        rs.getInt("duracion"),
                        rs.getString("indicacion")
                );

                detalles.add(detalle);
            }
        }
        return detalles;
    }

    public boolean insert(Receta receta) throws SQLException {
        Connection cn = null;
        try {
            cn = DB.getConnection();
            cn.setAutoCommit(false);

            String sqlReceta = "INSERT INTO recetas (id, personal_id, paciente_id, fecha_prescripcion, fecha_retiro, estado) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = cn.prepareStatement(sqlReceta)) {
                ps.setString(1, receta.getId());
                ps.setString(2, receta.getPersonal().getId());
                ps.setString(3, receta.getPaciente().getId());
                ps.setDate(4, Date.valueOf(receta.getFechaPrescripcion()));
                ps.setDate(5, Date.valueOf(receta.getFechaRetiro()));
                ps.setString(6, String.valueOf(receta.getEstado()));
                ps.executeUpdate();
            }

            String sqlDetalle = "INSERT INTO detalle_medicamentos (receta_id, medicamento_codigo, id_detalle, cantidad, duracion, indicacion) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = cn.prepareStatement(sqlDetalle)) {
                for (DetalleMedicamento detalle : receta.getDetalleMedicamentos()) {
                    ps.setString(1, receta.getId());
                    ps.setString(2, detalle.getMedicamento().getCodigo());
                    ps.setString(3, detalle.getIdDetalle());
                    ps.setInt(4, detalle.getCantidad());
                    ps.setInt(5, detalle.getDuracion());
                    ps.setString(6, detalle.getIndicacion());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            cn.commit();
            return true;

        } catch (SQLException e) {
            if (cn != null) {
                cn.rollback();
            }
            throw e;
        } finally {
            if (cn != null) {
                cn.setAutoCommit(true);
            }
        }
    }

    public boolean update(Receta receta) throws SQLException {
        Connection cn = null;
        try {
            cn = DB.getConnection();
            cn.setAutoCommit(false);

            String sqlReceta = "UPDATE recetas SET personal_id = ?, paciente_id = ?, fecha_prescripcion = ?, fecha_retiro = ?, estado = ? WHERE id = ?";
            try (PreparedStatement ps = cn.prepareStatement(sqlReceta)) {
                ps.setString(1, receta.getPersonal().getId());
                ps.setString(2, receta.getPaciente().getId());
                ps.setDate(3, Date.valueOf(receta.getFechaPrescripcion()));
                ps.setDate(4, Date.valueOf(receta.getFechaRetiro()));
                ps.setString(5, String.valueOf(receta.getEstado()));
                ps.setString(6, receta.getId());
                ps.executeUpdate();
            }

            String sqlDeleteDetalles = "DELETE FROM detalle_medicamentos WHERE receta_id = ?";
            try (PreparedStatement ps = cn.prepareStatement(sqlDeleteDetalles)) {
                ps.setString(1, receta.getId());
                ps.executeUpdate();
            }

            String sqlDetalle = "INSERT INTO detalle_medicamentos (receta_id, medicamento_codigo, id_detalle, cantidad, duracion, indicacion) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = cn.prepareStatement(sqlDetalle)) {
                for (DetalleMedicamento detalle : receta.getDetalleMedicamentos()) {
                    ps.setString(1, receta.getId());
                    ps.setString(2, detalle.getMedicamento().getCodigo());
                    ps.setString(3, detalle.getIdDetalle());
                    ps.setInt(4, detalle.getCantidad());
                    ps.setInt(5, detalle.getDuracion());
                    ps.setString(6, detalle.getIndicacion());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            cn.commit();
            return true;

        } catch (SQLException e) {
            if (cn != null) cn.rollback();
            throw e;
        } finally {
            if (cn != null) cn.setAutoCommit(true);
        }
    }

    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM recetas WHERE id = ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Receta> findByText(String texto) throws SQLException {
        String sql = """
            SELECT DISTINCT r.id 
            FROM recetas r
            JOIN pacientes p ON r.paciente_id = p.id
            JOIN personal per ON r.personal_id = per.id
            WHERE r.id LIKE ? OR p.nombre LIKE ? OR per.nombre LIKE ? OR p.id LIKE ?
            """;

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            String buscar = "%" + texto + "%";
            ps.setString(1, buscar);
            ps.setString(2, buscar);
            ps.setString(3, buscar);
            ps.setString(4, buscar);

            ResultSet rs = ps.executeQuery();
            List<Receta> list = new ArrayList<>();
            while (rs.next()) {
                Receta receta = findById(rs.getString("id"));
                if (receta != null) {
                    list.add(receta);
                }
            }
            return list;
        }
    }

    public List<Receta> findByMedico(String idMedico) throws SQLException {
        String sql = "SELECT id FROM recetas WHERE personal_id = ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, idMedico);
            ResultSet rs = ps.executeQuery();

            List<Receta> list = new ArrayList<>();
            while (rs.next()) {
                Receta receta = findById(rs.getString("id"));
                if (receta != null) {
                    list.add(receta);
                }
            }
            return list;
        }
    }

    public List<Receta> findByPaciente(String busqueda) throws SQLException {
        if (busqueda == null || busqueda.trim().isEmpty()) {
            //return findAll();
            return null;
        }

        List<Receta> todas = findAll();
        return todas.stream()
                .filter(r -> r.getPaciente().getId().contains(busqueda) ||
                        r.getPaciente().getNombre().toLowerCase().contains(busqueda.toLowerCase()))
                .collect(Collectors.toList());
    }



    public List<Receta> findByEstado(int estado) throws SQLException {
        String sql = "SELECT id FROM recetas WHERE estado = ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, String.valueOf(estado));
            ResultSet rs = ps.executeQuery();

            List<Receta> list = new ArrayList<>();
            while (rs.next()) {
                Receta receta = findById(rs.getString("id"));
                if (receta != null) {
                    list.add(receta);
                }
            }
            return list;
        }
    }

    public List<Receta> findByMedicamento(String codigoMedicamento) throws SQLException {
        String sql = "SELECT DISTINCT receta_id FROM detalle_medicamentos WHERE medicamento_codigo = ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, codigoMedicamento);
            ResultSet rs = ps.executeQuery();

            List<Receta> list = new ArrayList<>();
            while (rs.next()) {
                Receta receta = findById(rs.getString("receta_id"));
                if (receta != null) {
                    list.add(receta);
                }
            }
            return list;
        }
    }
}