package com.controleFinanceiro.view;

import com.controleFinanceiro.controller.SessaoAtual;
import com.controleFinanceiro.model.Transacao;
import com.controleFinanceiro.service.TransacaoService;
import com.controleFinanceiro.util.Formatador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TransacoesPanel extends JPanel {

    // ── Componentes ────────────────────────────────────────────────────────
    private JTable             tabela;
    private DefaultTableModel  model;
    private JComboBox<String>  cmbMes;
    private JComboBox<Integer> cmbAno;
    private JComboBox<String>  cmbFiltroTipo;
    private JLabel             lblReceitas;
    private JLabel             lblDespesas;
    private JLabel             lblSaldo;

    // ── Estado ─────────────────────────────────────────────────────────────
    private final TransacaoService service = new TransacaoService();
    private List<Transacao>        transacoesFiltradas;

    // ── Cores ──────────────────────────────────────────────────────────────
    private static final Color FUNDO       = new Color(15, 23, 42);
    private static final Color PAINEL      = new Color(30, 41, 59);
    private static final Color BORDA_C     = new Color(51, 65, 85);
    private static final Color TEXTO       = new Color(241, 245, 249);
    private static final Color TEXTO_SEC   = new Color(148, 163, 184);
    private static final Color VERDE       = new Color(16, 185, 129);
    private static final Color VERMELHO    = new Color(239, 68, 68);
    private static final Color AZUL        = new Color(59, 130, 246);
    private static final Color LINHA_PAR   = new Color(30, 41, 59);
    private static final Color LINHA_IMPAR = new Color(24, 33, 48);

    public TransacoesPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(FUNDO);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        construirUI();
        carregarTabela();
    }

    // CONSTRUÇÃO DA UI

    private void construirUI() {
        add(criarNorth(), BorderLayout.NORTH);
        add(criarCenter(), BorderLayout.CENTER);
        add(criarSouth(), BorderLayout.SOUTH);
    }

    // ── NORTH — título + filtros + botões ──────────────────────────────────
    private JPanel criarNorth() {
        JPanel north = new JPanel(new BorderLayout(0, 12));
        north.setBackground(FUNDO);
        north.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel titulo = new JLabel("Transacoes");
        titulo.setForeground(TEXTO);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        north.add(titulo, BorderLayout.NORTH);

        JPanel barraFiltros = new JPanel(new BorderLayout(0, 0));
        barraFiltros.setBackground(FUNDO);

        // Filtros à esquerda
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtros.setBackground(FUNDO);

        String[] meses = {"Janeiro","Fevereiro","Marco","Abril","Maio","Junho",
                "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
        cmbMes = new JComboBox<>(meses);
        cmbMes.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        estilizarCombo(cmbMes, 120);
        cmbMes.addActionListener(e -> carregarTabela());

        int anoAtual = LocalDate.now().getYear();
        cmbAno = new JComboBox<>(new Integer[]{anoAtual-1, anoAtual, anoAtual+1});
        cmbAno.setSelectedItem(anoAtual);
        estilizarCombo(cmbAno, 80);
        cmbAno.addActionListener(e -> carregarTabela());

        cmbFiltroTipo = new JComboBox<>(new String[]{"Todos", "Receitas", "Despesas"});
        estilizarCombo(cmbFiltroTipo, 130);
        cmbFiltroTipo.addActionListener(e -> filtrarTabela());

        JLabel lPer = new JLabel("Periodo:");
        lPer.setForeground(TEXTO_SEC);
        lPer.setFont(new Font("Arial", Font.PLAIN, 13));
        JLabel lTip = new JLabel("Tipo:");
        lTip.setForeground(TEXTO_SEC);
        lTip.setFont(new Font("Arial", Font.PLAIN, 13));

        filtros.add(lPer);
        filtros.add(cmbMes);
        filtros.add(cmbAno);
        filtros.add(Box.createHorizontalStrut(8));
        filtros.add(lTip);
        filtros.add(cmbFiltroTipo);

        // Botões à direita
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setBackground(FUNDO);

        JButton btnNova    = botao("+ Nova",   AZUL,                Color.WHITE);
        JButton btnEditar  = botao("Editar",   new Color(37,99,235), Color.WHITE);
        JButton btnExcluir = botao("Excluir",  new Color(127,29,29), new Color(252,165,165));

        btnNova.addActionListener(e -> abrirFormNova());
        btnEditar.addActionListener(e -> abrirFormEditar());
        btnExcluir.addActionListener(e -> excluirSelecionada());

        botoes.add(btnNova);
        botoes.add(btnEditar);
        botoes.add(btnExcluir);

        barraFiltros.add(filtros, BorderLayout.WEST);
        barraFiltros.add(botoes, BorderLayout.EAST);
        north.add(barraFiltros, BorderLayout.SOUTH);
        return north;
    }

    // ── CENTER — JTable ────────────────────────────────────────────────────
    private JScrollPane criarCenter() {
        String[] colunas = {"#", "Data", "Descricao", "Categoria", "Tipo", "Valor"};
        model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabela = new JTable(model);
        tabela.setBackground(LINHA_IMPAR);
        tabela.setForeground(TEXTO);
        tabela.setFont(new Font("Arial", Font.PLAIN, 13));
        tabela.setRowHeight(38);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setSelectionBackground(new Color(30, 58, 95));
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setFillsViewportHeight(true);

        tabela.getTableHeader().setBackground(new Color(13, 22, 38));
        tabela.getTableHeader().setForeground(new Color(100, 116, 139));
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        tabela.getTableHeader().setReorderingAllowed(false);

        int[] larguras = {40, 90, 200, 140, 90, 110};
        for (int i = 0; i < larguras.length; i++)
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);

        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBackground(sel ? new Color(30,58,95) : row%2==0 ? LINHA_PAR : LINHA_IMPAR);
                setForeground(TEXTO);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setFont(new Font("Arial", Font.PLAIN, 13));

                if (col == 5 && val != null) {
                    String v = val.toString();
                    setForeground(v.startsWith("+") ? VERDE : VERMELHO);
                    setFont(new Font("Arial", Font.BOLD, 13));
                }
                if (col == 4 && val != null) {
                    String v = val.toString();
                    setForeground("Receita".equals(v) ? VERDE : VERMELHO);
                    setFont(new Font("Arial", Font.BOLD, 13));
                }
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(BORDA_C, 1));
        scroll.setBackground(FUNDO);
        scroll.getViewport().setBackground(LINHA_IMPAR);
        return scroll;
    }

    // ── SOUTH — totalizadores ──────────────────────────────────────────────
    private JPanel criarSouth() {
        JPanel south = new JPanel(new GridLayout(1, 3, 12, 0));
        south.setBackground(FUNDO);
        south.setBorder(new EmptyBorder(12, 0, 0, 0));

        lblReceitas = criarCardTotal("Receitas", "R$ 0,00", VERDE,                  new Color(5,46,26));
        lblDespesas = criarCardTotal("Despesas", "R$ 0,00", VERMELHO,               new Color(28,10,10));
        lblSaldo    = criarCardTotal("Saldo",    "R$ 0,00", new Color(147,197,253), new Color(13,22,38));

        south.add(lblReceitas.getParent());
        south.add(lblDespesas.getParent());
        south.add(lblSaldo.getParent());
        return south;
    }

    private JLabel criarCardTotal(String titulo, String valor, Color corValor, Color corFundo) {
        CardKPI card = new CardKPI(corFundo, corFundo.brighter());
        card.setLayout(new GridLayout(2, 1));
        card.setPreferredSize(new Dimension(0, 60));

        JLabel lTit = new JLabel("  " + titulo);
        lTit.setForeground(corValor);
        lTit.setFont(new Font("Arial", Font.BOLD, 10));

        JLabel lVal = new JLabel("  " + valor);
        lVal.setForeground(Color.WHITE);
        lVal.setFont(new Font("Arial", Font.BOLD, 18));

        card.add(lTit);
        card.add(lVal);
        return lVal;
    }

    // ══════════════════════════════════════════════════════════════════════
    // LÓGICA DE DADOS
    // ══════════════════════════════════════════════════════════════════════

    public void carregarTabela() {
        int mes = cmbMes.getSelectedIndex() + 1;
        int ano = (Integer) cmbAno.getSelectedItem();
        int uid = SessaoAtual.getUsuarioId();
        transacoesFiltradas = service.listarPorMes(uid, mes, ano);
        popularModel(transacoesFiltradas);
        atualizarTotais(transacoesFiltradas);
    }

    private void filtrarTabela() {
        if (transacoesFiltradas == null) return;
        int idx = cmbFiltroTipo.getSelectedIndex();
        List<Transacao> filtradas = transacoesFiltradas.stream()
                .filter(t -> idx == 0
                        || (idx == 1 && "INCOME".equals(t.getTipo()))
                        || (idx == 2 && "EXPENSE".equals(t.getTipo())))
                .collect(java.util.stream.Collectors.toList());
        popularModel(filtradas);
        atualizarTotais(filtradas);
    }

    private void popularModel(List<Transacao> lista) {
        model.setRowCount(0);
        int i = 1;
        for (Transacao t : lista) {
            // ── CORRIGIDO: compara direto com o valor do banco ────────────
            boolean isReceita = "INCOME".equals(t.getTipo());
            String tipo  = isReceita ? "Receita" : "Despesa";
            String valor = (isReceita ? "+" : "-") + Formatador.moeda(t.getValor());
            String parc  = t.isParcelado()
                    ? t.getNumeroParcela() + "/" + t.getTotalParcelas() + " | " : "";
            model.addRow(new Object[]{
                    i++,
                    Formatador.data(t.getDataTransacao()),
                    parc + (t.getDescricao() != null ? t.getDescricao() : ""),
                    t.getCategoriaNome(),
                    tipo,
                    valor
            });
        }
    }

    private void atualizarTotais(List<Transacao> lista) {
        double rec = lista.stream()
                .filter(t -> "INCOME".equals(t.getTipo()))
                .mapToDouble(Transacao::getValor).sum();
        double des = lista.stream()
                .filter(t -> "EXPENSE".equals(t.getTipo()))
                .mapToDouble(Transacao::getValor).sum();
        double sal = rec - des;

        lblReceitas.setText("  " + Formatador.moeda(rec));
        lblDespesas.setText("  " + Formatador.moeda(des));
        lblSaldo.setText("  " + Formatador.moeda(sal));
        lblSaldo.setForeground(sal >= 0 ? new Color(147,197,253) : VERMELHO);
    }

    // ══════════════════════════════════════════════════════════════════════
    // AÇÕES DOS BOTÕES
    // ══════════════════════════════════════════════════════════════════════

    private void abrirFormNova() {
        TransacaoFormDialog d = new TransacaoFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), null);
        d.setVisible(true);
        if (d.isSalvo()) carregarTabela();
    }

    private void abrirFormEditar() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma transacao para editar.",
                    "Atencao", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (transacoesFiltradas == null || row >= transacoesFiltradas.size()) return;
        Transacao selecionada = transacoesFiltradas.get(row);
        TransacaoFormDialog d = new TransacaoFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), selecionada);
        d.setVisible(true);
        if (d.isSalvo()) carregarTabela();
    }

    private void excluirSelecionada() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma transacao para excluir.",
                    "Atencao", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (transacoesFiltradas == null || row >= transacoesFiltradas.size()) return;
        Transacao t = transacoesFiltradas.get(row);

        if (t.isParcelado() && t.getGrupoParcela() != null) {
            String[] opcoes = {"So esta parcela", "Todas as parcelas", "Cancelar"};
            int resp = JOptionPane.showOptionDialog(this,
                    "Esta e a parcela " + t.getNumeroParcela() + "/" + t.getTotalParcelas()
                            + ".\nO que deseja excluir?",
                    "Excluir Parcelamento",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, opcoes, opcoes[0]);
            if (resp == 0) service.excluir(t.getId(), SessaoAtual.getUsuarioId());
            else if (resp == 1) service.excluirGrupo(t.getGrupoParcela(), SessaoAtual.getUsuarioId());
            else return;
        } else {
            int conf = JOptionPane.showConfirmDialog(this,
                    "Excluir a transacao '" + t.getDescricao() + "'?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (conf != JOptionPane.YES_OPTION) return;
            service.excluir(t.getId(), SessaoAtual.getUsuarioId());
        }
        carregarTabela();
    }

    public void recarregar() { carregarTabela(); }

    // ══════════════════════════════════════════════════════════════════════
    // HELPERS DE ESTILO
    // ══════════════════════════════════════════════════════════════════════

    private <T> void estilizarCombo(JComboBox<T> c, int largura) {
        c.setBackground(PAINEL);
        c.setForeground(TEXTO);
        c.setFont(new Font("Arial", Font.PLAIN, 12));
        c.setPreferredSize(new Dimension(largura, 32));
        c.setBorder(BorderFactory.createLineBorder(BORDA_C, 1));
    }

    private JButton botao(String texto, Color bg, Color fg) {
        JButton b = new JButton(texto);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(100, 32));
        return b;
    }
}