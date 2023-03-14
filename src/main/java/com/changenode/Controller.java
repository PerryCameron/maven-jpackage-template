package com.changenode;


import com.changenode.FxInterface.ControllerFx;
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
        viewBuilder = new ViewBuilder(model, this::updateLog, this::requestUserAttention);
    }

    private void updateLog(Integer s, String t) {
        interactor.updateLogModel(s,t);
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

    public Region getViewBuilder() { return viewBuilder.build(); }
}
