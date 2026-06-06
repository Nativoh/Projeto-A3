package com.controleFinanceiro.view;

import com.controleFinanceiro.controller.OrcamentoController;
import com.controleFinanceiro.controller.SessaoAtual;
import com.controleFinanceiro.model.Orcamento;
import com.controleFinanceiro.util.Formatador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class OrcamentosPanel extends JPanel {

    private final OrcamentoController controller = new OrcamentoController();
    private List<Orcamento>           todosOrcamentos;
    private JPanel                    painelCards;
    private JComboBox<String>         cmbMes;
    private JComboBox<Integer>        cmbAno;
    private JComboBox<String>         cmbFiltro;
    private Orcamento                 selecionado  = null;
    private JPanel                    cardSelecionado = null;

    private static final Color FUNDO      = new Color(15, 23, 42);
    private static final Color PAINEL     = new Color(30, 41, 59);
    private static final Color PAINEL_SEL = new Color(30, 58, 95);
    private static final Color BORDA      = new Color(51, 65, 85);
    private static final Color TEXTO      = new Color(241, 245, 249);
    private static final Color TEXTO_SEC  = new Color(148, 163, 184);
    private static final Color VERDE      = new Color(16, 185, 129);
    private static final Color VERMELHO   = new Color(239, 68, 68);
    private static final Color AMARELO    = new Color(234, 179, 8);
    private static final Color AZUL       = new Color(59, 130, 246);

    public OrcamentosPanel() {
        setLayout(new BorderLayout());
        setBackground(FUNDO);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        construirUI();
        carregarOrcamentos();
    }

    private void construirUI() {
        add(criarNorth(), BorderLayout.NORTH);

        painelCards = new JPanel();
        painelCards.setLayout(new BoxLayout(painelCards, BoxLayout.Y_AXIS));
        painelCards.setBackground(FUNDO);

        JScrollPane scroll = new JScrollPane(painelCards);
        scroll.setBorder(BorderFactory.createLineBorder(BORDA, 1));
        scroll.setBackground(FUNDO);
        scroll.getViewport().setBackground(FUNDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        add(criarSouth(), BorderLayout.SOUTH);
    }

    private JPanel criarNorth() {
        JPanel north = new JPanel(new BorderLayout(0, 12));
        north.setBackground(FUNDO);
        north.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel titulo = new JLabel("Orcamentos");
        titulo.setForeground(TEXTO);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        north.add(titulo, BorderLayout.NORTH);

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(FUNDO);

        // Filtros
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtros.setBackground(FUNDO);

        String[] meses = {"Janeiro","Fevereiro","Marco","Abril","Maio","Junho",
                "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
        cmbMes = new JComboBox<>(meses);
        cmbMes.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        estilizarCombo(cmbMes, 120);
        cmbMes.addActionListener(e -> carregarOrcamentos());

        int anoAtual = LocalDate.now().getYear();
        cmbAno = new JComboBox<>(new Integer[]{anoAtual - 1, anoAtual, anoAtual + 1});
        cmbAno.setSelectedItem(anoAtual);
        estilizarCombo(cmbAno, 80);
        cmbAno.addActionListener(e -> carregarOrcamentos());

        cmbFiltro = new JComboBox<>(new String[]{"Todos", "No limite", "Atencao", "Estourado"});
        estilizarCombo(cmbFiltro, 120);
        cmbFiltro.addActionListener(e -> filtrar());

        JLabel lPer = new JLabel("Periodo:");
        lPer.setForeground(TEXTO_SEC);
        lPer.setFont(new Font("Arial", Font.PLAIN, 13));
        JLabel lFiltro = new JLabel("Status:");
        lFiltro.setForeground(TEXTO_SEC);
        lFiltro.setFont(new Font("Arial", Font.PLAIN, 13));

        filtros.add(lPer);
        filtros.add(cmbMes);
        filtros.add(cmbAno);
        filtros.add(Box.createHorizontalStrut(8));
        filtros.add(lFiltro);
        filtros.add(cmbFiltro);
        barra.add(filtros, BorderLayout.WEST);

        // Botoes
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setBackground(FUNDO);
        JButton btnNovo    = botao("+ Novo",  AZUL,                Color.WHITE,             90);
        JButton btnEditar  = botao("Editar",  new Color(37,99,235),Color.WHITE,              90);
        JButton btnExcluir = botao("Excluir", new Color(127,29,29),new Color(252,165,165),   90);
        btnNovo.addActionListener(e    -> abrirFormNovo());
        btnEditar.addActionListener(e  -> abrirFormEditar());
        btnExcluir.addActionListener(e -> excluir());
        botoes.add(btnNovo);
        botoes.add(btnEditar);
        botoes.add(btnExcluir);
        barra.add(botoes, BorderLayout.EAST);

        north.add(barra, BorderLayout.SOUTH);
        return north;
    }

    // ── Rodape com totalizadores ───────────────────────────────────────────
    private JPanel criarSouth() {
        JPanel south = new JPanel(new GridLayout(1, 3, 12, 0));
        south.setBackground(FUNDO);
        south.setBorder(new EmptyBorder(12, 0, 0, 0));
        south.setPreferredSize(new Dimension(0, 64));
        // Preenchido dinamicamente em atualizarTotais()
        south.setName("south");
        return south;
    }

    // ══════════════════════════════════════════════════════════════════════
    // DADOS
    // ══════════════════════════════════════════════════════════════════════

    public void carregarOrcamentos() {
        int mes = cmbMes.getSelectedIndex() + 1;
        int ano = (Integer) cmbAno.getSelectedItem();
        todosOrcamentos = controller.listarComGasto(
                SessaoAtual.getUsuarioId(), mes, ano);
        selecionado     = null;
        cardSelecionado = null;
        renderizar(todosOrcamentos);
        atualizarTotais(todosOrcamentos);
    }

    private void filtrar() {
        if (todosOrcamentos == null) return;
        String f = (String) cmbFiltro.getSelectedItem();
        List<Orcamento> filtrados = todosOrcamentos.stream().filter(o -> {
            double pct = o.getPercentualGasto();
            return switch (f) {
                case "No limite"  -> pct < 75;
                case "Atencao"    -> pct >= 75 && pct < 100;
                case "Estourado"  -> pct >= 100;
                default           -> true;
            };
        }).collect(Collectors.toList());
        selecionado     = null;
        cardSelecionado = null;
        renderizar(filtrados);
        atualizarTotais(filtrados);
    }

    private void renderizar(List<Orcamento> lista) {
        painelCards.removeAll();
        if (lista == null || lista.isEmpty()) {
            JLabel vazio = new JLabel(
                    "Nenhum orcamento encontrado para este periodo.",
                    SwingConstants.CENTER);
            vazio.setForeground(TEXTO_SEC);
            vazio.setFont(new Font("Arial", Font.PLAIN, 14));
            vazio.setAlignmentX(CENTER_ALIGNMENT);
            vazio.setBorder(new EmptyBorder(40, 0, 0, 0));
            painelCards.add(vazio);
        } else {
            for (Orcamento o : lista) {
                JPanel card = criarCard(o);
                adicionarClique(card, o);
                painelCards.add(card);
                painelCards.add(Box.createVerticalStrut(10));
            }
        }
        painelCards.revalidate();
        painelCards.repaint();
    }

    private void atualizarTotais(List<Orcamento> lista) {
        // Busca o painel south pelo nome
        for (Component c : getComponents()) {
            if (c instanceof JPanel p && "south".equals(p.getName())) {
                p.removeAll();
                double totalLimite = lista.stream()
                        .mapToDouble(Orcamento::getValorLimite).sum();
                double totalGasto  = lista.stream()
                        .mapToDouble(Orcamento::getValorGasto).sum();
                double totalDisp   = totalLimite - totalGasto;

                p.add(cardTotal("Total Limite",     Formatador.moeda(totalLimite),
                        new Color(147,197,253), new Color(13,22,38)));
                p.add(cardTotal("Total Gasto",      Formatador.moeda(totalGasto),
                        new Color(252,165,165), new Color(28,10,10)));
                p.add(cardTotal("Total Disponivel", Formatador.moeda(totalDisp),
                        totalDisp >= 0 ? VERDE : VERMELHO, new Color(5,46,26)));
                p.revalidate();
                p.repaint();
                break;
            }
        }
    }

    private JPanel cardTotal(String titulo, String valor, Color corValor, Color corFundo) {
        CardKPI card = new CardKPI(corFundo, corFundo.brighter());
        card.setLayout(new GridLayout(2, 1));
        JLabel lTit = new JLabel("  " + titulo);
        lTit.setForeground(corValor);
        lTit.setFont(new Font("Arial", Font.BOLD, 10));
        JLabel lVal = new JLabel("  " + valor);
        lVal.setForeground(Color.WHITE);
        lVal.setFont(new Font("Arial", Font.BOLD, 16));
        card.add(lTit);
        card.add(lVal);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════════
    // CARD VISUAL
    // ══════════════════════════════════════════════════════════════════════

    private JPanel criarCard(Orcamento o) {
        JPanel card = new JPanel(new BorderLayout(12, 6));
        card.setBackground(PAINEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        o.isEstourado() ? VERMELHO : BORDA, o.isEstourado() ? 2 : 1),
                new EmptyBorder(14, 16, 14, 16)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        card.setAlignmentX(LEFT_ALIGNMENT);

        // Topo: categoria + status textual
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(PAINEL);

        JLabel lblCat = new JLabel(o.getCategoriaNome());
        lblCat.setForeground(TEXTO);
        lblCat.setFont(new Font("Arial", Font.BOLD, 14));
        topo.add(lblCat, BorderLayout.WEST);

        double pct = o.getPercentualGasto();
        String statusTxt;
        Color  statusCor;
        if (pct >= 100)      { statusTxt = "ESTOURADO";  statusCor = VERMELHO; }
        else if (pct >= 75)  { statusTxt = "ATENCAO";    statusCor = AMARELO; }
        else                 { statusTxt = "OK";          statusCor = VERDE; }

        JLabel lblStatus = new JLabel(statusTxt);
        lblStatus.setForeground(statusCor);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 12));
        topo.add(lblStatus, BorderLayout.EAST);
        card.add(topo, BorderLayout.NORTH);

        // Meio: valores
        JPanel meio = new JPanel(new BorderLayout());
        meio.setBackground(PAINEL);
        JLabel lblValores = new JLabel(
                "Gasto: " + Formatador.moeda(o.getValorGasto())
                        + "   Limite: " + Formatador.moeda(o.getValorLimite())
                        + "   Disponivel: " + Formatador.moeda(o.getValorDisponivel()));
        lblValores.setForeground(TEXTO_SEC);
        lblValores.setFont(new Font("Arial", Font.PLAIN, 12));
        meio.add(lblValores, BorderLayout.WEST);
        JLabel lblPct = new JLabel(String.format("%.0f%%", pct));
        lblPct.setForeground(statusCor);
        lblPct.setFont(new Font("Arial", Font.BOLD, 13));
        meio.add(lblPct, BorderLayout.EAST);
        card.add(meio, BorderLayout.CENTER);

        // Barra de progresso
        JProgressBar barra = new JProgressBar(0, 100);
        barra.setValue(Math.min((int) pct, 100));
        barra.setStringPainted(false);
        barra.setBackground(BORDA);
        barra.setForeground(statusCor);
        barra.setBorder(BorderFactory.createEmptyBorder());
        barra.setPreferredSize(new Dimension(0, 8));
        card.add(barra, BorderLayout.SOUTH);

        return card;
    }

    // ── Clique com propagacao para filhos ──────────────────────────────────
    private void adicionarClique(JPanel card, Orcamento o) {
        MouseAdapter ml = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                selecionarCard(card, o);
            }
        };
        card.addMouseListener(ml);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        for (Component filho : card.getComponents()) {
            filho.addMouseListener(ml);
            filho.setCursor(new Cursor(Cursor.HAND_CURSOR));
            if (filho instanceof JPanel painel) {
                for (Component neto : painel.getComponents()) {
                    neto.addMouseListener(ml);
                    neto.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }
        }
    }

    private void selecionarCard(JPanel card, Orcamento o) {
        if (cardSelecionado != null) setCorCard(cardSelecionado, PAINEL);
        cardSelecionado = card;
        selecionado     = o;
        setCorCard(card, PAINEL_SEL);
    }

    private void setCorCard(JPanel card, Color cor) {
        card.setBackground(cor);
        for (Component filho : card.getComponents()) {
            filho.setBackground(cor);
            if (filho instanceof JPanel p) {
                for (Component neto : p.getComponents())
                    neto.setBackground(cor);
            }
        }
        card.repaint();
    }

    // ══════════════════════════════════════════════════════════════════════
    // ACOES
    // ══════════════════════════════════════════════════════════════════════

    private void abrirFormNovo() {
        OrcamentoFormDialog d = new OrcamentoFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), null);
        d.setVisible(true);
        if (d.isSalvo()) carregarOrcamentos();
    }

    private void abrirFormEditar() {
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Clique em um orcamento para seleciona-lo antes de editar.",
                    "Atencao", JOptionPane.WARNING_MESSAGE);
            return;
        }
        OrcamentoFormDialog d = new OrcamentoFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), selecionado);
        d.setVisible(true);
        if (d.isSalvo()) carregarOrcamentos();
    }

    private void excluir() {
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Clique em um orcamento para seleciona-lo antes de excluir.",
                    "Atencao", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this,
                "Excluir o orcamento de \"" + selecionado.getCategoriaNome() + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;
        if (controller.excluir(selecionado.getId(), SessaoAtual.getUsuarioId()))
            carregarOrcamentos();
    }

    public void recarregar() { carregarOrcamentos(); }

    // ══════════════════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════════════════

    private <T> void estilizarCombo(JComboBox<T> c, int largura) {
        c.setBackground(PAINEL);
        c.setForeground(TEXTO);
        c.setFont(new Font("Arial", Font.PLAIN, 12));
        c.setPreferredSize(new Dimension(largura, 32));
        c.setBorder(BorderFactory.createLineBorder(BORDA, 1));
    }

    private JButton botao(String texto, Color bg, Color fg, int largura) {
        JButton b = new JButton(texto);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(largura, 32));
        return b;
    }
}