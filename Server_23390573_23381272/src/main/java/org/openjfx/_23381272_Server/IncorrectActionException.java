package org.openjfx._23381272_Server;

public class IncorrectActionException extends Exception {
    public IncorrectActionException() {
        super("Not a Valid Action.");
    }

    public IncorrectActionException(String msg) {
        super(msg);
    }
}