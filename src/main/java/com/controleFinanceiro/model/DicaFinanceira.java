package com.controleFinanceiro.model;

public class DicaFinanceira {

    private int id;
    private String titulo;
    private String conteudo;
    private String categoria;
    private String condicaoGatilho;
    private boolean ativo;

    //construtores

    public DicaFinanceira() {}

    public DicaFinanceira(int id, String titulo, String conteudo,String condicaoGatilho, String categoria) {
        this.id = id;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.condicaoGatilho = condicaoGatilho;
        this.categoria = categoria;
        this.ativo = true;

    }

    //getters e setters

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getTitulo() {return titulo;}
    public void setTitulo(String v) {this.titulo = v;}

    public String getConteudo() {return conteudo;}
    public void setConteudo(String v) {this.conteudo = v;}

    public String getCategoria() {return categoria;}
    public void setCategoria(String v) {this.categoria = v;}

    public String getCondicaoGatilho() {return condicaoGatilho;}
    public void setCondicaoGatilho(String v) {this.condicaoGatilho = v;}

    public boolean isAtivo() {return ativo;}
    public void setAtivo(boolean v) {this.ativo = v;}

    @Override
    public String toString() {
        return titulo + "[" + condicaoGatilho + "]";
    }
}


