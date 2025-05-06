package org.sam.ferramentas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Mascara {
    public static void mascaraValor(JFormattedTextField campoValor){
        campoValor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = campoValor.getText().replaceAll("[^-0-9]", "");
                boolean isNegative = texto.startsWith("-");

                if (!texto.isEmpty() && !(texto.equals("-"))) {
                    double valor = Double.parseDouble(texto.replace("-", "")) / 100;

                    if (valor == 0) {
                        campoValor.setText("");
                        campoValor.setForeground(Color.DARK_GRAY.brighter());
                        campoValor.setCaretPosition(campoValor.getText().length());
                    } else {
                        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
                        symbols.setDecimalSeparator(',');
                        symbols.setGroupingSeparator('.');
                        DecimalFormat formato = new DecimalFormat("#,##0.00", symbols);
                        String formattedValue = (isNegative ? "-" : "") + "R$ " + formato.format(valor);

                        campoValor.setText(formattedValue);
                        campoValor.setCaretPosition(campoValor.getText().length());

                        if (isNegative) {
                            campoValor.setForeground(Color.RED);
                        } else {
                            campoValor.setForeground(Color.GREEN.darker());
                        }
                    }
                } else if (texto.equals("-")) {
                    campoValor.setText("-");
                    campoValor.setForeground(Color.RED);
                    campoValor.setCaretPosition(campoValor.getText().length());
                } else {
                    campoValor.setText("");
                    campoValor.setForeground(Color.DARK_GRAY.brighter());
                    campoValor.setCaretPosition(campoValor.getText().length());
                }
            }
        });
    }
}
