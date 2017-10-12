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
        
        inputList = initData(args[0], inputList);
        
        System.out.println("\n### PROCESS SCHEDULER ### \n");

        if (algoName.equals("fcfs")) { outputList = fcfs(inputList); }
        else if (algoName.equals("sjf")) { outputList = sjf(inputList); }
        else if (algoName.equals("sjfp")) { outputList = sjfp(inputList); }
        else if (algoName.equals("priority")) { outputList = priority(inputList); }
        else if (algoName.equals("priorityp")) { outputList = priorityp(inputList); }
        else if (algoName.equals("rr")) { outputList = rr(inputList); }

        System.out.println("\n\n# OUTPUT: \n-----------------------");
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
        for (int i=0; i<list.size(); i++) {
            System.out.println("P" + list.get(i).id);
            //System.out.println("Arrival Time: \t" + list.get(i).arrivalTime);
            //System.out.println("Burst Time: \t" + list.get(i).burstTime);
            //System.out.println("Priority: \t" + list.get(i).priority);
            if ((list.get(i).waitingTime>-1)&&(list.get(i).turnAround>-1)) {
                System.out.println("Waiting Time: \t" + list.get(i).waitingTime);
                System.out.println("Turnaround: \t" + list.get(i).turnAround);
                System.out.println("Response Time: \t" + list.get(i).responseTime);
            }
            System.out.println("-----------------------");
        }
    }

    public static void printStatistics(List<Process> list, String algoName) {
        int totalContextSwitch = (int)totalBurst - 1;
        int totalTurnaround = 0;
        int totalWaitingTime = 0;
        int totalResponseTime = 0;
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
        //System.out.println("| CPU Usage:\t\t\t" + ((totalBurst/(totalBurst + totalContextSwitch))*100) + "%" + "\t|");
        System.out.printf("| Throughput (average):\t\t%.2f \t|\n", list.size()/totalBurst);
        System.out.println("| Turnaround (average):\t\t" + totalTurnaround/list.size() + "\t|");
        System.out.println("| Waiting (average):\t\t" + totalWaitingTime/list.size() + "\t|");
        System.out.println("| Response time (average):\t" + totalResponseTime/list.size() + "\t|");
        //System.out.println("| Context switch (average):\t" + ?? + "\t|");
        System.out.println("| Number of processes:\t\t" + list.size() + "\t|");
        System.out.println("-----------------------------------------");
    }

    public static List<Process> nonPreemptive(List<Process> list) {
        Process executing = null;
        int executingBurst = 0;
        int jobsProcessed = 0;
        List<Process> result = new ArrayList<Process>();

        for(int slot=0; slot<totalBurst; slot++) {
            System.out.println("slot: " + slot);
            for(int k=0; k<list.size(); k++) {
                if ((list.get(k).arrivalTime <= slot)) {
                    System.out.println(" id: " + list.get(k).id + " wait: " + list.get(k).waitingTime);
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