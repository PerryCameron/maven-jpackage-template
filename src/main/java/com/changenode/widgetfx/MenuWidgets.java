package com.changenode.widgetfx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class MenuWidgets {
    public static MenuItem menuItemOf(String name, EventHandler<ActionEvent> action, KeyCode keyCode) {
        MenuItem item = new MenuItem(name);
        item.setOnAction(action);
        if (keyCode != null)
            item.setAccelerator(new KeyCodeCombination(keyCode, KeyCombination.SHORTCUT_DOWN));
        return item;
    }
}
