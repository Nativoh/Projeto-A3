package com.controleFinanceiro.controller;

import com.controleFinanceiro.model.Meta;
import com.controleFinanceiro.service.MetaService;

import java.util.List;

public class MetaController {

    private final MetaService service = new MetaService();

    public boolean criarMeta(Meta m) {
        try {
            return service.criar(m);
        } catch (IllegalArgumentException e) {
            throw e; // repassa para a view tratar
        }
    }

    public boolean editarMeta(Meta m) {
        return service.editar(m);
    }

    public boolean aportar(int metaId, int usuarioId, double valor) {
        return service.aportarValor(metaId, usuarioId, valor);
    }

    public boolean excluirMeta(int id, int usuarioId) {
        return service.excluir(id, usuarioId);
    }

    public List<Meta> listarTodas(int usuarioId) {
        return service.listarTodas(usuarioId);
    }

    public List<Meta> listarAtivas(int usuarioId) {
        return service.listarAtivas(usuarioId);
    }
}