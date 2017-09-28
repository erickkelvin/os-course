//Author: Erick Kelvin

import java.io.*;
import java.net.*;

public class Client implements Runnable {
    private static Socket clientSocket = null;
    private static PrintWriter out = null;
    private static BufferedReader in = null;
    private static BufferedReader stdIn = null;
    private static boolean closed = false;
    private static int id = -1;
    
    public static void main(String[] args) throws IOException {
        String hostName = "localhost";
        int portNumber = 6013;
        
        try {
            clientSocket = new Socket(hostName, portNumber);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            
            /*String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("Result: " + in.readLine());
            }*/
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                               hostName);
        }
        
        if (clientSocket != null && out != null && in != null) {
            try {
                new Thread(new Client()).start();
                while (!closed) {
                    out.println(stdIn.readLine().trim());
                }
                out.close();
                in.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
    }
    
    public void run() {
        String responseLine;
        try {
            while ((responseLine = in.readLine()) != null) {
                if (responseLine.substring(0,3).equalsIgnoreCase("id:")) {
                    id = Integer.parseInt(responseLine.substring(3));
                    System.out.println("\n> Welcome, Client #" + id + "!");
                    System.out.println("  Type '/send dest_id message' to send a message to another client");
                    System.out.println("  Type '/receive' to check your inbox");
                    System.out.println("  Type '/quit' to quit\n");
                    break;
                }
            }
            while ((responseLine = in.readLine()) != null) {
                System.out.println(responseLine);
                    if (responseLine.indexOf("Bye.") != -1)
                        break;
            }
            closed = true;
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }
}
