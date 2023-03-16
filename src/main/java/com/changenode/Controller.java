package com.changenode;


import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import com.changenode.FxInterface.ControllerFx;
import com.changenode.FxInterface.Log;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.layout.Region;

import static java.awt.Taskbar.getTaskbar;
import static java.lang.System.out;

public class Controller implements ControllerFx {

    private final Interactor interactor;
    private final ViewBuilder viewBuilder;
    public Controller() {
        Model model = new Model();
        interactor = new Interactor(model);
        viewBuilder = new ViewBuilder(model, this::requestUserAttention, this::setToggleDark);
    }

    private void updateLog(Log log) {
        interactor.updateLogModel(log);
    }

    private void requestUserAttention(Void unused) {
        Task<Void> task = new Task<>() {
            @Override
            public Void call() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getTaskbar().requestUserAttention(true, true);
                out.println("I need your attention");
                return null;
            }
        };
        new Thread(task).start();
    }

    // Here to decouple dependencies from AlantaFX from View
    private void setToggleDark(Boolean isDark) {
        if (isDark) Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        else Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
    }

    public Region getViewBuilder() { return viewBuilder.build(); }
}
