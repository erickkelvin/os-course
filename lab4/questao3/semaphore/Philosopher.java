class Philosopher extends Thread {
    private Fork leftFork;
    private Fork rightFork;
    private int id;
    
    Philosopher(Fork leftFork, Fork rightFork, int id) {
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.id = id;
    }
    
    public void run() {
        while (true) {
            leftFork.take();
            rightFork.take();

            System.out.println("Philosopher #" + id + " is EATING.");
            SleepUtilities.nap();
            //eating

            leftFork.release();
            rightFork.release();
            System.out.println("Philosopher #" + id + " is THINKING.");
            //thinking
        }
    }
}