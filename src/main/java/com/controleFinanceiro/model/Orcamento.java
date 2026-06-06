package com.controleFinanceiro.model;

import java.time.LocalDateTime;

public class Orcamento {

    private int            id;
    private int            usuarioId;
    private int            categoriaId;
    private String         categoriaNome;
    private double         valorLimite;
    private double         valorGasto;   // calculado via JOIN com transacoes
    private int            mes;
    private int            ano;
    private LocalDateTime  criadoEm;

    // ── Getters & Setters ──────────────────────────────────────────────────

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public int getUsuarioId()                   { return usuarioId; }
    public void setUsuarioId(int v)             { this.usuarioId = v; }

    public int getCategoriaId()                 { return categoriaId; }
    public void setCategoriaId(int v)           { this.categoriaId = v; }

    public String getCategoriaNome()            { return categoriaNome; }
    public void setCategoriaNome(String v)      { this.categoriaNome = v; }

    public double getValorLimite()              { return valorLimite; }
    public void setValorLimite(double v)        { this.valorLimite = v; }

    public double getValorGasto()               { return valorGasto; }
    public void setValorGasto(double v)         { this.valorGasto = v; }

    public int getMes()                         { return mes; }
    public void setMes(int v)                   { this.mes = v; }

    public int getAno()                         { return ano; }
    public void setAno(int v)                   { this.ano = v; }

    public LocalDateTime getCriadoEm()          { return criadoEm; }
    public void setCriadoEm(LocalDateTime v)    { this.criadoEm = v; }

    // ── Helpers ────────────────────────────────────────────────────────────

    /** Percentual do limite já gasto (0–100+). */
    public double getPercentualGasto() {
        if (valorLimite <= 0) return 0;
        return (valorGasto / valorLimite) * 100;
    }

    /** true se já ultrapassou o limite. */
    public boolean isEstourado() {
        return valorGasto > valorLimite;
    }

    /** Valor disponível restante (pode ser negativo se estourado). */
    public double getValorDisponivel() {
        return valorLimite - valorGasto;
    }
}