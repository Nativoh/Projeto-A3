package com.controleFinanceiro.service;

import com.controleFinanceiro.DAO.OrcamentoDAO;
import com.controleFinanceiro.model.Orcamento;

import java.util.List;

public class OrcamentoService {

    private final OrcamentoDAO dao = new OrcamentoDAO();

    public boolean criar(Orcamento o) {
        if (o.getValorLimite() <= 0)
            throw new IllegalArgumentException("O valor limite deve ser maior que zero.");
        if (o.getMes() < 1 || o.getMes() > 12)
            throw new IllegalArgumentException("Mes invalido.");
        if (o.getAno() < 2000)
            throw new IllegalArgumentException("Ano invalido.");
        return dao.inserir(o); // lança IllegalStateException se duplicata
    }

    public boolean editar(Orcamento o) {
        if (o.getValorLimite() <= 0)
            throw new IllegalArgumentException("O valor limite deve ser maior que zero.");
        return dao.atualizar(o);
    }

    public boolean excluir(int id, int usuarioId) {
        return dao.excluir(id, usuarioId);
    }

    public List<Orcamento> listarComGasto(int usuarioId, int mes, int ano) {
        return dao.listarComGasto(usuarioId, mes, ano);
    }
}