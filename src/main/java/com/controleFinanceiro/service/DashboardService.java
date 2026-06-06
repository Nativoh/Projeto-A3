package com.controleFinanceiro.service;

import com.controleFinanceiro.DAO.DicaFinanceiraDAO;
import com.controleFinanceiro.DAO.dashboardDAO;
import com.controleFinanceiro.DAO.UsuarioDAO;
import com.controleFinanceiro.model.*;

import java.util.List;
import java.util.Map;

public class DashboardService {

    private final dashboardDAO      dashboardDAO;
    private final DicaFinanceiraDAO dicaDAO;
    private final UsuarioDAO        usuarioDAO;

    public DashboardService() {
        this.dashboardDAO = new dashboardDAO();
        this.dicaDAO      = new DicaFinanceiraDAO();
        this.usuarioDAO   = new UsuarioDAO();
    }

    public DadosDashboard carregarDados(int usuarioId, int mes, int ano) {

        Usuario usuario = usuarioDAO.buscarPorId(usuarioId);
        ResumoMensal resumo = dashboardDAO.resumoMensal(usuarioId, mes, ano);

        // ── BASE DE CÁLCULO ────────────────────────────────────────────
        // Usa receitas reais do mês; se zero, tenta renda cadastrada;
        // se ambas zero, percentual = 0 (sem dados suficientes).
        double baseCalculo = resumo.getTotalReceitas() > 0
                ? resumo.getTotalReceitas()
                : (usuario != null && usuario.getRendaMensal() > 0
                   ? usuario.getRendaMensal()
                   : 0);

        double percentual = calcularPercentualGasto(
                resumo.getTotalDespesas(), baseCalculo);

        String saude = calcularSaude(percentual, resumo);

        DicaFinanceira dica = dicaDAO.buscarPorCondicao(saude);

        Map<String, Double> pizza  = dashboardDAO.gastosPorCategoria(usuarioId, mes, ano);
        List<ResumoMensal>  barras = dashboardDAO.evolucaoSeisMeses(usuarioId);

        return new DadosDashboard(resumo, pizza, barras, saude, dica, percentual);
    }

    // ── Percentual de gasto sobre a base ──────────────────────────────────
    public double calcularPercentualGasto(double totalDespesas, double base) {
        if (base <= 0) return 0;
        return (totalDespesas / base) * 100;
    }

    // ── Saúde considerando também saldo negativo ──────────────────────────
    public String calcularSaude(double percentual, ResumoMensal resumo) {
        // Saldo negativo = sempre RISCO, independente do percentual
        if (resumo.getSaldo() < 0) return "RISCO";

        // Despesas maiores ou iguais às receitas = RISCO
        if (resumo.getTotalReceitas() > 0
                && resumo.getTotalDespesas() >= resumo.getTotalReceitas()) return "RISCO";

        if (percentual >= 90) return "RISCO";
        if (percentual >= 70) return "ATENCAO";
        return "SAUDAVEL";
    }

    // Sobrecargas mantidas para compatibilidade
    public String calcularSaude(double percentual) {
        if (percentual >= 90) return "RISCO";
        if (percentual >= 70) return "ATENCAO";
        return "SAUDAVEL";
    }

    public String calcularSaude(double totalDespesas, double rendaMensal) {
        return calcularSaude(calcularPercentualGasto(totalDespesas, rendaMensal));
    }
}