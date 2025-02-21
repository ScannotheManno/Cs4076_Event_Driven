import java.net.*;
import java.io.*;

public class 23390573_23381272_Server {
    private static ServerSocket servSock;
    private static final int PORT = 5555;
    private int clientConnections = 0;

    public static void main(String[] args) {
        System.out.println("Opening port...\n");

        try{
            servSock = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println("Unable to attach to port");
            System.exit(1);
        } do {
            run();
        } while (true);
    }

    public static void run() {
        Socket link = null;

        try {
            link = servSock.accept();
            clientConnections++;
            BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
            PrintWriter out = new PrintWriter(link.getOutputStream(), true);
        } catch (IOException e) {
            e.printSrackTrace();
        } finally {
            try {
                System.out.println("\n Closing Connection...");
                link.close();
            } catch (IOException e) {
                System.out.println("Unable to disconnect");
                System.exit(1);
            }
        }
    }
}