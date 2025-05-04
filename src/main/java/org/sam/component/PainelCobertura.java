package org.sam.component;

import net.miginfocom.swing.MigLayout;
import org.sam.swing.ButtonOutLine;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static org.sam.main.CoresApp.BRANCO;
import static org.sam.main.CoresApp.COR_PADRAO;


public class PainelCobertura extends JPanel {
    private final DecimalFormat formatoDecimal = new DecimalFormat("##0.###", DecimalFormatSymbols.getInstance(Locale.US));

    private ActionListener evento;
    private final MigLayout layout;
    private JLabel titulo;
    private JLabel descricao;
    private JLabel descricao1;
    private ButtonOutLine botao;
    private boolean isLogin;

    public PainelCobertura() {
        iniciarComponentes();
        setOpaque(false);
        layout = new MigLayout("wrap, fill", "[center]", "push[]25[]10[]25[]push");
        setLayout(layout);
        iniciar();
    }

    private void iniciar() {
        titulo = new JLabel("Bem-vindo de volta!");
        titulo.setFont(new Font("sansserif", Font.BOLD, 30));
        titulo.setForeground(BRANCO);
        add(titulo);
        descricao = new JLabel("Para se manter conectado conosco");
        descricao.setForeground(BRANCO);
        add(descricao);
        descricao1 = new JLabel("faça login com suas informações pessoais");
        descricao1.setForeground(BRANCO);
        add(descricao1);
        botao = new ButtonOutLine();
        botao.setBackground(BRANCO);
        botao.setForeground(BRANCO);
        botao.setText("ENTRAR");
        botao.setFocusPainted(false);
        botao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                evento.actionPerformed(ae);
            }
        });
        add(botao, "w 60%, h 40");
    }

    private void iniciarComponentes() {
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 327, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
        );
    }

    @Override
    protected void paintComponent(Graphics graficos) {
        Graphics2D g2 = (Graphics2D) graficos;
        GradientPaint gradiente = new GradientPaint(0, 0, COR_PADRAO, 0, getHeight(), new Color(0, 0, 0));
        g2.setPaint(gradiente);
        g2.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(graficos);
    }

    public void adicionarEvento(ActionListener evento) {
        this.evento = evento;
    }

    public void abaRegistrarEsquerda(double v) {
        v = Double.parseDouble(formatoDecimal.format(v));
        login(false);
        layout.setComponentConstraints(titulo, "pad 0 -" + v + "% 0 0");
        layout.setComponentConstraints(descricao, "pad 0 -" + v + "% 0 0");
        layout.setComponentConstraints(descricao1, "pad 0 -" + v + "% 0 0");
    }

    public void abaRegistrarDireita(double v) {
        v = Double.parseDouble(formatoDecimal.format(v));
        login(false);
        layout.setComponentConstraints(titulo, "pad 0 -" + v + "% 0 0");
        layout.setComponentConstraints(descricao, "pad 0 -" + v + "% 0 0");
        layout.setComponentConstraints(descricao1, "pad 0 -" + v + "% 0 0");
    }

    public void abaLoginEsquerda(double v) {
        v = Double.parseDouble(formatoDecimal.format(v));
        login(true);
        layout.setComponentConstraints(titulo, "pad 0 " + v + "% 0 " + v + "%");
        layout.setComponentConstraints(descricao, "pad 0 " + v + "% 0 " + v + "%");
        layout.setComponentConstraints(descricao1, "pad 0 " + v + "% 0 " + v + "%");
    }

    public void abaLoginDireita(double v) {
        v = Double.parseDouble(formatoDecimal.format(v));
        login(true);
        layout.setComponentConstraints(titulo, "pad 0 " + v + "% 0 " + v + "%");
        layout.setComponentConstraints(descricao, "pad 0 " + v + "% 0 " + v + "%");
        layout.setComponentConstraints(descricao1, "pad 0 " + v + "% 0 " + v + "%");
    }

    public void login(boolean login) {
        if (this.isLogin != login) {
            if (login) {
                titulo.setText("Cadastre-se");
                descricao.setText("Digite seus dados pessoais");
                descricao1.setText("para criar uma conta");
                botao.setText("NOVO CADASTRO");
            } else {
                titulo.setText("Ja tem cadastro?");
                descricao1.setText("faça login com suas informações pessoais");
                botao.setText("ENTRAR");
            }
            this.isLogin = login;
        }
    }
}