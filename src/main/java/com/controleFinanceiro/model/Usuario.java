package com.controleFinanceiro.model;

public class Usuario {

    private int    id;
    private String nome;
    private String email;
    private String senha;
    private String funcao;
    private double rendaMensal;
    private String perfilInvestidor;
    private boolean ativo;

    public Usuario() {}

    public Usuario(int id, String nome, String email,
                   String senha, String funcao, double rendaMensal) {
        this.id          = id;
        this.nome        = nome;
        this.email       = email;
        this.senha       = senha;
        this.funcao      = funcao;
        this.rendaMensal = rendaMensal;
    }

    public int     getId()                       { return id; }
    public void    setId(int id)                 { this.id = id; }

    public String  getNome()                     { return nome; }
    public void    setNome(String v)             { this.nome = v; }

    public String  getEmail()                    { return email; }
    public void    setEmail(String v)            { this.email = v; }

    public String  getSenha()                    { return senha; }
    public void    setSenha(String v)            { this.senha = v; }

    public String  getFuncao()                   { return funcao; }
    public void    setFuncao(String v)           { this.funcao = v; }

    public double  getRendaMensal()              { return rendaMensal; }
    public void    setRendaMensal(double v)      { this.rendaMensal = v; }

    public String  getPerfilInvestidor()         { return perfilInvestidor; }
    public void    setPerfilInvestidor(String v) { this.perfilInvestidor = v; }

    public boolean isAtivo()                     { return ativo; }
    public void    setAtivo(boolean v)           { this.ativo = v; }

    public boolean isAdmin() { return "ADMIN".equals(funcao); }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nome=" + nome + ", funcao=" + funcao + "}";
    }
}