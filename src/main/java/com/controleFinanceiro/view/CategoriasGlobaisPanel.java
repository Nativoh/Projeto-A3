package com.controleFinanceiro.view;

import com.controleFinanceiro.DAO.CategoriaDAO;
import com.controleFinanceiro.util.Formatador;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategoriasGlobaisPanel extends JPanel {

    private final CategoriaDAO dao = new CategoriaDAO();

    private JComboBox<String>  cmbMes;
    private JComboBox<Integer> cmbAno;
    private JComboBox<String>  cmbTipo;
    private JTable             tabela;
    private DefaultTableModel  model;
    private JPanel             painelGraficos;
    private JPanel             painelDetalhe;

    private List<Object[]>     dadosAtuais;

    private static final Color FUNDO     = new Color(15, 23, 42);
    private static final Color PAINEL    = new Color(30, 41, 59);
    private static final Color BORDA     = new Color(51, 65, 85);
    private static final Color TEXTO     = new Color(241, 245, 249);
    private static final Color TEXTO_SEC = new Color(148, 163, 184);
    private static final Color VERDE     = new Color(16, 185, 129);
    private static final Color VERMELHO  = new Color(239, 68, 68);
    private static final Color AMARELO   = new Color(234, 179, 8);
    private static final Color AZUL      = new Color(59, 130, 246);
    private static final Color ROXO      = new Color(139, 92, 246);

    public CategoriasGlobaisPanel() {
        setLayout(new BorderLayout());
        setBackground(FUNDO);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        construirUI();
        carregarDados();
    }

    private void construirUI() {
        add(criarNorth(),  BorderLayout.NORTH);
        add(criarCenter(), BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════════════════════════════
    // NORTH
    // ══════════════════════════════════════════════════════════════════════

    private JPanel criarNorth() {
        JPanel north = new JPanel(new BorderLayout(0, 12));
        north.setBackground(FUNDO);
        north.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel titles = new JPanel(new BorderLayout());
        titles.setBackground(FUNDO);
        JLabel titulo = new JLabel("Categorias Globais — Analise de Gastos");
        titulo.setForeground(TEXTO);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel sub = new JLabel("Distribuicao de transacoes por categoria em todos os usuarios");
        sub.setForeground(TEXTO_SEC);
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        titles.add(titulo, BorderLayout.NORTH);
        titles.add(sub,    BorderLayout.SOUTH);
        north.add(titles, BorderLayout.NORTH);

        // Filtros
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(FUNDO);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtros.setBackground(FUNDO);

        String[] meses = {"Janeiro","Fevereiro","Marco","Abril","Maio","Junho",
                "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
        cmbMes = new JComboBox<>(meses);
        cmbMes.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        estilizarCombo(cmbMes, 120);
        cmbMes.addActionListener(e -> carregarDados());

        int anoAtual = LocalDate.now().getYear();
        cmbAno = new JComboBox<>(new Integer[]{anoAtual-1, anoAtual, anoAtual+1});
        cmbAno.setSelectedItem(anoAtual);
        estilizarCombo(cmbAno, 80);
        cmbAno.addActionListener(e -> carregarDados());

    cmbTipo = new JComboBox<>(new String[]{"Todos", "Despesas", "Receitas"});
        estilizarCombo(cmbTipo, 110);
        cmbTipo.addActionListener(e -> filtrarTipo());

        filtros.add(label("Periodo:"));
        filtros.add(cmbMes);
        filtros.add(cmbAno);
        filtros.add(Box.createHorizontalStrut(8));
        filtros.add(label("Tipo:"));
        filtros.add(cmbTipo);
        barra.add(filtros, BorderLayout.WEST);

        north.add(barra, BorderLayout.SOUTH);
        return north;
    }

    // ══════════════════════════════════════════════════════════════════════
    // CENTER — split: tabela + graficos
    // ══════════════════════════════════════════════════════════════════════

    private JSplitPane criarCenter() {
        JPanel esquerda = new JPanel(new BorderLayout(0, 12));
        esquerda.setBackground(FUNDO);

        JPanel kpiWrapper = new JPanel(new BorderLayout());
        kpiWrapper.setBackground(FUNDO);
        kpiWrapper.setName("kpi");
        kpiWrapper.setPreferredSize(new Dimension(0, 75));
        esquerda.add(kpiWrapper, BorderLayout.NORTH);
        esquerda.add(criarTabela(), BorderLayout.CENTER);

        JPanel direita = new JPanel(new BorderLayout(0, 12));
        direita.setBackground(FUNDO);

        painelGraficos = new JPanel(new GridLayout(2, 1, 0, 12));
        painelGraficos.setBackground(FUNDO);
        // ← limita altura dos graficos
        painelGraficos.setPreferredSize(new Dimension(0, 420));
        direita.add(painelGraficos, BorderLayout.NORTH);

        painelDetalhe = new JPanel(new BorderLayout());
        painelDetalhe.setBackground(FUNDO);
        direita.add(painelDetalhe, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, esquerda, direita);
        // ← proporção: 55% esquerda, 45% direita
        split.setResizeWeight(0.55);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setBackground(FUNDO);

        // Aplica a divisão proporcional após o layout ser calculado
        SwingUtilities.invokeLater(() ->
                split.setDividerLocation(0.55));

        return split;
    }

    private JScrollPane criarTabela() {
        String[] colunas = {
                "#", "Categoria", "Tipo", "Total (R$)",
                "Transacoes", "Usuarios", "Participacao"
        };
        model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabela = new JTable(model);
        tabela.setBackground(new Color(24, 33, 48));
        tabela.setForeground(TEXTO);
        tabela.setFont(new Font("Arial", Font.PLAIN, 13));
        tabela.setRowHeight(36);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setSelectionBackground(new Color(30, 58, 95));
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setBackground(new Color(13, 22, 38));
        tabela.getTableHeader().setForeground(TEXTO_SEC);
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        tabela.getTableHeader().setReorderingAllowed(false);

        int[] larg = {35, 150, 80, 110, 90, 80, 100};
        for (int i = 0; i < larg.length; i++)
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larg[i]);

        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t,val,sel,foc,row,col);
                setBackground(sel ? new Color(30,58,95)
                        : row%2==0 ? PAINEL : new Color(24,33,48));
                setForeground(TEXTO);
                setBorder(new EmptyBorder(0,10,0,10));
                setFont(new Font("Arial", Font.PLAIN, 13));

                if (val == null) return this;
                switch (col) {
                    case 2 -> { // Tipo
                        setForeground("EXPENSE".equals(val.toString())
                                ? VERMELHO : VERDE);
                        setText("EXPENSE".equals(val.toString())
                                ? "Despesa" : "Receita");
                        setFont(new Font("Arial", Font.BOLD, 11));
                    }
                    case 3 -> { // Total
                        setFont(new Font("Arial", Font.BOLD, 13));
                    }
                    case 6 -> { // Participacao — barra visual em texto
                        setFont(new Font("Arial", Font.BOLD, 12));
                        try {
                            double pct = Double.parseDouble(
                                    val.toString().replace("%","")
                                            .replace(",",".").trim());
                            setForeground(pct >= 30 ? VERMELHO
                                    : pct >= 15 ? AMARELO : VERDE);
                        } catch (NumberFormatException ignored) {}
                    }
                }
                return this;
            }
        });

        // Listener — ao clicar numa categoria, mostra detalhe
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() >= 0)
                mostrarDetalhe(tabela.getSelectedRow());
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(BORDA, 1));
        scroll.getViewport().setBackground(new Color(24, 33, 48));
        return scroll;
    }

    // ══════════════════════════════════════════════════════════════════════
    // DADOS
    // ══════════════════════════════════════════════════════════════════════

    public void carregarDados() {
        int mes = cmbMes.getSelectedIndex() + 1;
        int ano = (Integer) cmbAno.getSelectedItem();

        // ← dadosAtuais DEVE ser setado antes de qualquer filtro
        dadosAtuais = dao.listarGlobaisComGastos(mes, ano);

        atualizarKPI(mes, ano);
        popularTabela(dadosAtuais);   // passa dadosAtuais sem filtro
        atualizarGraficos(dadosAtuais);

        painelDetalhe.removeAll();
        painelDetalhe.revalidate();
        painelDetalhe.repaint();

        // Reseta o combo para "Todos" ao trocar mes/ano
        cmbTipo.removeActionListener(cmbTipo.getActionListeners()[0]);
        cmbTipo.setSelectedIndex(0);
        cmbTipo.addActionListener(e -> filtrarTipo());
    }

    private void filtrarTipo() {
        if (dadosAtuais == null) return;
        String tipo = (String) cmbTipo.getSelectedItem();
        List<Object[]> filtrados = "Todos".equals(tipo)
                ? dadosAtuais
                : dadosAtuais.stream()
                  .filter(r -> {
                      String tipoDb = "Despesas".equals(tipo) ? "EXPENSE" : "INCOME";
                      return tipoDb.equals(r[2]);
                  })
                  .collect(Collectors.toList());
        popularTabela(filtrados);
        atualizarGraficos(filtrados);
        tabela.revalidate();
        tabela.repaint();
    }

    private void atualizarKPI(int mes, int ano) {
        // Busca o painel KPI pelo nome
        for (Component c : ((JPanel)((JSplitPane)getComponent(1))
                .getLeftComponent()).getComponents()) {
            if (c instanceof JPanel p && "kpi".equals(p.getName())) {
                p.removeAll();
                p.setLayout(new GridLayout(1, 3, 10, 0));
                p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

                double[] resumo = dao.resumoGlobal(mes, ano);
                double totalDesp   = resumo[0];
                double totalRec    = resumo[1];
                double usuariosAtv = resumo[2];

                p.add(kpiCard("Total Despesas",
                        Formatador.moeda(totalDesp),
                        VERMELHO, new Color(28,10,10), new Color(127,29,29)));
                p.add(kpiCard("Total Receitas",
                        Formatador.moeda(totalRec),
                        VERDE, new Color(5,46,26), new Color(6,95,70)));
                p.add(kpiCard("Usuarios Ativos",
                        String.valueOf((int) usuariosAtv),
                        AZUL, new Color(13,22,38), new Color(30,58,95)));

                p.revalidate();
                p.repaint();
                break;
            }
        }
    }

    private JPanel kpiCard(String titulo, String valor,
                           Color cor, Color fundo, Color borda) {
        CardKPI c = new CardKPI(fundo, borda);
        c.setLayout(new GridLayout(2, 1, 0, 2));
        JLabel lTit = new JLabel("  " + titulo);
        lTit.setForeground(cor);
        lTit.setFont(new Font("Arial", Font.BOLD, 10));
        JLabel lVal = new JLabel("  " + valor);
        lVal.setForeground(Color.WHITE);
        lVal.setFont(new Font("Arial", Font.BOLD, 18));
        c.add(lTit);
        c.add(lVal);
        return c;
    }

    private void popularTabela(List<Object[]> lista) {
        model.setRowCount(0);

        // Calcula total para participacao
        double totalGeral = lista.stream()
                .mapToDouble(r -> (double) r[4]).sum();

        int i = 1;
        for (Object[] r : lista) {
            double total = (double) r[4];
            double pct   = totalGeral > 0 ? (total / totalGeral) * 100 : 0;
            model.addRow(new Object[]{
                    i++,
                    r[1],  // nome
                    r[2],  // tipo
                    Formatador.moeda(total),
                    r[5],  // total_transacoes
                    r[6],  // usuarios_distintos
                    String.format("%.1f%%", pct)
            });
        }
    }

    private void atualizarGraficos(List<Object[]> lista) {
        painelGraficos.removeAll();

        // Grafico pizza — distribuicao por categoria
        DefaultPieDataset dsPizza = new DefaultPieDataset();
        lista.stream()
                .filter(r -> (double) r[4] > 0)
                .limit(8) // limita para legibilidade
                .forEach(r -> dsPizza.setValue(
                        r[1].toString(), (double) r[4]));
        if (dsPizza.getItemCount() == 0)
            dsPizza.setValue("Sem dados", 1.0);

        JFreeChart pizza = ChartFactory.createPieChart(
                "Distribuicao por Categoria", dsPizza, true, true, false);
        pizza.setBackgroundPaint(PAINEL);
        pizza.getTitle().setPaint(TEXTO);
        pizza.getTitle().setFont(new Font("Arial", Font.BOLD, 12));
        ChartPanel cpPizza = new ChartPanel(pizza);
        cpPizza.setBackground(PAINEL);
        cpPizza.setBorder(BorderFactory.createLineBorder(BORDA, 1));

        // Grafico barras — top categorias por valor
        DefaultCategoryDataset dsBarras = new DefaultCategoryDataset();
        lista.stream()
                .filter(r -> (double) r[4] > 0)
                .limit(6)
                .forEach(r -> dsBarras.addValue(
                        (double) r[4],
                        "EXPENSE".equals(r[2]) ? "Despesa" : "Receita",
                        r[1].toString()));

        JFreeChart barras = ChartFactory.createBarChart(
                "Top Categorias por Valor",
                "Categoria", "R$", dsBarras,
                PlotOrientation.HORIZONTAL, true, true, false);
        barras.setBackgroundPaint(PAINEL);
        barras.getTitle().setPaint(TEXTO);
        barras.getTitle().setFont(new Font("Arial", Font.BOLD, 12));
        ChartPanel cpBarras = new ChartPanel(barras);
        cpBarras.setBackground(PAINEL);
        cpBarras.setBorder(BorderFactory.createLineBorder(BORDA, 1));

        painelGraficos.add(cpPizza);
        painelGraficos.add(cpBarras);
        painelGraficos.revalidate();
        painelGraficos.repaint();
    }

    // ══════════════════════════════════════════════════════════════════════
    // DETALHE DA CATEGORIA SELECIONADA
    // ══════════════════════════════════════════════════════════════════════

    private void mostrarDetalhe(int rowIndex) {
        if (dadosAtuais == null || rowIndex >= dadosAtuais.size()) return;

        String tipo = (String) cmbTipo.getSelectedItem();
        List<Object[]> visiveis = "Todos".equals(tipo)
                ? dadosAtuais
                : dadosAtuais.stream()
                  .filter(r -> {
                      String tipoDb = "Despesas".equals(tipo) ? "EXPENSE" : "INCOME";
                      return tipoDb.equals(r[2]);
                  })
                  .collect(Collectors.toList());

        if (rowIndex >= visiveis.size()) return;
        Object[] dado = visiveis.get(rowIndex);

        int    catId = (int)    dado[0];
        String nome  = (String) dado[1];
        int mes = cmbMes.getSelectedIndex() + 1;
        int ano = (Integer) cmbAno.getSelectedItem();

        painelDetalhe.removeAll();
        painelDetalhe.setLayout(new BorderLayout(0, 8));

        JLabel lblTit = new JLabel("Detalhe: " + nome);
        lblTit.setForeground(TEXTO);
        lblTit.setFont(new Font("Arial", Font.BOLD, 13));
        lblTit.setBorder(new EmptyBorder(8, 0, 4, 0));
        painelDetalhe.add(lblTit, BorderLayout.NORTH);

        JPanel interno = new JPanel(new GridLayout(1, 2, 10, 0));
        interno.setBackground(FUNDO);

        // Evolucao 6 meses
        Map<String, Double> evolucao = dao.evolucaoCategoria(catId);
        DefaultCategoryDataset dsEv = new DefaultCategoryDataset();
        evolucao.forEach((k, v) -> dsEv.addValue(v, nome, k));

        JFreeChart chartEv = ChartFactory.createLineChart(
                "Evolucao 6 meses", "Mes", "R$",
                dsEv, PlotOrientation.VERTICAL, false, true, false);
        chartEv.setBackgroundPaint(PAINEL);
        chartEv.getTitle().setPaint(TEXTO);
        chartEv.getTitle().setFont(new Font("Arial", Font.BOLD, 11));
        ChartPanel cpEv = new ChartPanel(chartEv);
        cpEv.setBackground(PAINEL);
        cpEv.setBorder(BorderFactory.createLineBorder(BORDA, 1));
        interno.add(cpEv);

        // Top usuarios
        List<Object[]> topUsers = dao.topUsuariosPorCategoria(catId, mes, ano);
        String[] cols = {"Usuario", "E-mail", "Total", "Transacoes"};
        DefaultTableModel mTop = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Object[] u : topUsers) {
            mTop.addRow(new Object[]{
                    u[0], u[1],
                    Formatador.moeda((double) u[2]),
                    u[3]
            });
        }
        if (topUsers.isEmpty())
            mTop.addRow(new Object[]{"Sem transacoes", "—", "—", "—"});

        JTable tTop = new JTable(mTop);
        tTop.setBackground(new Color(24, 33, 48));
        tTop.setForeground(TEXTO);
        tTop.setFont(new Font("Arial", Font.PLAIN, 12));
        tTop.setRowHeight(28);
        tTop.setShowGrid(false);
        tTop.setIntercellSpacing(new Dimension(0, 0));
        tTop.getTableHeader().setBackground(new Color(13, 22, 38));
        tTop.getTableHeader().setForeground(TEXTO_SEC);
        tTop.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        tTop.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBackground(sel ? new Color(30, 58, 95)
                        : row % 2 == 0 ? PAINEL : new Color(24, 33, 48));
                setForeground(col == 2 ? AMARELO : TEXTO);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                setFont(new Font("Arial", col == 2 ? Font.BOLD : Font.PLAIN, 12));
                return this;
            }
        });

        int[] lTop = {130, 160, 90, 80};
        for (int i = 0; i < lTop.length; i++)
            tTop.getColumnModel().getColumn(i).setPreferredWidth(lTop[i]);

        JScrollPane scrollTop = new JScrollPane(tTop);
        scrollTop.setBorder(BorderFactory.createLineBorder(BORDA, 1));
        scrollTop.getViewport().setBackground(new Color(24, 33, 48));
        interno.add(scrollTop);

        painelDetalhe.add(interno, BorderLayout.CENTER);
        painelDetalhe.revalidate();
        painelDetalhe.repaint();
    }

    public void recarregar() { carregarDados(); }

    // ── Helpers ────────────────────────────────────────────────────────────

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(TEXTO_SEC);
        l.setFont(new Font("Arial", Font.PLAIN, 13));
        return l;
    }

    private <T> void estilizarCombo(JComboBox<T> c, int largura) {
        c.setBackground(PAINEL);
        c.setForeground(TEXTO);
        c.setFont(new Font("Arial", Font.PLAIN, 12));
        c.setPreferredSize(new Dimension(largura, 32));
        c.setBorder(BorderFactory.createLineBorder(BORDA, 1));
    }
}