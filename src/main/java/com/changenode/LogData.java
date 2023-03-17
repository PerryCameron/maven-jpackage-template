package com.changenode;

import com.changenode.FxInterface.Log;

public record LogData(Log.LoggingType type, String message) { }