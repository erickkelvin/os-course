public class PhilosophersSemaphore {
    public static final int numPhilosophers = 5;

    public static void main(String[] args) {
        Fork[] forks = new Fork[numPhilosophers];
        Philosopher[] philosophers = new Philosopher[numPhilosophers];
    
        for (int i=0; i < numPhilosophers; i++) {
            forks[i] = new Fork(i);
        }
    
        for (int i=0; i < numPhilosophers; i++) {
            philosophers[i] = new Philosopher(forks[i], forks[(i+1) % numPhilosophers], i);
            philosophers[i].start();
        }
    }
}