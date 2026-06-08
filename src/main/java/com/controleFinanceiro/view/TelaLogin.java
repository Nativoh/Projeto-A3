package com.controleFinanceiro.view;

import com.controleFinanceiro.DAO.UsuarioDAO;
import com.controleFinanceiro.controller.SessaoAtual;
import com.controleFinanceiro.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.prefs.Preferences;

public class TelaLogin extends JFrame {

    // ── Componentes ────────────────────────────────────────────────────────
    private JTextField     txtEmail;
    private JPasswordField txtSenha;
    private JCheckBox      chkLembrar;
    private JLabel         lblErro;
    private JButton        btnLogin;
    private JButton        btnCadastrar;
    private JLabel         lblEsqueceu;

    // ── Preferências salvas ────────────────────────────────────────────────
    private final Preferences prefs = Preferences.userNodeForPackage(TelaLogin.class);

    // ── Cores ──────────────────────────────────────────────────────────────
    private static final Color FUNDO       = new Color(15, 23, 42);
    private static final Color PAINEL      = new Color(30, 41, 59);
    private static final Color CAMPO       = new Color(51, 65, 85);
    private static final Color BORDA       = new Color(71, 85, 105);
    private static final Color BORDA_FOCUS = new Color(59, 130, 246);
    private static final Color TEXTO       = new Color(241, 245, 249);
    private static final Color SEC         = new Color(148, 163, 184);
    private static final Color AZUL        = new Color(59, 130, 246);
    private static final Color AZUL_HOVER  = new Color(37, 99, 235);
    private static final Color VERDE       = new Color(16, 185, 129);
    private static final Color VERMELHO    = new Color(239, 68, 68);

    public TelaLogin() {
        construirUI();
        carregarEmailSalvo();
    }

    // ══════════════════════════════════════════════════════════════════════
    // CONSTRUÇÃO DA UI
    // ══════════════════════════════════════════════════════════════════════

