package com.changenode;

import com.changenode.FxInterface.Log;

public class Interactor {

    private final Model model;
    private final Fetcher fetcher;
    public Interactor(Model model) {
        this.model = model;
        this.fetcher = new Fetcher();
    }
    public void updateLogModel(Log log) {

    }
}
