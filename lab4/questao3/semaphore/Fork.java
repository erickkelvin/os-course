import java.util.concurrent.Semaphore;

class Fork {
    public Semaphore mutex = new Semaphore(1);
    public int id;
    
    Fork(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public void take() {
        try {
            mutex.acquire();
        }
        catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
    
    public void release() {
        mutex.release();
    }
}