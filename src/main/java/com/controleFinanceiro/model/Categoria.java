package com.controleFinanceiro.model;

public class Categoria {

    private int id;
    private Integer usuarioId;
    private String nome;
    private String tipo;
    private String icone;
    private String cor;

    public Categoria() {}

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Integer getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(Integer v) {
        this.usuarioId = v;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String v) {
        this.nome = v;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String v) {
        this.tipo = v;
    }
    public String getIcone() {
        return icone;
    }
    public void setIcone(String v) {
        this.icone = v;
    }
    public String getCor() {
        return cor;
    }
    public void setCor(String v) {
        this.cor = v;
    }

    @Override
    public String toString() {
        return nome;
    }
}
