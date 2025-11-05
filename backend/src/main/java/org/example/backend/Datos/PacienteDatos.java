package org.example.backend.Datos;

import org.example.proyectohospital.Modelo.Paciente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDatos {

    public List<Paciente> findAll() throws SQLException {
        String sql = "SELECT id, nombre, telefono, fecha_nacimiento FROM pacientes ORDER BY id";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Paciente> list = new ArrayList<>();
            while (rs.next()) {
                Paciente paciente = new Paciente();
                paciente.setId(rs.getString("id"));
                paciente.setNombre(rs.getString("nombre"));
                paciente.setTelefono(rs.getInt("telefono"));
                paciente.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
                list.add(paciente);
            }
            return list;
        }
    }

    public Paciente findById(String id) throws SQLException {
        String sql = "SELECT id, nombre, telefono, fecha_nacimiento FROM pacientes WHERE id = ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Paciente paciente = new Paciente();
                paciente.setId(rs.getString("id"));
                paciente.setNombre(rs.getString("nombre"));
                paciente.setTelefono(rs.getInt("telefono"));
                paciente.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
                return paciente;
            }
            return null;
        }
    }

    public Paciente insert(Paciente paciente) throws SQLException {
        String sql = "INSERT INTO pacientes (id, nombre, telefono, fecha_nacimiento) VALUES (?, ?, ?, ?)";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, paciente.getId());
            ps.setString(2, paciente.getNombre());
            ps.setInt(3, paciente.getTelefono());
            ps.setDate(4, Date.valueOf(paciente.getFechaNacimiento()));

            ps.executeUpdate();
            return paciente;
        }
    }

    public Paciente update(Paciente paciente) throws SQLException {
        String sql = "UPDATE pacientes SET nombre = ?, telefono = ?, fecha_nacimiento = ? WHERE id = ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, paciente.getNombre());
            ps.setInt(2, paciente.getTelefono());
            ps.setDate(3, Date.valueOf(paciente.getFechaNacimiento()));
            ps.setString(4, paciente.getId());

            int afectados = ps.executeUpdate();
            return afectados > 0 ? paciente : null;
        }
    }

    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM pacientes WHERE id = ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Paciente> findByText(String texto) throws SQLException {
        String sql = "SELECT id, nombre, telefono, fecha_nacimiento FROM pacientes WHERE nombre LIKE ? OR id LIKE ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            String buscar = "%" + texto + "%";
            ps.setString(1, buscar);
            ps.setString(2, buscar);

            ResultSet rs = ps.executeQuery();
            List<Paciente> list = new ArrayList<>();
            while (rs.next()) {
                Paciente paciente = new Paciente();
                paciente.setId(rs.getString("id"));
                paciente.setNombre(rs.getString("nombre"));
                paciente.setTelefono(rs.getInt("telefono"));
                paciente.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
                list.add(paciente);
            }
            return list;
        }
    }
}