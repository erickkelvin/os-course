//Author: Erick Kelvin

import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        int portNumber = 6013;
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int maxClients = 10;
        int maxMessages = 50;
        ClientThread[] threads = new ClientThread[maxClients];
        String[][] messages = new String[maxClients][maxMessages];
            
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
            
        }
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                
                int i = 0;
                for (i = 0; i < maxClients; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new ClientThread(clientSocket, threads, messages, i)).start();
                        break;
                    }
                }
                int j = 0;
                for (j = 0; i < maxMessages; i++) {
                    if (messages[j] == null) {
                        messages[j][0] = new String();
                        break;
                    }
                }
                if ((i == maxClients)||(j == maxMessages)) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Server is busy. Please try again later.");
                    os.close();
                    clientSocket.close();
                }
                
            } catch (IOException e) {
                System.out.println("Exception caught when trying to listen on port "
                                   + portNumber + " or listening for a connection");
                System.out.println(e.getMessage());
            }
    
        }
    }
}
