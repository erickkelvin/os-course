import java.util.concurrent.Semaphore;

public class Database implements ReadWriteLock {
    private int readerCount;
    private Semaphore mutex;
    private Semaphore db;

    public Database() { 
        readerCount = 0;
        mutex = new Semaphore(1);
        db = new Semaphore(1);
    }

    public void acquireReadLock(int readerId) {
        try {
           mutex.acquire();
        }
        catch (InterruptedException e) { }
     
        ++readerCount;
     
        //allow other readers to read simultaneously
        if (readerCount == 1) {
            try {
                db.acquire();
            }
            catch (InterruptedException e) { }
        }
     
        System.out.println("[Reader " + readerId + "] is reading...");
        mutex.release();
     }

     public void releaseReadLock(int readerId) {
        try {
           mutex.acquire();
        }
        catch (InterruptedException e) { }
     
        --readerCount;
     
        //all readers finished reading
        if (readerCount == 0) {
           db.release();
        }
     
        System.out.println("[Reader " + readerId + "] is done reading!");
     
        mutex.release();
     }

     public void acquireWriteLock(int writerId) {
        try {
           db.acquire();
        }
        catch (InterruptedException e) { }
        System.out.println("[Writer " + writerId + "] is writing...");
     }
  
      public void releaseWriteLock(int writerId) {
        System.out.println("[Writer " + writerId + "] is done writing!");
        db.release();
     }
}