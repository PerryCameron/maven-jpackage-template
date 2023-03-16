package com.changenode;

import com.changenode.FxInterface.Log;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LogIt implements Log {
    private final Model model;
    private final StringBuilder sb;
    StringProperty textProperty;

    public LogIt(Model model) {
        this.sb = new StringBuilder(model.mainTextProperty().get());
        this.model = model;
        textProperty = new SimpleStringProperty(sb.toString());
    }

    @Override
    public void log(LoggingType type, String message) {
        switch (type) {
            case LOG -> appendText(message + System.lineSeparator());
            case STATUS_BAR -> appendLabel(message);
            case BOTH -> appendText(message + System.lineSeparator());
        }
    }

    public void appendText(String text) {
        sb.append(text);
        model.mainTextProperty().set(sb.toString());
    }

    public void appendLabel(String text) {
        model.statusLabelProperty().set(text);
    }
}

