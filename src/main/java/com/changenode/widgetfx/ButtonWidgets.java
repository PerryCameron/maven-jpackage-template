package com.changenode.widgetfx;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Button;

public class ButtonWidgets {

    public static Button boundDarkButton(BooleanProperty booleanProperty) {
        Button button = new Button();
        button.setText("Light");
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
