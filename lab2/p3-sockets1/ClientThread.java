import java.net.*;
import java.io.*;

public class ClientThread extends Thread {
    protected Socket socket;
    
    public ClientThread(Socket clientSocket) {
        this.socket = clientSocket;
    }
    
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            return;
        }
        String inputLine;
        while (true) {
            try {
                inputLine = in.readLine();
                if ((inputLine == null) || inputLine.equalsIgnoreCase("QUIT")) {
                    socket.close();
                    return;
                } else {
                    out.println(calculate(inputLine));
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
    
    public static double calculate(String inputLine) {
        String[] input = inputLine.split(":");
        double op1 = Double.parseDouble(input[1]);
        double op2 = Double.parseDouble(input[2]);
        double result = 0.0;
        
        switch(input[0]) {
            case "+":
                result = op1 + op2;
                break;
            case "-":
                result = op1 - op2;
                break;
            case "*":
                result = op1 * op2;
                break;
            case "/":
                result = op1 / op2;
                break;
        }
        
        return result;
    }
}
