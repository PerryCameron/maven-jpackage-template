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
            case LOG -> appendText(message);
            case STATUS_BAR -> appendLabel(message);
            case BOTH -> appendBoth(message);
        }
    }

    public void appendLogData(LogData data) { log(data.type(), data.message());}

    public void appendBoth(String text) {
        appendText(text);
        appendLabel(text);
    }

    public void appendText(String text) {
        sb.append(text  + System.lineSeparator());
        model.mainTextProperty().set(sb.toString());
    }

    public void appendLabel(String text) {
        model.statusLabelProperty().set(text);
    }
}

