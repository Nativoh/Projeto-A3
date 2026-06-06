package com.controleFinanceiro.DAO;

import com.controleFinanceiro.db.ConnectionFactory;
import com.controleFinanceiro.model.Usuario;
import com.mysql.cj.util.DnsSrv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario Login(String email, String senha) {
        String sql = "SELECT * FROM usuarios WHERE email = ? and senha = ?";

        try {
            Connection c = new ConnectionFactory().obterConexao();
            PreparedStatement stmt = c.prepareStatement(sql);

            stmt.setString(1, email);
            stmt.setString(2, senha);

            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
               Usuario usuario = new Usuario();
               usuario.setId(rs.getInt("id"));
               usuario.setNome(rs.getString("nome"));
               usuario.setEmail(rs.getString("email"));
               usuario.setFuncao(rs.getString("funcao"));

               return usuario;
            }
        } catch (SQLException e) {
            System.out.println("Erro no login" + e.getMessage());
        }
        return null;
    }
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try {
            Connection c = new ConnectionFactory().obterConexao();
            PreparedStatement stmt = c.prepareStatement(sql);

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setFuncao(rs.getString("funcao"));
                usuario.setRendaMensal(rs.getDouble("renda_mensal")); // novo campo
                return usuario;
            }

        } catch (SQLException e) {
            System.out.println("Erro buscarPorId: " + e.getMessage());
        }

        return null;
    }

    public List<Usuario> listarTodos() {
        String sql = "SELECT * FROM usuarios ORDER BY ativo DESC, nome ASC";
        List<Usuario> lista = new ArrayList<>();
        try {
            Connection c = new ConnectionFactory().obterConexao();
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNome(rs.getString("nome"));
                u.setEmail(rs.getString("email"));
                u.setFuncao(rs.getString("funcao"));
                u.setRendaMensal(rs.getDouble("renda_mensal"));
                u.setAtivo(rs.getBoolean("ativo"));
                lista.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Erro listarTodos: " + e.getMessage());
        }
        return lista;
    }



}
