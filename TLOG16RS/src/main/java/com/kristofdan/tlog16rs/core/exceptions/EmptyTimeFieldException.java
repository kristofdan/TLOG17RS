package com.kristofdan.tlog16rs.core.exceptions;

public class EmptyTimeFieldException extends Exception {

    public EmptyTimeFieldException() {
    }

    public EmptyTimeFieldException(String msg) {
        super(msg);
    }
}
