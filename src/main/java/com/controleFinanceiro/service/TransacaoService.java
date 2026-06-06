package com.controleFinanceiro.service;

import com.controleFinanceiro.DAO.TransacaoDAO;
import com.controleFinanceiro.model.Transacao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


 //Service de Transações — regras de negócio e validações.

public class TransacaoService {

    private final TransacaoDAO dao = new TransacaoDAO();

    // REGISTRAR TRANSAÇÃO SIMPLES

    public boolean registrar(Transacao t) {
        validar(t);
        t.setParcelado(false);
        return dao.inserir(t);
    }

    // REGISTRAR PARCELAMENTO

    public boolean registrarParcelado(Transacao base, int qtd) {
        if (qtd < 2) throw new IllegalArgumentException("Parcelamento mínimo é 2x.");
        validar(base);

        String grupo = UUID.randomUUID().toString();
        double valorParcela = base.getValor() / qtd;
        List<Transacao> parcelas = new ArrayList<>();

        for (int i = 1; i <= qtd; i++) {
            Transacao p = new Transacao();
            p.setUsuarioId(base.getUsuarioId());
            p.setCategoriaId(base.getCategoriaId());
            p.setValor(Math.round(valorParcela * 100.0) / 100.0); // arredonda 2 casas
            p.setTipo(base.getTipo());
            p.setDescricao(base.getDescricao() + " (" + i + "/" + qtd + ")");
            p.setDataTransacao(base.getDataTransacao().plusMonths(i - 1));
            p.setParcelado(true);
            p.setNumeroParcela(i);
            p.setTotalParcelas(qtd);
            p.setGrupoParcela(grupo);
            parcelas.add(p);
        }

        return dao.inserirLote(parcelas);
    }

    // EDITAR E EXCLUIR

    public boolean editar(Transacao t) {
        validar(t);
        return dao.atualizar(t);
    }

    public boolean excluir(int id, int usuarioId) {
        return dao.excluir(id, usuarioId);
    }

    public boolean excluirGrupo(String grupoParcela, int usuarioId) {
        return dao.excluirGrupo(grupoParcela, usuarioId);
    }

    // LISTAR

    public List<Transacao> listarPorMes(int usuarioId, int mes, int ano) {
        return dao.listarPorMes(usuarioId, mes, ano);
    }

    public List<Transacao> listarTodas(int usuarioId) {
        return dao.listarTodas(usuarioId);
    }

    // VALIDAÇÃO

    private void validar(Transacao t) {
        if (t.getValor() <= 0)
            throw new IllegalArgumentException("O valor deve ser maior que zero.");
        if (t.getCategoriaId() <= 0)
            throw new IllegalArgumentException("Selecione uma categoria.");
        if (t.getDataTransacao() == null)
            throw new IllegalArgumentException("Informe a data da transação.");
        if (t.getTipo() == null || t.getTipo().isEmpty())
            throw new IllegalArgumentException("Selecione o tipo (Receita ou Despesa).");
    }
}
