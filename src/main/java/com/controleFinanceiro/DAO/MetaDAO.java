package com.controleFinanceiro.DAO;

import com.controleFinanceiro.db.ConnectionFactory;
import com.controleFinanceiro.model.Meta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MetaDAO {

    private final ConnectionFactory cf = new ConnectionFactory();

    // INSERIR
    public boolean inserir(Meta m) {
        String sql = "INSERT INTO metas "
                + "(usuario_id, nome, descricao, valor_alvo, valor_atual, prazo, status) "
                + "VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getUsuarioId());
            ps.setString(2, m.getNome());
            ps.setString(3, m.getDescricao());
            ps.setDouble(4, m.getValorAlvo());
            ps.setDouble(5, m.getValorAtual());
            ps.setDate(6, Date.valueOf(m.getPrazo()));
            ps.setString(7, m.getStatus() != null ? m.getStatus() : "ATIVA");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro MetaDAO.inserir: " + e.getMessage());
            return false;
        }
    }

    // LISTAR
    public List<Meta> listarPorUsuario(int usuarioId) {
        String sql = "SELECT * FROM metas WHERE usuario_id = ? ORDER BY prazo ASC";
        List<Meta> lista = new ArrayList<>();
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Erro MetaDAO.listarPorUsuario: " + e.getMessage());
        }
        return lista;
    }

    public List<Meta> listarAtivasPorUsuario(int usuarioId) {
        String sql = "SELECT * FROM metas WHERE usuario_id = ? AND status = 'ATIVA' ORDER BY prazo ASC";
        List<Meta> lista = new ArrayList<>();
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Erro MetaDAO.listarAtivasPorUsuario: " + e.getMessage());
        }
        return lista;
    }

    // ATUALIZAR
    public boolean atualizar(Meta m) {
        String sql = "UPDATE metas SET nome=?, descricao=?, valor_alvo=?, "
                + "valor_atual=?, prazo=?, status=? WHERE id=? AND usuario_id=?";
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNome());
            ps.setString(2, m.getDescricao());
            ps.setDouble(3, m.getValorAlvo());
            ps.setDouble(4, m.getValorAtual());
            ps.setDate(5, Date.valueOf(m.getPrazo()));
            ps.setString(6, m.getStatus());
            ps.setInt(7, m.getId());
            ps.setInt(8, m.getUsuarioId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro MetaDAO.atualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizarValorAtual(int id, int usuarioId, double novoValor) {
        String sql = "UPDATE metas SET valor_atual=?, "
                + "status = CASE WHEN ? >= valor_alvo THEN 'CONCLUIDA' ELSE status END "
                + "WHERE id=? AND usuario_id=?";
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, novoValor);
            ps.setDouble(2, novoValor);
            ps.setInt(3, id);
            ps.setInt(4, usuarioId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro MetaDAO.atualizarValorAtual: " + e.getMessage());
            return false;
        }
    }

    // EXCLUIR
    public boolean excluir(int id, int usuarioId) {
        String sql = "DELETE FROM metas WHERE id=? AND usuario_id=?";
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, usuarioId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro MetaDAO.excluir: " + e.getMessage());
            return false;
        }
    }

    // MAPPER
    private Meta mapear(ResultSet rs) throws SQLException {
        Meta m = new Meta();
        m.setId(rs.getInt("id"));
        m.setUsuarioId(rs.getInt("usuario_id"));
        m.setNome(rs.getString("nome"));
        m.setDescricao(rs.getString("descricao"));
        m.setValorAlvo(rs.getDouble("valor_alvo"));
        m.setValorAtual(rs.getDouble("valor_atual"));
        m.setPrazo(rs.getDate("prazo").toLocalDate());
        m.setStatus(rs.getString("status"));
        Timestamp criado = rs.getTimestamp("criado_em");
        if (criado != null) m.setCriadoEm(criado.toLocalDateTime());
        Timestamp atualizado = rs.getTimestamp("atualizado_em");
        if (atualizado != null) m.setAtualizadoEm(atualizado.toLocalDateTime());
        return m;
    }
}