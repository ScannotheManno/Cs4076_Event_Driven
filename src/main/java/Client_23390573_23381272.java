import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client_23390573_23381272 {

    private static InetAddress host;
    private static final int PORT = 5555;

    static {
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.out.println("Host not Found");
            System.exit(1);
        }
    }

    public String sendMessage(String message) {
        String response = "";
        try (Socket link = new Socket(host, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
             PrintWriter out = new PrintWriter(link.getOutputStream(), true)) {

            out.println(message);
            response = in.readLine(); // Read response from server

        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
