package org.sam.component;

import javax.swing.*;
import java.awt.*;

public class JPanelArredondado extends JPanel {
    private final int arc = 20;

    public JPanelArredondado() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground() != null ? getBackground() : Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }
}

