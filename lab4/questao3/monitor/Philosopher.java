class Philosopher extends Thread {
    private PhilosophersMonitor monitor;
    private int id;
    
    Philosopher(int id) {
        this.id = id;
        this.monitor = new PhilosophersMonitor();
    }
    
    public void run() {
        while (true) {
            monitor.takeForks(id);
            
            SleepUtilities.nap();
            //eating

            monitor.returnForks(id);
            //thinking
        }
    }

}