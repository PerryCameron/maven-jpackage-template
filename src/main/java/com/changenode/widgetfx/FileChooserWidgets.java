package com.changenode.widgetfx;

import javafx.stage.FileChooser;

import java.io.File;

public class FileChooserWidgets {

    public static FileChooser fileChooseOf(String text, String startDirectory) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(text);
        fileChooser.setInitialDirectory(new File(System.getProperty(startDirectory)));
        return fileChooser;
    }

}
