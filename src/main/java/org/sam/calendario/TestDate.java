package org.sam.calendario;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TestDate extends JFrame {
    private final DatePicker datePicker;

    public TestDate() {
        setLayout(new MigLayout("wrap"));
        datePicker = new DatePicker();
        datePicker.addDateSelectionListener(dateEvent -> {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            if (datePicker.getDateSelectionMode() == DatePicker.DateSelectionMode.SINGLE_DATE_SELECTED) {
                LocalDate date = datePicker.getSelectedDate();
                if (date != null) {
                    System.out.println("date change " + df.format(datePicker.getSelectedDate()));
                } else {
                    System.out.println("date change to null");
                }
            } else {
                LocalDate[] dates = datePicker.getSelectedDateRange();
                if (dates != null) {
                    System.out.println("date change " + df.format(dates[0]) + " to " + df.format(dates[1]));
                } else {
                    System.out.println("date change to null");
                }
            }
        });

        datePicker.setDateSelectionAble((date) -> !date.isAfter(LocalDate.now()));
        datePicker.now();
        JFormattedTextField editor = new JFormattedTextField();
        datePicker.setEditor(editor);
        add(editor, "width 250");
    }

    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        FlatMacDarkLaf.setup();
        EventQueue.invokeLater(() -> new TestDate().setVisible(true));
    }
}
