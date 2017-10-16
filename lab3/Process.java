//Author: Erick Santos

public class Process {
    public int id = -1;
    public int arrivalTime = -1;
    public int burstTime = -1;
    public int priority = -1;

    public int turnAround = -1;
    public int waitingTime = -1;
    public int responseTime = -1;

    public int remainingBurst = -1;

    public Process(int id, int arrivalTime, int burstTime, int priority) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.turnAround = 0;
        this.waitingTime = 0;
        this.responseTime = 0;
        this.remainingBurst = this.burstTime;
    }

    public Process(Process p) {
        this.id = p.id;
        this.arrivalTime = p.arrivalTime;
        this.burstTime = p.burstTime;
        this.priority = p.priority;
        this.turnAround = p.turnAround;
        this.waitingTime = p.waitingTime;
        this.responseTime = p.responseTime;
        this.remainingBurst = p.remainingBurst;
    }
}