public class Reader implements Runnable {
    private int readerId;
    private ReadWriteLock db;
    
    public Reader(int readerId, ReadWriteLock db) {
        this.readerId = readerId;
        this.db = db;
    }

    public void run() { 
        while (true) {
            SleepUtilities.nap();
            
            System.out.println("[Reader " + readerId + "] asked for permission to read.");
            db.acquireReadLock(readerId);
        
            //reading...
            SleepUtilities.nap();
        
            db.releaseReadLock(readerId);
        } 
    }
}