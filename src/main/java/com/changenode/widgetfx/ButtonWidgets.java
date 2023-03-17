package com.changenode.widgetfx;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;

public class ButtonWidgets {

    public static Button menuButtonOf(String text, Boolean traversable) {
        Button button = new Button();
        button.setText(text);
        button.setFocusTraversable(traversable);
        return button;
    }

    public static ToggleButton nftToggleButtonOf(String text, BooleanProperty booleanProperty) {
        ToggleButton button = new ToggleButton(text);
        booleanProperty.bindBidirectional(button.selectedProperty());
        return button;
    }
}
