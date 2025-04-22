package org.openjfx.servergui;

public class IncorrectActionException extends Exception {
    // No-arg constructor
    public IncorrectActionException() {
        super("Invalid Request: ");
    }
    // One parameter Constructor
    public IncorrectActionException(String msg) {
        super(msg);
    }
    
}