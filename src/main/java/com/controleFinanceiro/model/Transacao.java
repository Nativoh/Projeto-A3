package com.controleFinanceiro.model;

import java.time.LocalDate;

public class Transacao {

    private int id;
    private int usuaioId;
    private int categoriaId;
    private String categoriaNome;
    private double valor;
    private String tipo;
    private String descricao;
    private LocalDate dataTransacao;
    private boolean parcelado;
    private int numeroParcela;
    private int totalParcelas;
    private String grupoParcela;

    //Contrutores
    public Transacao() {
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getUsuarioId() {
        return  usuaioId;
    }
    public void setUsuarioId(int usuarioId) {
        this.usuaioId = usuarioId;
    }
    public int getCategoriaId() {
        return categoriaId;
    }
    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }
    public String getCategoriaNome() {
        return categoriaNome;
    }
    public void setCategoriaNome(String categoriaNome) {
        this.categoriaNome = categoriaNome;
    }
    public double getValor() {
        return valor;
    }
    public void setValor(double valor) {
        this.valor = valor;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public LocalDate getDataTransacao() {
        return dataTransacao;
    }
    public void setDataTransacao(LocalDate dataTransacao) {
        this.dataTransacao = dataTransacao;
    }
    public boolean isParcelado() {
        return parcelado;
    }
    public void setParcelado(boolean parcelado) {
        this.parcelado = parcelado;
    }
    public int getNumeroParcela() {
        return numeroParcela;
    }
    public void setNumeroParcela(int numeroParcela) {
        this.numeroParcela = numeroParcela;
    }
    public int getTotalParcelas() {
        return totalParcelas;
    }
    public void setTotalParcelas(int totalParcelas) {
        this.totalParcelas = totalParcelas;
    }
    public String getGrupoParcela() {
        return grupoParcela;
    }
    public void setGrupoParcela(String grupoParcela) {
        this.grupoParcela = grupoParcela;
    }

    public boolean isReceita(){
        return "EXPENSE".equalsIgnoreCase(tipo);
    }
    public boolean isDespesa(){
        return "INCOME".equalsIgnoreCase(tipo);
    }

    @Override
    public String toString() {
        return String.format(
                "Transacao{id=%d, descricao='%s', valor=%.2f, tipo='%s'}", id, descricao, valor, tipo);
    }
}





