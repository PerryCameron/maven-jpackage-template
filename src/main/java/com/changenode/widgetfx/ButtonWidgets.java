package com.changenode.widgetfx;

import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;

public class ButtonWidgets {

    public static ToggleButton createDarkButton() {
        ToggleButton button = new ToggleButton();
        button.setText("Dark");
        button.setFocusTraversable(false);
        return button;
    }

    public static Button helloWorldButton() {
        Button button = new Button();
        button.setText("Hello World");
        button.setFocusTraversable(false);
        return button;
    }
}
