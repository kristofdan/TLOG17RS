package com.kristofdan.tlog16rs.core.exceptions;

public class NegativeMinutesOfWorkException extends Exception {

    public NegativeMinutesOfWorkException() {
    }

    public NegativeMinutesOfWorkException(String msg) {
        super(msg);
    }
}
