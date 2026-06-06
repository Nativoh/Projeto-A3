package com.controleFinanceiro.model;

import java.util.List;
import java.util.Map;

public class DadosDashboard {

    private ResumoMensal        resumo;
    private Map<String, Double> gastosPorCategoria;
    private List<ResumoMensal>  evolucaoMeses;
    private List<Meta>          metasAtivas;
    private int                 totalMetasAtivas;
    private List<Orcamento>     orcamentos;
    private String              saude;
    private DicaFinanceira      dica;
    private double              percentualGasto;

    public DadosDashboard(ResumoMensal resumo,
                          Map<String, Double> gastosPorCategoria,
                          List<ResumoMensal> evolucaoMeses,
                          String saude,
                          DicaFinanceira dica,
                          double percentualGasto) {
        this.resumo             = resumo;
        this.gastosPorCategoria = gastosPorCategoria;
        this.evolucaoMeses      = evolucaoMeses;
        this.saude              = saude;
        this.dica               = dica;
        this.percentualGasto    = percentualGasto;
    }

    public ResumoMensal         getResumo()                      { return resumo; }
    public void                 setResumo(ResumoMensal v)        { this.resumo = v; }

    public Map<String, Double>  getGastosPorCategoria()          { return gastosPorCategoria; }
    public void                 setGastosPorCategoria(Map<String,Double> v) { this.gastosPorCategoria = v; }

    public List<ResumoMensal>   getEvolucaoMeses()               { return evolucaoMeses; }
    public void                 setEvolucaoMeses(List<ResumoMensal> v)      { this.evolucaoMeses = v; }

    public List<Meta>           getMetasAtivas()                 { return metasAtivas; }
    public void                 setMetasAtivas(List<Meta> v)     { this.metasAtivas = v; }

    public int                  getTotalMetasAtivas()            { return totalMetasAtivas; }
    public void                 setTotalMetasAtivas(int v)       { this.totalMetasAtivas = v; }

    public List<Orcamento>      getOrcamentos()                  { return orcamentos; }
    public void                 setOrcamentos(List<Orcamento> v) { this.orcamentos = v; }

    public String               getSaude()                       { return saude; }
    public void                 setSaude(String v)               { this.saude = v; }

    public DicaFinanceira       getDica()                        { return dica; }
    public void                 setDica(DicaFinanceira v)        { this.dica = v; }

    public double               getPercentualGasto()             { return percentualGasto; }
    public void                 setPercentualGasto(double v)     { this.percentualGasto = v; }
}