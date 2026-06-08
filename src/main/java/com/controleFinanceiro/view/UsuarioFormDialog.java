package com.controleFinanceiro.view;

import com.controleFinanceiro.DAO.UsuarioDAO;
import com.controleFinanceiro.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UsuarioFormDialog extends JDialog {

    private JTextField        txtNome;
    private JTextField        txtEmail;
    private JPasswordField    txtSenha;
    private JTextField        txtRenda;
    private JComboBox<String> cmbFuncao;
    private JComboBox<String> cmbPerfil;
    private JCheckBox         chkAtivo;
    private JButton           btnSalvar;
    private JButton           btnCancelar;

    private final Usuario    usuarioEdicao;
    private final UsuarioDAO dao;
    private boolean          salvo = false;

    private static final Color COR_FUNDO  = new Color(15, 23, 42);
    private static final Color COR_PAINEL = new Color(30, 41, 59);
    private static final Color COR_CAMPO  = new Color(51, 65, 85);
    private static final Color COR_TEXTO  = new Color(241, 245, 249);
    private static final Color COR_LABEL  = new Color(148, 163, 184);
    private static final Color COR_AZUL   = new Color(59, 130, 246);

    public UsuarioFormDialog(Frame parent, Usuario usuario) {
        super(parent, usuario == null ? "Novo Usuario" : "Editar Usuario", true);
        this.usuarioEdicao = usuario;
        this.dao           = new UsuarioDAO();
        construirUI();
        if (usuario != null) preencherCampos(usuario);
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
                usuarioEdicao == null ? "Novo Usuario" : "Editar Usuario");
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

        // Nome
        g.gridx=0; g.gridy=0; g.weightx=0; form.add(label("Nome:"), g);
        g.gridx=1; g.weightx=1;
        txtNome = campo();
        form.add(txtNome, g);

        // Email
        g.gridx=0; g.gridy=1; g.weightx=0; form.add(label("E-mail:"), g);
        g.gridx=1; g.weightx=1;
        txtEmail = campo();
        form.add(txtEmail, g);

        // Senha
        g.gridx=0; g.gridy=2; g.weightx=0; form.add(label("Senha:"), g);
        g.gridx=1; g.weightx=1;
        txtSenha = new JPasswordField(20);
        estilizarField(txtSenha);
        JLabel lblSenhaHint = new JLabel(
                usuarioEdicao != null ? "Deixe vazio para manter a atual" : "");
        lblSenhaHint.setForeground(COR_LABEL);
        lblSenhaHint.setFont(new Font("Arial", Font.ITALIC, 11));
        JPanel painelSenha = new JPanel(new BorderLayout(0, 2));
        painelSenha.setBackground(COR_FUNDO);
        painelSenha.add(txtSenha,    BorderLayout.NORTH);
        painelSenha.add(lblSenhaHint,BorderLayout.SOUTH);
        form.add(painelSenha, g);

        // Funcao
        g.gridx=0; g.gridy=3; g.weightx=0; form.add(label("Funcao:"), g);
        g.gridx=1; g.weightx=1;
        cmbFuncao = new JComboBox<>(new String[]{"USER", "ADMIN"});
        estilizarCombo(cmbFuncao);
        form.add(cmbFuncao, g);

        // Renda mensal
        g.gridx=0; g.gridy=4; g.weightx=0; form.add(label("Renda (R$):"), g);
        g.gridx=1; g.weightx=1;
        txtRenda = campo();
        txtRenda.setText("0,00");
        form.add(txtRenda, g);

        // Perfil investidor
        g.gridx=0; g.gridy=5; g.weightx=0; form.add(label("Perfil:"), g);
        g.gridx=1; g.weightx=1;
        cmbPerfil = new JComboBox<>(
                new String[]{"—", "CONSERVADOR", "MODERADO", "AGRESSIVO"});
        estilizarCombo(cmbPerfil);
        form.add(cmbPerfil, g);

        // Ativo
        g.gridx=0; g.gridy=6; g.weightx=0; form.add(label("Ativo:"), g);
        g.gridx=1; g.weightx=1;
        chkAtivo = new JCheckBox("Usuario ativo no sistema");
        chkAtivo.setBackground(COR_FUNDO);
        chkAtivo.setForeground(COR_LABEL);
        chkAtivo.setFont(new Font("Arial", Font.PLAIN, 13));
        chkAtivo.setSelected(true);
        form.add(chkAtivo, g);

        root.add(form, BorderLayout.CENTER);

        // Botoes
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rodape.setBackground(COR_FUNDO);
        rodape.setBorder(new EmptyBorder(16, 0, 0, 0));

        btnCancelar = new JButton("Cancelar");
        estilizarBotao(btnCancelar, COR_PAINEL, COR_LABEL);
        btnCancelar.addActionListener(e -> dispose());

        btnSalvar = new JButton(usuarioEdicao == null ? "Salvar" : "Atualizar");
        estilizarBotao(btnSalvar, COR_AZUL, Color.WHITE);
        btnSalvar.addActionListener(e -> salvar());

        rodape.add(btnCancelar);
        rodape.add(btnSalvar);
        root.add(rodape, BorderLayout.SOUTH);

        setContentPane(root);
        setMinimumSize(new Dimension(420, 460));
    }

    private void salvar() {
        try {
            String nome  = txtNome.getText().trim();
            String email = txtEmail.getText().trim();
            String senha = new String(txtSenha.getPassword()).trim();

            if (nome.isEmpty())  throw new Exception("Nome e obrigatorio.");
            if (email.isEmpty()) throw new Exception("E-mail e obrigatorio.");
            if (usuarioEdicao == null && senha.isEmpty())
                throw new Exception("Senha e obrigatoria para novos usuarios.");

            if (dao.emailExiste(email, usuarioEdicao != null ? usuarioEdicao.getId() : 0))
                throw new Exception("Este e-mail ja esta em uso.");

            double renda = Double.parseDouble(
                    txtRenda.getText().replace(",", ".").trim());

            Usuario u = usuarioEdicao != null ? usuarioEdicao : new Usuario();
            u.setNome(nome);
            u.setEmail(email);
            if (!senha.isEmpty()) u.setSenha(senha);
            u.setFuncao((String) cmbFuncao.getSelectedItem());
            u.setRendaMensal(renda);
            String perfil = (String) cmbPerfil.getSelectedItem();
            u.setPerfilInvestidor("—".equals(perfil) ? null : perfil);
            u.setAtivo(chkAtivo.isSelected());

            boolean ok = usuarioEdicao == null
                    ? dao.inserir(u)
                    : dao.atualizar(u);

            if (ok) { salvo = true; dispose(); }
            else JOptionPane.showMessageDialog(this,
                    "Erro ao salvar.", "Erro", JOptionPane.ERROR_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Renda invalida. Use o formato: 2500,00",
                    "Atencao", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    e.getMessage(), "Atencao", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void preencherCampos(Usuario u) {
        txtNome.setText(u.getNome());
        txtEmail.setText(u.getEmail());
        txtRenda.setText(String.format("%.2f", u.getRendaMensal()).replace(".", ","));
        cmbFuncao.setSelectedItem(u.getFuncao());
        cmbPerfil.setSelectedItem(
                u.getPerfilInvestidor() != null ? u.getPerfilInvestidor() : "—");
        chkAtivo.setSelected(u.isAtivo());
    }

    public boolean isSalvo() { return salvo; }

    // ── Helpers ────────────────────────────────────────────────────────────
    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(COR_LABEL);
        l.setFont(new Font("Arial", Font.PLAIN, 13));
        l.setPreferredSize(new Dimension(100, 30));
        return l;
    }

    private JTextField campo() {
        JTextField f = new JTextField(20);
        estilizarField(f);
        return f;
    }

    private void estilizarField(JTextField f) {
        f.setBackground(COR_CAMPO);
        f.setForeground(COR_TEXTO);
        f.setCaretColor(COR_TEXTO);
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
                new EmptyBorder(6, 10, 6, 10)));
        f.setPreferredSize(new Dimension(260, 36));
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