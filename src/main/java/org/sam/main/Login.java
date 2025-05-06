package org.sam.main;


import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import org.sam.component.PainelCobertura;
import org.sam.component.PainelLoginERegistro;
import org.sam.repository.usuario.UsuarioRepository;
import raven.popup.GlassPanePopup;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

import static java.lang.Double.parseDouble;

public class Login extends JFrame {
    private static Login instancia;
    private JLayeredPane fundo;
    private final DecimalFormat formatoDecimal = new DecimalFormat("##0.###", DecimalFormatSymbols.getInstance(Locale.US));
    private MigLayout migLayout;
    private PainelCobertura painelCobertura;
    private PainelLoginERegistro painelLoginERegistro;
    private boolean isLogin = true;
    private final double tamanhoAdicional = 30;
    private final double tamanhoCobertura = 40;
    private final double tamanhoLogin = 60;

    public Login() {
        instancia = this;
        GlassPanePopup.install(this);
        FlatRobotoFont.install();
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        FlatLightLaf.setup();

        ImageIcon imageIcon = new ImageIcon(
                Objects.requireNonNull(
                    getClass()
                    .getClassLoader()
                    .getResource("icones/funcionarioCadastro.png")));
        setIconImage(imageIcon.getImage());

        iniciarComponentes();
        iniciar();

    }

    public static void fechar() {
        if (instancia != null) {
            SwingUtilities.invokeLater(instancia::dispose);
        }
    }

    private void iniciar() {
        migLayout = new MigLayout("fill, insets 0");
        painelCobertura = new PainelCobertura();
        painelLoginERegistro = new PainelLoginERegistro(new UsuarioRepository());

        TimingTarget alvo = new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fracao) {
                double fracaoCobertura;
                double fracaoLogin;
                double tamanho = tamanhoCobertura;

                if (fracao <= 0.5f) tamanho += fracao * tamanhoAdicional;
                else tamanho += tamanhoAdicional - fracao * tamanhoAdicional;

                if (isLogin) {
                    fracaoCobertura = 1f - fracao;
                    fracaoLogin = fracao;

                    if (fracao >= 0.5f) painelCobertura.abaRegistrarDireita(fracaoCobertura * 100);
                    else painelCobertura.abaLoginDireita(fracaoLogin * 100);

                } else {
                    fracaoCobertura = fracao;
                    fracaoLogin = 1f - fracao;

                    if (fracao <= 0.5f) painelCobertura.abaRegistrarEsquerda(fracao * 100);
                    else painelCobertura.abaLoginEsquerda((1f - fracao) * 100);
                }

                if (fracao >= 0.5f) painelLoginERegistro.mostrarRegistro(isLogin);

                fracaoCobertura = parseDouble(formatoDecimal.format(fracaoCobertura));
                fracaoLogin = parseDouble(formatoDecimal.format(fracaoLogin));
                migLayout.setComponentConstraints(painelCobertura, "width " + tamanho + "%, pos " + fracaoCobertura + "al 0 n 100%");
                migLayout.setComponentConstraints(painelLoginERegistro, "width " + tamanhoLogin + "%, pos " + fracaoLogin + "al 0 n 100%");
                fundo.revalidate();
            }

            @Override
            public void end() { isLogin = !isLogin; }
        };

        Animator animador = new Animator(800, alvo);
        animador.setAcceleration(0.5f);
        animador.setDeceleration(0.5f);
        animador.setResolution(0);

        fundo.setLayout(migLayout);
        fundo.add(painelCobertura, "width " + tamanhoCobertura + "%, pos " + (isLogin ? "1al" : "0al") + " 0 n 100%");
        fundo.add(painelLoginERegistro, "width " + tamanhoLogin + "%, pos " + (isLogin ? "0al" : "1al") + " 0 n 100%");
        painelLoginERegistro.mostrarRegistro(!isLogin);
        painelCobertura.login(isLogin);

        painelCobertura.adicionarEvento(evento -> {
            if (!animador.isRunning()) {
                animador.start();
            }
        });
    }

    private void iniciarComponentes() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        fundo = new JLayeredPane();
        fundo.setBackground(new Color(255, 255, 255));
        fundo.setOpaque(true);

        GroupLayout fundoLayout = new GroupLayout(fundo);
        fundo.setLayout(fundoLayout);
        fundoLayout.setHorizontalGroup(
                fundoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 933, Short.MAX_VALUE)
        );
        fundoLayout.setVerticalGroup(
                fundoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 537, Short.MAX_VALUE)
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(fundo, GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(fundo)
        );

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new Login().setVisible(true));
    }
}