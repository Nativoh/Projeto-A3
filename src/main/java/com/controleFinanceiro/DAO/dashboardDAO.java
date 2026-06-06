package com.controleFinanceiro.DAO;

import com.controleFinanceiro.db.ConnectionFactory;
import com.controleFinanceiro.model.ResumoMensal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class dashboardDAO {


    private final ConnectionFactory connectionFactory = new ConnectionFactory();

    // 1. RESUMO MENSAL — alimenta os cards KPI
    public ResumoMensal resumoMensal(int usuarioId, int mes, int ano) {

        String sql = "SELECT total_receitas, total_despesas, saldo "
                + "FROM vw_resumo_mensal "
                + "WHERE usuario_id = ? AND mes = ? AND ano = ?";

        try (Connection conn = connectionFactory.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            ps.setInt(2, mes);
            ps.setInt(3, ano);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ResumoMensal(
                            rs.getDouble("total_receitas"),
                            rs.getDouble("total_despesas"),
                            rs.getDouble("saldo")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro DashboardDAO.resumoMensal: " + e.getMessage());
        }

        // Retorna vazio se não há dados
        return new ResumoMensal(0, 0, 0);
    }

    //2. GASTOS POR CATEGORIA — alimenta o gráfico de pizza

    public Map<String, Double> gastosPorCategoria(int usuarioId, int mes, int ano) {

        String sql = "SELECT c.nome, SUM(t.valor) AS total "
                + "FROM transacoes t "
                + "JOIN categorias c ON c.id = t.categoria_id "
                + "WHERE t.usuario_id = ? "
                + "  AND t.tipo = 'EXPENSE' "
                + "  AND MONTH(t.data_transacao) = ? "
                + "  AND YEAR(t.data_transacao)  = ? "
                + "GROUP BY c.nome "
                + "ORDER BY total DESC";

        Map<String, Double> resultado = new LinkedHashMap<>();

        try (Connection conn = connectionFactory.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            ps.setInt(2, mes);
            ps.setInt(3, ano);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.put(
                            rs.getString("nome"),
                            rs.getDouble("total")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro DashboardDAO.gastosPorCategoria: " + e.getMessage());
        }

        return resultado;
    }

    // 3. EVOLUÇÃO 6 MESES — alimenta o gráfico de barras

    public List<ResumoMensal> evolucaoSeisMeses(int usuarioId) {

        String sql = "SELECT ano, mes, total_receitas, total_despesas, saldo "
                + "FROM vw_resumo_mensal "
                + "WHERE usuario_id = ? "
                + "ORDER BY ano DESC, mes DESC "
                + "LIMIT 6";

        List<ResumoMensal> lista = new ArrayList<>();

        try (Connection conn = connectionFactory.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ResumoMensal r = new ResumoMensal();
                    r.setAno(rs.getInt("ano"));
                    r.setMes(rs.getInt("mes"));
                    r.setTotalReceitas(rs.getDouble("total_receitas"));
                    r.setTotalDespesas(rs.getDouble("total_despesas"));
                    r.setSaldo(rs.getDouble("saldo"));
                    lista.add(r);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro DashboardDAO.evolucaoSeisMeses: " + e.getMessage());
        }

        // Inverte para ordem cronológica (mais antigo primeiro no gráfico)
        Collections.reverse(lista);
        return lista;
    }

    // 4. ALERTAS DE ORÇAMENTO — categorias acima de 80% do limite

    public List<String> alertasOrcamento(int usuarioId, int mes, int ano) {

        // declarado FORA do try para estar no escopo do return
        List<String> alertas = new ArrayList<>();

        String sql = "SELECT c.nome, o.valor_limite, SUM(t.valor) AS gasto "
                + "FROM orcamentos o "
                + "JOIN categorias c ON c.id  = o.categoria_id "
                + "JOIN transacoes t ON t.categoria_id = o.categoria_id "
                + "                 AND t.usuario_id   = o.usuario_id "
                + "                 AND MONTH(t.data_transacao) = o.mes "
                + "                 AND YEAR(t.data_transacao)  = o.ano "
                + "WHERE o.usuario_id = ? "
                + "  AND o.mes = ? "
                + "  AND o.ano = ? "
                + "  AND t.tipo = 'EXPENSE' "
                + "GROUP BY c.nome, o.valor_limite "
                + "HAVING SUM(t.valor) / o.valor_limite > 0.8";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = connectionFactory.obterConexao();
            ps   = conn.prepareStatement(sql);

            ps.setInt(1, usuarioId);
            ps.setInt(2, mes);
            ps.setInt(3, ano);

            rs = ps.executeQuery();

            while (rs.next()) {
                alertas.add(rs.getString("nome"));
            }

        } catch (SQLException e) {
            System.err.println("Erro DashboardDAO.alertasOrcamento: " + e.getMessage());
        } finally {
            // fecha os recursos manualmente para evitar problemas de escopo
            try { if (rs   != null) rs.close();   } catch (SQLException ignored) {}
            try { if (ps   != null) ps.close();   } catch (SQLException ignored) {}
            try { if (conn != null) conn.close();  } catch (SQLException ignored) {}
        }

        return alertas;
    }

    // 5. CONTAGEM DE METAS ATIVAS — card KPI

    public int contarMetasAtivas(int usuarioId) {

        String sql = "SELECT COUNT(*) AS total "
                + "FROM metas "
                + "WHERE usuario_id = ? AND status = 'ATIVA'";

        try (Connection conn = connectionFactory.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro DashboardDAO.contarMetasAtivas: " + e.getMessage());
        }

        return 0;
    }
}