package com.controleFinanceiro.view;

import com.controleFinanceiro.controller.MetaController;
import com.controleFinanceiro.controller.SessaoAtual;
import com.controleFinanceiro.model.Meta;
import com.controleFinanceiro.util.Formatador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MetasPanel extends JPanel {

    private final MetaController controller = new MetaController();
    private List<Meta>           todasMetas;
    private JPanel               painelCards;
    private JComboBox<String>    cmbFiltro;
    private Meta                 metaSelecionada = null;
    private JPanel               cardSelecionado = null;

    private static final Color FUNDO        = new Color(15, 23, 42);
    private static final Color PAINEL       = new Color(30, 41, 59);
    private static final Color PAINEL_SEL   = new Color(30, 58, 95);  // azul selecionado
    private static final Color BORDA        = new Color(51, 65, 85);
    private static final Color TEXTO        = new Color(241, 245, 249);
    private static final Color TEXTO_SEC    = new Color(148, 163, 184);
    private static final Color VERDE        = new Color(16, 185, 129);
    private static final Color VERMELHO     = new Color(239, 68, 68);
    private static final Color AMARELO      = new Color(234, 179, 8);
    private static final Color AZUL         = new Color(59, 130, 246);

    public MetasPanel() {
        setLayout(new BorderLayout());
        setBackground(FUNDO);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        construirUI();
        carregarMetas();
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
    }

    private JPanel criarNorth() {
        JPanel north = new JPanel(new BorderLayout(0, 12));
        north.setBackground(FUNDO);
        north.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel titulo = new JLabel("Metas Financeiras");
        titulo.setForeground(TEXTO);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        north.add(titulo, BorderLayout.NORTH);

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(FUNDO);

        // Filtro
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtros.setBackground(FUNDO);
        JLabel lFiltro = new JLabel("Status:");
        lFiltro.setForeground(TEXTO_SEC);
        lFiltro.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbFiltro = new JComboBox<>(new String[]{"Todas", "ATIVA", "CONCLUIDA", "CANCELADA"});
        cmbFiltro.setBackground(PAINEL);
        cmbFiltro.setForeground(TEXTO);
        cmbFiltro.setFont(new Font("Arial", Font.PLAIN, 12));
        cmbFiltro.setPreferredSize(new Dimension(140, 32));
        cmbFiltro.addActionListener(e -> filtrarMetas());
        filtros.add(lFiltro);
        filtros.add(cmbFiltro);
        barra.add(filtros, BorderLayout.WEST);

        // Botoes
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setBackground(FUNDO);
        JButton btnNova    = botao("+ Nova Meta", AZUL,                Color.WHITE,              120);
        JButton btnAportar = botao("Aportar",     VERDE,               Color.WHITE,              100);
        JButton btnEditar  = botao("Editar",      new Color(37,99,235),Color.WHITE,               90);
        JButton btnExcluir = botao("Excluir",     new Color(127,29,29),new Color(252,165,165),    90);

        btnNova.addActionListener(e    -> abrirFormNova());
        btnAportar.addActionListener(e -> aportar());
        btnEditar.addActionListener(e  -> abrirFormEditar());
        btnExcluir.addActionListener(e -> excluir());

        botoes.add(btnNova);
        botoes.add(btnAportar);
        botoes.add(btnEditar);
        botoes.add(btnExcluir);
        barra.add(botoes, BorderLayout.EAST);

        north.add(barra, BorderLayout.SOUTH);
        return north;
    }

    // ══════════════════════════════════════════════════════════════════════
    // DADOS
    // ══════════════════════════════════════════════════════════════════════

    public void carregarMetas() {
        todasMetas      = controller.listarTodas(SessaoAtual.getUsuarioId());
        metaSelecionada = null;
        cardSelecionado = null;
        renderizar(todasMetas);
    }

    private void filtrarMetas() {
        if (todasMetas == null) return;
        String filtro = (String) cmbFiltro.getSelectedItem();
        List<Meta> filtradas = "Todas".equals(filtro)
                ? todasMetas
                : todasMetas.stream()
                  .filter(m -> filtro.equals(m.getStatus()))
                  .collect(Collectors.toList());
        metaSelecionada = null;
        cardSelecionado = null;
        renderizar(filtradas);
    }

    // ── Renderiza a lista de cards clicaveis ───────────────────────────────
    private void renderizar(List<Meta> metas) {
        painelCards.removeAll();
        if (metas == null || metas.isEmpty()) {
            JLabel vazio = new JLabel("Nenhuma meta encontrada.", SwingConstants.CENTER);
            vazio.setForeground(TEXTO_SEC);
            vazio.setFont(new Font("Arial", Font.PLAIN, 14));
            vazio.setAlignmentX(CENTER_ALIGNMENT);
            vazio.setBorder(new EmptyBorder(40, 0, 0, 0));
            painelCards.add(vazio);
        } else {
            for (Meta m : metas) {
                JPanel card = criarCard(m);
                adicionarListenerClique(card, m); // ← listener em card E filhos
                painelCards.add(card);
                painelCards.add(Box.createVerticalStrut(10));
            }
        }
        painelCards.revalidate();
        painelCards.repaint();
    }

    // ── Propaga clique do card e de todos os filhos ────────────────────────
    private void adicionarListenerClique(JPanel card, Meta m) {
        MouseAdapter listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selecionarCard(card, m);
            }
        };
        card.addMouseListener(listener);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Propaga para todos os subcomponentes
        for (Component filho : card.getComponents()) {
            filho.addMouseListener(listener);
            filho.setCursor(new Cursor(Cursor.HAND_CURSOR));
            // Se o filho for um painel, propaga mais um nível
            if (filho instanceof JPanel painel) {
                for (Component neto : painel.getComponents()) {
                    neto.addMouseListener(listener);
                    neto.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }
        }
    }

    private void selecionarCard(JPanel card, Meta m) {
        // Desmarca o anterior
        if (cardSelecionado != null) {
            definirCorCard(cardSelecionado, PAINEL);
        }
        // Marca o novo
        cardSelecionado = card;
        metaSelecionada = m;
        definirCorCard(card, PAINEL_SEL);
    }

    // Muda a cor de fundo do card e de todos os seus filhos
    private void definirCorCard(JPanel card, Color cor) {
        card.setBackground(cor);
        for (Component filho : card.getComponents()) {
            filho.setBackground(cor);
            if (filho instanceof JPanel painel) {
                for (Component neto : painel.getComponents()) {
                    neto.setBackground(cor);
                }
            }
        }
        card.repaint();
    }

    // ══════════════════════════════════════════════════════════════════════
    // CARD VISUAL
    // ══════════════════════════════════════════════════════════════════════

    private JPanel criarCard(Meta m) {
        JPanel card = new JPanel(new BorderLayout(12, 8));
        card.setBackground(PAINEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA, 1),
                new EmptyBorder(14, 16, 14, 16)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        card.setAlignmentX(LEFT_ALIGNMENT);

        // Topo: nome + badge status + prazo
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(PAINEL);

        JLabel lblNome = new JLabel(m.getNome());
        lblNome.setForeground(TEXTO);
        lblNome.setFont(new Font("Arial", Font.BOLD, 15));
        topo.add(lblNome, BorderLayout.WEST);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        direita.setBackground(PAINEL);

        JLabel badge = new JLabel(" " + m.getStatus() + " ");
        badge.setFont(new Font("Arial", Font.BOLD, 11));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));
        switch (m.getStatus()) {
            case "ATIVA"     -> { badge.setBackground(new Color(5,46,26));  badge.setForeground(VERDE); }
            case "CONCLUIDA" -> { badge.setBackground(new Color(13,22,38)); badge.setForeground(AZUL); }
            case "CANCELADA" -> { badge.setBackground(new Color(28,10,10)); badge.setForeground(VERMELHO); }
        }
        direita.add(badge);

        if (m.getPrazo() != null) {
            boolean vencida = m.getPrazo().isBefore(LocalDate.now()) && m.isAtiva();
            JLabel lblPrazo = new JLabel(
                    "Prazo: " + m.getPrazo().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            lblPrazo.setForeground(vencida ? VERMELHO : TEXTO_SEC);
            lblPrazo.setFont(new Font("Arial", Font.PLAIN, 12));
            direita.add(lblPrazo);
        }
        topo.add(direita, BorderLayout.EAST);
        card.add(topo, BorderLayout.NORTH);

        // Meio: descricao
        if (m.getDescricao() != null && !m.getDescricao().isBlank()) {
            JLabel lblDesc = new JLabel(m.getDescricao());
            lblDesc.setForeground(TEXTO_SEC);
            lblDesc.setFont(new Font("Arial", Font.PLAIN, 12));
            card.add(lblDesc, BorderLayout.CENTER);
        }

        // Rodape: valores + barra de progresso
        JPanel rodape = new JPanel(new BorderLayout(0, 4));
        rodape.setBackground(PAINEL);

        double pct = m.getPercentual();
        Color corBarra = pct >= 100 ? VERDE : pct >= 50 ? AMARELO : AZUL;

        JPanel linhaValores = new JPanel(new BorderLayout());
        linhaValores.setBackground(PAINEL);
        JLabel lblValores = new JLabel(
                Formatador.moeda(m.getValorAtual()) + " de " + Formatador.moeda(m.getValorAlvo()));
        lblValores.setForeground(TEXTO_SEC);
        lblValores.setFont(new Font("Arial", Font.PLAIN, 12));
        JLabel lblPct = new JLabel(String.format("%.0f%%", pct));
        lblPct.setForeground(corBarra);
        lblPct.setFont(new Font("Arial", Font.BOLD, 12));
        linhaValores.add(lblValores, BorderLayout.WEST);
        linhaValores.add(lblPct,     BorderLayout.EAST);

        JProgressBar barra = new JProgressBar(0, 100);
        barra.setValue((int) pct);
        barra.setStringPainted(false);
        barra.setBackground(BORDA);
        barra.setForeground(corBarra);
        barra.setBorder(BorderFactory.createEmptyBorder());
        barra.setPreferredSize(new Dimension(0, 8));

        rodape.add(linhaValores, BorderLayout.NORTH);
        rodape.add(barra,        BorderLayout.SOUTH);
        card.add(rodape, BorderLayout.SOUTH);

        return card;
    }

    // ══════════════════════════════════════════════════════════════════════
    // ACOES
    // ══════════════════════════════════════════════════════════════════════

    private void abrirFormNova() {
        MetaFormDialog d = new MetaFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), null);
        d.setVisible(true);
        if (d.isSalvo()) carregarMetas();
    }

    private void abrirFormEditar() {
        if (metaSelecionada == null) {
            JOptionPane.showMessageDialog(this,
                    "Clique em uma meta para selecioná-la antes de editar.",
                    "Atencao", JOptionPane.WARNING_MESSAGE);
            return;
        }
        MetaFormDialog d = new MetaFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), metaSelecionada);
        d.setVisible(true);
        if (d.isSalvo()) carregarMetas();
    }

    private void aportar() {
        if (metaSelecionada == null) {
            JOptionPane.showMessageDialog(this,
                    "Clique em uma meta para selecioná-la antes de aportar.",
                    "Atencao", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!metaSelecionada.isAtiva()) {
            JOptionPane.showMessageDialog(this,
                    "So e possivel aportar em metas ativas.",
                    "Atencao", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String input = JOptionPane.showInputDialog(this,
                "Valor do aporte para \"" + metaSelecionada.getNome() + "\" (R$):",
                "Aportar Valor", JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.isBlank()) return;
        try {
            double valor = Double.parseDouble(input.replace(",", ".").trim());
            boolean ok = controller.aportar(
                    metaSelecionada.getId(), SessaoAtual.getUsuarioId(), valor);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Aporte realizado com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                carregarMetas();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Valor invalido. Use o formato: 500,00",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (metaSelecionada == null) {
            JOptionPane.showMessageDialog(this,
                    "Clique em uma meta para selecioná-la antes de excluir.",
                    "Atencao", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this,
                "Excluir a meta \"" + metaSelecionada.getNome() + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;
        if (controller.excluirMeta(metaSelecionada.getId(), SessaoAtual.getUsuarioId()))
            carregarMetas();
    }

    public void recarregar() { carregarMetas(); }

    // ══════════════════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════════════════

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