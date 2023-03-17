package com.changenode;

import javafx.beans.property.*;

import java.awt.image.BufferedImage;

public class Model {

    private final StringProperty mainText = new SimpleStringProperty("");
    private final StringProperty statusLabel = new SimpleStringProperty("");
    private final BooleanProperty isDark = new SimpleBooleanProperty(false);
    private final IntegerProperty currentIconProgress = new SimpleIntegerProperty(1);
    private final ObjectProperty<BufferedImage> defaultIcon = new SimpleObjectProperty<>();
    private final ObjectProperty<BufferedImage> redCircleIcon = new SimpleObjectProperty<>();
    private final BooleanProperty fileChooserOpen = new SimpleBooleanProperty(false);
    private final ObjectProperty<LogData> logData = new SimpleObjectProperty<>();

    public ObjectProperty<LogData> logDataProperty() { return logData;}
    public LogData getLogData() {
        return logData.get();
    }
    public void setLogData(LogData logData) {
        this.logData.set(logData);
    }
    public boolean isFileChooserOpen() {
        return fileChooserOpen.get();
    }
    public BooleanProperty fileChooserOpenProperty() {
        return fileChooserOpen;
    }
    public void setFileChooserOpen(boolean fileChooserOpen) {
        this.fileChooserOpen.set(fileChooserOpen);
    }
    public StringProperty mainTextProperty() {
        return mainText;
    }
    public void setMainText(String mainText) {
        this.mainText.set(mainText);
    }
    public StringProperty statusLabelProperty() {
        return statusLabel;
    }
    public void setStatusLabel(String statusLabel) {
        this.statusLabel.set(statusLabel);
    }
    public BooleanProperty isDarkProperty() { return isDark; }
    public void setIsDark(boolean isDark) { this.isDark.set(isDark); }

    public IntegerProperty currentIconProgressProperty() { return currentIconProgress; }
    public void setCurrentIconProgress(int currentIconProgress) { this.currentIconProgress.set(currentIconProgress); }

    public ObjectProperty<BufferedImage> defaultIconProperty() { return defaultIcon; }
    public void setDefaultIcon(BufferedImage defaultIcon) { this.defaultIcon.set(defaultIcon); }

    public ObjectProperty<BufferedImage> redCircleIconProperty() { return redCircleIcon; }
    public void setRedCircleIcon(BufferedImage redCircleIcon) { this.redCircleIcon.set(redCircleIcon); }
}

