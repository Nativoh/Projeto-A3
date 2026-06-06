package com.controleFinanceiro.view;

import com.controleFinanceiro.controller.SessaoAtual;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Janela principal do usuário (USER).
 * Navegação entre painéis via CardLayout — menu lateral fixo.
 */
public class dashBoardPrincipal extends JFrame {

    // ── Painéis de conteúdo ────────────────────────────────────────────────
    private DashboardPanel   dashboardPanel;
    private TransacoesPanel  transacoesPanel;
    private MetasPanel metasPanel;
    private OrcamentosPanel orcamentosPanel;

    // ── CardLayout ─────────────────────────────────────────────────────────
    private CardLayout cardLayout;
    private JPanel     painelConteudo;

    // ── Botões do menu ─────────────────────────────────────────────────────
    private JButton btnDashboard;
    private JButton btnTransacoes;
    private JButton btnMetas;
    private JButton btnOrcamentos;

    // ── Cores ──────────────────────────────────────────────────────────────
    private static final Color FUNDO_LATERAL  = new Color(30, 41, 59);
    private static final Color BTN_ATIVO      = new Color(30, 58, 95);
    private static final Color BTN_NORMAL     = new Color(30, 41, 59);
    private static final Color BTN_HOVER      = new Color(38, 50, 73);
    private static final Color TEXTO_ATIVO    = new Color(147, 197, 253);
    private static final Color TEXTO_NORMAL   = new Color(148, 163, 184);
    private static final Color SEP_COR        = new Color(51, 65, 85);

    // ── Aba atual ──────────────────────────────────────────────────────────
    private JButton btnAtual = null;

    @SuppressWarnings("unchecked")
    private void initComponents() {}

    public dashBoardPrincipal() {
        initComponents();
        configurarJanela();
        construirUI();
        navegarPara("dashboard", btnDashboard);
    }

    // ══════════════════════════════════════════════════════════════════════
    // CONFIGURAÇÃO DA JANELA
    // ══════════════════════════════════════════════════════════════════════

