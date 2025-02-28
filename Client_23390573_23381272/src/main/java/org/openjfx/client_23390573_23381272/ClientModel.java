/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package org.openjfx.client_23390573_23381272;

/**
 * 
 * @author Luke
 */

import java.io.*;
import java.net.*;

public class ClientModel {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    
    private final InetAddress host;
    private final int port;
    ;
    
    public ClientModel(InetAddress host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public boolean initialise() {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String sendMessage(String message) throws IOException {
        if (out == null)
            return null;
        out.println(message);
        return in.readLine();
    }
    
    public void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
