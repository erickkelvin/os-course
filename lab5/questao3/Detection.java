// Author: Erick Kelvin

import java.io.*;
import java.util.*;
import java.lang.*;

public class Detection {
    private static int[] available;
    private static int[] work;
    private static boolean[] finish;

    private static int[] request;
    private static int requestId;

    private static int[][] alloc;
    private static int[][] req;

    private static List<String> finalSequence;

    public static void main(String[] args) {

        if (args.length < 1 || args.length > 1) {
            System.out.println("\nERROR! Number of arguments is invalid!\n\nExample: java Detection file.txt\n");
            System.exit(-1);
        }

        System.out.println("\n# DEADLOCK DETECTION ALGORITHM");

        init(args[0]);
        execute();

        printResults();

        Scanner s = new Scanner(System.in);

        boolean hasRequests = true;
        while (hasRequests) {
            System.out.print("\n> Request a new resource? [Y/N]\n  ");
            String input = s.nextLine().toLowerCase();
            
            if(input.equals("y")) {
                System.out.print("\n> For which process number? [Example: 2]\n  ");
                requestId = s.nextInt();
                s.nextLine();
                System.out.print("\n> Which resources? [Example: 1 2 3]\n  ");
                input = s.nextLine();
                String[] resources = input.split(" ");
                for (int i=0; i<resources.length; i++) {
                    req[requestId][i] += Integer.parseInt(resources[i]);
                }
                execute();
                printResults();
            }
            else {
                hasRequests = false;
            }
        }
        System.out.println("\nExiting...");
        s.close(); 
        System.exit(0);
    }

    private static void init(String fileName) {
        String line;
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            int lineId = 0;
            while((line = bufferedReader.readLine()) != null) {
                String[] n = line.split(", ");
                if (lineId==0) { 
                    alloc = new int[n.length][];
                    finish = new boolean[n.length];
                }
                else if (lineId==1) {
                    req = new int[n.length][];
                }
                for(int i=0; i<n.length; i++) {
                    String[] m = n[i].split(" ");
                    if (lineId==0) {
                        alloc[i] = new int[m.length];
                        request = new int[m.length];
                    }
                    else if (lineId==1) {
                        req[i] = new int[m.length];
                    }
                    else if (lineId==2) {
                        available = new int[m.length];
                    }
                    for (int j=0; j<m.length; j++) {
                        if (lineId==0) {
                            alloc[i][j] = Integer.parseInt(m[j]);
                        }
                        else if (lineId==1) {
                            req[i][j] = Integer.parseInt(m[j]);
                        }
                        else if (lineId==2) {
                            available[j] = Integer.parseInt(m[j]);
                        }
                    }
                }
                lineId++;
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

        for(int j=0; j<alloc[0].length;j++) {
            for(int i=0; i<alloc.length;i++) {
                available[j] -= alloc[i][j];
            }
        }
    }

    private static void execute() {
        finalSequence = new ArrayList<String>();
        for(int i=0;i<alloc.length;i++) {
            int procAlloc = 0;
            for(int j=0; j<alloc[i].length;j++) {
                procAlloc += alloc[i][j]; 
            }
            if (procAlloc!=0) {
                finish[i] = false;
            }
            else {
                finish[i] = true;
            }
        }

        work = Arrays.copyOf(available, available.length);

        while(!isFinished()) {
            boolean exists = true;
            for(int i=0; i<finish.length; i++) {
                if (!finish[i] && reqIsLessThanWork(i)) {
                    for (int j=0; j<work.length; j++) {
                        work[j] += alloc[i][j];
                    }
                    finish[i] = true;
                    finalSequence.add("P" + i);
                    exists = true;
                }
                else if (!finish[i] && !reqIsLessThanWork(i)) {
                    exists = false;
                }
            }
            if (!exists) {
                for(int i=0; i<finish.length; i++) {
                    if (!finish[i]) {
                        System.out.println("\nDeadlock detected in process "+ i +"!\n");
                        System.exit(-1);
                    }
                }
            }
        }
    }

    private static boolean reqIsLessThanWork(int i) {
        for(int j=0;j<req[i].length;j++) {
            if (req[i][j]>work[j]) {
                return false;
            }
        }
        return true;
    }

    private static boolean isFinished() {
        for (int i=0; i<finish.length; i++) {
            if (finish[i]==false) {
                return false;
            }
        }
        return true;
    }

    private static void print2D(String title, int[][] data) {
        System.out.println("\n" + title + ":");
        for(int i=0; i<data.length;i++) {
            for(int j=0; j<data[i].length;j++) {
                System.out.print(data[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static void printResults() {
        print2D("Allocation", alloc);
        print2D("Request", req);
        System.out.println("\nAvailable resources:\n" + Arrays.toString(available));
        System.out.println("\nSafe sequence:\n" + finalSequence);
    }
}