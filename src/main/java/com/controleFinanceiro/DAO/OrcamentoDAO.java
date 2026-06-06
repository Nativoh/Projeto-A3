package com.controleFinanceiro.DAO;

import com.controleFinanceiro.db.ConnectionFactory;
import com.controleFinanceiro.model.Orcamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrcamentoDAO {

    private final ConnectionFactory cf = new ConnectionFactory();

    // INSERIR

    public boolean inserir(Orcamento o) {
        // Impede duplicata: um orçamento por categoria/mês/ano
        if (existePorCategoriaMesAno(o.getUsuarioId(),
                o.getCategoriaId(), o.getMes(), o.getAno())) {
            throw new IllegalStateException(
                    "Ja existe um orcamento para essa categoria neste mes/ano.");
        }
        String sql = "INSERT INTO orcamentos "
                + "(usuario_id, categoria_id, valor_limite, mes, ano) "
                + "VALUES (?,?,?,?,?)";
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, o.getUsuarioId());
            ps.setInt(2, o.getCategoriaId());
            ps.setDouble(3, o.getValorLimite());
            ps.setInt(4, o.getMes());
            ps.setInt(5, o.getAno());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro OrcamentoDAO.inserir: " + e.getMessage());
            return false;
        }
    }

    // LISTAR COM VALOR GASTO (JOIN com transacoes)

    public List<Orcamento> listarComGasto(int usuarioId, int mes, int ano) {
        String sql = "SELECT o.*, c.nome AS categoria_nome, "
                + "COALESCE(SUM(t.valor), 0) AS valor_gasto "
                + "FROM orcamentos o "
                + "JOIN categorias c ON c.id = o.categoria_id "
                + "LEFT JOIN transacoes t "
                + "  ON t.categoria_id = o.categoria_id "
                + "  AND t.usuario_id  = o.usuario_id "
                + "  AND t.tipo        = 'EXPENSE' "
                + "  AND MONTH(t.data_transacao) = o.mes "
                + "  AND YEAR(t.data_transacao)  = o.ano "
                + "WHERE o.usuario_id = ? AND o.mes = ? AND o.ano = ? "
                + "GROUP BY o.id "
                + "ORDER BY c.nome ASC";
        List<Orcamento> lista = new ArrayList<>();
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, mes);
            ps.setInt(3, ano);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Erro OrcamentoDAO.listarComGasto: " + e.getMessage());
        }
        return lista;
    }

    // ATUALIZAR

    public boolean atualizar(Orcamento o) {
        String sql = "UPDATE orcamentos SET valor_limite=? WHERE id=? AND usuario_id=?";
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, o.getValorLimite());
            ps.setInt(2, o.getId());
            ps.setInt(3, o.getUsuarioId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro OrcamentoDAO.atualizar: " + e.getMessage());
            return false;
        }
    }

    // EXCLUIR

    public boolean excluir(int id, int usuarioId) {
        String sql = "DELETE FROM orcamentos WHERE id=? AND usuario_id=?";
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, usuarioId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro OrcamentoDAO.excluir: " + e.getMessage());
            return false;
        }
    }

    // VERIFICAR DUPLICATA

    public boolean existePorCategoriaMesAno(int usuarioId, int categoriaId,
                                            int mes, int ano) {
        String sql = "SELECT COUNT(*) FROM orcamentos "
                + "WHERE usuario_id=? AND categoria_id=? AND mes=? AND ano=?";
        try (Connection conn = cf.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, categoriaId);
            ps.setInt(3, mes);
            ps.setInt(4, ano);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Erro OrcamentoDAO.existePorCategoriaMesAno: " + e.getMessage());
            return false;
        }
    }

    // MAPPER

    private Orcamento mapear(ResultSet rs) throws SQLException {
        Orcamento o = new Orcamento();
        o.setId(rs.getInt("id"));
        o.setUsuarioId(rs.getInt("usuario_id"));
        o.setCategoriaId(rs.getInt("categoria_id"));
        o.setCategoriaNome(rs.getString("categoria_nome"));
        o.setValorLimite(rs.getDouble("valor_limite"));
        o.setValorGasto(rs.getDouble("valor_gasto"));
        o.setMes(rs.getInt("mes"));
        o.setAno(rs.getInt("ano"));
        Timestamp criado = rs.getTimestamp("criado_em");
        if (criado != null) o.setCriadoEm(criado.toLocalDateTime());
        return o;
    }
}