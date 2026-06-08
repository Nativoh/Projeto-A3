package com.controleFinanceiro.view;

import com.controleFinanceiro.DAO.UsuarioDAO;
import com.controleFinanceiro.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class CadastroDialog extends JDialog {

    private JTextField     txtNome;
    private JTextField     txtEmail;
    private JPasswordField txtSenha;
    private JPasswordField txtConfirmar;
    private JLabel         lblErro;
    private String         emailCadastrado = null;

    private static final Color FUNDO      = new Color(15, 23, 42);
    private static final Color PAINEL     = new Color(30, 41, 59);
    private static final Color CAMPO      = new Color(51, 65, 85);
    private static final Color BORDA      = new Color(71, 85, 105);
    private static final Color BORDA_FOC  = new Color(59, 130, 246);
    private static final Color TEXTO      = new Color(241, 245, 249);
    private static final Color SEC        = new Color(148, 163, 184);
    private static final Color AZUL       = new Color(59, 130, 246);
    private static final Color AZUL_HOVER = new Color(37, 99, 235);
    private static final Color VERMELHO   = new Color(239, 68, 68);
    private static final Color VERDE      = new Color(16, 185, 129);

    public CadastroDialog(Frame parent) {
        super(parent, "Criar nova conta", true);
        construirUI();
        setSize(420, 480);
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void construirUI() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(FUNDO);
        root.setBorder(new EmptyBorder(28, 32, 28, 32));

        // Título
        JLabel titulo = new JLabel("Criar nova conta");
        titulo.setForeground(TEXTO);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Preencha os dados para se cadastrar");
        sub.setForeground(SEC);
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        root.add(titulo);
        root.add(Box.createVerticalStrut(4));
        root.add(sub);
        root.add(Box.createVerticalStrut(22));

        // Campos
        root.add(label("Nome completo"));
        root.add(Box.createVerticalStrut(5));
        txtNome = campo();
        root.add(txtNome);
        root.add(Box.createVerticalStrut(14));

        root.add(label("E-mail"));
        root.add(Box.createVerticalStrut(5));
        txtEmail = campo();
        root.add(txtEmail);
        root.add(Box.createVerticalStrut(14));

        root.add(label("Senha"));
        root.add(Box.createVerticalStrut(5));
        txtSenha = senha();
        root.add(txtSenha);
        root.add(Box.createVerticalStrut(14));

        root.add(label("Confirmar senha"));
        root.add(Box.createVerticalStrut(5));
        txtConfirmar = senha();
        root.add(txtConfirmar);
        root.add(Box.createVerticalStrut(14));

        // Erro
        lblErro = new JLabel(" ");
        lblErro.setForeground(VERMELHO);
        lblErro.setFont(new Font("Arial", Font.PLAIN, 12));
        lblErro.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(lblErro);
        root.add(Box.createVerticalStrut(8));

        // Botões
        JPanel botoes = new JPanel(new GridLayout(1, 2, 10, 0));
        botoes.setBackground(FUNDO);
        botoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        botoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(PAINEL);
        btnCancelar.setForeground(SEC);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 13));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dispose());

        JButton btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.setBackground(VERDE);
        btnCadastrar.setForeground(Color.WHITE);
        btnCadastrar.setFont(new Font("Arial", Font.BOLD, 13));
        btnCadastrar.setFocusPainted(false);
        btnCadastrar.setBorderPainted(false);
        btnCadastrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCadastrar.addActionListener(e -> cadastrar());

        botoes.add(btnCancelar);
        botoes.add(btnCadastrar);
        root.add(botoes);

        setContentPane(root);
        getContentPane().setBackground(FUNDO);
    }

    private void cadastrar() {
        String nome     = txtNome.getText().trim();
        String email    = txtEmail.getText().trim();
        String senha    = new String(txtSenha.getPassword());
        String confirma = new String(txtConfirmar.getPassword());

        // Validações
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            lblErro.setText("⚠️ Preencha todos os campos.");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            lblErro.setText("⚠️ E-mail inválido.");
            return;
        }
        if (senha.length() < 6) {
            lblErro.setText("⚠️ Senha mínima de 6 caracteres.");
            return;
        }
        if (!senha.equals(confirma)) {
            lblErro.setText("⚠️ As senhas não coincidem.");
            return;
        }

        // Verifica se e-mail já existe
        UsuarioDAO dao = new UsuarioDAO();
        if (dao.buscarPorEmail(email) != null) {
            lblErro.setText("⚠️ Este e-mail já está cadastrado.");
            return;
        }

        // Cria o usuário
        Usuario novo = new Usuario();
        novo.setNome(nome);
        novo.setEmail(email);
        novo.setSenha(senha);
        novo.setFuncao("USER");
        novo.setRendaMensal(0);
        novo.setAtivo(true);

        if (dao.inserir(novo)) {
            emailCadastrado = email;
            dispose();
        } else {
            lblErro.setText("⚠️ Erro ao cadastrar. Tente novamente.");
        }
    }

    public String getEmailCadastrado() { return emailCadastrado; }

    // ── Helpers ─────────────────────────────────────────────────────────────
    private JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(SEC);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField campo() {
        JTextField f = new JTextField();
        estilizar(f);
        return f;
    }

    private JPasswordField senha() {
        JPasswordField f = new JPasswordField();
        estilizar(f);
        return f;
    }

    private void estilizar(JTextField f) {
        f.setBackground(CAMPO);
        f.setForeground(TEXTO);
        f.setCaretColor(TEXTO);
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA, 1),
                new EmptyBorder(8, 10, 8, 10)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDA_FOC, 1),
                        new EmptyBorder(8, 10, 8, 10)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDA, 1),
                        new EmptyBorder(8, 10, 8, 10)));
            }
        });
    }
}