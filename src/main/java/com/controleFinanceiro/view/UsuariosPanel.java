package com.controleFinanceiro.view;

import com.controleFinanceiro.DAO.AdminDashboardDAO;
import com.controleFinanceiro.DAO.UsuarioDAO;
import com.controleFinanceiro.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class UsuariosPanel extends JPanel {

    private final UsuarioDAO        dao    = new UsuarioDAO();
    private final AdminDashboardDAO admDao = new AdminDashboardDAO();

    private List<Usuario>       todosUsuarios;
    private List<Object[]>      condicoes;
    private JTable              tabela;
    private DefaultTableModel   model;
    private JComboBox<String>   cmbFiltro;
    private JTextField          txtBusca;

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

    public UsuariosPanel() {
        setLayout(new BorderLayout());
        setBackground(FUNDO);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        construirUI();
        carregarUsuarios();
    }

    private void construirUI() {
        add(criarNorth(), BorderLayout.NORTH);
        add(criarCenter(), BorderLayout.CENTER);
        add(criarSouth(), BorderLayout.SOUTH);
    }

    // ══════════════════════════════════════════════════════════════════════
    // NORTH
    // ══════════════════════════════════════════════════════════════════════

    private JPanel criarNorth() {
        JPanel north = new JPanel(new BorderLayout(0, 12));
        north.setBackground(FUNDO);
        north.setBorder(new EmptyBorder(0, 0, 16, 0));

        // Titulo + subtitulo
        JPanel titles = new JPanel(new BorderLayout());
        titles.setBackground(FUNDO);
        JLabel titulo = new JLabel("Usuarios do Sistema");
        titulo.setForeground(TEXTO);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel sub = new JLabel("Visualizacao e analise — sem edicao de dados sensiveis");
        sub.setForeground(TEXTO_SEC);
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        titles.add(titulo, BorderLayout.NORTH);
        titles.add(sub,    BorderLayout.SOUTH);
        north.add(titles, BorderLayout.NORTH);

        // Barra de filtros
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(FUNDO);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtros.setBackground(FUNDO);

        txtBusca = new JTextField(18);
        txtBusca.setBackground(PAINEL);
        txtBusca.setForeground(TEXTO);
        txtBusca.setCaretColor(TEXTO);
        txtBusca.setFont(new Font("Arial", Font.PLAIN, 12));
        txtBusca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA, 1),
                new EmptyBorder(4, 8, 4, 8)));
        txtBusca.setPreferredSize(new Dimension(200, 32));
        txtBusca.addActionListener(e -> filtrar());

        JButton btnBuscar = botao("Buscar", AZUL, Color.WHITE, 80);
        btnBuscar.addActionListener(e -> filtrar());

        cmbFiltro = new JComboBox<>(new String[]{
                "Todos", "Ativos", "Inativos",
                "ADMIN", "USER",
                "SAUDAVEL", "ATENCAO", "RISCO"
        });
        cmbFiltro.setBackground(PAINEL);
        cmbFiltro.setForeground(TEXTO);
        cmbFiltro.setFont(new Font("Arial", Font.PLAIN, 12));
        cmbFiltro.setPreferredSize(new Dimension(130, 32));
        cmbFiltro.addActionListener(e -> filtrar());

        JLabel lBusca  = label("Buscar:");
        JLabel lFiltro = label("Filtro:");

        filtros.add(lBusca);
        filtros.add(txtBusca);
        filtros.add(btnBuscar);
        filtros.add(Box.createHorizontalStrut(8));
        filtros.add(lFiltro);
        filtros.add(cmbFiltro);
        barra.add(filtros, BorderLayout.WEST);

        // Botao atualizar
        JButton btnAtualizar = botao("Atualizar", new Color(37,99,235), Color.WHITE, 100);
        btnAtualizar.addActionListener(e -> carregarUsuarios());
        JPanel pbtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pbtn.setBackground(FUNDO);
        pbtn.add(btnAtualizar);
        barra.add(pbtn, BorderLayout.EAST);

        north.add(barra, BorderLayout.SOUTH);
        return north;
    }

    // ══════════════════════════════════════════════════════════════════════
    // CENTER — tabela analítica
    // ══════════════════════════════════════════════════════════════════════

    private JScrollPane criarCenter() {
        // Colunas: dados do usuario + condicao financeira do mes atual
        String[] colunas = {
                "#", "Nome", "E-mail", "Funcao", "Perfil Investidor",
                "Renda Mensal", "Receitas Mes", "Despesas Mes",
                "Saldo", "% Gasto", "Saude", "Metas", "Transacoes", "Status Conta"
        };

        model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabela = new JTable(model);
        tabela.setBackground(new Color(24, 33, 48));
        tabela.setForeground(TEXTO);
        tabela.setFont(new Font("Arial", Font.PLAIN, 12));
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

        int[] larg = {35,160,200,70,120,110,110,110,100,70,80,60,90,80};
        for (int i = 0; i < larg.length; i++)
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larg[i]);

        // Renderer geral com colunas coloridas
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t,val,sel,foc,row,col);
                Color bg = sel ? new Color(30,58,95)
                        : row%2==0 ? PAINEL : new Color(24,33,48);
                setBackground(bg);
                setForeground(TEXTO);
                setBorder(new EmptyBorder(0,8,0,8));
                setFont(new Font("Arial", Font.PLAIN, 12));

                if (val == null) return this;
                String s = val.toString();

                switch (col) {
                    case 3 -> { // Funcao
                        setForeground("ADMIN".equals(s) ? ROXO : AZUL);
                        setFont(new Font("Arial", Font.BOLD, 11));
                    }
                    case 8 -> { // Saldo
                        setForeground(s.contains("-") ? VERMELHO : VERDE);
                        setFont(new Font("Arial", Font.BOLD, 12));
                    }
                    case 9 -> { // % Gasto
                        try {
                            double pct = Double.parseDouble(
                                    s.replace("%","").replace(",",".").trim());
                            setForeground(pct >= 90 ? VERMELHO
                                    : pct >= 70 ? AMARELO : VERDE);
                            setFont(new Font("Arial", Font.BOLD, 12));
                        } catch (NumberFormatException ignored) {}
                    }
                    case 10 -> { // Saude
                        setFont(new Font("Arial", Font.BOLD, 12));
                        switch (s) {
                            case "RISCO"    -> setForeground(VERMELHO);
                            case "ATENCAO"  -> setForeground(AMARELO);
                            case "SAUDAVEL" -> { setForeground(VERDE); setText("OK"); }
                        }
                    }
                    case 13 -> { // Status Conta
                        setForeground("Ativo".equals(s) ? VERDE : VERMELHO);
                        setFont(new Font("Arial", Font.BOLD, 12));
                    }
                }
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(BORDA, 1));
        scroll.getViewport().setBackground(new Color(24, 33, 48));
        return scroll;
    }

    // ══════════════════════════════════════════════════════════════════════
    // SOUTH — badges de totais
    // ══════════════════════════════════════════════════════════════════════

    private JPanel criarSouth() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        p.setBackground(FUNDO);
        p.setName("south");
        return p;
    }

    private void atualizarSouth(List<Object[]> lista) {
        for (Component c : getComponents()) {
            if (c instanceof JPanel p && "south".equals(p.getName())) {
                p.removeAll();

                long ativos   = todosUsuarios.stream().filter(Usuario::isAtivo).count();
                long inativos = todosUsuarios.size() - ativos;
                long admins   = todosUsuarios.stream()
                        .filter(u -> "ADMIN".equals(u.getFuncao())).count();
                long risco    = lista.stream()
                        .filter(r -> "RISCO".equals(r[8])).count();
                long atencao  = lista.stream()
                        .filter(r -> "ATENCAO".equals(r[8])).count();
                long saudavel = lista.stream()
                        .filter(r -> "SAUDAVEL".equals(r[8])).count();

                p.add(badge("Total: " + todosUsuarios.size(), AZUL));
                p.add(badge("Ativos: " + ativos,             VERDE));
                p.add(badge("Inativos: " + inativos,         VERMELHO));
                p.add(badge("Admins: " + admins,             ROXO));
                p.add(Box.createHorizontalStrut(16));
                p.add(badge("OK: " + saudavel,               VERDE));
                p.add(badge("Atencao: " + atencao,           AMARELO));
                p.add(badge("Risco: " + risco,               VERMELHO));

                p.revalidate();
                p.repaint();
                break;
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // DADOS
    // ══════════════════════════════════════════════════════════════════════

    public void carregarUsuarios() {
        int mes = LocalDate.now().getMonthValue();
        int ano = LocalDate.now().getYear();

        todosUsuarios = dao.listarTodos();
        // Usa listarUsuariosComCondicao do AdminDashboardDAO
        // indice: 0=id,1=nome,2=email,3=renda,4=receitas,5=despesas,
        //         6=saldo,7=pct,8=status,9=perfil,10=ativo,11=metas,12=transacoes,13=criado
        condicoes = admDao.listarUsuariosComCondicao(mes, ano);

        popularTabela(condicoes);
        atualizarSouth(condicoes);
    }

    private void filtrar() {
        if (condicoes == null) return;
        String busca  = txtBusca.getText().trim().toLowerCase();
        String filtro = (String) cmbFiltro.getSelectedItem();

        List<Object[]> filtrados = condicoes.stream()
                .filter(r -> {
                    String nome  = r[1].toString().toLowerCase();
                    String email = r[2].toString().toLowerCase();
                    return busca.isEmpty()
                            || nome.contains(busca)
                            || email.contains(busca);
                })
                .filter(r -> switch (filtro) {
                    case "Ativos"   -> Boolean.TRUE.equals(r[10]);
                    case "Inativos" -> Boolean.FALSE.equals(r[10]);
                    case "ADMIN"    -> {
                        // busca funcao no todosUsuarios pelo id
                        int id = (int) r[0];
                        yield todosUsuarios.stream()
                                .filter(u -> u.getId() == id)
                                .findFirst()
                                .map(u -> "ADMIN".equals(u.getFuncao()))
                                .orElse(false);
                    }
                    case "USER"     -> {
                        int id = (int) r[0];
                        yield todosUsuarios.stream()
                                .filter(u -> u.getId() == id)
                                .findFirst()
                                .map(u -> "USER".equals(u.getFuncao()))
                                .orElse(true);
                    }
                    case "SAUDAVEL" -> "SAUDAVEL".equals(r[8]);
                    case "ATENCAO"  -> "ATENCAO".equals(r[8]);
                    case "RISCO"    -> "RISCO".equals(r[8]);
                    default         -> true;
                })
                .collect(Collectors.toList());

        popularTabela(filtrados);
        atualizarSouth(filtrados);
    }

    private void popularTabela(List<Object[]> lista) {
        model.setRowCount(0);
        int i = 1;
        for (Object[] r : lista) {
            // Busca funcao e status conta do todosUsuarios
            int id = (int) r[0];
            Usuario u = todosUsuarios.stream()
                    .filter(x -> x.getId() == id)
                    .findFirst().orElse(null);

            String funcao      = u != null ? u.getFuncao()  : "USER";
            String statusConta = u != null
                    ? (u.isAtivo() ? "Ativo" : "Inativo") : "—";

            model.addRow(new Object[]{
                    i++,
                    r[1],  // nome
                    r[2],  // email
                    funcao,
                    r[9],  // perfil_investidor
                    String.format("R$ %.2f", (double) r[3]),   // renda
                    String.format("R$ %.2f", (double) r[4]),   // receitas
                    String.format("R$ %.2f", (double) r[5]),   // despesas
                    String.format("R$ %.2f", (double) r[6]),   // saldo
                    String.format("%.1f%%",  (double) r[7]),   // % gasto
                    r[8],  // saude
                    r[11], // metas ativas
                    r[12], // transacoes mes
                    statusConta
            });
        }
    }

    public void recarregar() { carregarUsuarios(); }

    // ── Helpers ────────────────────────────────────────────────────────────

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(TEXTO_SEC);
        l.setFont(new Font("Arial", Font.PLAIN, 13));
        return l;
    }

    private JLabel badge(String texto, Color cor) {
        JLabel l = new JLabel("  " + texto + "  ");
        l.setForeground(cor);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setOpaque(true);
        l.setBackground(new Color(
                Math.max(cor.getRed()  /6, 0),
                Math.max(cor.getGreen()/6, 0),
                Math.max(cor.getBlue() /6, 0)));
        l.setBorder(BorderFactory.createLineBorder(cor, 1));
        return l;
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