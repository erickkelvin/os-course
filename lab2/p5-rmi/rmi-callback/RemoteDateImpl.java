/**
 * RemoteDate interface implementation.
 *
 */
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
 
import java.rmi.*;

public class RemoteDateImpl extends UnicastRemoteObject implements RemoteDate
{
    public RemoteDateImpl() throws RemoteException {  }
    
    public  Date getDate() throws RemoteException {
        return new Date();
    }
    
    public  void getDate(DateObject date) throws RemoteException {
        date.setDate(new java.util.Date());
    }
    
   public static void main(String[] args)  {
        try {
            RemoteDate dateServer = new RemoteDateImpl();

            // Bind this object instance to the name "DateServer"
            //Naming.rebind("DateServer", dateServer);
            Naming.rebind("//localhost:1099/DateServer", dateServer);

            System.out.println("DateServer bound in registry");
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }
  
   
   
}
