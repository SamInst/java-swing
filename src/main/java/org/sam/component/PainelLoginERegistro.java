package org.sam.component;

import net.miginfocom.swing.MigLayout;
import org.sam.repository.usuario.Usuario;
import org.sam.repository.usuario.UsuarioRepository;
import org.sam.swing.Button;
import org.sam.swing.MyPasswordField;
import org.sam.swing.MyTextField;
import raven.alerts.MessageAlerts;

import javax.swing.*;
import java.awt.*;

import static org.sam.main.CoresApp.BRANCO;
import static org.sam.main.CoresApp.COR_PADRAO;

public class PainelLoginERegistro extends JLayeredPane {
    private final UsuarioRepository usuarioRepository;

    private JPanel login;
    private JPanel registro;
    private static final Color COR_TEXTO = Color.DARK_GRAY;

    public PainelLoginERegistro(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        iniciarComponentes();
        iniciarRegistro();
        iniciarLogin();
        login.setVisible(false);
        registro.setVisible(true);
    }

    private void iniciarRegistro() {
        registro.setLayout(new MigLayout("wrap", "push[center]push", "push[]25[]10[]10[]25[]push"));
        JLabel rotulo = new JLabel("Criar usuario");
        rotulo.setFont(new Font("sansserif", Font.BOLD, 30));
        rotulo.setForeground(COR_PADRAO);
        registro.add(rotulo);

        MyTextField txtUsuario = new MyTextField();
        txtUsuario.setPrefixIcon(new ImageIcon(("src/icones/usuario.png")));
        txtUsuario.setHint("Nome");
        txtUsuario.setForeground(COR_TEXTO);
        registro.add(txtUsuario, "w 60%");

        MyTextField txtEmail = new MyTextField();
        txtEmail.setPrefixIcon(new ImageIcon("src/icones/email.png"));
        txtEmail.setHint("Email");
        txtEmail.setForeground(COR_TEXTO);
        registro.add(txtEmail, "w 60%");

        MyPasswordField txtSenha = new MyPasswordField();
        txtSenha.setPrefixIcon(new ImageIcon("src/icones/senha.png"));
        txtSenha.setHint("Senha");
        txtSenha.setForeground(COR_TEXTO);
        registro.add(txtSenha, "w 60%");

        Button botao = new Button();
        botao.setBackground(COR_PADRAO);
        botao.setForeground(BRANCO);
        botao.setText("CADASTRAR");
        botao.setFocusPainted(false);
        registro.add(botao, "w 40%, h 40");

        botao.addActionListener(e -> {
            if (txtUsuario.getText().trim().isEmpty() ||
                    txtEmail.getText().trim().isEmpty() ||
                    txtSenha.getPassword().length == 0) {
                MessageAlerts.getInstance()
                        .showMessage(
                                "Alerta",
                                "Todos os campos devem ser preenchidos",
                                MessageAlerts.MessageType.WARNING);
                return;
            }

            String usuario = txtUsuario.getText().trim();
            String email = txtEmail.getText().trim();
            String senha = new String(txtSenha.getPassword());

            Usuario novoUsuario = new Usuario(usuario, email, senha);

            try {
                boolean sucesso = usuarioRepository.criarPerfil(novoUsuario);

                if (sucesso) {
                    MessageAlerts.getInstance()
                            .showMessage(
                                    "Cadastro realizado",
                                    "Usuário cadastrado com sucesso!",
                                    MessageAlerts.MessageType.SUCCESS);
                    txtUsuario.setText("");
                    txtEmail.setText("");
                    txtSenha.setText("");
                }
            } catch (RuntimeException ex) {
            }
        });
    }

    private void iniciarLogin() {
        login.setLayout(new MigLayout("wrap", "push[center]push", "push[]25[]10[]10[]25[]push"));
        JLabel rotulo = new JLabel("Entrar");
        rotulo.setFont(new Font("sansserif", Font.BOLD, 30));
        rotulo.setForeground(COR_PADRAO);
        login.add(rotulo);

        MyTextField txtEmail = new MyTextField();
        txtEmail.setPrefixIcon(new ImageIcon("src/icones/email.png"));
        txtEmail.setHint("Email");
        txtEmail.setForeground(COR_TEXTO);
        login.add(txtEmail, "w 60%");

        MyPasswordField txtSenha = new MyPasswordField();
        txtSenha.setPrefixIcon(new ImageIcon("src/icones/senha.png"));
        txtSenha.setHint("Senha");
        txtSenha.setForeground(COR_TEXTO);
        login.add(txtSenha, "w 60%");

        Button botao = new Button();
        botao.setBackground(COR_PADRAO);
        botao.setForeground(BRANCO);
        botao.setText("ENTRAR");
        botao.setFocusPainted(false);
        login.add(botao, "w 40%, h 40");

        botao.addActionListener(e -> {
            String senha = new String(txtSenha.getPassword());
            try {
                Usuario usuarioAutenticado = usuarioRepository.autenticarUsuario(txtEmail.getText(), senha);

                MessageAlerts.getInstance()
                        .showMessage(
                                "Autenticação bem-sucedida",
                                "Bem-vindo, " + usuarioAutenticado.usuario() + "!",
                                MessageAlerts.MessageType.SUCCESS);

            } catch (RuntimeException ex) {
                MessageAlerts.getInstance()
                        .showMessage(
                                "Falha na autenticação",
                                "Email ou senha incorretos",
                                MessageAlerts.MessageType.WARNING);
            }
        });
    }

    public void mostrarRegistro(boolean mostrar) {
        if (mostrar) {
            registro.setVisible(true);
            login.setVisible(false);
        } else {
            registro.setVisible(false);
            login.setVisible(true);
        }
    }

    private void iniciarComponentes() {
        login = new JPanel();
        registro = new JPanel();

        setLayout(new CardLayout());

        login.setBackground(BRANCO);

        GroupLayout loginLayout = new GroupLayout(login);
        login.setLayout(loginLayout);

        loginLayout.setHorizontalGroup(
                loginLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 327, Short.MAX_VALUE)
        );
        loginLayout.setVerticalGroup(
                loginLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
        );

        add(login, "card3");

        registro.setBackground(BRANCO);

        GroupLayout registroLayout = new GroupLayout(registro);
        registro.setLayout(registroLayout);
        registroLayout.setHorizontalGroup(
                registroLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 327, Short.MAX_VALUE)
        );

        registroLayout.setVerticalGroup(
                registroLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
        );

        add(registro, "card2");
    }
}