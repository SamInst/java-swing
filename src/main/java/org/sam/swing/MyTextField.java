package org.sam.swing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static org.sam.main.CoresApp.COR_PADRAO;

public class MyTextField extends JTextField {
    public void setHint(String hint) {
        this.hint = hint;
    }

    public void setPrefixIcon(Icon prefixIcon) {
        this.prefixIcon = prefixIcon;
        initBorder();
    }
    private Icon prefixIcon;
    private Icon suffixIcon;
    private String hint = "";
    private final int borderRadius = 15;

    public MyTextField() {
        setOpaque(false);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(0, 0, 0, 0));
        setForeground(Color.DARK_GRAY);
        setFont(new java.awt.Font("sansserif", Font.PLAIN, 13));
        setSelectionColor(COR_PADRAO);
        setCaretColor(Color.DARK_GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), borderRadius, borderRadius);
        g2.setColor(COR_PADRAO);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, borderRadius, borderRadius);

        paintIcon(g);
        super.paintComponent(g);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (getText().length() == 0) {
            int h = getHeight();
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Insets ins = getInsets();
            FontMetrics fm = g.getFontMetrics();
            g.setColor(Color.GRAY);
            g.drawString(hint, ins.left, h / 2 + fm.getAscent() / 2 - 2);
        }
    }

    private void paintIcon(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (prefixIcon != null) {
            Image prefix = ((ImageIcon) prefixIcon).getImage();
            int y = (getHeight() - prefixIcon.getIconHeight()) / 2;
            g2.drawImage(prefix, 10, y, this);
        }
        if (suffixIcon != null) {
            Image suffix = ((ImageIcon) suffixIcon).getImage();
            int y = (getHeight() - suffixIcon.getIconHeight()) / 2;
            g2.drawImage(suffix, getWidth() - suffixIcon.getIconWidth() - 10, y, this);
        }
    }

    private void initBorder() {
        int left = 15;
        int right = 15;
        if (prefixIcon != null) left = prefixIcon.getIconWidth() + 15;
        if (suffixIcon != null) right = suffixIcon.getIconWidth() + 15;
        setBorder(BorderFactory.createEmptyBorder(10, left, 10, right));
    }
}