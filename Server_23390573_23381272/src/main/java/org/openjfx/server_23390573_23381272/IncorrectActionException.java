/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.openjfx.server_23390573_23381272;

/**
 *
 * @author Luke
 */

public class IncorrectActionException extends Exception {
    public IncorrectActionException() {
        super("Not a Valid Action.");
    }

    public IncorrectActionException(String msg) {
        super(msg);
    }
}