    private void configurarJanela() {
        setTitle("Controle Financeiro — " + SessaoAtual.getUsuario().getNome());
        setSize(1150, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // ══════════════════════════════════════════════════════════════════════
    // CONSTRUÇÃO DA UI
    // ══════════════════════════════════════════════════════════════════════

    private void construirUI() {
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(new Color(15, 23, 42));
        add(criarMenuLateral(), BorderLayout.WEST);
        add(criarPainelConteudo(), BorderLayout.CENTER);
    }

    // ── Menu lateral ───────────────────────────────────────────────────────
    private JPanel criarMenuLateral() {
        JPanel lateral = new JPanel();
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.setBackground(FUNDO_LATERAL);
        lateral.setPreferredSize(new Dimension(190, getHeight()));
        lateral.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Logo
        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setBackground(FUNDO_LATERAL);
        cabecalho.setBorder(new EmptyBorder(20, 16, 16, 16));
        cabecalho.setMaximumSize(new Dimension(190, 80));

        JLabel lblLogo = new JLabel("Controle Financeiro");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel lblUser = new JLabel(SessaoAtual.getUsuario().getNome());
        lblUser.setForeground(new Color(100, 116, 139));
        lblUser.setFont(new Font("Arial", Font.PLAIN, 11));

        cabecalho.add(lblLogo);
        cabecalho.add(Box.createVerticalStrut(4));
        cabecalho.add(lblUser);
        lateral.add(cabecalho);

        lateral.add(separador());
        lateral.add(Box.createVerticalStrut(8));

        // Botões de navegação
        btnDashboard   = botaoMenu("Dashboard");
        btnTransacoes  = botaoMenu("Transações");
        btnMetas       = botaoMenu("Metas");
        btnOrcamentos  = botaoMenu("Orçamentos");

        btnDashboard.addActionListener(e  -> navegarPara("dashboard",   btnDashboard));
        btnTransacoes.addActionListener(e -> navegarPara("transacoes",  btnTransacoes));
        btnMetas.addActionListener(e      -> navegarPara("metas",       btnMetas));
        btnOrcamentos.addActionListener(e -> navegarPara("orcamentos",  btnOrcamentos));

        lateral.add(btnDashboard);
        lateral.add(btnTransacoes);
        lateral.add(btnMetas);
        lateral.add(btnOrcamentos);
        lateral.add(Box.createVerticalGlue());

        lateral.add(separador());
        lateral.add(botaoLogout());
        lateral.add(Box.createVerticalStrut(12));

        return lateral;
    }

    // ── Painel de conteúdo com CardLayout ─────────────────────────────────
    private JPanel criarPainelConteudo() {
        cardLayout     = new CardLayout();
        painelConteudo = new JPanel(cardLayout);
        painelConteudo.setBackground(new Color(15, 23, 42));

        // ── Instancia os painéis ──────────────────────────────────────────
        dashboardPanel  = new DashboardPanel();
        transacoesPanel = new TransacoesPanel();
        metasPanel = new MetasPanel();
        orcamentosPanel = new OrcamentosPanel();

        // ── Adiciona ao CardLayout — nome deve bater com navegarPara() ────
        painelConteudo.add(wrapScroll(dashboardPanel),  "dashboard");
        painelConteudo.add(transacoesPanel,             "transacoes");
        painelConteudo.add(metasPanel,            "metas");
        painelConteudo.add(orcamentosPanel,       "orcamentos");

        return painelConteudo;
    }

    // ══════════════════════════════════════════════════════════════════════
    // NAVEGAÇÃO
    // ══════════════════════════════════════════════════════════════════════

    private void navegarPara(String nome, JButton botao) {
        // Atualiza visual dos botões
        if (btnAtual != null) {
            btnAtual.setBackground(BTN_NORMAL);
            btnAtual.setForeground(TEXTO_NORMAL);
        }
        botao.setBackground(BTN_ATIVO);
        botao.setForeground(TEXTO_ATIVO);
        btnAtual = botao;

        // Troca o painel
        cardLayout.show(painelConteudo, nome);

        // Recarrega os dados da aba que ficou visível
        switch (nome) {
            case "dashboard"  -> dashboardPanel.recarregar();
            case "transacoes" -> transacoesPanel.recarregar();
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════════════════

    private JButton botaoMenu(String texto) {
        JButton b = new JButton(texto);
        b.setMaximumSize(new Dimension(190, 44));
        b.setMinimumSize(new Dimension(190, 44));
        b.setPreferredSize(new Dimension(190, 44));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFont(new Font("Arial", Font.PLAIN, 13));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBackground(BTN_NORMAL);
        b.setForeground(TEXTO_NORMAL);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(0, 16, 0, 0));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (b != btnAtual) b.setBackground(BTN_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (b != btnAtual) b.setBackground(BTN_NORMAL);
            }
        });
        return b;
    }

    private JButton botaoLogout() {
        JButton b = botaoMenu("Sair");
        b.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this,
                    "Deseja sair do sistema?", "Logout", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                SessaoAtual.encerrar();
                new TelaLogin().setVisible(true);
                dispose();
            }
        });
        return b;
    }

    private JSeparator separador() {
        JSeparator s = new JSeparator();
        s.setForeground(SEP_COR);
        s.setMaximumSize(new Dimension(190, 1));
        return s;
    }

    /** Envolve um painel em JScrollPane — usado no DashboardPanel. */
    private JScrollPane wrapScroll(JPanel painel) {
        JScrollPane sc = new JScrollPane(painel);
        sc.setBorder(null);
        sc.getVerticalScrollBar().setUnitIncrement(16);
        sc.setBackground(new Color(15, 23, 42));
        sc.getViewport().setBackground(new Color(15, 23, 42));
        return sc;
    }

    /** Painel temporário para abas ainda não implementadas. */
    private JPanel placeholder(String titulo, String msg) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(15, 23, 42));
        JLabel l = new JLabel(titulo + " — " + msg);
        l.setForeground(new Color(100, 116, 139));
        l.setFont(new Font("Arial", Font.PLAIN, 18));
        p.add(l);
        return p;
    }
}
