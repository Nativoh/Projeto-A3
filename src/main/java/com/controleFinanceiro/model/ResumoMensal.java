package com.controleFinanceiro.model;

public class ResumoMensal {

    private int usuarioId;
    private int ano;
    private int mes;
    private double totalReceitas;
    private double totalDespesas;
    private double saldo;

    // Construtor completo

    public ResumoMensal(int usuarioId, int ano, int mes, double totalReceitas, double totalDespesas) {

        this.usuarioId = usuarioId;
        this.ano = ano;
        this.mes = mes;
        this.totalReceitas = totalReceitas;
        this.totalDespesas = totalDespesas;
        this.saldo = saldo;
    }

    // Construtor simplificado - quando não precisar do ano e mes

    public ResumoMensal(double totalReceitas, double totalDespesas, double saldo) {
        this.totalReceitas = totalReceitas;
        this.totalDespesas = totalDespesas;
        this.saldo = saldo;
    }

    //Construtor vazio - para quando não houver dados no banco de dados

    public ResumoMensal() {
        this.totalReceitas = 0;
        this.totalDespesas = 0;
        this.saldo = 0;
    }

    // Getters e setters

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int v) { this.usuarioId = v; }

    public int getAno() { return ano; }
    public void setAno(int v) { this.ano = v; }

    public int getMes() { return mes; }
    public void setMes(int v) { this.mes = v; }

    public double getTotalReceitas() { return totalReceitas; }
    public void setTotalReceitas(double v) { this.totalReceitas = v; }

    public double getTotalDespesas() { return totalDespesas; }
    public void setTotalDespesas(double v) { this.totalDespesas = v; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double v) { this.saldo = v; }

    @Override

    public String toString() {
        return "ResumoMensal{mes=" + mes + "/" + ano
                + ", receitas=" + totalReceitas
                + ", despesas=" + totalDespesas
                + ", saldo=" + saldo + "}";
    }
}