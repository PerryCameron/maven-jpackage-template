package com.changenode;


import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import com.changenode.FxInterface.ControllerFx;
import com.changenode.FxInterface.Log;
import com.changenode.widgetfx.FileChooserWidgets;
import javafx.application.Application;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;

import java.io.File;

import static java.awt.Taskbar.getTaskbar;
import static java.lang.System.out;

public class Controller implements ControllerFx {

    private final Interactor interactor;
    private final ViewBuilder viewBuilder;
    private LogIt logger;
    public Controller() {
        Model model = new Model();
        interactor = new Interactor(model);
        viewBuilder = new ViewBuilder(model, this::requestUserAttention);
        logger = new LogIt(model);
        model.isDarkProperty().addListener(observable -> {
            setToggleDark(model.isDarkProperty().get());
        });
        model.logDataProperty().addListener(observable -> {
            refreshLog(model.logDataProperty().get());
        });
        model.fileChooserOpenProperty().addListener(observable -> {
            openFileDialog(model);
        });
        setToggleDark(false);
    }

    private void refreshLog(LogData logData) {
        logger.processData(logData);
    }

    private void requestUserAttention(Void unused) {
        Runnable task = () -> {
            try {
                Thread.sleep(5000); // delay for 5 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getTaskbar().requestUserAttention(true, true);
            out.println("I need your attention");
        };
        new Thread(task).start();
    }

    private void openFileDialog(Model model) {
        if(model.fileChooserOpenProperty().get()) {
            FileChooser fileChooser = FileChooserWidgets.fileChooseOf("Open File", "user.home");
            File file = fileChooser.showOpenDialog(BaseApplication.getMainStage());
            if (file != null) model.setLogData(new LogData(Log.LoggingType.BOTH, file.getAbsolutePath()));
            else model.setLogData(new LogData(Log.LoggingType.BOTH, "Open File cancelled."));
        }
        model.setFileChooserOpen(false);
    }

    private void setToggleDark(Boolean isDark) {
        if (isDark) Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        else Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
    }

    public Region getViewBuilder() { return viewBuilder.build(); }
}
