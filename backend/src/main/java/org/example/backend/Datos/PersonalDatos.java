package org.example.backend.Datos;

import org.example.proyectohospital.Modelo.Administrador;
import org.example.proyectohospital.Modelo.Farmaceuta;
import org.example.proyectohospital.Modelo.Medico;
import org.example.proyectohospital.Modelo.Personal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonalDatos {

    public List<Personal> findAll() throws SQLException {
        String sql = "SELECT id, nombre, clave, tipo, especialidad FROM personal ORDER BY id";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Personal> list = new ArrayList<>();
            while (rs.next()) {
                Personal personal = creaPersonalRS(rs);
                if (personal != null) {
                    list.add(personal);
                }
            }
            return list;
        }
    }

    public Personal findById(String id) throws SQLException {
        String sql = "SELECT id, nombre, clave, tipo, especialidad FROM personal WHERE id = ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return creaPersonalRS(rs);
            }
            return null;
        }
    }

    public Personal verificarCredenciales(String id, String clave) throws SQLException {
        String sql = "SELECT id, nombre, clave, tipo, especialidad FROM personal WHERE id = ? AND clave = ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setString(2, clave);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return creaPersonalRS(rs);
            }
            return null;
        }
    }

    private Personal creaPersonalRS(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo");
        String id = rs.getString("id");
        String nombre = rs.getString("nombre");
        String clave = rs.getString("clave");

        switch (tipo) {
            case "Medico":
                Medico medico = new Medico(nombre, id, clave, rs.getString("especialidad"));
                return medico;
            case "Administrador":
                return new Administrador(nombre, id, clave);
            case "Farmaceuta":
                return new Farmaceuta(nombre, id, clave);
            default:
                return null;
        }
    }

    public Personal insert(Personal personal) throws SQLException {
        String sql = "INSERT INTO personal (id, nombre, clave, tipo, especialidad) VALUES (?, ?, ?, ?, ?)";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, personal.getId());
            ps.setString(2, personal.getNombre());
            ps.setString(3, personal.getClave());
            ps.setString(4, personal.tipo());

            if (personal instanceof Medico) {
                ps.setString(5, ((Medico) personal).getEspecialidad());
            } else {
                ps.setNull(5, Types.VARCHAR);
            }

            ps.executeUpdate();
            return personal;
        }
    }

    public Personal update(Personal personal) throws SQLException {
        String sql = "UPDATE personal SET nombre = ?, clave = ?, tipo = ?, especialidad = ? WHERE id = ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, personal.getNombre());
            ps.setString(2, personal.getClave());
            ps.setString(3, personal.tipo());

            if (personal instanceof Medico) {
                ps.setString(4, ((Medico) personal).getEspecialidad());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            ps.setString(5, personal.getId());

            int afectados = ps.executeUpdate();
            return afectados > 0 ? personal : null;
        }
    }

    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM personal WHERE id = ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Personal> findByText(String texto) throws SQLException {
        String sql = "SELECT id, nombre, clave, tipo, especialidad FROM personal WHERE nombre LIKE ? OR id LIKE ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            String searchText = "%" + texto + "%";
            ps.setString(1, searchText);
            ps.setString(2, searchText);

            ResultSet rs = ps.executeQuery();
            List<Personal> list = new ArrayList<>();
            while (rs.next()) {
                Personal personal = creaPersonalRS(rs);
                if (personal != null) {
                    list.add(personal);
                }
            }
            return list;
        }
    }

    public boolean cambiarClave(String id, String nuevaClave) throws SQLException {
        String sql = "UPDATE personal SET clave = ? WHERE id = ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nuevaClave);
            ps.setString(2, id);

            return ps.executeUpdate() > 0;
        }
    }
}