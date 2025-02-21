import java.net.*;
import java.io.*;

public class 23390573_23381272_Server {
    
    private static InetAddress host;
    private static final int PORT = 5555;

    public static void main(String[] args) {

        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.out.println("Host not Found");
            System.exit(1);
        }

    }

    public static void run() {
        Socket link = null;
        try {
            link = new Socket(host, PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
            PrintWriter out = new PrintWriter(link.getOutputStream(), true);
        } catch (IOException e) {
            e.printSrackTrace();
        } finally {
            try {
                System.out.println("\n Closing Connection...");
                System.exit(1);
            } catch (IOException e) {
                System.out.println("Unable to disconnect");
                system.exit(1);
            }
        }
    }
}