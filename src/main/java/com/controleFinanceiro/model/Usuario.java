package com.controleFinanceiro.model;


public class Usuario {

    private int    id;
    private String nome;
    private String email;
    private String senha;
    private String funcao;       // "ADMIN" ou "USER"
    private double rendaMensal;// usado pelo DashboardService
    public boolean isAtivo;

    // ── Construtores ───────────────────────────────────────────────────────

    public Usuario() {}

    public Usuario(int id, String nome, String email,
                   String senha, String funcao, double rendaMensal) {
        this.id           = id;
        this.nome         = nome;
        this.email        = email;
        this.senha        = senha;
        this.funcao       = funcao;
        this.rendaMensal  = rendaMensal;
    }

    // ── Getters e Setters ──────────────────────────────────────────────────

    public int    getId()                { return id; }
    public void   setId(int id)          { this.id = id; }

    public String getNome()              { return nome; }
    public void   setNome(String nome)   { this.nome = nome; }

    public String getEmail()             { return email; }
    public void   setEmail(String email) { this.email = email; }

    public String getSenha()             { return senha; }
    public void   setSenha(String senha) { this.senha = senha; }

    public String getFuncao()                { return funcao; }
    public void   setFuncao(String funcao)   { this.funcao = funcao; }

    public double getRendaMensal()               { return rendaMensal; }
    public void   setRendaMensal(double renda)   { this.rendaMensal = renda; }

    public boolean isAdmin() {
        return "ADMIN".equals(this.funcao);
    }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nome=" + nome + ", funcao=" + funcao + "}";
    }

    public void setAtivo(boolean ativo) {
    }
}