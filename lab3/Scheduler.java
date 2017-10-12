import java.io.*;
import java.util.*;

import jdk.nashorn.internal.runtime.FindProperty;

import java.math.*;

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
        else if (algoName.equals("rr")) { outputList = rr(inputList); }

        printList(outputList);

        printStatistics(outputList, algoName);
    }

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

    public static void printList(List<Process> list) {
        System.out.println("-----------------------------------------");
        System.out.println("| SCHEDULED PROCESSES\t\t\t|\n-----------------------------------------");
        for (int i=0; i<list.size(); i++) {
            System.out.println("| P" + list.get(i).id + ":  " + list.get(i).turnAround + "\t\t\t\t|");
        }
        System.out.println("-----------------------------------------\n");
    }

    public static void printStatistics(List<Process> list, String algoName) {
        int totalContextSwitch = (int)totalBurst - 1;
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
        return result;
    }

    public static List<Process> fcfs(List<Process> list) {
        return nonPreemptive(list);
    }

    public static List<Process> sjf(List<Process> list) {
        Collections.sort(list, new Comparator<Process>() {
            public int compare(Process p1, Process p2) {
                return p1.burstTime - p2.burstTime;
            }
        });

        return nonPreemptive(list);
    }

    public static List<Process> sjfp(List<Process> list) {
        Collections.sort(list, new Comparator<Process>() {
            public int compare(Process p1, Process p2) {
                return p1.burstTime - p2.burstTime;
            }
        });

        int[] count = new int[list.size()];
        int i = 0; //current process index
        for(int slot=0; slot<totalBurst; slot++) {

            //new process has arrived and it's waiting
            for(int j=0; j<list.size(); j++) {
                if ((list.get(j).arrivalTime < slot) && (i!=j) && (list.get(j).burstTime>0)) {
                    if (list.get(j).burstTime <= list.get(i).burstTime) {
                        i = j;
                        break;
                    }
                }
            }

            if (count[i]==0) list.get(i).responseTime = list.get(i).waitingTime;
            count[i]++;
            
            list.get(i).burstTime--;
            list.get(i).turnAround++;
            

            for(int j=0; j<list.size(); j++) {
                if ((list.get(j).burstTime>0)&&(list.get(j).arrivalTime < slot)&&(i!=j)) {
                    list.get(j).turnAround++;
                    list.get(j).waitingTime++;
                }
                    
            }
            
            //check if a process finished executing
            boolean found = false;
            if (list.get(i).burstTime == 0) {
                for (int k=0; k<list.size(); k++) {
                    if ((list.get(k).burstTime>0)&&(found==false)&&((list.get(k).arrivalTime<=slot))) {
                        System.out.println("i: " + i + " k: " + k + " slot: " + slot);
                        i = k;
                        found = true;
                    }
                    else if ((list.get(k).burstTime>0)&&(i!=k)){
                        list.get(k).turnAround++;
                        list.get(k).waitingTime++;
                    }
                }
                found = false;
            }

        }

        return list;
    }

    public static List<Process> priority(List<Process> list) {
        Collections.sort(list, new Comparator<Process>() {
            public int compare(Process p1, Process p2) {
                return p1.priority- p2.priority;
            }
        });

        return nonPreemptive(list);
    }

    public static List<Process> priorityp(List<Process> list) {
        return list;
    }
    
    public static List<Process> rr(List<Process> list) {

        return list;
    }

}