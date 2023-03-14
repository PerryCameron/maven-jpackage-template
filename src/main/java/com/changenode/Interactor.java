package com.changenode;

public class Interactor {

    Fetcher fetcher = new Fetcher();
    private LogHelper log;
    private final Model model;
    public Interactor(Model model) {
        this.model = model;
        this.log = new LogHelper(model);
    }
    public void updateLogModel(Integer s, String t) {
        log.log(s,t);
    }

}
