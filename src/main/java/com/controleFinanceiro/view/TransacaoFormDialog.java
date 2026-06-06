package com.controleFinanceiro.view;

import com.controleFinanceiro.DAO.CategoriaDAO;
import com.controleFinanceiro.controller.SessaoAtual;
import com.controleFinanceiro.model.Categoria;
import com.controleFinanceiro.model.Transacao;
import com.controleFinanceiro.service.TransacaoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TransacaoFormDialog extends JDialog {

    // ── Componentes ────────────────────────────────────────────────────────
    private JComboBox<String>    cmbTipo;
    private JComboBox<Categoria> cmbCategoria;
    private JTextField           txtValor;
    private JTextField           txtDescricao;
    private JFormattedTextField  txtData;
    private JCheckBox            chkParcelado;
    private JSpinner             spnParcelas;
    private JLabel               lblParcelas;
    private JButton              btnSalvar;
    private JButton              btnCancelar;

    // ── Estado ─────────────────────────────────────────────────────────────
    private final Transacao        transacaoEdicao; // null = nova
    private final TransacaoService service;
    private final CategoriaDAO     categoriaDAO;
    private boolean                salvo = false;

    // Listener do combo de tipo, guardado para poder remover/re-adicionar
    private final ActionListener listenerTipo = e -> atualizarCategorias();

    // ── Cores ──────────────────────────────────────────────────────────────
    private static final Color COR_FUNDO   = new Color(15, 23, 42);
    private static final Color COR_PAINEL  = new Color(30, 41, 59);
    private static final Color COR_CAMPO   = new Color(51, 65, 85);
    private static final Color COR_TEXTO   = new Color(241, 245, 249);
    private static final Color COR_LABEL   = new Color(148, 163, 184);
    private static final Color COR_VERDE   = new Color(16, 185, 129);
    private static final Color COR_AZUL    = new Color(59, 130, 246);

    public TransacaoFormDialog(Frame parent, Transacao transacao) {
        super(parent, transacao == null ? "Nova Transação" : "Editar Transação", true);
        this.transacaoEdicao = transacao;
        this.service         = new TransacaoService();
        this.categoriaDAO    = new CategoriaDAO();
        construirUI();
        if (transacao != null) preencherCampos(transacao);
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    // ══════════════════════════════════════════════════════════════════════
    // CONSTRUÇÃO DA UI
    // ══════════════════════════════════════════════════════════════════════

    private void construirUI() {
        setBackground(COR_FUNDO);
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(COR_FUNDO);
        root.setBorder(new EmptyBorder(20, 24, 20, 24));

        // ── Título ─────────────────────────────────────────────────────────
        JLabel lblTitulo = new JLabel(
                transacaoEdicao == null ? "💸 Nova Transação" : "✏️ Editar Transação");
        lblTitulo.setForeground(COR_TEXTO);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 16, 0));
        root.add(lblTitulo, BorderLayout.NORTH);

        // ── Formulário ─────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(COR_FUNDO);
        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(6, 0, 6, 12);
        g.anchor  = GridBagConstraints.WEST;
        g.fill    = GridBagConstraints.HORIZONTAL;

        // Linha 0 — Tipo
        g.gridx=0; g.gridy=0; g.weightx=0;
        form.add(label("Tipo:"), g);
        g.gridx=1; g.weightx=1;
        cmbTipo = combo(new String[]{"INCOME", "EXPENSE"});
        cmbTipo.setRenderer(new TipoRenderer());
        cmbTipo.addActionListener(listenerTipo); // ← listener guardado em variável
        form.add(cmbTipo, g);

        // Linha 1 — Categoria
        g.gridx=0; g.gridy=1; g.weightx=0;
        form.add(label("Categoria:"), g);
        g.gridx=1; g.weightx=1;
        cmbCategoria = new JComboBox<>();
        estilizarCombo(cmbCategoria);
        form.add(cmbCategoria, g);
        atualizarCategorias(); // popula com base no tipo inicial

        // Linha 2 — Valor
        g.gridx=0; g.gridy=2; g.weightx=0;
        form.add(label("Valor (R$):"), g);
        g.gridx=1; g.weightx=1;
        txtValor = campo("0,00");
        form.add(txtValor, g);

        // Linha 3 — Descrição
        g.gridx=0; g.gridy=3; g.weightx=0;
        form.add(label("Descrição:"), g);
        g.gridx=1; g.weightx=1;
        txtDescricao = campo("Descrição opcional...");
        form.add(txtDescricao, g);

        // Linha 4 — Data
        g.gridx=0; g.gridy=4; g.weightx=0;
        form.add(label("Data:"), g);
        g.gridx=1; g.weightx=1;
        txtData = criarCampoData();
        form.add(txtData, g);

        // Linha 5 — Parcelado
        g.gridx=0; g.gridy=5; g.weightx=0;
        form.add(label("Parcelado:"), g);
        g.gridx=1; g.weightx=1;
        chkParcelado = new JCheckBox("Sim, é uma compra parcelada");
        chkParcelado.setBackground(COR_FUNDO);
        chkParcelado.setForeground(COR_LABEL);
        chkParcelado.setFont(new Font("Arial", Font.PLAIN, 13));
        chkParcelado.addActionListener(e -> {
            lblParcelas.setVisible(chkParcelado.isSelected());
            spnParcelas.setVisible(chkParcelado.isSelected());
        });
        form.add(chkParcelado, g);

        // Linha 6 — Qtd Parcelas (oculto por padrão)
        g.gridx=0; g.gridy=6; g.weightx=0;
        lblParcelas = label("Parcelas:");
        lblParcelas.setVisible(false);
        form.add(lblParcelas, g);
        g.gridx=1; g.weightx=1;
        spnParcelas = new JSpinner(new SpinnerNumberModel(2, 2, 48, 1));
        estilizarSpinner(spnParcelas);
        spnParcelas.setVisible(false);
        form.add(spnParcelas, g);

        root.add(form, BorderLayout.CENTER);

        // ── Botões ─────────────────────────────────────────────────────────
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rodape.setBackground(COR_FUNDO);
        rodape.setBorder(new EmptyBorder(16, 0, 0, 0));

        btnCancelar = new JButton("Cancelar");
        estilizarBotao(btnCancelar, COR_PAINEL, COR_LABEL);
        btnCancelar.addActionListener(e -> dispose());

        btnSalvar = new JButton(transacaoEdicao == null ? "Salvar" : "Atualizar");
        estilizarBotao(btnSalvar, COR_AZUL, Color.WHITE);
        btnSalvar.addActionListener(e -> salvar());

        rodape.add(btnCancelar);
        rodape.add(btnSalvar);
        root.add(rodape, BorderLayout.SOUTH);

        setContentPane(root);
        setMinimumSize(new Dimension(420, 380));
    }

    // ══════════════════════════════════════════════════════════════════════
    // LÓGICA
    // ══════════════════════════════════════════════════════════════════════

    private void atualizarCategorias() {
        String tipo = (String) cmbTipo.getSelectedItem();
        int uid = SessaoAtual.getUsuarioId();
        List<Categoria> cats = categoriaDAO.listarVisivelsPorTipo(uid, tipo);
        cmbCategoria.removeAllItems();
        for (Categoria c : cats) cmbCategoria.addItem(c);
    }

    private void salvar() {
        try {
            // ── Montar objeto ──────────────────────────────────────────────
            Transacao t = transacaoEdicao != null
                    ? transacaoEdicao : new Transacao();

            t.setUsuarioId(SessaoAtual.getUsuarioId());
            t.setTipo((String) cmbTipo.getSelectedItem());

            Categoria cat = (Categoria) cmbCategoria.getSelectedItem();
            if (cat == null) throw new Exception("Selecione uma categoria.");
            t.setCategoriaId(cat.getId());

            String valorStr = txtValor.getText().replace(",", ".").trim();
            double valor = Double.parseDouble(valorStr);
            if (valor <= 0) throw new Exception("O valor deve ser maior que zero.");
            t.setValor(valor);

            t.setDescricao(txtDescricao.getText().trim());

            String dataStr = txtData.getText().replace("_", "").trim();
            if (dataStr.length() < 10) throw new Exception("Informe a data completa no formato dd/MM/yyyy.");
            LocalDate dataLocal;
            try {
                dataLocal = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException ex) {
                throw new Exception("Data inválida. Use o formato dd/MM/yyyy.");
            }
            t.setDataTransacao(dataLocal);

            // ── Salvar ─────────────────────────────────────────────────────
            boolean ok;
            if (transacaoEdicao != null) {
                ok = service.editar(t);
            } else if (chkParcelado.isSelected()) {
                int qtd = (int) spnParcelas.getValue();
                ok = service.registrarParcelado(t, qtd);
            } else {
                ok = service.registrar(t);
            }

            if (ok) {
                salvo = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erro ao salvar. Verifique os dados.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Valor inválido. Use o formato: 150,00",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            e.printStackTrace(); // imprime o stack trace completo no console
            JOptionPane.showMessageDialog(this,
                    msg, "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void preencherCampos(Transacao t) {
        // ── CORREÇÃO: remove o listener antes de setar o tipo para evitar
        //    dupla chamada a atualizarCategorias() ──────────────────────────
        cmbTipo.removeActionListener(listenerTipo);
        cmbTipo.setSelectedItem(t.getTipo());
        cmbTipo.addActionListener(listenerTipo); // re-adiciona depois

        atualizarCategorias(); // chama uma única vez com o tipo já definido

        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
            if (cmbCategoria.getItemAt(i).getId() == t.getCategoriaId()) {
                cmbCategoria.setSelectedIndex(i);
                break;
            }
        }
        txtValor.setText(String.format("%.2f", t.getValor()).replace(".", ","));
        txtDescricao.setText(t.getDescricao());
        if (t.getDataTransacao() != null) {
            txtData.setText(t.getDataTransacao()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
    }

    /** Retorna true se o usuário clicou em Salvar com sucesso. */
    public boolean isSalvo() { return salvo; }

    // ══════════════════════════════════════════════════════════════════════
    // HELPERS DE ESTILO
    // ══════════════════════════════════════════════════════════════════════

    private JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(COR_LABEL);
        l.setFont(new Font("Arial", Font.PLAIN, 13));
        l.setPreferredSize(new Dimension(90, 30));
        return l;
    }

    private JTextField campo(String placeholder) {
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

    private JComboBox<String> combo(String[] itens) {
        JComboBox<String> c = new JComboBox<>(itens);
        estilizarCombo(c);
        return c;
    }

    private <T> void estilizarCombo(JComboBox<T> c) {
        c.setBackground(COR_CAMPO);
        c.setForeground(COR_TEXTO);
        c.setFont(new Font("Arial", Font.PLAIN, 13));
        c.setPreferredSize(new Dimension(260, 36));
        c.setBorder(BorderFactory.createLineBorder(new Color(71, 85, 105), 1));
    }

    private JFormattedTextField criarCampoData() {
        MaskFormatter mascara = null;
        try {
            mascara = new MaskFormatter("##/##/####");
            mascara.setPlaceholderCharacter('_');
        } catch (ParseException ignored) {}

        JFormattedTextField f = new JFormattedTextField(mascara);
        // Preenche com a data de hoje por padrão
        f.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        f.setBackground(COR_CAMPO);
        f.setForeground(COR_TEXTO);
        f.setCaretColor(COR_TEXTO);
        f.setOpaque(true);
        f.setSelectionColor(new Color(59, 130, 246));
        f.setSelectedTextColor(Color.WHITE);
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
                new EmptyBorder(6, 10, 6, 10)));
        f.setPreferredSize(new Dimension(260, 36));
        return f;
    }

    private void estilizarSpinner(JSpinner s) {
        s.setBackground(COR_CAMPO);
        s.setForeground(COR_TEXTO);
        s.setFont(new Font("Arial", Font.PLAIN, 13));
        s.setPreferredSize(new Dimension(260, 36));
        s.setBorder(BorderFactory.createLineBorder(new Color(71, 85, 105), 1));

        JComponent editor = s.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(COR_CAMPO);
            tf.setForeground(COR_TEXTO);
            tf.setCaretColor(COR_TEXTO);
            tf.setOpaque(true);
            tf.setSelectionColor(new Color(59, 130, 246));
            tf.setSelectedTextColor(Color.WHITE);
        }
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

    // Renderer para exibir INCOME/EXPENSE em português no combo
    private static class TipoRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list,
                                                      Object value, int index, boolean isSelected, boolean hasFocus) {
            super.getListCellRendererComponent(list,value,index,isSelected,hasFocus);
            if ("INCOME".equals(value))  setText("💰 Receita");
            if ("EXPENSE".equals(value)) setText("💸 Despesa");
            setBackground(isSelected ? new Color(59,130,246) : new Color(51,65,85));
            setForeground(Color.WHITE);
            return this;
        }
    }
}