package org.example.proyectohospital.Datos;

import org.example.proyectohospital.Modelo.Medicamento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoDatos {

    public List<Medicamento> findAll() throws SQLException {
        String sql = "SELECT codigo, nombre, presentacion FROM medicamentos ORDER BY codigo";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Medicamento> list = new ArrayList<>();
            while (rs.next()) {
                Medicamento medicamento = new Medicamento(
                        rs.getString("nombre"),
                        rs.getString("presentacion"),
                        rs.getString("codigo")
                );
                list.add(medicamento);
            }
            return list;
        }
    }

    public Medicamento findByCodigo(String codigo) throws SQLException {
        String sql = "SELECT codigo, nombre, presentacion FROM medicamentos WHERE codigo = ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Medicamento(
                        rs.getString("nombre"),
                        rs.getString("presentacion"),
                        rs.getString("codigo")
                );
            }
            return null;
        }
    }

    public Medicamento insert(Medicamento medicamento) throws SQLException {
        String sql = "INSERT INTO medicamentos (codigo, nombre, presentacion) VALUES (?, ?, ?)";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, medicamento.getCodigo());
            ps.setString(2, medicamento.getNombre());
            ps.setString(3, medicamento.getPresentacion());

            ps.executeUpdate();
            return medicamento;
        }
    }

    public Medicamento update(Medicamento medicamento) throws SQLException {
        String sql = "UPDATE medicamentos SET nombre = ?, presentacion = ? WHERE codigo = ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, medicamento.getNombre());
            ps.setString(2, medicamento.getPresentacion());
            ps.setString(3, medicamento.getCodigo());

            int affected = ps.executeUpdate();
            return affected > 0 ? medicamento : null;
        }
    }

    public boolean delete(String codigo) throws SQLException {
        String sql = "DELETE FROM medicamentos WHERE codigo = ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, codigo);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Medicamento> findByText(String texto) throws SQLException {
        String sql = "SELECT codigo, nombre, presentacion FROM medicamentos WHERE nombre LIKE ? OR codigo LIKE ? OR presentacion LIKE ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            String searchText = "%" + texto + "%";
            ps.setString(1, searchText);
            ps.setString(2, searchText);
            ps.setString(3, searchText);

            ResultSet rs = ps.executeQuery();
            List<Medicamento> list = new ArrayList<>();
            while (rs.next()) {
                Medicamento medicamento = new Medicamento(
                        rs.getString("nombre"),
                        rs.getString("presentacion"),
                        rs.getString("codigo")
                );
                list.add(medicamento);
            }
            return list;
        }
    }
}