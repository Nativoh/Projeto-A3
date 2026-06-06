package com.controleFinanceiro.DAO;

import com.controleFinanceiro.db.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboardDAO {

    private final ConnectionFactory cf = new ConnectionFactory();

    // ══════════════════════════════════════════════════════════════════════
    // CONTAGENS
    // ══════════════════════════════════════════════════════════════════════

    public int contarUsuariosAtivos() {
        return contar("SELECT COUNT(*) FROM usuarios WHERE ativo = TRUE AND funcao = 'USER'");
    }

    public int contarCategoriasGlobais() {
        return contar("SELECT COUNT(*) FROM categorias WHERE usuario_id IS NULL");
    }

    public int contarDicasAtivas() {
        return contar("SELECT COUNT(*) FROM dicas_financeiras WHERE ativo = TRUE");
    }

    public int contarTransacoesMes(int mes, int ano) {
        String sql = "SELECT COUNT(*) FROM transacoes "
                + "WHERE MONTH(data_transacao) = ? AND YEAR(data_transacao) = ?";
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mes);
            ps.setInt(2, ano);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Erro AdminDashboardDAO.contarTransacoesMes: " + e.getMessage());
        }
        return 0;
    }

    // ══════════════════════════════════════════════════════════════════════
    // SAUDE GLOBAL
    // ══════════════════════════════════════════════════════════════════════

    public Map<String, Integer> distribuicaoSaude(int mes, int ano) {
        String sql = "SELECT u.renda_mensal, "
                + "COALESCE(v.total_despesas, 0) AS despesas, "
                + "COALESCE(v.total_receitas, 0) AS receitas "
                + "FROM usuarios u "
                + "LEFT JOIN vw_resumo_mensal v "
                + "  ON v.usuario_id = u.id AND v.mes = ? AND v.ano = ? "
                + "WHERE u.ativo = TRUE AND u.funcao = 'USER'";

        Map<String, Integer> dist = new LinkedHashMap<>();
        dist.put("SAUDAVEL", 0);
        dist.put("ATENCAO",  0);
        dist.put("RISCO",    0);

        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mes);
            ps.setInt(2, ano);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                double renda    = rs.getDouble("renda_mensal");
                double despesas = rs.getDouble("despesas");
                double receitas = rs.getDouble("receitas");
                String status   = calcularStatus(despesas, receitas, renda);
                dist.put(status, dist.get(status) + 1);
            }
        } catch (SQLException e) {
            System.err.println("Erro AdminDashboardDAO.distribuicaoSaude: " + e.getMessage());
        }
        return dist;
    }

    // ══════════════════════════════════════════════════════════════════════
    // LISTA COMPLETA DE USUARIOS COM CONDICAO FINANCEIRA
    // ══════════════════════════════════════════════════════════════════════

    public List<Object[]> listarUsuariosComCondicao(int mes, int ano) {
        String sql = "SELECT u.id, u.nome, u.email, u.renda_mensal, "
                + "u.perfil_investidor, u.ativo, u.criado_em, "
                + "COALESCE(v.total_receitas,  0) AS receitas, "
                + "COALESCE(v.total_despesas,  0) AS despesas, "
                + "COALESCE(v.saldo,           0) AS saldo, "
                + "(SELECT COUNT(*) FROM metas m "
                + " WHERE m.usuario_id = u.id AND m.status = 'ATIVA') AS metas_ativas, "
                + "(SELECT COUNT(*) FROM transacoes t "
                + " WHERE t.usuario_id = u.id "
                + " AND MONTH(t.data_transacao) = ? "
                + " AND YEAR(t.data_transacao)  = ?) AS transacoes_mes "
                + "FROM usuarios u "
                + "LEFT JOIN vw_resumo_mensal v "
                + "  ON v.usuario_id = u.id AND v.mes = ? AND v.ano = ? "
                + "WHERE u.funcao = 'USER' "
                + "ORDER BY u.nome ASC";

        List<Object[]> lista = new ArrayList<>();
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mes);
            ps.setInt(2, ano);
            ps.setInt(3, mes);
            ps.setInt(4, ano);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                double renda    = rs.getDouble("renda_mensal");
                double receitas = rs.getDouble("receitas");
                double despesas = rs.getDouble("despesas");
                double saldo    = rs.getDouble("saldo");
                String status   = calcularStatus(despesas, receitas, renda);
                double base     = receitas > 0 ? receitas : renda;
                double pct      = base > 0 ? (despesas / base) * 100 : 0;

                lista.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        renda,
                        receitas,
                        despesas,
                        saldo,
                        pct,
                        status,
                        rs.getString("perfil_investidor") != null
                                ? rs.getString("perfil_investidor") : "—",
                        rs.getBoolean("ativo"),
                        rs.getInt("metas_ativas"),
                        rs.getInt("transacoes_mes"),
                        rs.getTimestamp("criado_em") != null
                                ? rs.getTimestamp("criado_em")
                                  .toLocalDateTime().toLocalDate().toString()
                                : "—"
                });
            }
        } catch (SQLException e) {
            System.err.println("Erro AdminDashboardDAO.listarUsuariosComCondicao: "
                    + e.getMessage());
        }
        return lista;
    }

    public List<Object[]> listarAlertas(int mes, int ano) {
        String sql = "SELECT u.nome, u.email, u.renda_mensal, "
                + "COALESCE(v.total_despesas, 0) AS despesas, "
                + "COALESCE(v.total_receitas, 0) AS receitas "
                + "FROM usuarios u "
                + "LEFT JOIN vw_resumo_mensal v "
                + "  ON v.usuario_id = u.id AND v.mes = ? AND v.ano = ? "
                + "WHERE u.ativo = TRUE AND u.funcao = 'USER' "
                + "ORDER BY (COALESCE(v.total_despesas,0) / "
                + "  NULLIF(GREATEST(v.total_receitas, u.renda_mensal), 0)) DESC";

        List<Object[]> lista = new ArrayList<>();
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mes);
            ps.setInt(2, ano);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                double renda    = rs.getDouble("renda_mensal");
                double despesas = rs.getDouble("despesas");
                double receitas = rs.getDouble("receitas");
                String status   = calcularStatus(despesas, receitas, renda);
                double base     = receitas > 0 ? receitas : renda;
                double pct      = base > 0 ? (despesas / base) * 100 : 0;
                if (!"SAUDAVEL".equals(status)) {
                    lista.add(new Object[]{
                            rs.getString("nome"),
                            rs.getString("email"),
                            String.format("%.1f%%", pct),
                            status
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro AdminDashboardDAO.listarAlertas: " + e.getMessage());
        }
        return lista;
    }

    public Map<String, Double> topCategorias(int mes, int ano) {
        String sql = "SELECT c.nome, SUM(t.valor) AS total "
                + "FROM transacoes t "
                + "JOIN categorias c ON c.id = t.categoria_id "
                + "WHERE t.tipo = 'EXPENSE' "
                + "  AND MONTH(t.data_transacao) = ? AND YEAR(t.data_transacao) = ? "
                + "GROUP BY c.nome ORDER BY total DESC LIMIT 5";

        Map<String, Double> resultado = new LinkedHashMap<>();
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mes);
            ps.setInt(2, ano);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                resultado.put(rs.getString("nome"), rs.getDouble("total"));
        } catch (SQLException e) {
            System.err.println("Erro AdminDashboardDAO.topCategorias: " + e.getMessage());
        }
        return resultado;
    }

    // ══════════════════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════════════════

    private String calcularStatus(double despesas, double receitas, double renda) {
        // Saldo negativo = RISCO direto
        if (receitas > 0 && despesas >= receitas) return "RISCO";
        double base = receitas > 0 ? receitas : renda;
        if (base <= 0) return "SAUDAVEL";
        double pct = (despesas / base) * 100;
        if (pct >= 90) return "RISCO";
        if (pct >= 70) return "ATENCAO";
        return "SAUDAVEL";
    }

    private int contar(String sql) {
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Erro AdminDashboardDAO.contar: " + e.getMessage());
        }
        return 0;
    }
}