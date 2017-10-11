/**
 * RemoteDate interface iplementation.
 */
public class DateObjectImpl extends java.rmi.server.UnicastRemoteObject implements DateObject
{
	private java.util.Date date;

	public DateObjectImpl() throws java.rmi.RemoteException { }

	public void setDate(java.util.Date date) throws java.rmi.RemoteException {
		this.date = date;
	}

	public java.util.Date getDate() {
		return date;
	}

	public String toString() {
		return date.toString();
	}
}
