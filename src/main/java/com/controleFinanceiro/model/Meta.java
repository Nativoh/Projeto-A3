package com.controleFinanceiro.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Meta {

    private int           id;
    private int           usuarioId;
    private String        nome;
    private String        descricao;
    private double        valorAlvo;
    private double        valorAtual;
    private LocalDate     prazo;
    private String        status; // ATIVA, CONCLUIDA, CANCELADA
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    // ── Getters & Setters ──────────────────────────────────────────────────

    public int getId()                        { return id; }
    public void setId(int id)                 { this.id = id; }

    public int getUsuarioId()                 { return usuarioId; }
    public void setUsuarioId(int usuarioId)   { this.usuarioId = usuarioId; }

    public String getNome()                   { return nome; }
    public void setNome(String nome)          { this.nome = nome; }

    public String getDescricao()              { return descricao; }
    public void setDescricao(String d)        { this.descricao = d; }

    public double getValorAlvo()              { return valorAlvo; }
    public void setValorAlvo(double v)        { this.valorAlvo = v; }

    public double getValorAtual()             { return valorAtual; }
    public void setValorAtual(double v)       { this.valorAtual = v; }

    public LocalDate getPrazo()               { return prazo; }
    public void setPrazo(LocalDate prazo)     { this.prazo = prazo; }

    public String getStatus()                 { return status; }
    public void setStatus(String status)      { this.status = status; }

    public LocalDateTime getCriadoEm()        { return criadoEm; }
    public void setCriadoEm(LocalDateTime t)  { this.criadoEm = t; }

    public LocalDateTime getAtualizadoEm()       { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime t) { this.atualizadoEm = t; }

    // ── Helpers ────────────────────────────────────────────────────────────

    /** Percentual concluído (0–100). */
    public double getPercentual() {
        if (valorAlvo <= 0) return 0;
        return Math.min((valorAtual / valorAlvo) * 100, 100);
    }

    public boolean isConcluida()  { return "CONCLUIDA".equals(status); }
    public boolean isCancelada()  { return "CANCELADA".equals(status); }
    public boolean isAtiva()      { return "ATIVA".equals(status); }

    @Override
    public String toString() { return nome; }
}