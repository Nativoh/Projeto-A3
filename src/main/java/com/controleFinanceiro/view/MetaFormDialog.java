package com.controleFinanceiro.view;

import com.controleFinanceiro.controller.MetaController;
import com.controleFinanceiro.controller.SessaoAtual;
import com.controleFinanceiro.model.Meta;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MetaFormDialog extends JDialog {

    // ── Componentes ────────────────────────────────────────────────────────
    private JTextField           txtNome;
    private JTextField           txtDescricao;
    private JTextField           txtValorAlvo;
    private JTextField           txtValorAtual;
    private JFormattedTextField  txtPrazo;
    private JComboBox<String>    cmbStatus;
    private JButton              btnSalvar;
    private JButton              btnCancelar;

    // ── Estado ─────────────────────────────────────────────────────────────
    private final Meta             metaEdicao;
    private final MetaController   controller;
    private boolean                salvo = false;

    // ── Cores ──────────────────────────────────────────────────────────────
    private static final Color COR_FUNDO  = new Color(15, 23, 42);
    private static final Color COR_PAINEL = new Color(30, 41, 59);
    private static final Color COR_CAMPO  = new Color(51, 65, 85);
    private static final Color COR_TEXTO  = new Color(241, 245, 249);
    private static final Color COR_LABEL  = new Color(148, 163, 184);
    private static final Color COR_AZUL   = new Color(59, 130, 246);

    public MetaFormDialog(Frame parent, Meta meta) {
        super(parent, meta == null ? "Nova Meta" : "Editar Meta", true);
        this.metaEdicao = meta;
        this.controller = new MetaController();
        construirUI();
        if (meta != null) preencherCampos(meta);
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void construirUI() {
        setBackground(COR_FUNDO);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COR_FUNDO);
        root.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Titulo
        JLabel lblTitulo = new JLabel(metaEdicao == null ? "Nova Meta" : "Editar Meta");
        lblTitulo.setForeground(COR_TEXTO);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 16, 0));
        root.add(lblTitulo, BorderLayout.NORTH);

        // Formulario
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(COR_FUNDO);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 0, 6, 12);
        g.anchor = GridBagConstraints.WEST;
        g.fill   = GridBagConstraints.HORIZONTAL;

        // Nome
        g.gridx=0; g.gridy=0; g.weightx=0; form.add(label("Nome:"), g);
        g.gridx=1; g.weightx=1;
        txtNome = campo();
        form.add(txtNome, g);

        // Descricao
        g.gridx=0; g.gridy=1; g.weightx=0; form.add(label("Descricao:"), g);
        g.gridx=1; g.weightx=1;
        txtDescricao = campo();
        form.add(txtDescricao, g);

        // Valor Alvo
        g.gridx=0; g.gridy=2; g.weightx=0; form.add(label("Valor Alvo (R$):"), g);
        g.gridx=1; g.weightx=1;
        txtValorAlvo = campo();
        txtValorAlvo.setText("0,00");
        form.add(txtValorAlvo, g);

        // Valor Atual
        g.gridx=0; g.gridy=3; g.weightx=0; form.add(label("Valor Atual (R$):"), g);
        g.gridx=1; g.weightx=1;
        txtValorAtual = campo();
        txtValorAtual.setText("0,00");
        form.add(txtValorAtual, g);

        // Prazo
        g.gridx=0; g.gridy=4; g.weightx=0; form.add(label("Prazo:"), g);
        g.gridx=1; g.weightx=1;
        txtPrazo = criarCampoPrazo();
        form.add(txtPrazo, g);

        // Status (so aparece na edicao)
        g.gridx=0; g.gridy=5; g.weightx=0; form.add(label("Status:"), g);
        g.gridx=1; g.weightx=1;
        cmbStatus = new JComboBox<>(new String[]{"ATIVA", "CONCLUIDA", "CANCELADA"});
        estilizarCombo(cmbStatus);
        form.add(cmbStatus, g);

        root.add(form, BorderLayout.CENTER);

        // Botoes
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rodape.setBackground(COR_FUNDO);
        rodape.setBorder(new EmptyBorder(16, 0, 0, 0));

        btnCancelar = new JButton("Cancelar");
        estilizarBotao(btnCancelar, COR_PAINEL, COR_LABEL);
        btnCancelar.addActionListener(e -> dispose());

        btnSalvar = new JButton(metaEdicao == null ? "Salvar" : "Atualizar");
        estilizarBotao(btnSalvar, COR_AZUL, Color.WHITE);
        btnSalvar.addActionListener(e -> salvar());

        rodape.add(btnCancelar);
        rodape.add(btnSalvar);
        root.add(rodape, BorderLayout.SOUTH);

        setContentPane(root);
        setMinimumSize(new Dimension(440, 400));
    }

    private void salvar() {
        try {
            Meta m = metaEdicao != null ? metaEdicao : new Meta();
            m.setUsuarioId(SessaoAtual.getUsuarioId());

            String nome = txtNome.getText().trim();
            if (nome.isEmpty()) throw new Exception("Nome da meta e obrigatorio.");
            m.setNome(nome);
            m.setDescricao(txtDescricao.getText().trim());

            double valorAlvo = Double.parseDouble(
                    txtValorAlvo.getText().replace(",", ".").trim());
            double valorAtual = Double.parseDouble(
                    txtValorAtual.getText().replace(",", ".").trim());
            m.setValorAlvo(valorAlvo);
            m.setValorAtual(valorAtual);

            String prazoStr = txtPrazo.getText().replace("_", "").trim();
            if (prazoStr.length() < 10) throw new Exception("Informe o prazo completo (dd/MM/yyyy).");
            try {
                m.setPrazo(LocalDate.parse(prazoStr,
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } catch (DateTimeParseException ex) {
                throw new Exception("Prazo invalido. Use dd/MM/yyyy.");
            }

            m.setStatus((String) cmbStatus.getSelectedItem());

            boolean ok = metaEdicao == null
                    ? controller.criarMeta(m)
                    : controller.editarMeta(m);

            if (ok) {
                salvo = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erro ao salvar a meta.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Valor invalido. Use o formato: 1500,00",
                    "Atencao", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    e.getMessage(), "Atencao", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void preencherCampos(Meta m) {
        txtNome.setText(m.getNome());
        txtDescricao.setText(m.getDescricao() != null ? m.getDescricao() : "");
        txtValorAlvo.setText(String.format("%.2f", m.getValorAlvo()).replace(".", ","));
        txtValorAtual.setText(String.format("%.2f", m.getValorAtual()).replace(".", ","));
        if (m.getPrazo() != null)
            txtPrazo.setText(m.getPrazo().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        cmbStatus.setSelectedItem(m.getStatus());
    }

    public boolean isSalvo() { return salvo; }

    // ── Helpers de estilo ──────────────────────────────────────────────────

    private JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(COR_LABEL);
        l.setFont(new Font("Arial", Font.PLAIN, 13));
        l.setPreferredSize(new Dimension(110, 30));
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

    private JFormattedTextField criarCampoPrazo() {
        MaskFormatter mascara = null;
        try {
            mascara = new MaskFormatter("##/##/####");
            mascara.setPlaceholderCharacter('_');
        } catch (ParseException ignored) {}
        JFormattedTextField f = new JFormattedTextField(mascara);
        f.setText(LocalDate.now().plusMonths(1)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
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

    private void estilizarCombo(JComboBox<String> c) {
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