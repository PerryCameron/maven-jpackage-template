package com.changenode.FxInterface;

public interface Log {
    enum LoggingType {
        STATUS_BAR,
        LOG,
        BOTH;
    }

    void log(LoggingType type, String message);
}
