//Author: Erick Santos

import java.io.*;
import java.util.*;
import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;

public class Scheduler {

    public static int totalContexts = 0;
    public static int totalSlots = 0;
    public static double totalBurst = 0;

    public static void main(String[] args) throws IOException {
        if (((args.length<2)||(args.length>3)) || (((args[1]).toLowerCase().equals("rr"))&&((args.length<3)||(!args[2].contains("quantum="))))) {
            System.out.println("\n> Number of arguments is invalid! You should use:");
            System.out.println("   Scheduler <File name> <Algorithm name> <Quantum (optional, only works with RR)>");
            System.out.println("> Examples:\n   Scheduler processos.csv SJF\n   Scheduler processos.csv RR quantum=3\n");
            System.exit(0);
        }

        String fileName = args[0];
        String algoName = (args[1]).toLowerCase();
        int quantum = -1;
        if (algoName.equals("rr")) {
            quantum = Integer.parseInt(args[2].split("quantum=")[1]);
        }

        List<Process> inputList = new ArrayList<Process>();
        List<Process> outputList = new ArrayList<Process>();
        
        inputList = initData(fileName, inputList);
        
        System.out.println("\n### PROCESS SCHEDULER ### \n");

        if (algoName.equals("fcfs")) { outputList = fcfs(inputList); }
        else if (algoName.equals("sjf")) { outputList = sjf(inputList); }
        else if (algoName.equals("sjfp")) { outputList = sjfp(inputList); }
        else if (algoName.equals("priority")) { outputList = priority(inputList); }
        else if (algoName.equals("priorityp")) { outputList = priorityp(inputList); }
        else if (algoName.equals("rr")) { outputList = rr(inputList, quantum); }

        printList(outputList, algoName);

        printStatistics(outputList, algoName, quantum);
    }

