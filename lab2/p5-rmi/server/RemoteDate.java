/**
 * The remote interface
 */

import java.util.Date;
import java.rmi.*;

public interface RemoteDate extends Remote
{
	public abstract Date getDate() throws RemoteException;
}
