package com.controleFinanceiro.view;

import com.controleFinanceiro.DAO.AdminDashboardDAO;

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

public class AdminDashboardPanel extends JPanel {

    private final AdminDashboardDAO dao = new AdminDashboardDAO();

    private static final Color FUNDO    = new Color(15, 23, 42);
    private static final Color PAINEL   = new Color(30, 41, 59);
    private static final Color BORDA    = new Color(51, 65, 85);
    private static final Color TEXTO    = new Color(241, 245, 249);
    private static final Color SEC      = new Color(148, 163, 184);
    private static final Color VERDE    = new Color(16, 185, 129);
    private static final Color AMARELO  = new Color(245, 158, 11);
    private static final Color VERMELHO = new Color(239, 68, 68);
    private static final Color AZUL     = new Color(59, 130, 246);
    private static final Color ROXO     = new Color(139, 92, 246);

    public AdminDashboardPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(FUNDO);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        construirUI();
    }

    private void construirUI() {
        int mes = LocalDate.now().getMonthValue();
        int ano = LocalDate.now().getYear();

        add(criarTitulo());
        add(Box.createVerticalStrut(16));

        add(criarCardsContagem(mes, ano));
        add(Box.createVerticalStrut(12));

        add(criarCardsSaude(mes, ano));
        add(Box.createVerticalStrut(12));

        add(criarGraficos(mes, ano));
        add(Box.createVerticalStrut(16));

        // Tabela completa de usuarios
        add(criarTabelaUsuarios(mes, ano));
        add(Box.createVerticalStrut(16));

        // Tabela de alertas (só RISCO e ATENCAO)
        add(criarTabelaAlertas(mes, ano));
    }

    // ══════════════════════════════════════════════════════════════════════
    // TITULO
    // ══════════════════════════════════════════════════════════════════════

    private JPanel criarTitulo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(FUNDO);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel t = new JLabel("Visao Geral do Sistema");
        t.setForeground(TEXTO);
        t.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel s = new JLabel("Analise consolidada de todos os usuarios");
        s.setForeground(SEC);
        s.setFont(new Font("Arial", Font.PLAIN, 12));

        p.add(t, BorderLayout.NORTH);
        p.add(s, BorderLayout.SOUTH);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════
    // CARDS CONTAGEM
    // ══════════════════════════════════════════════════════════════════════

    private JPanel criarCardsContagem(int mes, int ano) {
        JPanel linha = new JPanel(new GridLayout(1, 4, 10, 0));
        linha.setBackground(FUNDO);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        linha.add(card("Usuarios Ativos",
                String.valueOf(dao.contarUsuariosAtivos()),
                new Color(13,22,38), new Color(30,58,95), AZUL));
        linha.add(card("Categorias Globais",
                String.valueOf(dao.contarCategoriasGlobais()),
                new Color(23,9,50), new Color(88,28,135), ROXO));
        linha.add(card("Dicas Ativas",
                String.valueOf(dao.contarDicasAtivas()),
                new Color(4,38,42), new Color(6,95,115), new Color(103,232,249)));
        linha.add(card("Transacoes no Mes",
                String.valueOf(dao.contarTransacoesMes(mes, ano)),
                new Color(5,46,26), new Color(6,95,70), VERDE));
        return linha;
    }

    private JPanel card(String titulo, String valor,
                        Color fundo, Color borda, Color cor) {
        CardKPI c = new CardKPI(fundo, borda);
        c.setLayout(new GridLayout(2, 1, 0, 2));
        JLabel lTit = new JLabel("  " + titulo);
        lTit.setForeground(cor);
        lTit.setFont(new Font("Arial", Font.BOLD, 10));
        JLabel lVal = new JLabel("  " + valor);
        lVal.setForeground(Color.WHITE);
        lVal.setFont(new Font("Arial", Font.BOLD, 26));
        c.add(lTit);
        c.add(lVal);
        return c;
    }

    // ══════════════════════════════════════════════════════════════════════
    // CARDS SAUDE GLOBAL
    // ══════════════════════════════════════════════════════════════════════

    private JPanel criarCardsSaude(int mes, int ano) {
        JPanel linha = new JPanel(new GridLayout(1, 3, 10, 0));
        linha.setBackground(FUNDO);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        Map<String, Integer> dist = dao.distribuicaoSaude(mes, ano);

        linha.add(cardSaude("Em Risco",
                dist.getOrDefault("RISCO",    0),
                VERMELHO, new Color(28,10,10), new Color(127,29,29)));
        linha.add(cardSaude("Em Atencao",
                dist.getOrDefault("ATENCAO",  0),
                AMARELO,  new Color(28,20,0),  new Color(120,53,15)));
        linha.add(cardSaude("Saudaveis",
                dist.getOrDefault("SAUDAVEL", 0),
                VERDE,    new Color(5,46,26),  new Color(6,95,70)));

        return linha;
    }

    private JPanel cardSaude(String titulo, int qtd,
                             Color cor, Color fundo, Color borda) {
        CardKPI c = new CardKPI(fundo, borda);
        c.setLayout(new GridLayout(2, 1, 0, 2));
        JLabel lTit = new JLabel("  " + titulo);
        lTit.setForeground(cor);
        lTit.setFont(new Font("Arial", Font.BOLD, 11));
        JLabel lVal = new JLabel("  " + qtd + " usuario" + (qtd != 1 ? "s" : ""));
        lVal.setForeground(Color.WHITE);
        lVal.setFont(new Font("Arial", Font.PLAIN, 13));
        c.add(lTit);
        c.add(lVal);
        return c;
    }

    // ══════════════════════════════════════════════════════════════════════
    // GRAFICOS
    // ══════════════════════════════════════════════════════════════════════

    private JPanel criarGraficos(int mes, int ano) {
        JPanel linha = new JPanel(new GridLayout(1, 2, 10, 0));
        linha.setBackground(FUNDO);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));

        Map<String, Integer> dist = dao.distribuicaoSaude(mes, ano);
        DefaultPieDataset dsPizza = new DefaultPieDataset();
        dist.forEach((k, v) -> { if (v > 0) dsPizza.setValue(k, v); });
        if (dsPizza.getItemCount() == 0) dsPizza.setValue("Sem dados", 1);

        JFreeChart pizza = ChartFactory.createPieChart(
                "Saude Financeira dos Usuarios", dsPizza, true, true, false);
        pizza.setBackgroundPaint(PAINEL);
        pizza.getTitle().setPaint(TEXTO);
        pizza.getTitle().setFont(new Font("Arial", Font.BOLD, 12));
        ChartPanel cpPizza = new ChartPanel(pizza);
        cpPizza.setBackground(PAINEL);

        Map<String, Double> top = dao.topCategorias(mes, ano);
        DefaultCategoryDataset dsBarras = new DefaultCategoryDataset();
        top.forEach((k, v) -> dsBarras.addValue(v, "Gastos", k));

        JFreeChart barras = ChartFactory.createBarChart(
                "Top 5 Categorias do Sistema",
                "Categoria", "R$", dsBarras,
                PlotOrientation.VERTICAL, false, true, false);
        barras.setBackgroundPaint(PAINEL);
        barras.getTitle().setPaint(TEXTO);
        barras.getTitle().setFont(new Font("Arial", Font.BOLD, 12));
        ChartPanel cpBarras = new ChartPanel(barras);
        cpBarras.setBackground(PAINEL);

        linha.add(cpPizza);
        linha.add(cpBarras);
        return linha;
    }

    // ══════════════════════════════════════════════════════════════════════
    // TABELA COMPLETA DE USUARIOS
    // ══════════════════════════════════════════════════════════════════════

    private JPanel criarTabelaUsuarios(int mes, int ano) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setBackground(FUNDO);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        // Cabecalho com titulo + filtro
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(FUNDO);

        JLabel lblTit = new JLabel("Condicao Financeira dos Usuarios");
        lblTit.setForeground(TEXTO);
        lblTit.setFont(new Font("Arial", Font.BOLD, 14));
        cabecalho.add(lblTit, BorderLayout.WEST);

        // Filtro por status
        JComboBox<String> cmbFiltro = new JComboBox<>(
                new String[]{"Todos", "SAUDAVEL", "ATENCAO", "RISCO"});
        cmbFiltro.setBackground(PAINEL);
        cmbFiltro.setForeground(TEXTO);
        cmbFiltro.setFont(new Font("Arial", Font.PLAIN, 12));
        cmbFiltro.setPreferredSize(new Dimension(130, 28));
        cabecalho.add(cmbFiltro, BorderLayout.EAST);

        wrapper.add(cabecalho, BorderLayout.NORTH);

        // Colunas
        String[] colunas = {
                "Nome", "E-mail", "Perfil", "Receitas", "Despesas",
                "Saldo", "% Gasto", "Status", "Metas", "Transacoes", "Desde"
        };

        List<Object[]> dados = dao.listarUsuariosComCondicao(mes, ano);

        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // Indice: 0=id,1=nome,2=email,3=renda,4=receitas,5=despesas,
        //         6=saldo,7=pct,8=status,9=perfil,10=ativo,11=metas,12=transacoes,13=criado
        for (Object[] row : dados) {
            model.addRow(new Object[]{
                    row[1],  // nome
                    row[2],  // email
                    row[9],  // perfil_investidor
                    String.format("R$ %.2f", (double) row[4]),  // receitas
                    String.format("R$ %.2f", (double) row[5]),  // despesas
                    String.format("R$ %.2f", (double) row[6]),  // saldo
                    String.format("%.1f%%",  (double) row[7]),  // pct
                    row[8],  // status
                    row[11], // metas_ativas
                    row[12], // transacoes_mes
                    row[13]  // criado_em
            });
        }

        JTable tabela = criarTabela(model);

        // Coluna Status — colorida
        tabela.getColumnModel().getColumn(7)
                .setCellRenderer(new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable t, Object val,
                                                                   boolean sel, boolean foc, int row, int col) {
                        super.getTableCellRendererComponent(t,val,sel,foc,row,col);
                        setBackground(sel ? new Color(30,58,95)
                                : row%2==0 ? PAINEL : new Color(24,33,48));
                        setBorder(new EmptyBorder(0,12,0,12));
                        setFont(new Font("Arial", Font.BOLD, 12));
                        if (val != null) switch (val.toString()) {
                            case "RISCO"    -> { setForeground(VERMELHO); setText("RISCO"); }
                            case "ATENCAO"  -> { setForeground(AMARELO);  setText("ATENCAO"); }
                            case "SAUDAVEL" -> { setForeground(VERDE);    setText("OK"); }
                            default         -> setForeground(SEC);
                        }
                        return this;
                    }
                });

        // Coluna Saldo — verde/vermelho
        tabela.getColumnModel().getColumn(5)
                .setCellRenderer(new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable t, Object val,
                                                                   boolean sel, boolean foc, int row, int col) {
                        super.getTableCellRendererComponent(t,val,sel,foc,row,col);
                        setBackground(sel ? new Color(30,58,95)
                                : row%2==0 ? PAINEL : new Color(24,33,48));
                        setBorder(new EmptyBorder(0,12,0,12));
                        setFont(new Font("Arial", Font.BOLD, 12));
                        if (val != null) {
                            boolean neg = val.toString().contains("-");
                            setForeground(neg ? VERMELHO : VERDE);
                        }
                        return this;
                    }
                });

        // Larguras
        int[] larg = {160, 200, 100, 100, 100, 100, 70, 80, 60, 90, 90};
        for (int i = 0; i < larg.length; i++)
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larg[i]);

        // Filtro por status
        cmbFiltro.addActionListener(e -> {
            String filtro = (String) cmbFiltro.getSelectedItem();
            model.setRowCount(0);
            for (Object[] row : dados) {
                if ("Todos".equals(filtro) || filtro.equals(row[8])) {
                    model.addRow(new Object[]{
                            row[1], row[2], row[9],
                            String.format("R$ %.2f", (double) row[4]),
                            String.format("R$ %.2f", (double) row[5]),
                            String.format("R$ %.2f", (double) row[6]),
                            String.format("%.1f%%",  (double) row[7]),
                            row[8], row[11], row[12], row[13]
                    });
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(BORDA, 1));
        scroll.getViewport().setBackground(new Color(24, 33, 48));
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    // ══════════════════════════════════════════════════════════════════════
    // TABELA DE ALERTAS
    // ══════════════════════════════════════════════════════════════════════

    private JPanel criarTabelaAlertas(int mes, int ano) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setBackground(FUNDO);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        JLabel lblTit = new JLabel("Usuarios com Alertas Financeiros");
        lblTit.setForeground(VERMELHO);
        lblTit.setFont(new Font("Arial", Font.BOLD, 14));
        wrapper.add(lblTit, BorderLayout.NORTH);

        String[] colunas = {"Nome", "E-mail", "% Gasto", "Status"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<Object[]> alertas = dao.listarAlertas(mes, ano);
        if (alertas.isEmpty()) {
            model.addRow(new Object[]{"Nenhum alerta no momento", "—", "—", "—"});
        } else {
            for (Object[] row : alertas) model.addRow(row);
        }

        JTable tabela = criarTabela(model);

        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t,val,sel,foc,row,col);
                setBackground(sel ? new Color(30,58,95)
                        : row%2==0 ? PAINEL : new Color(24,33,48));
                setForeground(TEXTO);
                setBorder(new EmptyBorder(0,12,0,12));
                setFont(new Font("Arial", Font.PLAIN, 13));
                if (col == 3 && val != null) {
                    switch (val.toString()) {
                        case "RISCO"   -> { setForeground(VERMELHO);
                            setFont(new Font("Arial",Font.BOLD,13));
                            setText("RISCO"); }
                        case "ATENCAO" -> { setForeground(AMARELO);
                            setFont(new Font("Arial",Font.BOLD,13));
                            setText("ATENCAO"); }
                    }
                }
                return this;
            }
        });

        int[] larg = {220, 280, 120, 120};
        for (int i = 0; i < larg.length; i++)
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larg[i]);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(BORDA, 1));
        scroll.getViewport().setBackground(new Color(24, 33, 48));
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    // ══════════════════════════════════════════════════════════════════════
    // HELPER — cria JTable ja estilizada
    // ══════════════════════════════════════════════════════════════════════

    private JTable criarTabela(DefaultTableModel model) {
        JTable tabela = new JTable(model);
        tabela.setBackground(new Color(24, 33, 48));
        tabela.setForeground(TEXTO);
        tabela.setFont(new Font("Arial", Font.PLAIN, 12));
        tabela.setRowHeight(36);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setSelectionBackground(new Color(30, 58, 95));
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setBackground(new Color(13, 22, 38));
        tabela.getTableHeader().setForeground(SEC);
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        tabela.getTableHeader().setReorderingAllowed(false);

        // Renderer padrao para linhas alternadas
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t,val,sel,foc,row,col);
                setBackground(sel ? new Color(30,58,95)
                        : row%2==0 ? PAINEL : new Color(24,33,48));
                setForeground(TEXTO);
                setBorder(new EmptyBorder(0,12,0,12));
                setFont(new Font("Arial", Font.PLAIN, 12));
                return this;
            }
        });
        return tabela;
    }

    public void recarregar() {
        removeAll();
        construirUI();
        revalidate();
        repaint();
    }
}