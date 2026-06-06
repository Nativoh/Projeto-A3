package com.controleFinanceiro.DAO;

import com.controleFinanceiro.db.ConnectionFactory;
import com.controleFinanceiro.model.DicaFinanceira;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DicaFinanceiraDAO {

    // BUSCAR POR CONDIÇÃO — usado pelo DashboardService

    public DicaFinanceira buscarPorCondicao(String condicao) {

        String sql = "SELECT * FROM dicas_financeiras "
                + "WHERE condicao_gatilho = ? AND ativo = TRUE "
                + "LIMIT 1";

        try (Connection conn = new ConnectionFactory().obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, condicao);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro DicaFinanceiraDAO.buscarPorCondicao: " + e.getMessage());
        }

        return null;
    }

     // LISTAR TODAS — usado pelo AdminPanel

    public List<DicaFinanceira> listarTodas() {

        String sql = "SELECT * FROM dicas_financeiras ORDER BY condicao_gatilho, titulo";
        List<DicaFinanceira> lista = new ArrayList<>();

        try (Connection conn = new  ConnectionFactory().obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro DicaFinanceiraDAO.listarTodas: " + e.getMessage());
        }

        return lista;
    }

    public int contarAtivas() {
        String sql = "SELECT COUNT(*) FROM dicas_financeiras WHERE ativo = TRUE";
        try (Connection conn = new ConnectionFactory().obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Erro DicaFinanceiraDAO.contarAtivas: " + e.getMessage());
        }
        return 0;
    }

    // CRUD — gerenciado pelo ADMIN

    public boolean inserir(DicaFinanceira d) {
        String sql = "INSERT INTO dicas_financeiras "
                + "(titulo, conteudo, categoria, condicao_gatilho, ativo) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = new ConnectionFactory().obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getTitulo());
            ps.setString(2, d.getConteudo());
            ps.setString(3, d.getCategoria());
            ps.setString(4, d.getCondicaoGatilho());
            ps.setBoolean(5, d.isAtivo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro DicaFinanceiraDAO.inserir: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizar(DicaFinanceira d) {
        String sql = "UPDATE dicas_financeiras "
                + "SET titulo=?, conteudo=?, categoria=?, condicao_gatilho=?, ativo=? "
                + "WHERE id=?";
        try (Connection conn = new  ConnectionFactory().obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getTitulo());
            ps.setString(2, d.getConteudo());
            ps.setString(3, d.getCategoria());
            ps.setString(4, d.getCondicaoGatilho());
            ps.setBoolean(5, d.isAtivo());
            ps.setInt(6, d.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro DicaFinanceiraDAO.atualizar: " + e.getMessage());
            return false;
        }
    }

    /** Ativa ou desativa a dica sem excluí-la do banco. */
    public boolean alternarAtivo(int id) {
        String sql = "UPDATE dicas_financeiras SET ativo = NOT ativo WHERE id = ?";
        try (Connection conn = new  ConnectionFactory().obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro DicaFinanceiraDAO.alternarAtivo: " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // MAPPER — ResultSet → DicaFinanceira
    // ══════════════════════════════════════════════════════════════════════

    private DicaFinanceira mapear(ResultSet rs) throws SQLException {
        DicaFinanceira d = new DicaFinanceira();
        d.setId(rs.getInt("id"));
        d.setTitulo(rs.getString("titulo"));
        d.setConteudo(rs.getString("conteudo"));
        d.setCategoria(rs.getString("categoria"));
        d.setCondicaoGatilho(rs.getString("condicao_gatilho"));
        d.setAtivo(rs.getBoolean("ativo"));
        return d;
    }
}