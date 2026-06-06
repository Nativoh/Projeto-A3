package com.controleFinanceiro.controller;

import com.controleFinanceiro.model.Orcamento;
import com.controleFinanceiro.service.OrcamentoService;

import java.util.List;

public class OrcamentoController {

    private final OrcamentoService service = new OrcamentoService();

    public boolean criar(Orcamento o) {
        return service.criar(o);
    }

    public boolean editar(Orcamento o) {
        return service.editar(o);
    }

    public boolean excluir(int id, int usuarioId) {
        return service.excluir(id, usuarioId);
    }

    public List<Orcamento> listarComGasto(int usuarioId, int mes, int ano) {
        return service.listarComGasto(usuarioId, mes, ano);
    }
}