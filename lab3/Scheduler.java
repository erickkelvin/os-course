import java.io.*;
import java.util.*;

import jdk.nashorn.internal.runtime.FindProperty;

import java.math.*;

public class Scheduler {

    public static int totalSlots = 0;
    public static double totalBurst = 0;

    public static void main(String[] args) throws IOException {
        List<Process> inputList = new ArrayList<Process>();
        List<Process> outputList = new ArrayList<Process>();
        
        inputList = initData(args[0], inputList);
        
        System.out.println("\n### PROCESS SCHEDULER ### \n");
        //System.out.println("\n# INPUT: \n-----------------------");
        //printList(inputList);

        outputList = sjf(inputList);
        System.out.println("\n\n# OUTPUT: \n-----------------------");
        printList(outputList);

        printStatistics(outputList, "sjf");
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
            System.out.println("Arrival Time: " + list.get(i).arrivalTime);
            System.out.println("Burst Time: " + list.get(i).burstTime);
            System.out.println("Priority: " + list.get(i).priority);
            if ((list.get(i).waitingTime>-1)&&(list.get(i).turnAround>-1)) {
                System.out.println("Waiting Time: " + list.get(i).waitingTime);
                System.out.println("Turnaround: " + list.get(i).turnAround);
                System.out.println("Response Time: " + list.get(i).responseTime);
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

    public static List<Process> fcfs(List<Process> list) {
        int i = 0; //current process index
        for(int slot=0; slot<totalBurst; slot++) {

            //new process has arrived and it's waiting
            for(int j=0; j<list.size(); j++) {
                if ((list.get(j).arrivalTime < slot)&&(i!=j)&&(list.get(j).burstTime>0)) {
                    list.get(j).waitingTime++;
                }
            }

            list.get(i).burstTime--;
            list.get(i).responseTime = list.get(i).waitingTime;
            
            //check if a process finished executing
            if (list.get(i).burstTime == 0) {
                list.get(i).turnAround = slot + 1 - list.get(i).arrivalTime;
                i++;
            }
        }

        return list;
    }

    public static List<Process> sjf(List<Process> list) {
        Collections.sort(list, new Comparator<Process>() {
            public int compare(Process p1, Process p2) {
                return p1.burstTime - p2.burstTime;
            }
        });

        List<Process> queue = new ArrayList<Process>();
        List<Process> result = new ArrayList<Process>();

        int i = 0; //current process index
        int j = -1; //current queue process index
        for(int slot=0; slot<totalBurst; slot++) {
            for(int k=0; k<list.size(); k++) {
                if ((list.get(k).arrivalTime <= slot)) {
                    if ((j==-1)||(queue.get(j).burstTime==0)) {
                        Process p = new Process(list.get(k));
                        queue.add(p);
                        j++;
                        list.remove(k);
                        break;
                    }
                    else {
                        list.get(k).waitingTime++;
                        list.get(k).turnAround++;
                    }
                }
            }
            System.out.println(queue.get(0).id);
            queue.get(j).burstTime--;
            queue.get(j).turnAround++;

            if (queue.get(j).burstTime == 0) {
                Process p = new Process(queue.get(j));
                result.add(p);
                queue.remove(j);
                j--;
            }

        }

        return result;
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
        return list;
    }

    public static List<Process> priorityp(List<Process> list) {
        return list;
    }
    
    public static List<Process> rr(List<Process> list) {
        return list;
    }

}