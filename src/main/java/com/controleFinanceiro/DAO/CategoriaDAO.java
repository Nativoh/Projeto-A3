package com.controleFinanceiro.DAO;

import com.controleFinanceiro.db.ConnectionFactory;
import com.controleFinanceiro.model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    private final ConnectionFactory cf = new ConnectionFactory();

    public List<Categoria> listarVisiveis(int usuarioId) {
        String sql = "SELECT * FROM categorias "
                + "WHERE usuario_id IS NULL OR usuario_id = ? "
                + "ORDER BY tipo, nome";

        List<Categoria> lista = new ArrayList<>();

        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro CategoriaDAO.listarVisiveis: " + e.getMessage());
        }

        return lista;
    }

    public List<Categoria> listarVisivelsPorTipo(int usuarioId, String tipo) {
        String sql = "SELECT * FROM categorias "
                + "WHERE (usuario_id IS NULL OR usuario_id = ?) "
                + "AND tipo = ? "
                + "ORDER BY nome";

        List<Categoria> lista = new ArrayList<>();

        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            ps.setString(2, tipo);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro CategoriaDAO.listarVisivelsPorTipo: " + e.getMessage());
        }

        return lista;
    }

    public int contarGlobais() {
        String sql = "SELECT COUNT(*) FROM categorias WHERE usuario_id IS NULL";

        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Erro CategoriaDAO.contarGlobais: " + e.getMessage());
        }

        return 0;
    }

    private Categoria mapear(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();

        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        c.setTipo(rs.getString("tipo"));
        c.setIcone(rs.getString("icone"));
        c.setCor(rs.getString("cor"));

        int uid = rs.getInt("usuario_id");
        c.setUsuarioId(rs.wasNull() ? null : uid);

        return c;
    }
}

