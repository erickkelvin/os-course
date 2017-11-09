import java.util.Random;
public class Consumer implements Runnable {
    private final Dropbox dropbox;
    private final boolean even;
    public Consumer(boolean even, Dropbox dropbox) {
        this.even = even;
        this.dropbox = dropbox;
    }
    public void run() {
        Random random = new Random();
        while (true) {
            dropbox.take(even);
            try {
                Thread.sleep(random.nextInt(100));
            } 
            catch (InterruptedException e) { }
        }
    }
}
    