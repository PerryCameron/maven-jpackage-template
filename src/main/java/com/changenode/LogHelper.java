package com.changenode;

import com.changenode.FxInterface.Log;
import com.changenode.FxInterface.LogConstants;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LogHelper implements Log, LogConstants {
    private final StringBuilder sb;
    private final Model model;

    public LogHelper(Model model) {
        this.model = model;
        this.sb = new StringBuilder(model.mainTextProperty().get());
        StringProperty textProperty = new SimpleStringProperty(sb.toString());
        model.mainTextProperty().bindBidirectional(textProperty);
    }

    public void log(Integer s, String t) {
        switch(s) {
            case TO_LABEL:
                appendLabel(t);
                break;
            case TO_TEXT:
                appendText(t + System.lineSeparator());
                break;
            default:
                appendText(t + System.lineSeparator());
                appendLabel(t);
                break;
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

