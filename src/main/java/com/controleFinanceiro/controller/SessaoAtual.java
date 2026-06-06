package com.controleFinanceiro.controller;

import com.controleFinanceiro.model.Usuario;

public class SessaoAtual {

    // único usuário logado na aplicação
    private static Usuario usuarioLogado = null;

    // construtor privado — impede instanciação
    private SessaoAtual() {}

    // MÉTODOS PRINCIPAIS

    /**
     * Inicia a sessão salvando o usuário que acabou de fazer login.
     * Chamado logo após o login ser validado com sucesso.
    */

    public static void iniciar(Usuario usuario) {
        usuarioLogado = usuario;
    }

    /**
     * Retorna o usuário atualmente logado.
     */
    public static Usuario getUsuario() {
        return usuarioLogado;
    }

   /**
     * Encerra a sessão — usado ao fazer logout.
     * Limpa o usuário logado da memória.
     */
    public static void encerrar() {
        usuarioLogado = null;
    }

    // MÉTODOS AUXILIARES

    /**
     * Verifica se há algum usuário logado no momento.
     *
     * @return true se há usuário logado, false caso contrário
     */

    public static boolean estaLogado() {
        return usuarioLogado != null;
    }

    /**
     * Verifica se o usuário logado é ADMIN.
     * Usado para mostrar/esconder menus e validar operações restritas.
     *
     * @return true se a funcao do usuário for "ADMIN"
     */

    public static boolean isAdmin() {
        return usuarioLogado != null
                && "ADMIN".equals(usuarioLogado.getFuncao());
    }

    /**
     * Retorna o ID do usuário logado.
     * Atalho para evitar SessaoAtual.getUsuario().getId() espalhado no código.
     *
     * @return ID do usuário, ou -1 se ninguém estiver logado
     */
    public static int getUsuarioId() {
        return usuarioLogado != null ? usuarioLogado.getId() : -1;
    }
}