    private void construirUI() {
        setTitle("Controle Financeiro — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));
        getContentPane().setBackground(FUNDO);

        add(criarLadoEsquerdo());
        add(criarLadoDireito());
    }

    // ── Lado esquerdo — visual / branding ─────────────────────────────────
    private JPanel criarLadoEsquerdo() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradiente de fundo
                GradientPaint gp = new GradientPaint(
                        0, 0,            new Color(30, 58, 95),
                        0, getHeight(),  new Color(15, 23, 42));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Círculos decorativos
                g2.setColor(new Color(59, 130, 246, 30));
                g2.fillOval(-60, -60, 300, 300);
                g2.setColor(new Color(16, 185, 129, 20));
                g2.fillOval(getWidth()-150, getHeight()-150, 250, 250);
            }
        };
        p.setLayout(new GridBagLayout());

        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setOpaque(false);
        conteudo.setBorder(new EmptyBorder(0, 40, 0, 40));

        // Logo
        JLabel logo = new JLabel("💰");
        logo.setFont(new Font("Arial", Font.PLAIN, 52));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Nome do sistema
        JLabel nome = new JLabel("Controle Financeiro");
        nome.setFont(new Font("Arial", Font.BOLD, 32));
        nome.setForeground(Color.WHITE);
        nome.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Slogan
        JLabel slogan = new JLabel("Controle Financeiro Inteligente");
        slogan.setFont(new Font("Arial", Font.PLAIN, 14));
        slogan.setForeground(new Color(147, 197, 253));
        slogan.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Separador
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(59, 130, 246, 80));
        sep.setMaximumSize(new Dimension(200, 1));

        // Benefícios
        conteudo.add(logo);
        conteudo.add(Box.createVerticalStrut(12));
        conteudo.add(nome);
        conteudo.add(Box.createVerticalStrut(6));
        conteudo.add(slogan);
        conteudo.add(Box.createVerticalStrut(24));
        conteudo.add(sep);
        conteudo.add(Box.createVerticalStrut(24));

        p.add(conteudo);
        return p;
    }

    private JPanel beneficio(String emoji, String texto) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(280, 30));

        JLabel ic = new JLabel(emoji);
        ic.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel txt = new JLabel(texto);
        txt.setForeground(new Color(203, 213, 225));
        txt.setFont(new Font("Arial", Font.PLAIN, 13));

        p.add(ic);
        p.add(txt);
        return p;
    }

    // ── Lado direito — formulário ──────────────────────────────────────────
    private JPanel criarLadoDireito() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(FUNDO);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(FUNDO);
        form.setBorder(new EmptyBorder(0, 48, 0, 48));
        form.setPreferredSize(new Dimension(400, 500));

        // Título do formulário
        JLabel titulo = new JLabel("Bem-vindo!!");
        titulo.setForeground(TEXTO);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Entre com suas credenciais");
        sub.setForeground(SEC);
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(titulo);
        form.add(Box.createVerticalStrut(4));
        form.add(sub);
        form.add(Box.createVerticalStrut(28));

        // Campo Email
        form.add(criarLabel("E-mail"));
        form.add(Box.createVerticalStrut(6));
        txtEmail = criarCampo("seu@email.com", false);
        form.add(txtEmail);
        form.add(Box.createVerticalStrut(16));

        // Campo Senha
        form.add(criarLabel("Senha"));
        form.add(Box.createVerticalStrut(6));
        txtSenha = (JPasswordField) criarCampo("••••••••", true);
        form.add(txtSenha);
        form.add(Box.createVerticalStrut(10));

        // Lembrar + Esqueceu
        JPanel opcoes = new JPanel(new BorderLayout());
        opcoes.setBackground(FUNDO);
        opcoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        chkLembrar = new JCheckBox("Lembrar e-mail");
        chkLembrar.setBackground(FUNDO);
        chkLembrar.setForeground(SEC);
        chkLembrar.setFont(new Font("Arial", Font.PLAIN, 12));
        chkLembrar.setFocusPainted(false);

        lblEsqueceu = new JLabel("Esqueceu a senha?");
        lblEsqueceu.setForeground(AZUL);
        lblEsqueceu.setFont(new Font("Arial", Font.PLAIN, 12));
        lblEsqueceu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblEsqueceu.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { esqueceuSenha(); }
            public void mouseEntered(MouseEvent e) {
                lblEsqueceu.setForeground(AZUL_HOVER);
            }
            public void mouseExited(MouseEvent e) {
                lblEsqueceu.setForeground(AZUL);
            }
        });

        opcoes.add(chkLembrar, BorderLayout.WEST);
        opcoes.add(lblEsqueceu, BorderLayout.EAST);
        form.add(opcoes);
        form.add(Box.createVerticalStrut(20));

        // Mensagem de erro
        lblErro = new JLabel(" ");
        lblErro.setForeground(VERMELHO);
        lblErro.setFont(new Font("Arial", Font.PLAIN, 12));
        lblErro.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblErro);
        form.add(Box.createVerticalStrut(8));

        // Botão Login
        btnLogin = new JButton("Entrar");
        estilizarBotao(btnLogin, AZUL, AZUL_HOVER, Color.WHITE);
        btnLogin.addActionListener(e -> fazerLogin());
        // Enter no campo senha aciona o login
        txtSenha.addActionListener(e -> fazerLogin());
        form.add(btnLogin);
        form.add(Box.createVerticalStrut(12));

        // Divisor
        form.add(criarDivisor());
        form.add(Box.createVerticalStrut(12));

        // Botão Cadastrar
        btnCadastrar = new JButton("Criar nova conta");
        estilizarBotaoOutline(btnCadastrar);
        btnCadastrar.addActionListener(e -> abrirCadastro());
        form.add(btnCadastrar);

        outer.add(form);
        return outer;
    }

    // ══════════════════════════════════════════════════════════════════════
    // LÓGICA
    // ══════════════════════════════════════════════════════════════════════

    private void fazerLogin() {
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword());

        lblErro.setText(" ");

        if (email.isEmpty() || senha.isEmpty()) {
            mostrarErro("Preencha o e-mail e a senha.");
            return;
        }

        // Feedback visual — desabilita o botão durante a consulta
        btnLogin.setEnabled(false);
        btnLogin.setText("Entrando...");

        SwingWorker<Usuario, Void> worker = new SwingWorker<>() {
            @Override
            protected Usuario doInBackground() {
                return new UsuarioDAO().Login(email, senha);
            }

            @Override
            protected void done() {
                try {
                    Usuario usuario = get();
                    if (usuario != null) {
                        // Salvar email se "Lembrar" marcado
                        if (chkLembrar.isSelected()) {
                            prefs.put("emailSalvo", email);
                        } else {
                            prefs.remove("emailSalvo");
                        }

                        SessaoAtual.iniciar(usuario);

                        if (usuario.isAdmin()) {
                            new dashBoardAdmin().setVisible(true);
                        } else {
                            new dashBoardPrincipal().setVisible(true);
                        }
                        dispose();
                    } else {
                        mostrarErro("E-mail ou senha incorretos.");
                    }
                } catch (Exception ex) {
                    mostrarErro("Erro ao conectar. Tente novamente.");
                } finally {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Entrar");
                }
            }
        };
        worker.execute();
    }

    private void abrirCadastro() {
        CadastroDialog dialog = new CadastroDialog(this);
        dialog.setVisible(true);
        // Se cadastrou com sucesso, preenche o email
        if (dialog.getEmailCadastrado() != null) {
            txtEmail.setText(dialog.getEmailCadastrado());
            txtSenha.requestFocus();
            mostrarSucesso("Conta criada! Faça login para continuar.");
        }
    }

    private void esqueceuSenha() {
        String email = JOptionPane.showInputDialog(
                this,
                "Digite seu e-mail cadastrado:",
                "Recuperar Senha",
                JOptionPane.QUESTION_MESSAGE);

        if (email == null || email.trim().isEmpty()) return;

        Usuario u = new UsuarioDAO().buscarPorEmail(email.trim());
        if (u != null) {
            JOptionPane.showMessageDialog(this,
                    "Sua senha é: " + u.getSenha() + "\n\n"
                            + "Por segurança, altere sua senha após o login.",
                    "Recuperação de Senha",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "E-mail não encontrado no sistema.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void carregarEmailSalvo() {
        String emailSalvo = prefs.get("emailSalvo", "");
        if (!emailSalvo.isEmpty()) {
            txtEmail.setText(emailSalvo);
            chkLembrar.setSelected(true);
            txtSenha.requestFocus();
        }
    }

    private void mostrarErro(String msg) {
        lblErro.setForeground(VERMELHO);
        lblErro.setText("⚠️ " + msg);
        // Shake animation no campo
        txtSenha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VERMELHO, 1),
                new EmptyBorder(10, 12, 10, 12)));
    }

    private void mostrarSucesso(String msg) {
        lblErro.setForeground(VERDE);
        lblErro.setText("✅ " + msg);
    }

    // ══════════════════════════════════════════════════════════════════════
    // HELPERS DE ESTILO
    // ══════════════════════════════════════════════════════════════════════

    private JLabel criarLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(SEC);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField criarCampo(String placeholder, boolean senha) {
        JTextField f = senha ? new JPasswordField() : new JTextField();
        f.setBackground(CAMPO);
        f.setForeground(TEXTO);
        f.setCaretColor(TEXTO);
        f.setFont(new Font("Arial", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA, 1),
                new EmptyBorder(10, 12, 10, 12)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        // Foco — muda cor da borda
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDA_FOCUS, 1),
                        new EmptyBorder(10, 12, 10, 12)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDA, 1),
                        new EmptyBorder(10, 12, 10, 12)));
            }
        });
        return f;
    }

    private void estilizarBotao(JButton b, Color bg, Color hover, Color fg) {
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);

        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
    }

    private void estilizarBotaoOutline(JButton b) {
        b.setBackground(FUNDO);
        b.setForeground(SEC);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA, 1),
                new EmptyBorder(10, 12, 10, 12)));

        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(PAINEL);
                b.setForeground(TEXTO);
            }
            public void mouseExited(MouseEvent e) {
                b.setBackground(FUNDO);
                b.setForeground(SEC);
            }
        });
    }

    private JPanel criarDivisor() {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setBackground(FUNDO);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JSeparator s1 = new JSeparator();
        s1.setForeground(BORDA);
        JSeparator s2 = new JSeparator();
        s2.setForeground(BORDA);

        JLabel ou = new JLabel("ou");
        ou.setForeground(SEC);
        ou.setFont(new Font("Arial", Font.PLAIN, 12));
        ou.setHorizontalAlignment(SwingConstants.CENTER);

        p.add(s1, BorderLayout.WEST);
        p.add(ou, BorderLayout.CENTER);
        p.add(s2, BorderLayout.EAST);
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}