package com.controleFinanceiro.view;

import com.controleFinanceiro.controller.SessaoAtual;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class dashBoardAdmin extends JFrame {

    private AdminDashboardPanel adminDashboardPanel;
    private UsuariosPanel  usuariosPanel;
    private CategoriasGlobaisPanel categoriasGlobaisPanel;
    private CardLayout          cardLayout;
    private JPanel              painelConteudo;

    private JButton btnDashboard;
    private JButton btnUsuarios;
    private JButton btnCategorias;
    private JButton btnDicas;
    private JButton btnAtual = null;

    private static final Color FUNDO_LATERAL = new Color(30, 41, 59);
    private static final Color BTN_ATIVO     = new Color(46, 16, 101);
    private static final Color BTN_NORMAL    = new Color(30, 41, 59);
    private static final Color BTN_HOVER     = new Color(40, 30, 65);
    private static final Color TEXTO_ATIVO   = new Color(196, 181, 253);
    private static final Color TEXTO_NORMAL  = new Color(148, 163, 184);
    private static final Color SEP_COR       = new Color(51, 65, 85);

    public dashBoardAdmin() {
        configurarJanela();
        construirUI();
        navegarPara("dashboard", btnDashboard);
    }

    private void configurarJanela() {
        setTitle("Controle Financeiro - Administrador");
        setSize(1150, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void construirUI() {
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(new Color(15, 23, 42));
        add(criarMenuLateral(),    BorderLayout.WEST);
        add(criarPainelConteudo(), BorderLayout.CENTER);
    }

    private JPanel criarMenuLateral() {
        JPanel lateral = new JPanel();
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.setBackground(FUNDO_LATERAL);
        lateral.setPreferredSize(new Dimension(190, getHeight()));

        JPanel cab = new JPanel();
        cab.setLayout(new BoxLayout(cab, BoxLayout.Y_AXIS));
        cab.setBackground(FUNDO_LATERAL);
        cab.setBorder(new EmptyBorder(20, 16, 16, 16));
        cab.setMaximumSize(new Dimension(190, 80));

        JLabel lblLogo = new JLabel("Controle Financeiro");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel lblRole = new JLabel("Administrador");
        lblRole.setForeground(new Color(196, 181, 253));
        lblRole.setFont(new Font("Arial", Font.BOLD, 11));

        cab.add(lblLogo);
        cab.add(Box.createVerticalStrut(4));
        cab.add(lblRole);
        lateral.add(cab);
        lateral.add(separador());
        lateral.add(Box.createVerticalStrut(8));

        btnDashboard  = botaoMenu("Dashboard");
        btnUsuarios   = botaoMenu("Usuarios");
        btnCategorias = botaoMenu("Categorias");

        btnDashboard.addActionListener(e  -> navegarPara("dashboard",  btnDashboard));
        btnUsuarios.addActionListener(e   -> navegarPara("usuarios",   btnUsuarios));
        btnCategorias.addActionListener(e -> navegarPara("categorias", btnCategorias));

        lateral.add(btnDashboard);
        lateral.add(btnUsuarios);
        lateral.add(btnCategorias);
        lateral.add(Box.createVerticalGlue());
        lateral.add(separador());
        lateral.add(botaoLogout());
        lateral.add(Box.createVerticalStrut(12));
        return lateral;
    }

    private JPanel criarPainelConteudo() {
        cardLayout     = new CardLayout();
        painelConteudo = new JPanel(cardLayout);
        painelConteudo.setBackground(new Color(15, 23, 42));

        adminDashboardPanel = new AdminDashboardPanel();
        usuariosPanel = new UsuariosPanel();
        categoriasGlobaisPanel = new CategoriasGlobaisPanel();

        painelConteudo.add(wrapScroll(adminDashboardPanel), "dashboard");
        painelConteudo.add(wrapScroll(usuariosPanel),       "usuarios");
        painelConteudo.add(wrapScroll(categoriasGlobaisPanel), "categorias");
        painelConteudo.add(placeholder("Dicas Financeiras", "Em breve"), "dicas");

        return painelConteudo;
    }

    private void navegarPara(String nome, JButton botao) {
        if (btnAtual != null) {
            btnAtual.setBackground(BTN_NORMAL);
            btnAtual.setForeground(TEXTO_NORMAL);
        }
        botao.setBackground(BTN_ATIVO);
        botao.setForeground(TEXTO_ATIVO);
        btnAtual = botao;
        cardLayout.show(painelConteudo, nome);
        if ("dashboard".equals(nome)) adminDashboardPanel.recarregar();
    }

    private JButton botaoMenu(String texto) {
        JButton b = new JButton(texto);
        b.setMaximumSize(new Dimension(190, 44));
        b.setMinimumSize(new Dimension(190, 44));
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
                    "Deseja sair do sistema?", "Logout",
                    JOptionPane.YES_NO_OPTION);
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

    private JScrollPane wrapScroll(JPanel painel) {
        JScrollPane sc = new JScrollPane(painel);
        sc.setBorder(null);
        sc.getVerticalScrollBar().setUnitIncrement(16);
        sc.getViewport().setBackground(new Color(15, 23, 42));
        return sc;
    }

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