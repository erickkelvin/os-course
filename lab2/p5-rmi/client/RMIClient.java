/**
 * The RMI Client
 */

import java.rmi.*;

public class RMIClient 
{  
   public static void main(String args[]) { 
    try {
      RemoteDate dateServer = (RemoteDate)Naming.lookup(host);
      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
      String userInput;

      while ((userInput = stdIn.readLine()) != null) {
          if (userInput.startsWith("/send")) {
            String[] input = userInput.split(" ", 3);
            int dest_id = Integer.parseInt(input[1]);
            int i=0;
            while (messages[dest_id][i] != null) {
                i++;
            }
            messages[dest_id][i] = id + " " + input[2];
            threads[id].out.println("Message sent!");
          }
          System.out.println("Result: " + in.readLine());
      } 
      String host = "rmi://127.0.0.1/DateServer";
      
      System.out.println(dateServer.getDate());


    }
    catch (Exception e) {
        System.err.println(e);
    }
   }
}

