package com.controleFinanceiro.DAO;

import com.controleFinanceiro.db.ConnectionFactory;
import com.controleFinanceiro.model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Erro CategoriaDAO.listarVisiveis: " + e.getMessage());
        }
        return lista;
    }

    public List<Categoria> listarVisivelsPorTipo(int usuarioId, String tipo) {
        String sql = "SELECT * FROM categorias "
                + "WHERE (usuario_id IS NULL OR usuario_id = ?) AND tipo = ? "
                + "ORDER BY nome";
        List<Categoria> lista = new ArrayList<>();
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setString(2, tipo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
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
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Erro CategoriaDAO.contarGlobais: " + e.getMessage());
        }
        return 0;
    }

    // ── MÉTODOS DE ANÁLISE GLOBAL ──────────────────────────────────────────

    /**
     * Lista categorias globais com totais de gastos/receitas de todos usuarios,
     * no mes/ano informado.
     * Retorna: id, nome, tipo, cor, total_valor, total_transacoes, usuarios_distintos
     */
    public List<Object[]> listarGlobaisComGastos(int mes, int ano) {
        String sql = "SELECT c.id, c.nome, c.tipo, c.cor, "
                + "COALESCE(SUM(t.valor), 0)      AS total_valor, "
                + "COUNT(t.id)                     AS total_transacoes, "
                + "COUNT(DISTINCT t.usuario_id)    AS usuarios_distintos "
                + "FROM categorias c "
                + "LEFT JOIN transacoes t "
                + "  ON t.categoria_id = c.id "
                + "  AND MONTH(t.data_transacao) = ? "
                + "  AND YEAR(t.data_transacao)  = ? "
                + "WHERE c.usuario_id IS NULL "
                + "GROUP BY c.id, c.nome, c.tipo, c.cor "
                + "ORDER BY total_valor DESC";

        List<Object[]> lista = new ArrayList<>();
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mes);
            ps.setInt(2, ano);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("tipo"),
                        rs.getString("cor"),
                        rs.getDouble("total_valor"),
                        rs.getInt("total_transacoes"),
                        rs.getInt("usuarios_distintos")
                });
            }
        } catch (SQLException e) {
            System.err.println("Erro CategoriaDAO.listarGlobaisComGastos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Evolução mensal de uma categoria nos últimos 6 meses.
     * Retorna mapa mes/ano -> total
     */
    public Map<String, Double> evolucaoCategoria(int categoriaId) {
        String sql = "SELECT DATE_FORMAT(data_transacao, '%m/%Y') AS periodo, "
                + "SUM(valor) AS total "
                + "FROM transacoes "
                + "WHERE categoria_id = ? "
                + "  AND data_transacao >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) "
                + "GROUP BY periodo "
                + "ORDER BY MIN(data_transacao) ASC";

        Map<String, Double> mapa = new LinkedHashMap<>();
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoriaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                mapa.put(rs.getString("periodo"), rs.getDouble("total"));
        } catch (SQLException e) {
            System.err.println("Erro CategoriaDAO.evolucaoCategoria: " + e.getMessage());
        }
        return mapa;
    }

    /**
     * Top usuarios que mais gastaram em uma categoria no mes/ano.
     */
    public List<Object[]> topUsuariosPorCategoria(int categoriaId, int mes, int ano) {
        String sql = "SELECT u.nome, u.email, SUM(t.valor) AS total, "
                + "COUNT(t.id) AS transacoes "
                + "FROM transacoes t "
                + "JOIN usuarios u ON u.id = t.usuario_id "
                + "WHERE t.categoria_id = ? "
                + "  AND MONTH(t.data_transacao) = ? "
                + "  AND YEAR(t.data_transacao)  = ? "
                + "GROUP BY u.id, u.nome, u.email "
                + "ORDER BY total DESC "
                + "LIMIT 10";

        List<Object[]> lista = new ArrayList<>();
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoriaId);
            ps.setInt(2, mes);
            ps.setInt(3, ano);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getDouble("total"),
                        rs.getInt("transacoes")
                });
            }
        } catch (SQLException e) {
            System.err.println("Erro CategoriaDAO.topUsuariosPorCategoria: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Resumo geral das categorias globais: total gasto, total receita, no mes.
     */
    public double[] resumoGlobal(int mes, int ano) {
        String sql = "SELECT "
                + "SUM(CASE WHEN c.tipo='EXPENSE' THEN t.valor ELSE 0 END) AS total_despesas, "
                + "SUM(CASE WHEN c.tipo='INCOME'  THEN t.valor ELSE 0 END) AS total_receitas, "
                + "COUNT(DISTINCT t.usuario_id) AS usuarios_ativos "
                + "FROM transacoes t "
                + "JOIN categorias c ON c.id = t.categoria_id "
                + "WHERE c.usuario_id IS NULL "
                + "  AND MONTH(t.data_transacao) = ? "
                + "  AND YEAR(t.data_transacao)  = ?";

        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mes);
            ps.setInt(2, ano);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new double[]{
                        rs.getDouble("total_despesas"),
                        rs.getDouble("total_receitas"),
                        rs.getDouble("usuarios_ativos")
                };
            }
        } catch (SQLException e) {
            System.err.println("Erro CategoriaDAO.resumoGlobal: " + e.getMessage());
        }
        return new double[]{0, 0, 0};
    }

    // MAPPER
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