    //get, parse and initialize data with objects
    public static List<Process> initData(String fileName, List<Process> list) {
        String line;
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                String[] temp = line.split(", ");
                
                Process p = new Process(Integer.parseInt(temp[1]),  //id
                                        Integer.parseInt(temp[0]),  //arrival time
                                        Integer.parseInt(temp[2]),  //burst time
                                        Integer.parseInt(temp[3])); //priority
                list.add(p);
                totalBurst += p.burstTime;
            }
            bufferedReader.close();  
        }
        catch(FileNotFoundException e) {
            System.out.println("Unable to open file '" + fileName + "'");                
        }
        catch(IOException e) {
            System.out.println("Error reading file '" + fileName + "'");                  
            e.printStackTrace();
        }

        Collections.sort(list, new Comparator<Process>() {
            public int compare(Process p1, Process p2) {
                return p1.arrivalTime - p2.arrivalTime;
            }
        });

        return list; 
    }

    //print and save a file with scheduled processes
    public static void printList(List<Process> list, String algoName) {
        String output = "";
        output += "-----------------------------------------\n";
        output += "SCHEDULED PROCESSES: " + algoName + "\n-----------------------------------------\n";
        for (int i=0; i<list.size(); i++) {
            output += "P" + list.get(i).id + ": " + list.get(i).turnAround + "\n";
            
        }
        output += "-----------------------------------------\n";

        System.out.println(output);
        byte data[] = output.getBytes();
        Path p = Paths.get("./output-" + algoName + ".txt");
    
        try (OutputStream out = new BufferedOutputStream(
            Files.newOutputStream(p, CREATE, TRUNCATE_EXISTING))) {
            out.write(data, 0, data.length);
        } 
        catch (IOException e) {
            System.err.println(e);
        }
    }

    //print statistics
    public static void printStatistics(List<Process> list, String algoName, int quantum) {
        double totalContextSwitch = totalContexts - 1;
        double totalTurnaround = 0;
        double totalWaitingTime = 0;
        double totalResponseTime = 0;
        for (int i=0; i<list.size(); i++) {
            totalTurnaround += list.get(i).turnAround;
            totalWaitingTime += list.get(i).waitingTime;
            totalResponseTime += list.get(i).responseTime;
        }
        System.out.println("-----------------------------------------");
        System.out.println("STATISTICS");
        System.out.println("-----------------------------------------");
        System.out.println("Algorithm:\t\t\t" + algoName);
        if (algoName.equals("rr")) { System.out.println("Quantum:\t\t\t" + quantum); }
        System.out.println("Processing time (total):\t" + totalSlots);
        System.out.printf("CPU Usage:\t\t\t%.2f%%\n", (((totalSlots-totalContextSwitch)/totalSlots)*100));
        System.out.printf("Throughput (average):\t\t%.2f\n", (double) list.size()/totalSlots);
        System.out.printf("Turnaround (average):\t\t%.2f\n", totalTurnaround/list.size());
        System.out.printf("Waiting (average):\t\t%.2f\n", totalWaitingTime/list.size());
        System.out.printf("Response time (average):\t%.2f\n", totalResponseTime/list.size());
        System.out.printf("Context switch (average):\t%.2f\n", totalContextSwitch/list.size());
        System.out.println("Number of processes:\t\t" + list.size());
        System.out.println("-----------------------------------------\n");
    }

    /* NON-PREEMPTIVE ALGORITHMS */

    //common function to non-preemptive algorithms
    public static List<Process> nonPreemptive(List<Process> queue) {
        Process executing = null;
        List<Process> result = new ArrayList<Process>();
        int slot = 0;
        //each iteration is a slot in the graph
        while((queue.size()>0)||(executing!=null)){
            //checks if a more important job has arrived
            for(int k=0; k<queue.size(); k++) {
                if ((queue.get(k).arrivalTime <= slot)) {
                    if (executing == null) {
                        executing = new Process(queue.get(k)); //new process is executed
                        totalContexts++;
                        queue.remove(k);
                    }
                }
            }

            //executes tasks in the slot
            if (executing!=null) {

                //executes a job
                executing.remainingBurst--;
                executing.turnAround++;

                //increase waiting time, response time and turn around for the rest of the queue
                for(int k=0; k<queue.size(); k++) {
                    if ((queue.get(k).arrivalTime <= slot)&&(queue.get(k).remainingBurst>0)) {
                        queue.get(k).waitingTime++;
                        queue.get(k).turnAround++;
                        if (queue.get(k).remainingBurst==queue.get(k).burstTime) {
                            queue.get(k).responseTime++;
                        }
                    }
                }

                //if a job has finished, add it to the result list
                if (executing.remainingBurst == 0) {
                    result.add(new Process(executing));
                    executing = null;
                }
            }
            slot++;
        }
        totalSlots = slot;
        return result;
    }

    //first-come, first-serve
    public static List<Process> fcfs(List<Process> queue) {
        return nonPreemptive(queue);
    }

    //shortest-job first
    public static List<Process> sjf(List<Process> queue) {
        Collections.sort(queue, new Comparator<Process>() {
            public int compare(Process p1, Process p2) {
                return p1.burstTime - p2.burstTime;
            }
        });

        return nonPreemptive(queue);
    }

    //priority
    public static List<Process> priority(List<Process> queue) {
        Collections.sort(queue, new Comparator<Process>() {
            public int compare(Process p1, Process p2) {
                return p1.priority- p2.priority;
            }
        });

        return nonPreemptive(queue);
    }

    /* PREEMPTIVE ALGORITHMS */

    //shortest-job first (preemptive)
    public static List<Process> sjfp(List<Process> queue) {

        //sorts input list to put shortest jobs first
        Collections.sort(queue, new Comparator<Process>() {
            public int compare(Process p1, Process p2) {
                return p1.burstTime - p2.burstTime;
            }
        });
        Process executing = null;
        List<Process> result = new ArrayList<Process>();
        int slot = 0;

        //each iteration is a slot in the graph
        while((queue.size()>0)||(executing!=null)){

            //checks if a more important job has arrived
            for(int k=0; k<queue.size(); k++) {
                if ((queue.get(k).arrivalTime <= slot)&&(queue.get(k).remainingBurst>0)) {
                    if ((executing != null)) {
                        if (executing.remainingBurst<queue.get(k).remainingBurst) {
                            break;
                        }
                        queue.add(executing); //current job is interrupted and goes back to the queue
                    }
                    totalContexts++;
                    executing = new Process(queue.get(k)); //new process is executed
                    queue.remove(k);
                }
            }
            
            //executes tasks in the slot
            if (executing!=null) {

                //executes a job
                executing.remainingBurst--;
                executing.turnAround++;

                //increase waiting time, response time and turn around for the rest of the queue
                for(int k=0; k<queue.size(); k++) {
                    if ((queue.get(k).arrivalTime <= slot)&&(queue.get(k).remainingBurst>0)) {
                        queue.get(k).waitingTime++;
                        queue.get(k).turnAround++;
                        if (queue.get(k).remainingBurst==queue.get(k).burstTime) {
                            queue.get(k).responseTime++;
                        }
                    }
                }

                //if a job has finished, add it to the result list
                if (executing.remainingBurst == 0) {
                    result.add(new Process(executing));
                    //System.out.println("TERMINA slot: "+(slot+1)+" id: P"+executing.id);
                    executing = null;
                }

                //sort list in terms of remainingBurst to put shortest jobs first
                Collections.sort(queue, new Comparator<Process>() {
                    public int compare(Process p1, Process p2) {
                        return p1.remainingBurst- p2.remainingBurst;
                    }
                });
            }
            slot++;
            
        }
        totalSlots = slot;
        return result;
    }

    //priority (preemptive)
    public static List<Process> priorityp(List<Process> queue) {
        
        //sorts input list to put shortest priority jobs first
        Collections.sort(queue, new Comparator<Process>() {
            public int compare(Process p1, Process p2) {
                return p1.priority - p2.priority;
            }
        });
        Process executing = null;
        List<Process> result = new ArrayList<Process>();
        int slot = 0;

        //each iteration is a slot in the graph
        while((queue.size()>0)||(executing!=null)){

            //checks if a more important job has arrived
            for(int k=0; k<queue.size(); k++) {
                if ((queue.get(k).arrivalTime <= slot)&&(queue.get(k).remainingBurst>0)) {
                    if ((executing != null)) {
                        if (executing.priority<queue.get(k).priority) {
                            break;
                        }
                        queue.add(executing); //current job is interrupted and goes back to the queue
                    }
                    totalContexts++;
                    executing = new Process(queue.get(k)); //new process is executed
                    queue.remove(k);
                }
            }
            
            //executes tasks in the slot
            if (executing!=null) {

                //executes a job
                executing.remainingBurst--;
                executing.turnAround++;

                //increase waiting time, response time and turn around for the rest of the queue
                for(int k=0; k<queue.size(); k++) {
                    if ((queue.get(k).arrivalTime <= slot)&&(queue.get(k).remainingBurst>0)) {
                        queue.get(k).waitingTime++;
                        queue.get(k).turnAround++;
                        if (queue.get(k).remainingBurst==queue.get(k).burstTime) {
                            queue.get(k).responseTime++;
                        }
                    }
                }

                //if a job has finished, add it to the result list
                if (executing.remainingBurst == 0) {
                    result.add(new Process(executing));
                    //System.out.println("TERMINA slot: "+(slot+1)+" id: P"+executing.id);
                    executing = null;
                }

                //sorts input list to put shortest priority jobs first
                Collections.sort(queue, new Comparator<Process>() {
                    public int compare(Process p1, Process p2) {
                        return p1.priority- p2.priority;
                    }
                });
            }
            slot++;
        }
        totalSlots = slot;
        return result;
    }
    
    //round-robin
    public static List<Process> rr(List<Process> queue, int quantum) {
        Process executing = null;
        List<Process> result = new ArrayList<Process>();
        int currQuantum = 0;
        int slot = 0;

        //each iteration is a slot in the graph
        while((queue.size()>0)||(executing!=null)){

            //starts executing a new job
            if ((executing == null)&&(queue.get(0).arrivalTime <= slot)) {
                executing = new Process(queue.get(0));
                //System.out.println("\nCOMEÇA slot: "+(slot)+" id: P"+executing.id);
                queue.remove(0);
            }

            if (executing != null) {
                //executes a job
                executing.remainingBurst--;
                executing.turnAround++;
                currQuantum++;

                //increase waiting time, response time and turn around for the rest of the queue
                for(int k=0; k<queue.size(); k++) {
                    if ((queue.get(k).arrivalTime <= slot)&&(queue.get(k).remainingBurst>0)) {
                        queue.get(k).waitingTime++;
                        queue.get(k).turnAround++;
                        if (queue.get(k).remainingBurst==queue.get(k).burstTime) {
                            queue.get(k).responseTime++;
                        }
                    }
                }

                //finishes a quantum context and starts another
                if ((currQuantum==quantum)&&(executing.remainingBurst > 0)) {
                    currQuantum=0;
                    queue.add(executing);
                    //System.out.println("TERMINA slot: "+(slot+1)+" id: P"+executing.id);
                    executing = null;
                    totalContexts++;
                }
                else if (executing.remainingBurst == 0) {
                    result.add(new Process(executing)); //if a job has finished, goes to result list
                    //System.out.println("TERMINA slot: "+(slot+1)+" id: P"+executing.id);
                    executing = null;
                    currQuantum = 0;
                    totalContexts++;
                }

            }
            slot++;   
        }
        totalSlots = slot;
        return result;
    }

}