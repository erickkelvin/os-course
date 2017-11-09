public class Database implements ReadWriteLock {
    private int readerCount;
    private boolean dbWriting;

    public Database() { 
        readerCount = 0;
        dbWriting = false;
    }

    public synchronized void acquireReadLock(int readerId) {
        while (dbWriting == true) {
            try { 
                wait();
            }
            catch(InterruptedException e) { }
        }
        System.out.println("[Reader " + readerId + "] is reading...");
        ++readerCount;
    }

    public synchronized void releaseReadLock(int readerId) {
        --readerCount;
        System.out.println("[Reader " + readerId + "] is done reading!");
       
        //all readers finished reading
        if (readerCount == 0) {
            notify();
        }
    }

    public synchronized void acquireWriteLock(int writerId) { 
        while (readerCount > 0 || dbWriting == true) {
            try { 
                wait();
            }
            catch(InterruptedException e) { }
        }
        dbWriting = true;
        System.out.println("[Writer " + writerId + "] is writing...");
    }

    public synchronized void releaseWriteLock(int writerId) {
        dbWriting = false;
        notifyAll();
        System.out.println("[Writer " + writerId + "] is done writing!");
    }
}