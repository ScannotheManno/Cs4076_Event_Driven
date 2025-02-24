/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.openjfx.server_23390573_23381272;

/**
 *
 * @author lukes
 */
public class IncorrectActionException extends Exception {
    private String msg;
    
    public IncorrectActionException() {
        this.msg = "Not a Valid Action.";
    }
    
    public IncorrectActionException(String msg) {
        super(msg);
    }
    
    public String getIncorrectActionMsg() {
        return this.msg;
    }
}
