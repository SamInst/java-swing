package org.sam.calendario.util;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class InputUtils extends MaskFormatter {
    private static Map<Component, OldEditorProperty> inputMap;

    public static LocalDate dateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDate stringToDate(DateTimeFormatter format, String value) {
        try {
            return LocalDate.from(format.parse(value));
        } catch (Exception e) {
            return null;
        }
    }

    public static LocalDate[] stringToDate(DateTimeFormatter format, String separator, String value) {
        try {
            String[] dates = value.split(separator);
            LocalDate from = LocalDate.from(format.parse(dates[0]));
            LocalDate to = LocalDate.from(format.parse(dates[1]));
            return new LocalDate[]{from, to};
        } catch (Exception e) {
            return null;
        }
    }

    public static void useDateInput(JFormattedTextField txt, String pattern, boolean between, String separator, ValueCallback callback, InputValidationListener inputValidationListener) {
        try {
            String format = datePatternToInputFormat(pattern, "#");
            DateInputFormat mask = new DateInputFormat(between ? format + separator + format : format, between, separator, pattern, inputValidationListener);
            OldEditorProperty oldEditorProperty = initEditor(txt, mask, callback);

            if (callback != null) {
                PropertyChangeListener propertyChangeListener = evt -> callback.valueChanged(txt.getValue());
                txt.addPropertyChangeListener("value", propertyChangeListener);
                oldEditorProperty.propertyChangeListener = propertyChangeListener;
            }
            putPropertyChange(txt, oldEditorProperty);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
    }

    public static String datePatternToInputFormat(String pattern, String rpm) {
        String regex = "[dmy]";
        return pattern.toLowerCase().replaceAll(regex, rpm);
    }

    private static OldEditorProperty initEditor(JFormattedTextField txt, MaskFormatter format, ValueCallback callback) {
        removePropertyChange(txt);
        OldEditorProperty oldEditorProperty = OldEditorProperty.getFromOldEditor(txt);
        format.setCommitsOnValidEdit(true);
        format.setPlaceholderCharacter('-');
        DefaultFormatterFactory df = new DefaultFormatterFactory(format);
        txt.setFormatterFactory(df);

        txt.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        txt.putClientProperty(FlatClientProperties.TEXT_FIELD_CLEAR_CALLBACK, (Consumer<Object>) o -> {
            txt.setValue(null);
            if (callback != null) {
                callback.valueChanged(null);
            }
        });
        return oldEditorProperty;
    }

    private static void putPropertyChange(JFormattedTextField txt, OldEditorProperty oldEditorProperty) {
        if (inputMap == null) {
            inputMap = new HashMap<>();
        }
        inputMap.put(txt, oldEditorProperty);
    }

    public static void removePropertyChange(JFormattedTextField txt) {
        if (inputMap == null) {
            return;
        }
        OldEditorProperty oldEditorProperty = inputMap.get(txt);
        if (oldEditorProperty != null) {
            oldEditorProperty.removeFromEditor(txt);
            inputMap.remove(txt);
        }
    }

    private static class DateInputFormat extends MaskFormatter {
        private final boolean between;
        private final String separator;
        private final DateFormat dateFormat;
        private final InputValidationListener inputValidationListener;

        public DateInputFormat(String mark, boolean between, String separator, String pattern, InputValidationListener inputValidationListener) throws ParseException {
            super(mark);
            this.between = between;
            this.separator = separator;
            this.dateFormat = new SimpleDateFormat(pattern);
            this.inputValidationListener = inputValidationListener;
            this.dateFormat.setLenient(false);
        }

        @Override
        public Object stringToValue(String value) throws ParseException {
            checkTime(value);
            return super.stringToValue(value);
        }

        public void checkTime(String value) throws ParseException {
            try {
                if (between) {
                    String[] values = value.split(separator);
                    Date fromDate = dateFormat.parse(values[0]);
                    Date toDate = dateFormat.parse(values[1]);

                    if (inputValidationListener == null) return;
                    if (inputValidationListener.isValidation()) {
                        LocalDate date1 = dateToLocalDate(fromDate);
                        LocalDate date2 = dateToLocalDate(toDate);
                        if (!inputValidationListener.checkSelectionAble(date1) ||
                                !inputValidationListener.checkSelectionAble(date2)) {
                            throw new ParseException("error selection able", 0);
                        }
                    }
                } else {
                    Date date = dateFormat.parse(value);

                    if (inputValidationListener == null) return;
                    if (inputValidationListener.isValidation()) {
                        LocalDate d = dateToLocalDate(date);
                        if (!inputValidationListener.checkSelectionAble(d)) {
                            throw new ParseException("error selection able", 0);
                        }
                    }
                }
                inputValidationListener.inputChanged(true);
            } catch (ParseException e) {
                if (inputValidationListener != null) {
                    inputValidationListener.inputChanged(false);
                }
                throw e;
            }
        }
    }

    public interface ValueCallback {
        void valueChanged(Object value);
    }

    private static class OldEditorProperty {

        protected PropertyChangeListener propertyChangeListener;
        protected JFormattedTextField.AbstractFormatterFactory formatter;
        protected Component oldTrailingComponent;
        private boolean isShowClearButton;
        private Consumer clearButtonCallback;
        private String outline;
        protected Object value;
        protected String text;

        protected static OldEditorProperty getFromOldEditor(JFormattedTextField editor) {
            OldEditorProperty oldEditorProperty = new OldEditorProperty();
            oldEditorProperty.formatter = editor.getFormatterFactory();
            oldEditorProperty.oldTrailingComponent = FlatClientProperties.clientProperty(editor, FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, null, Component.class);
            oldEditorProperty.isShowClearButton = FlatClientProperties.clientPropertyBoolean(editor, FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, false);
            oldEditorProperty.clearButtonCallback = FlatClientProperties.clientProperty(editor, FlatClientProperties.TEXT_FIELD_CLEAR_CALLBACK, null, Consumer.class);
            oldEditorProperty.outline = FlatClientProperties.clientProperty(editor, FlatClientProperties.OUTLINE, null, String.class);
            oldEditorProperty.value = editor.getValue();
            oldEditorProperty.text = editor.getText();
            return oldEditorProperty;
        }

        protected void removeFromEditor(JFormattedTextField editor) {
            editor.removePropertyChangeListener("value", propertyChangeListener);
            editor.setFormatterFactory(formatter);
            editor.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, oldTrailingComponent);
            editor.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, isShowClearButton);
            editor.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, clearButtonCallback);
            editor.putClientProperty(FlatClientProperties.OUTLINE, outline);
            editor.setValue(value);
            editor.setText(text);
        }
    }
}
