package com.controleFinanceiro.controller;

import com.controleFinanceiro.DAO.MetaDAO;
import com.controleFinanceiro.DAO.OrcamentoDAO;
import com.controleFinanceiro.model.DadosDashboard;
import com.controleFinanceiro.model.Meta;
import com.controleFinanceiro.model.Orcamento;
import com.controleFinanceiro.service.DashboardService;

import java.time.LocalDate;
import java.util.List;

public class DashboardController {

    private final DashboardService service;
    private final MetaDAO          metaDAO;
    private final OrcamentoDAO     orcamentoDAO;

    public DashboardController() {
        this.service      = new DashboardService();
        this.metaDAO      = new MetaDAO();
        this.orcamentoDAO = new OrcamentoDAO();
    }

    public DadosDashboard carregarDados(int mes, int ano) {
        int usuarioId = SessaoAtual.getUsuarioId();
        DadosDashboard dados = service.carregarDados(usuarioId, mes, ano);

        // Injeta metas ativas
        List<Meta> metasAtivas = metaDAO.listarAtivasPorUsuario(usuarioId);
        dados.setMetasAtivas(metasAtivas);
        dados.setTotalMetasAtivas(metasAtivas.size());

        // Injeta orcamentos do mes com valor gasto
        List<Orcamento> orcamentos = orcamentoDAO.listarComGasto(usuarioId, mes, ano);
        dados.setOrcamentos(orcamentos);

        return dados;
    }

    public DadosDashboard carregarDados() {
        int mes = LocalDate.now().getMonthValue();
        int ano = LocalDate.now().getYear();
        return carregarDados(mes, ano);
    }
}