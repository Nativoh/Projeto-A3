package com.controleFinanceiro.view;

import com.controleFinanceiro.DAO.CategoriaDAO;
import com.controleFinanceiro.controller.OrcamentoController;
import com.controleFinanceiro.controller.SessaoAtual;
import com.controleFinanceiro.model.Categoria;
import com.controleFinanceiro.model.Orcamento;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class OrcamentoFormDialog extends JDialog {

    private JComboBox<Categoria> cmbCategoria;
    private JTextField           txtValorLimite;
    private JComboBox<String>    cmbMes;
    private JComboBox<Integer>   cmbAno;
    private JButton              btnSalvar;
    private JButton              btnCancelar;

    private final Orcamento          orcamentoEdicao;
    private final OrcamentoController controller;
    private final CategoriaDAO        categoriaDAO;
    private boolean                   salvo = false;

    private static final Color COR_FUNDO  = new Color(15, 23, 42);
    private static final Color COR_PAINEL = new Color(30, 41, 59);
    private static final Color COR_CAMPO  = new Color(51, 65, 85);
    private static final Color COR_TEXTO  = new Color(241, 245, 249);
    private static final Color COR_LABEL  = new Color(148, 163, 184);
    private static final Color COR_AZUL   = new Color(59, 130, 246);

    public OrcamentoFormDialog(Frame parent, Orcamento orcamento) {
        super(parent, orcamento == null ? "Novo Orcamento" : "Editar Orcamento", true);
        this.orcamentoEdicao = orcamento;
        this.controller      = new OrcamentoController();
        this.categoriaDAO    = new CategoriaDAO();
        construirUI();
        if (orcamento != null) preencherCampos(orcamento);
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void construirUI() {
        setBackground(COR_FUNDO);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COR_FUNDO);
        root.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel lblTitulo = new JLabel(
                orcamentoEdicao == null ? "Novo Orcamento" : "Editar Orcamento");
        lblTitulo.setForeground(COR_TEXTO);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 16, 0));
        root.add(lblTitulo, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(COR_FUNDO);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 0, 6, 12);
        g.anchor = GridBagConstraints.WEST;
        g.fill   = GridBagConstraints.HORIZONTAL;

        // Categoria (desabilitado na edicao)
        g.gridx=0; g.gridy=0; g.weightx=0; form.add(label("Categoria:"), g);
        g.gridx=1; g.weightx=1;
        cmbCategoria = new JComboBox<>();
        carregarCategorias();
        estilizarCombo(cmbCategoria);
        cmbCategoria.setEnabled(orcamentoEdicao == null); // nao muda categoria ao editar
        form.add(cmbCategoria, g);

        // Valor limite
        g.gridx=0; g.gridy=1; g.weightx=0; form.add(label("Limite (R$):"), g);
        g.gridx=1; g.weightx=1;
        txtValorLimite = campo();
        txtValorLimite.setText("0,00");
        form.add(txtValorLimite, g);

        // Mes
        g.gridx=0; g.gridy=2; g.weightx=0; form.add(label("Mes:"), g);
        g.gridx=1; g.weightx=1;
        String[] meses = {"Janeiro","Fevereiro","Marco","Abril","Maio","Junho",
                "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
        cmbMes = new JComboBox<>(meses);
        cmbMes.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        cmbMes.setEnabled(orcamentoEdicao == null);
        estilizarCombo(cmbMes);
        form.add(cmbMes, g);

        // Ano
        g.gridx=0; g.gridy=3; g.weightx=0; form.add(label("Ano:"), g);
        g.gridx=1; g.weightx=1;
        int anoAtual = LocalDate.now().getYear();
        cmbAno = new JComboBox<>(new Integer[]{anoAtual - 1, anoAtual, anoAtual + 1});
        cmbAno.setSelectedItem(anoAtual);
        cmbAno.setEnabled(orcamentoEdicao == null);
        estilizarCombo(cmbAno);
        form.add(cmbAno, g);

        root.add(form, BorderLayout.CENTER);

        // Botoes
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rodape.setBackground(COR_FUNDO);
        rodape.setBorder(new EmptyBorder(16, 0, 0, 0));

        btnCancelar = new JButton("Cancelar");
        estilizarBotao(btnCancelar, COR_PAINEL, COR_LABEL);
        btnCancelar.addActionListener(e -> dispose());

        btnSalvar = new JButton(orcamentoEdicao == null ? "Salvar" : "Atualizar");
        estilizarBotao(btnSalvar, COR_AZUL, Color.WHITE);
        btnSalvar.addActionListener(e -> salvar());

        rodape.add(btnCancelar);
        rodape.add(btnSalvar);
        root.add(rodape, BorderLayout.SOUTH);

        setContentPane(root);
        setMinimumSize(new Dimension(400, 320));
    }

    private void carregarCategorias() {
        // Carrega apenas categorias de EXPENSE (orcamento e sempre para despesas)
        List<Categoria> cats = categoriaDAO
                .listarVisivelsPorTipo(SessaoAtual.getUsuarioId(), "EXPENSE");
        cmbCategoria.removeAllItems();
        for (Categoria c : cats) cmbCategoria.addItem(c);
    }

    private void salvar() {
        try {
            Orcamento o = orcamentoEdicao != null ? orcamentoEdicao : new Orcamento();
            o.setUsuarioId(SessaoAtual.getUsuarioId());

            if (orcamentoEdicao == null) {
                Categoria cat = (Categoria) cmbCategoria.getSelectedItem();
                if (cat == null) throw new Exception("Selecione uma categoria.");
                o.setCategoriaId(cat.getId());
                o.setMes(cmbMes.getSelectedIndex() + 1);
                o.setAno((Integer) cmbAno.getSelectedItem());
            }

            double limite = Double.parseDouble(
                    txtValorLimite.getText().replace(",", ".").trim());
            o.setValorLimite(limite);

            boolean ok = orcamentoEdicao == null
                    ? controller.criar(o)
                    : controller.editar(o);

            if (ok) {
                salvo = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erro ao salvar o orcamento.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Valor invalido. Use o formato: 500,00",
                    "Atencao", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    e.getMessage(), "Atencao", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void preencherCampos(Orcamento o) {
        txtValorLimite.setText(
                String.format("%.2f", o.getValorLimite()).replace(".", ","));
    }

    public boolean isSalvo() { return salvo; }

    private JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(COR_LABEL);
        l.setFont(new Font("Arial", Font.PLAIN, 13));
        l.setPreferredSize(new Dimension(100, 30));
        return l;
    }

    private JTextField campo() {
        JTextField f = new JTextField(20);
        f.setBackground(COR_CAMPO);
        f.setForeground(COR_TEXTO);
        f.setCaretColor(COR_TEXTO);
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
                new EmptyBorder(6, 10, 6, 10)));
        f.setPreferredSize(new Dimension(260, 36));
        return f;
    }

    private <T> void estilizarCombo(JComboBox<T> c) {
        c.setBackground(COR_CAMPO);
        c.setForeground(COR_TEXTO);
        c.setFont(new Font("Arial", Font.PLAIN, 13));
        c.setPreferredSize(new Dimension(260, 36));
        c.setBorder(BorderFactory.createLineBorder(new Color(71, 85, 105), 1));
    }

    private void estilizarBotao(JButton b, Color bg, Color fg) {
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(110, 38));
    }
}