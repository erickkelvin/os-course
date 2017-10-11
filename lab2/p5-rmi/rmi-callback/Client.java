/**
 * Program example using RMI callback where the server can invoke a method
 * on the client object.
 */
import java.rmi.*;
import java.util.Date;
 
public class Client 
{  
   public Client(String hostIP)
   {
        // get the IP address of the host
        String host = "rmi://" + hostIP + "/DateServer";
        
        try {
                /**
                    * Naming.lookup() returns a stub for the remote object
                    * server is a reference to this remote object
                    */
                RemoteDate dateServer = (RemoteDate)Naming.lookup(host);
                
                // now get the time of day
               
                /** 
                 * The first technique - not using a callback
                 */
                System.out.println(dateServer.getDate());
                
                /**
                 * The second technique - using a callback
                 */
                 
                // first create the object the server will "callback" to
		DateObject timeOfDay = new DateObjectImpl(); 
                
                // now invoke the remote method, this time passing a remote object
                // to the server
		dateServer.getDate(timeOfDay);
                
                System.out.println("#2:"+timeOfDay);
        }
        catch (Exception e) {
                e.printStackTrace();
        }
	finally {
		System.out.println("Done");
	}
   }

   public static void main(String args[]) { 
      if (args.length != 1) {
        System.err.println("Usage: java Client <IP Address>");
        System.exit(0);
      }
      
      Client server = new Client(args[0]);
   }
}

