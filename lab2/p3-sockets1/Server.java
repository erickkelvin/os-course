import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        int portNumber = 6014;
        ServerSocket serverSocket = null;
        Socket socket = null;
            
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
            
        }
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("Exception caught when trying to listen on port "
                                   + portNumber + " or listening for a connection");
                System.out.println(e.getMessage());
            }
            // new thread for a client
            new ClientThread(socket).start();
        }
    }
}
