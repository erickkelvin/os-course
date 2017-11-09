public class RWMonitor{
    public static final int numReaders = 3;
    public static final int numWriters = 2;

    public static void main(String args[]){
        ReadWriteLock db = new Database();
    
        Thread[] readers = new Thread[numReaders];
        Thread[] writers = new Thread[numWriters];
    
        for (int i=0; i < numReaders; i++) {
            readers[i] = new Thread(new Reader(i, db));
            readers[i].start();
        }
    
        for (int i=0; i < numWriters; i++) {
            writers[i] = new Thread(new Writer(i, db));
            writers[i].start();
        }
    }
}