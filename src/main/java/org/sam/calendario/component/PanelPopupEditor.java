package org.sam.calendario.component;

import com.formdev.flatlaf.FlatClientProperties;
import org.sam.calendario.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

public abstract class PanelPopupEditor extends JPanel {
    protected JFormattedTextField editor;
    protected JPopupMenu popupMenu;
    protected boolean editorValidation = true;
    protected boolean isValid;
    protected boolean validationOnNull;
    protected String defaultPlaceholder;

    protected LookAndFeel oldThemes = UIManager.getLookAndFeel();

    public PanelPopupEditor() {}

    public void showPopup() {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();
            popupMenu.putClientProperty(FlatClientProperties.STYLE, "" +
                    "borderInsets:1,1,1,1");
            popupMenu.add(this);
        }
        if (UIManager.getLookAndFeel() != oldThemes) {
            SwingUtilities.updateComponentTreeUI(popupMenu);
            oldThemes = UIManager.getLookAndFeel();
        }
        Point point = Utils.adjustPopupLocation(popupMenu, editor);
        popupMenu.show(editor, point.x, point.y);
    }

    public void closePopup() {
        if (popupMenu != null) {
            popupMenu.setVisible(false);
            repaint();
        }
    }

    protected void checkValidation(boolean status) {
        boolean valid = status || isEditorValidationOnNull();
        if (isValid != valid) {
            isValid = valid;
            if (editor != null) {
                if (editorValidation) {
                    validChanged(editor, valid);
                }
            }
        }
    }

    protected void validChanged(JFormattedTextField editor, boolean isValid) {
        String style = isValid ? null : FlatClientProperties.OUTLINE_ERROR;
        editor.putClientProperty(FlatClientProperties.OUTLINE, style);
    }

    protected boolean isEditorValidationOnNull() {
        if (validationOnNull) {
            return false;
        }
        return editor != null && editor.getText().equals(getDefaultPlaceholder());
    }

    protected void commitEdit() {
        if (editor != null && editorValidation) {
            try {
                editor.commitEdit();
            } catch (ParseException e) {
            }
        }
    }

    protected abstract String getDefaultPlaceholder();
}
