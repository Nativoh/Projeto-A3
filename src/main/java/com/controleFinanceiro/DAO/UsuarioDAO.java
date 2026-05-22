package com.controleFinanceiro.DAO;

import com.controleFinanceiro.db.ConnectionFactory;
import com.controleFinanceiro.model.Usuario;
import com.mysql.cj.util.DnsSrv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
