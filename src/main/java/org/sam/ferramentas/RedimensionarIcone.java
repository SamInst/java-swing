package org.sam.ferramentas;

import javax.swing.*;
import java.awt.*;

public class RedimensionarIcone {
    public static ImageIcon redimensionarIcone(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        int imgWidth = img.getWidth(null);
        int imgHeight = img.getHeight(null);

        int newWidth = width;
        int newHeight = (imgHeight * width) / imgWidth;

        if (newHeight > height) {
            newHeight = height;
            newWidth = (imgWidth * height) / imgHeight;
        }

        Image scaledImg = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

}
