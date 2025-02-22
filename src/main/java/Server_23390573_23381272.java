import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server_23390573_23381272 {
    private static ServerSocket servSock;
    private static final int PORT = 5555;
    private static int clientConnections = 0;

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
            String message = in.readLine();
            try {
                if (message.equals("Hello")) {
                    throw new IncorrectActionException();
                } else {
                    System.out.println(message);
                }
            } catch (IncorrectActionException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
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