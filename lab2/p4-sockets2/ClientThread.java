//Author: Erick Kelvin

import java.net.*;
import java.io.*;

public class ClientThread extends Thread {
    private Socket socket = null;
    private final ClientThread[] threads;
    private int maxClients;
    private final String[][] messages;
    private int maxMessages;
    private PrintWriter out = null;
    private int id = -1;
    
    public ClientThread(Socket clientSocket, ClientThread[] threads, String[][] messages, int id) {
        this.socket = clientSocket;
        this.threads = threads;
        maxClients = threads.length;
        this.messages = messages;
        maxMessages = messages.length;
        this.id = id;
    }
    
    public void run() {
        int maxClients = this.maxClients;
        ClientThread[] threads = this.threads;
        String[][] messages = this.messages;
        BufferedReader in = null;
        
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            threads[id].out.println("id:" + id);
            String inputLine;
            while (true) {
                try {
                    inputLine = in.readLine();
                    if ((inputLine == null) || inputLine.equalsIgnoreCase("/quit")) {
                        threads[id].out.println("Bye.");
                        socket.close();
                        return;
                    }
                    else if (inputLine.equalsIgnoreCase("/receive")) {
                        int i=0;
                        while (messages[id][i] != null) {
                            String[] message = messages[id][i].split(" ", 2);
                            threads[id].out.println("\n> Message #" + i +
                                                    "\n  From: Client #" + message[0] +
                                                    "\n  To: Client #" + id +
                                                    "\n  Content: " + message[1] + "\n");
                            i++;
                        }
                    }
                    else if (inputLine.startsWith("/send")) {
                        String[] input = inputLine.split(" ", 3);
                        int dest_id = Integer.parseInt(input[1]);
                        int i=0;
                        while (messages[dest_id][i] != null) {
                            i++;
                        }
                        messages[dest_id][i] = id + " " + input[2];
                        threads[id].out.println("Message sent!");
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            
        }
        catch (IOException e) {
            return;
        }
        
    }
}
