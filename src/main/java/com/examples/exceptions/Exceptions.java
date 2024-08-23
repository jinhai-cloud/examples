package com.examples.exceptions;

import com.examples.commons.Placeholder;

public class Exceptions {

    public static String getMessage(Throwable e) {
        if (e == null) {
            return "";
        }
        return Placeholder.format("{}: {}", e.getClass().getSimpleName(), e.getMessage());
    }

    public static String getMessage(String message, Throwable e) {
        String msg = getMessage(e);
        if (message == null) {
            return msg;
        }
        return Placeholder.format("{}. {}", message, msg);
    }
}
