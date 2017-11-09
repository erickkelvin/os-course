public class Writer implements Runnable {
    private int writerId;
    private ReadWriteLock db;

    public Writer(int writerId, ReadWriteLock db) { 
        this.writerId = writerId;
        this.db = db;
    }

    public void run() { 
        while (true){
            SleepUtilities.nap();
         
            System.out.println("[Writer " + writerId + "] asked for permission to write.");
            db.acquireWriteLock(writerId);

            //writing...
            SleepUtilities.nap();
         
            db.releaseWriteLock(writerId);
         }
    }
}
