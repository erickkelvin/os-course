/**
 * RemoteDate interface
 *
 */
import java.util.Date;
import java.rmi.*;

public interface RemoteDate extends Remote
{
	public abstract Date getDate() throws RemoteException;
        public abstract void getDate(DateObject date) throws RemoteException;
}
