import java.io.*;
import java.util.*;
import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;

public class Scheduler {

    public static int totalSlots = 0;
    public static double totalBurst = 0;

    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        String algoName = args[1];
        algoName = algoName.toLowerCase();

        List<Process> inputList = new ArrayList<Process>();
        List<Process> outputList = new ArrayList<Process>();
        
        inputList = initData(fileName, inputList);
        
        System.out.println("\n### PROCESS SCHEDULER ### \n");

        if (algoName.equals("fcfs")) { outputList = fcfs(inputList); }
        else if (algoName.equals("sjf")) { outputList = sjf(inputList); }
        else if (algoName.equals("sjfp")) { outputList = sjfp(inputList); }
        else if (algoName.equals("priority")) { outputList = priority(inputList); }
        else if (algoName.equals("priorityp")) { outputList = priorityp(inputList); }
        else if (algoName.equals("rr")) { outputList = rr(inputList, 5); }

        printList(outputList, algoName);

        printStatistics(outputList, algoName);
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
        output += "| SCHEDULED PROCESSES: " + algoName + "\t\t|\n-----------------------------------------\n";
        for (int i=0; i<list.size(); i++) {
            output += "| P" + list.get(i).id + ":  " + list.get(i).turnAround + "\t\t\t\t|\n";
        }
        output += "-----------------------------------------\n";

        System.out.println(output);
        byte data[] = output.getBytes();
        Path p = Paths.get("./output-" + algoName + ".txt");
    
        try (OutputStream out = new BufferedOutputStream(
            Files.newOutputStream(p, CREATE, WRITE))) {
            out.write(data, 0, data.length);
        } 
        catch (IOException e) {
            System.err.println(e);
        }
    }

    //print statistics
    public static void printStatistics(List<Process> list, String algoName) {
        int totalContextSwitch = totalSlots - 1;
        double totalTurnaround = 0;
        double totalWaitingTime = 0;
        double totalResponseTime = 0;
        for (int i=0; i<list.size(); i++) {
            totalTurnaround += list.get(i).turnAround;
            totalWaitingTime += list.get(i).waitingTime;
            totalResponseTime += list.get(i).responseTime;
        }
        System.out.println("-----------------------------------------");
        System.out.println("| STATISTICS\t\t\t\t|");
        System.out.println("-----------------------------------------");
        System.out.println("| Algorithm:\t\t\t" + algoName + "\t|");
        System.out.println("| \t\t\t\t\t|");
        System.out.println("| Processing time (total):\t" + (int)totalBurst + "\t|");
        System.out.printf("| CPU Usage:\t\t\t%.2f%%\t|\n", ((totalBurst/(totalBurst + totalContextSwitch))*100));
        System.out.printf("| Throughput (average):\t\t%.2f \t|\n", list.size()/totalBurst);
        System.out.printf("| Turnaround (average):\t\t%.2f\t|\n", totalTurnaround/list.size());
        System.out.printf("| Waiting (average):\t\t%.2f\t|\n", totalWaitingTime/list.size());
        System.out.printf("| Response time (average):\t%.2f\t|\n", totalResponseTime/list.size());
        System.out.println("| Context switch (average):\t" + totalContextSwitch/list.size() + "\t|");
        System.out.println("| Number of processes:\t\t" + list.size() + "\t|");
        System.out.println("-----------------------------------------");
    }

    /* NON-PREEMPTIVE ALGORITHMS */

    //common function to non-preemptive algorithms
    public static List<Process> nonPreemptive(List<Process> list) {
        Process executing = null;
        int executingBurst = 0;
        int jobsProcessed = 0;
        List<Process> result = new ArrayList<Process>();

        for(int slot=0; slot<totalBurst; slot++) {
            for(int k=0; k<list.size(); k++) {
                if ((list.get(k).arrivalTime <= slot)) {
                    if (executing == null) {
                        Process p = new Process(list.get(k));
                        executing = p;
                        executingBurst = executing.burstTime;
                        list.remove(k);
                    }
                }
            }

            executingBurst--;

            if (executingBurst == 0) {
                Process p = new Process(executing);
                result.add(p);
                jobsProcessed++;
                if (result.size()==1) {
                    result.get(0).responseTime = 0;
                    result.get(0).waitingTime = 0;
                    result.get(0).turnAround = result.get(0).burstTime;
                }
                else {
                    result.get(jobsProcessed-1).responseTime = result.get(jobsProcessed-2).turnAround - result.get(jobsProcessed-1).arrivalTime + result.get(jobsProcessed-2).arrivalTime;
                    result.get(jobsProcessed-1).waitingTime = result.get(jobsProcessed-1).responseTime;
                    result.get(jobsProcessed-1).turnAround = result.get(jobsProcessed-1).burstTime + result.get(jobsProcessed-1).waitingTime;
                }
                executing = null;
                executingBurst = 0;
            }
        }
        totalSlots = result.size();
        return result;
    }

    //first-come, first-serve
    public static List<Process> fcfs(List<Process> list) {
        return nonPreemptive(list);
    }

    //shortest-job first
    public static List<Process> sjf(List<Process> list) {
        Collections.sort(list, new Comparator<Process>() {
            public int compare(Process p1, Process p2) {
                return p1.burstTime - p2.burstTime;
            }
        });

        return nonPreemptive(list);
    }

    //priority
    public static List<Process> priority(List<Process> list) {
        Collections.sort(list, new Comparator<Process>() {
            public int compare(Process p1, Process p2) {
                return p1.priority- p2.priority;
            }
        });

        return nonPreemptive(list);
    }

    /* PREEMPTIVE ALGORITHMS */

    //shortest-job first (preemptive)
    public static List<Process> sjfp(List<Process> list) {
        Collections.sort(list, new Comparator<Process>() {
            public int compare(Process p1, Process p2) {
                return p1.burstTime - p2.burstTime;
            }
        });

        

        return list;
    }

    //priority (preemptive)
    public static List<Process> priorityp(List<Process> list) {
        return list;
    }
    
    //round-robin
    public static List<Process> rr(List<Process> list, int quantum) {
        return list;
    }

}