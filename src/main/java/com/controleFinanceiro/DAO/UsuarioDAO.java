package com.controleFinanceiro.DAO;

import com.controleFinanceiro.db.ConnectionFactory;
import com.controleFinanceiro.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private final ConnectionFactory cf = new ConnectionFactory();

    // LOGIN
    public Usuario Login(String email, String senha) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";
        try (Connection c = cf.obterConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, senha);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Erro UsuarioDAO.Login: " + e.getMessage());
        }
        return null;
    }

    // BUSCAR POR ID
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection c = cf.obterConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Erro UsuarioDAO.buscarPorId: " + e.getMessage());
        }
        return null;
    }

    // LISTAR TODOS
    public List<Usuario> listarTodos() {
        String sql = "SELECT * FROM usuarios ORDER BY ativo DESC, nome ASC";
        List<Usuario> lista = new ArrayList<>();
        try (Connection c = cf.obterConexao();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Erro UsuarioDAO.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    // INSERIR
    public boolean inserir(Usuario u) {
        String sql = "INSERT INTO usuarios "
                + "(nome, email, senha, funcao, renda_mensal, perfil_investidor, ativo) "
                + "VALUES (?,?,?,?,?,?,?)";
        try (Connection c = cf.obterConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getSenha());
            ps.setString(4, u.getFuncao() != null ? u.getFuncao() : "USER");
            ps.setDouble(5, u.getRendaMensal());
            ps.setString(6, u.getPerfilInvestidor());
            ps.setBoolean(7, u.isAtivo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro UsuarioDAO.inserir: " + e.getMessage());
            if (e.getMessage().contains("Duplicate")) {
                throw new IllegalStateException("Este e-mail ja esta em uso.");
            }
            return false;
        }
    }

    // ATUALIZAR
    public boolean atualizar(Usuario u) {
        String sql = "UPDATE usuarios SET nome=?, email=?, funcao=?, "
                + "renda_mensal=?, perfil_investidor=?, ativo=? "
                + "WHERE id=?";
        try (Connection c = cf.obterConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getFuncao());
            ps.setDouble(4, u.getRendaMensal());
            ps.setString(5, u.getPerfilInvestidor());
            ps.setBoolean(6, u.isAtivo());
            ps.setInt(7, u.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro UsuarioDAO.atualizar: " + e.getMessage());
            return false;
        }
    }

    // ALTERNAR ATIVO/INATIVO
    public boolean alternarAtivo(int id, boolean ativo) {
        String sql = "UPDATE usuarios SET ativo=? WHERE id=?";
        try (Connection c = cf.obterConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, ativo);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro UsuarioDAO.alternarAtivo: " + e.getMessage());
            return false;
        }
    }

    // EXCLUIR
    public boolean excluir(int id) {
        String sql = "DELETE FROM usuarios WHERE id=?";
        try (Connection c = cf.obterConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro UsuarioDAO.excluir: " + e.getMessage());
            return false;
        }
    }

    // VERIFICAR EMAIL DUPLICADO
    public boolean emailExiste(String email, int ignorarId) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email=? AND id != ?";
        try (Connection c = cf.obterConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, ignorarId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Erro UsuarioDAO.emailExiste: " + e.getMessage());
            return false;
        }
    }

    // MAPPER
    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNome(rs.getString("nome"));
        u.setEmail(rs.getString("email"));
        u.setSenha(rs.getString("senha"));
        u.setFuncao(rs.getString("funcao"));
        u.setRendaMensal(rs.getDouble("renda_mensal"));
        u.setPerfilInvestidor(rs.getString("perfil_investidor"));
        u.setAtivo(rs.getBoolean("ativo"));
        return u;
    }

    /** Insere novo usuário — usado pelo CadastroDialog */
    public boolean cadastrar(Usuario u) {
        String sql = "INSERT INTO usuarios "
                + "(nome, email, senha, funcao, renda_mensal, ativo) "
                + "VALUES (?,?,?,?,?,?)";
        try {
            Connection c = new ConnectionFactory().obterConexao();
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getSenha());
            ps.setString(4, u.getFuncao());
            ps.setDouble(5, u.getRendaMensal());
            ps.setBoolean(6, u.isAtivo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro UsuarioDAO.inserir: " + e.getMessage());
            return false;
        }
    }

    /** Busca por e-mail — usado no login e na validação do cadastro */
    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try {
            Connection c = new ConnectionFactory().obterConexao();
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNome(rs.getString("nome"));
                u.setEmail(rs.getString("email"));
                u.setSenha(rs.getString("senha"));
                u.setFuncao(rs.getString("funcao"));
                u.setRendaMensal(rs.getDouble("renda_mensal"));
                u.setAtivo(rs.getBoolean("ativo"));
                return u;
            }
        } catch (SQLException e) {
            System.out.println("Erro buscarPorEmail: " + e.getMessage());
        }
        return null;
    }
}