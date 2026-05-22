package com.controleFinanceiro.model; // 👈 deve ser esse

public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String senha;
    private String funcao;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getFuncao() {return funcao;}
    public void setFuncao(String funcao) { this.funcao = funcao; }

    public boolean isAdmin(){
        return "ADMIN".equals(funcao);
    }
    public boolean isUser(){
        return "USER".equals(funcao);
    }
}