package org.sam.calendario;
import javax.swing.*;
import java.awt.*;

public class PanelDateOption extends JPanel {
    private boolean disableChange;

    public void setSelectedCustom() {
        if (!disableChange) {
            JToggleButton customButton = null;
            for (Component com : getComponents()) {
                String name = com.getName();
                if (name != null && name.equals("custom")) {
                    customButton = (JToggleButton) com;
                    break;
                }
            }
            if (customButton != null) {
                customButton.setSelected(true);
            }
        }
        disableChange = false;
    }
}
