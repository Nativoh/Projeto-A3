package com.controleFinanceiro.service;

import com.controleFinanceiro.DAO.MetaDAO;
import com.controleFinanceiro.model.Meta;

import java.time.LocalDate;
import java.util.List;

public class MetaService {

    private final MetaDAO dao = new MetaDAO();

    public boolean criar(Meta m) {
        if (m.getNome() == null || m.getNome().isBlank())
            throw new IllegalArgumentException("Nome da meta e obrigatorio.");
        if (m.getValorAlvo() <= 0)
            throw new IllegalArgumentException("Valor alvo deve ser maior que zero.");
        if (m.getPrazo() == null || m.getPrazo().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Prazo deve ser uma data futura.");
        if (m.getStatus() == null) m.setStatus("ATIVA");
        return dao.inserir(m);
    }

    public boolean editar(Meta m) {
        if (m.getNome() == null || m.getNome().isBlank())
            throw new IllegalArgumentException("Nome da meta e obrigatorio.");
        if (m.getValorAlvo() <= 0)
            throw new IllegalArgumentException("Valor alvo deve ser maior que zero.");
        return dao.atualizar(m);
    }

    public boolean aportarValor(int metaId, int usuarioId, double aporte) {
        if (aporte <= 0)
            throw new IllegalArgumentException("O aporte deve ser maior que zero.");
        List<Meta> metas = dao.listarPorUsuario(usuarioId);
        Meta meta = metas.stream().filter(m -> m.getId() == metaId).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Meta nao encontrada."));
        double novoValor = meta.getValorAtual() + aporte;
        return dao.atualizarValorAtual(metaId, usuarioId, novoValor);
    }

    public boolean excluir(int id, int usuarioId) {
        return dao.excluir(id, usuarioId);
    }

    public List<Meta> listarTodas(int usuarioId) {
        return dao.listarPorUsuario(usuarioId);
    }

    public List<Meta> listarAtivas(int usuarioId) {
        return dao.listarAtivasPorUsuario(usuarioId);
    }
}