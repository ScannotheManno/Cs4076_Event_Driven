package org.openjfx.server_23390573_23381272;

public class IncorrectActionException extends Exception {
    public IncorrectActionException() {
        super("Not a Valid Action.");
    }

    public IncorrectActionException(String msg) {
        super(msg);
    }
}