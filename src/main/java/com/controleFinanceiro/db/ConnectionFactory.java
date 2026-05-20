package com.controleFinanceiro.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private String usuario = "root";
    private String senha = "254127767Vi@";
    private String host = "localhost";
    private int port = 3306;
    private String cb = "controle_financeiro";

    public Connection obterConexao() throws SQLException {
        try {
            Connection c = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + cb +
                            "?useTimezone=true&serverTimezone=UTC", usuario, senha
            );
            System.out.println("Conexão efetuada com sucesso!");
            return c;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados: " + e.getMessage(), e);
        }
    }
}