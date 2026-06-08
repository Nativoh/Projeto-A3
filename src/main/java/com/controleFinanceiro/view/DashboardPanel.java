package com.controleFinanceiro.view;

import com.controleFinanceiro.controller.DashboardController;
import com.controleFinanceiro.model.DadosDashboard;
import com.controleFinanceiro.model.DicaFinanceira;
import com.controleFinanceiro.model.Meta;
import com.controleFinanceiro.model.Orcamento;
import com.controleFinanceiro.model.ResumoMensal;
import com.controleFinanceiro.util.Formatador;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class DashboardPanel extends JPanel {

    private final DashboardController controller;

    // ── Filtros de periodo ─────────────────────────────────────────────────
    private JComboBox<String>  cmbMes;
    private JComboBox<Integer> cmbAno;

    // ── Area de conteudo dinamico ──────────────────────────────────────────
    private JPanel painelConteudo;
    private JScrollPane scrollConteudo;

    private static final Color COR_FUNDO = new Color(15, 23, 42);
    private static final Color VERDE     = new Color(16, 185, 129);
    private static final Color VERMELHO  = new Color(239, 68, 68);
    private static final Color AMARELO   = new Color(234, 179, 8);
    private static final Color AZUL      = new Color(59, 130, 246);
    private static final Color PAINEL    = new Color(30, 41, 59);
    private static final Color BORDA     = new Color(51, 65, 85);
    private static final Color TEXTO_SEC = new Color(148, 163, 184);

    public DashboardPanel() {
        this.controller = new DashboardController();
        setLayout(new BorderLayout(0, 0));
        setBackground(COR_FUNDO);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

        add(criarBarraFiltro(), BorderLayout.NORTH);
        add(criarAreaConteudo(), BorderLayout.CENTER);

        carregarDados();
    }

    // ══════════════════════════════════════════════════════════════════════
    // BARRA DE FILTRO — fixa no topo
    // ══════════════════════════════════════════════════════════════════════

    private JPanel criarBarraFiltro() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(COR_FUNDO);
        barra.setBorder(new EmptyBorder(0, 0, 12, 0));

        // Titulo
        JPanel titles = new JPanel(new BorderLayout());
        titles.setBackground(COR_FUNDO);
        JLabel lblTitulo = new JLabel("Dashboard");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        titles.add(lblTitulo, BorderLayout.NORTH);
        barra.add(titles, BorderLayout.WEST);

        // Filtros
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filtros.setBackground(COR_FUNDO);

        JLabel lPer = new JLabel("Periodo:");
        lPer.setForeground(TEXTO_SEC);
        lPer.setFont(new Font("Arial", Font.PLAIN, 13));

        String[] meses = {
                "Janeiro","Fevereiro","Marco","Abril","Maio","Junho",
                "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"
        };
        cmbMes = new JComboBox<>(meses);
        cmbMes.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        estilizarCombo(cmbMes, 120);

        int anoAtual = LocalDate.now().getYear();
        cmbAno = new JComboBox<>(new Integer[]{
                anoAtual - 2, anoAtual - 1, anoAtual, anoAtual + 1});
        cmbAno.setSelectedItem(anoAtual);
        estilizarCombo(cmbAno, 80);

        JButton btnAplicar = new JButton("Aplicar");
        btnAplicar.setBackground(AZUL);
        btnAplicar.setForeground(Color.WHITE);
        btnAplicar.setFont(new Font("Arial", Font.BOLD, 12));
        btnAplicar.setFocusPainted(false);
        btnAplicar.setBorderPainted(false);
        btnAplicar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAplicar.setPreferredSize(new Dimension(80, 32));
        btnAplicar.addActionListener(e -> carregarDados());

        // Atalho: Enter no combo ja aplica
        cmbMes.addActionListener(e -> carregarDados());
        cmbAno.addActionListener(e -> carregarDados());

        filtros.add(lPer);
        filtros.add(cmbMes);
        filtros.add(cmbAno);
        filtros.add(btnAplicar);
        barra.add(filtros, BorderLayout.EAST);

        return barra;
    }

    // ══════════════════════════════════════════════════════════════════════
    // AREA DE CONTEUDO — scroll interno
    // ══════════════════════════════════════════════════════════════════════

    private JScrollPane criarAreaConteudo() {
        painelConteudo = new JPanel();
        painelConteudo.setLayout(new BoxLayout(painelConteudo, BoxLayout.Y_AXIS));
        painelConteudo.setBackground(COR_FUNDO);

        scrollConteudo = new JScrollPane(painelConteudo);
        scrollConteudo.setBorder(null);
        scrollConteudo.getVerticalScrollBar().setUnitIncrement(16);
        scrollConteudo.setBackground(COR_FUNDO);
        scrollConteudo.getViewport().setBackground(COR_FUNDO);
        return scrollConteudo;
    }

    // ══════════════════════════════════════════════════════════════════════
    // CARREGAR DADOS
    // ══════════════════════════════════════════════════════════════════════

    private void carregarDados() {
        int mes = cmbMes.getSelectedIndex() + 1;
        int ano = (Integer) cmbAno.getSelectedItem();

        DadosDashboard dados = controller.carregarDados(mes, ano);

        // Reconstroi o conteudo
        painelConteudo.removeAll();

        // Subtitulo com mes/ano selecionado
        JLabel lSub = new JLabel("Resumo de " + meses()[mes - 1] + "/" + ano);
        lSub.setForeground(TEXTO_SEC);
        lSub.setFont(new Font("Arial", Font.PLAIN, 12));
        lSub.setBorder(new EmptyBorder(0, 0, 12, 0));
        lSub.setAlignmentX(LEFT_ALIGNMENT);
        painelConteudo.add(lSub);

        // 1. Saude financeira
        JPanel saude = criarPainelSaude(dados.getSaude(), dados.getDica());
        saude.setAlignmentX(LEFT_ALIGNMENT);
        painelConteudo.add(saude);
        painelConteudo.add(Box.createVerticalStrut(12));

        // 2. Cards KPI
        JPanel kpi = criarCardsKPI(dados.getResumo(), dados.getTotalMetasAtivas());
        kpi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));
        kpi.setAlignmentX(LEFT_ALIGNMENT);
        painelConteudo.add(kpi);
        painelConteudo.add(Box.createVerticalStrut(12));

        // 3. Graficos
        JPanel graficos = new JPanel(new GridLayout(1, 2, 10, 0));
        graficos.setOpaque(false);
        graficos.add(criarGraficoPizza(dados.getGastosPorCategoria()));
        graficos.add(criarGraficoBarras(dados.getEvolucaoMeses()));
        graficos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
        graficos.setAlignmentX(LEFT_ALIGNMENT);
        painelConteudo.add(graficos);
        painelConteudo.add(Box.createVerticalStrut(12));

        // 4. Metas e Orcamentos
        boolean temMetas      = dados.getMetasAtivas() != null
                && !dados.getMetasAtivas().isEmpty();
        boolean temOrcamentos = dados.getOrcamentos() != null
                && !dados.getOrcamentos().isEmpty();

        if (temMetas || temOrcamentos) {
            JPanel secoes = new JPanel(new GridLayout(
                    1, (temMetas && temOrcamentos) ? 2 : 1, 12, 0));
            secoes.setOpaque(false);
            secoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            secoes.setAlignmentX(LEFT_ALIGNMENT);
            if (temMetas)      secoes.add(criarSecaoMetas(dados.getMetasAtivas()));
            if (temOrcamentos) secoes.add(criarSecaoOrcamentos(dados.getOrcamentos()));
            painelConteudo.add(secoes);
        }

        painelConteudo.revalidate();
        painelConteudo.repaint();

        // Volta o scroll pro topo
        SwingUtilities.invokeLater(() ->
                scrollConteudo.getVerticalScrollBar().setValue(0));
    }

    /** Chamado pelo dashBoardPrincipal ao navegar para esta aba. */
    public void recarregar() {
        carregarDados();
    }

    // ══════════════════════════════════════════════════════════════════════
    // 1. SAUDE FINANCEIRA
    // ══════════════════════════════════════════════════════════════════════

    private JPanel criarPainelSaude(String status, DicaFinanceira dica) {
        Color corFundo, corBorda, corTexto;
        String icone;
        switch (status) {
            case "SAUDAVEL" -> {
                corFundo = new Color(5,46,26);    corBorda = new Color(6,95,70);
                corTexto = new Color(110,231,183); icone = "[OK]";
            }
            case "ATENCAO" -> {
                corFundo = new Color(28,20,0);    corBorda = new Color(120,53,15);
                corTexto = new Color(252,211,77);  icone = "[!]";
            }
            default -> {
                corFundo = new Color(28,10,10);   corBorda = new Color(127,29,29);
                corTexto = new Color(252,165,165); icone = "[X]";
            }
        }

        CardKPI card = new CardKPI(corFundo, corBorda);
        card.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel lStatus = new JLabel(icone + "  Saude Financeira: " + status);
        lStatus.setForeground(corTexto);
        lStatus.setFont(new Font("Arial", Font.BOLD, 13));

        String textoDica = dica != null
                ? "  |  " + dica.getConteudo()
                : "  |  Configure sua renda mensal para ver a analise.";
        JLabel lDica = new JLabel(textoDica);
        lDica.setForeground(corTexto);
        lDica.setFont(new Font("Arial", Font.PLAIN, 11));

        card.add(lStatus);
        card.add(lDica);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════════
    // 2. CARDS KPI
    // ══════════════════════════════════════════════════════════════════════

    private JPanel criarCardsKPI(ResumoMensal resumo, int totalMetasAtivas) {
        JPanel p = new JPanel(new GridLayout(1, 4, 10, 0));
        p.setOpaque(false);

        CardKPI cReceita = new CardKPI(new Color(5,46,26), new Color(6,95,70));
        popularCard(cReceita, "RECEITAS DO MES",
                Formatador.moeda(resumo.getTotalReceitas()), new Color(110,231,183));

        CardKPI cDespesa = new CardKPI(new Color(28,10,10), new Color(127,29,29));
        popularCard(cDespesa, "DESPESAS DO MES",
                Formatador.moeda(resumo.getTotalDespesas()), new Color(252,165,165));

        Color corSaldo = resumo.getSaldo() >= 0
                ? new Color(147,197,253) : new Color(252,165,165);
        CardKPI cSaldo = new CardKPI(new Color(13,22,38), new Color(30,58,95));
        popularCard(cSaldo, "SALDO DO MES",
                Formatador.moeda(resumo.getSaldo()), corSaldo);

        CardKPI cMetas = new CardKPI(new Color(28,20,0), new Color(120,53,15));
        popularCard(cMetas, "METAS ATIVAS",
                totalMetasAtivas > 0 ? String.valueOf(totalMetasAtivas) : "--",
                new Color(252,211,77));

        p.add(cReceita);
        p.add(cDespesa);
        p.add(cSaldo);
        p.add(cMetas);
        return p;
    }

    private void popularCard(CardKPI card, String titulo, String valor, Color cor) {
        JLabel lTit = new JLabel(titulo);
        lTit.setForeground(cor);
        lTit.setFont(new Font("Arial", Font.BOLD, 10));

        JLabel lVal = new JLabel(valor);
        lVal.setForeground(Color.WHITE);
        lVal.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel inner = new JPanel(new GridLayout(2, 1, 0, 2));
        inner.setOpaque(false);
        inner.add(lTit);
        inner.add(lVal);
        card.add(inner, BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════════════════════════════
    // 3A. GRAFICO PIZZA
    // ══════════════════════════════════════════════════════════════════════

    private JPanel criarGraficoPizza(Map<String, Double> gastos) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        if (gastos != null && !gastos.isEmpty()) {
            gastos.forEach(dataset::setValue);
        } else {
            dataset.setValue("Sem dados", 1.0);
        }
        JFreeChart chart = ChartFactory.createPieChart(
                "Gastos por Categoria", dataset, true, true, false);
        chart.setBackgroundPaint(PAINEL);
        chart.getTitle().setPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 12));

        ChartPanel cp = new ChartPanel(chart);
        cp.setPreferredSize(new Dimension(300, 220));
        cp.setBackground(PAINEL);
        return cp;
    }

    // ══════════════════════════════════════════════════════════════════════
    // 3B. GRAFICO BARRAS
    // ══════════════════════════════════════════════════════════════════════

    private JPanel criarGraficoBarras(List<ResumoMensal> evolucao) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (evolucao != null && !evolucao.isEmpty()) {
            for (ResumoMensal r : evolucao) {
                String periodo = r.getMes() + "/" + r.getAno();
                dataset.addValue(r.getTotalReceitas(), "Receitas", periodo);
                dataset.addValue(r.getTotalDespesas(), "Despesas", periodo);
            }
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Receitas vs Despesas", "Mes", "R$",
                dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setBackgroundPaint(PAINEL);
        chart.getTitle().setPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 12));

        ChartPanel cp = new ChartPanel(chart);
        cp.setPreferredSize(new Dimension(300, 220));
        cp.setBackground(PAINEL);
        return cp;
    }

    // ══════════════════════════════════════════════════════════════════════
    // 4A. SECAO METAS
    // ══════════════════════════════════════════════════════════════════════

    private JPanel criarSecaoMetas(List<Meta> metas) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(COR_FUNDO);

        JLabel lTit = new JLabel("Metas Ativas");
        lTit.setForeground(Color.WHITE);
        lTit.setFont(new Font("Arial", Font.BOLD, 14));
        lTit.setBorder(new EmptyBorder(0, 0, 8, 0));
        lTit.setAlignmentX(LEFT_ALIGNMENT);
        container.add(lTit);

        for (Meta m : metas) {
            container.add(criarCardMeta(m));
            container.add(Box.createVerticalStrut(8));
        }
        return container;
    }

    private JPanel criarCardMeta(Meta m) {
        JPanel card = new JPanel(new BorderLayout(10, 4));
        card.setBackground(PAINEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA, 1),
                new EmptyBorder(10, 14, 10, 14)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(PAINEL);

        JLabel lblNome = new JLabel(m.getNome());
        lblNome.setForeground(Color.WHITE);
        lblNome.setFont(new Font("Arial", Font.BOLD, 13));

        boolean vencida = m.getPrazo() != null
                && m.getPrazo().isBefore(LocalDate.now());
        JLabel lblPrazo = new JLabel(m.getPrazo() != null
                ? "Prazo: " + m.getPrazo()
                              .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "");
        lblPrazo.setForeground(vencida ? VERMELHO : TEXTO_SEC);
        lblPrazo.setFont(new Font("Arial", Font.PLAIN, 11));

        topo.add(lblNome,  BorderLayout.WEST);
        topo.add(lblPrazo, BorderLayout.EAST);
        card.add(topo, BorderLayout.NORTH);

        JPanel baixo = new JPanel(new BorderLayout(8, 0));
        baixo.setBackground(PAINEL);

        double pct = m.getPercentual();
        Color corBarra = pct >= 100 ? VERDE : pct >= 50 ? AMARELO : AZUL;

        JProgressBar barra = new JProgressBar(0, 100);
        barra.setValue((int) pct);
        barra.setStringPainted(false);
        barra.setBackground(BORDA);
        barra.setForeground(corBarra);
        barra.setBorder(BorderFactory.createEmptyBorder());
        barra.setPreferredSize(new Dimension(0, 8));

        JLabel lblInfo = new JLabel(
                Formatador.moeda(m.getValorAtual()) + " / "
                        + Formatador.moeda(m.getValorAlvo())
                        + "  (" + String.format("%.0f%%", pct) + ")");
        lblInfo.setForeground(TEXTO_SEC);
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 11));

        baixo.add(barra,   BorderLayout.CENTER);
        baixo.add(lblInfo, BorderLayout.EAST);
        card.add(baixo, BorderLayout.SOUTH);

        return card;
    }

    // ══════════════════════════════════════════════════════════════════════
    // 4B. SECAO ORCAMENTOS
    // ══════════════════════════════════════════════════════════════════════

    private JPanel criarSecaoOrcamentos(List<Orcamento> orcamentos) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(COR_FUNDO);

        JLabel lTit = new JLabel("Orcamentos do Mes");
        lTit.setForeground(Color.WHITE);
        lTit.setFont(new Font("Arial", Font.BOLD, 14));
        lTit.setBorder(new EmptyBorder(0, 0, 8, 0));
        lTit.setAlignmentX(LEFT_ALIGNMENT);
        container.add(lTit);

        for (Orcamento o : orcamentos) {
            container.add(criarCardOrcamento(o));
            container.add(Box.createVerticalStrut(8));
        }
        return container;
    }

    private JPanel criarCardOrcamento(Orcamento o) {
        JPanel card = new JPanel(new BorderLayout(10, 4));
        card.setBackground(PAINEL);

        double pct = o.getPercentualGasto();
        Color corStatus = pct >= 100 ? VERMELHO : pct >= 75 ? AMARELO : VERDE;
        String textoStatus = pct >= 100 ? "ESTOURADO" : pct >= 75 ? "ATENCAO" : "OK";

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        o.isEstourado() ? VERMELHO : BORDA,
                        o.isEstourado() ? 2 : 1),
                new EmptyBorder(10, 14, 10, 14)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(PAINEL);

        JLabel lblCat = new JLabel(o.getCategoriaNome());
        lblCat.setForeground(Color.WHITE);
        lblCat.setFont(new Font("Arial", Font.BOLD, 13));

        JLabel lblStatus = new JLabel(
                textoStatus + "  " + String.format("%.0f%%", pct));
        lblStatus.setForeground(corStatus);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 11));

        topo.add(lblCat,    BorderLayout.WEST);
        topo.add(lblStatus, BorderLayout.EAST);
        card.add(topo, BorderLayout.NORTH);

        JPanel baixo = new JPanel(new BorderLayout(8, 0));
        baixo.setBackground(PAINEL);

        JProgressBar barra = new JProgressBar(0, 100);
        barra.setValue(Math.min((int) pct, 100));
        barra.setStringPainted(false);
        barra.setBackground(BORDA);
        barra.setForeground(corStatus);
        barra.setBorder(BorderFactory.createEmptyBorder());
        barra.setPreferredSize(new Dimension(0, 8));

        JLabel lblInfo = new JLabel(
                "Gasto: " + Formatador.moeda(o.getValorGasto())
                        + "  /  Limite: " + Formatador.moeda(o.getValorLimite()));
        lblInfo.setForeground(TEXTO_SEC);
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 11));

        baixo.add(barra,   BorderLayout.CENTER);
        baixo.add(lblInfo, BorderLayout.EAST);
        card.add(baixo, BorderLayout.SOUTH);

        return card;
    }

    // ══════════════════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════════════════

    private <T> void estilizarCombo(JComboBox<T> c, int largura) {
        c.setBackground(PAINEL);
        c.setForeground(Color.WHITE);
        c.setFont(new Font("Arial", Font.PLAIN, 12));
        c.setPreferredSize(new Dimension(largura, 32));
        c.setBorder(BorderFactory.createLineBorder(BORDA, 1));
    }

    private String[] meses() {
        return new String[]{
                "Janeiro","Fevereiro","Marco","Abril","Maio","Junho",
                "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"
        };
    }
}