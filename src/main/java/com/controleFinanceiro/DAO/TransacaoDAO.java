package com.controleFinanceiro.DAO;

import com.controleFinanceiro.db.ConnectionFactory;
import com.controleFinanceiro.model.Transacao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransacaoDAO {

    private final ConnectionFactory cf = new ConnectionFactory();

    // INSERIR

    public boolean inserir(Transacao t) {
        String sql = "INSERT INTO transacoes "
                + "(usuario_id, categoria_id, valor, tipo, descricao, "
                + " data_transacao, parcelado, numero_parcela, total_parcela, grupo_parcela) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getUsuarioId());
            ps.setInt(2, t.getCategoriaId());
            ps.setDouble(3, t.getValor());
            ps.setString(4, t.getTipo());
            ps.setString(5, t.getDescricao());
            ps.setDate(6, Date.valueOf(t.getDataTransacao()));
            ps.setBoolean(7, t.isParcelado());
            ps.setObject(8, t.isParcelado() ? t.getNumeroParcela() : null);
            ps.setObject(9, t.isParcelado() ? t.getTotalParcelas() : null);
            ps.setString(10, t.getGrupoParcela());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro TransacaoDAO.inserir: " + e.getMessage());
            return false;
        }
    }

    public boolean inserirLote(List<Transacao> parcelas) {
        for (Transacao t : parcelas) {
            if (!inserir(t)) return false;
        }
        return true;
    }

    // LISTAR

    public List<Transacao> listarPorMes(int usuarioId, int mes, int ano) {
        String sql = "SELECT t.*, c.nome AS categoria_nome "
                + "FROM transacoes t "
                + "JOIN categorias c ON c.id = t.categoria_id "
                + "WHERE t.usuario_id = ? "
                + "  AND MONTH(t.data_transacao) = ? "
                + "  AND YEAR(t.data_transacao)  = ? "
                + "ORDER BY t.data_transacao DESC";
        List<Transacao> lista = new ArrayList<>();
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, mes);
            ps.setInt(3, ano);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Erro TransacaoDAO.listarPorMes: " + e.getMessage());
        }
        return lista;
    }

    public List<Transacao> listarTodas(int usuarioId) {
        String sql = "SELECT t.*, c.nome AS categoria_nome "
                + "FROM transacoes t "
                + "JOIN categorias c ON c.id = t.categoria_id "
                + "WHERE t.usuario_id = ? "
                + "ORDER BY t.data_transacao DESC";
        List<Transacao> lista = new ArrayList<>();
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Erro TransacaoDAO.listarTodas: " + e.getMessage());
        }
        return lista;
    }

    // ATUALIZAR

    public boolean atualizar(Transacao t) {
        String sql = "UPDATE transacoes SET "
                + "categoria_id=?, valor=?, tipo=?, descricao=?, data_transacao=? "
                + "WHERE id=? AND usuario_id=?";
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getCategoriaId());
            ps.setDouble(2, t.getValor());
            ps.setString(3, t.getTipo());
            ps.setString(4, t.getDescricao());
            ps.setDate(5, Date.valueOf(t.getDataTransacao()));
            ps.setInt(6, t.getId());
            ps.setInt(7, t.getUsuarioId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro TransacaoDAO.atualizar: " + e.getMessage());
            return false;
        }
    }

    // EXCLUIR

    public boolean excluir(int id, int usuarioId) {
        String sql = "DELETE FROM transacoes WHERE id=? AND usuario_id=?";
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, usuarioId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro TransacaoDAO.excluir: " + e.getMessage());
            return false;
        }
    }

    public boolean excluirGrupo(String grupoParcela, int usuarioId) {
        String sql = "DELETE FROM transacoes WHERE grupo_parcela=? AND usuario_id=?";
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, grupoParcela);
            ps.setInt(2, usuarioId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro TransacaoDAO.excluirGrupo: " + e.getMessage());
            return false;
        }
    }

    // MAPPER

    private Transacao mapear(ResultSet rs) throws SQLException {
        Transacao t = new Transacao();
        t.setId(rs.getInt("id"));
        t.setUsuarioId(rs.getInt("usuario_id"));
        t.setCategoriaId(rs.getInt("categoria_id"));
        t.setCategoriaNome(rs.getString("categoria_nome"));
        t.setValor(rs.getDouble("valor"));
        t.setTipo(rs.getString("tipo"));
        t.setDescricao(rs.getString("descricao"));
        t.setDataTransacao(rs.getDate("data_transacao").toLocalDate());
        t.setParcelado(rs.getBoolean("parcelado"));
        t.setNumeroParcela(rs.getInt("numero_parcela"));
        t.setTotalParcelas(rs.getInt("total_parcela")); // coluna no banco: total_parcela
        t.setGrupoParcela(rs.getString("grupo_parcela"));
        return t;
    }
}