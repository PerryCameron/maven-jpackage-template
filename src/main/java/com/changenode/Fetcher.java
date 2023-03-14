package com.changenode;

import java.io.*;

import static javax.swing.filechooser.FileSystemView.getFileSystemView;

public class Fetcher {
    public static File outputFile;
    public Fetcher() {
        try {
            outputFile = File.createTempFile("debug", ".log", getFileSystemView().getDefaultDirectory());
            PrintStream output = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile)), true);
            System.setOut(output);
            System.setErr(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
