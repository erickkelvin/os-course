public class PhilosophersMonitor {
    public static final int numPhilosophers = 5;

    private enum State {THINKING, HUNGRY, EATING};
    private static State[] states = new State[numPhilosophers];
    private static Object[] self = new Object[numPhilosophers];

    public static void main(String[] args) {
        Philosopher[] philosophers = new Philosopher[numPhilosophers];

        for (int i=0; i < numPhilosophers; i++) {
            states[i] = State.THINKING;
            System.out.println("Philosopher #" + i + " is " + "THINKING.");
            self[i] = new Object();
        }
    
        for (int i=0; i < numPhilosophers; i++) {
            philosophers[i] = new Philosopher(i);
            philosophers[i].start();
        }
    }

    public synchronized void takeForks(int i) {
        states[i] = State.HUNGRY;
        System.out.println("Philosopher #" + i + " is " + "HUNGRY.");
    
        test(i);
    
        if (states[i] != State.EATING) {
            try {
                synchronized (self[i]) {
                    self[i].wait();
                }
    
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void returnForks(int i) {
        states[i] = State.THINKING;
        System.out.println("Philosopher #" + i + " is " + "THINKING.");
    
        test((i+numPhilosophers-1) % numPhilosophers);
        test((i+1) % numPhilosophers);
    }

    private void test(int i) {
        if ( (states[(i+numPhilosophers-1) % numPhilosophers] != State.EATING) &&
             (states[i] == State.HUNGRY) &&
             (states[(i+1) % numPhilosophers] != State.EATING) 
            ) {
            states[i] = State.EATING;
            System.out.println("Philosopher #" + i + " is " + "EATING.");
            
            synchronized (self[i]) {
                self[i].notifyAll();
            }
        }
    }
}