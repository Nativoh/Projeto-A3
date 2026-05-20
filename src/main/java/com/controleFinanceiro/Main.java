package com.controleFinanceiro;

import com.controleFinanceiro.view.TelaLogin;

public class Main {
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new TelaLogin().setVisible(true);
        });
    }
}