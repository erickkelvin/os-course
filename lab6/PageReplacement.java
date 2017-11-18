// Author: Erick Kelvin

import java.io.*;
import java.util.*;
import java.lang.*;

public class PageReplacement {
    private static int[] frames;
    private static int[] inputArray;

    private static int totalFaults = 0;
    private static int totalMemAccess = 0;

    public static void main(String[] args) {
        if (args.length < 2 || args.length > 2) {
            System.out.println("\nERROR! Number of arguments is invalid!\n\nExample: java PageReplacement algorithm-name(fifo/lru/optimal) file-name.txt\n");
            System.exit(-1);
        }
        System.out.println("\n# PAGE REPLACEMENT ALGORITHMS");

        init(args[1]);

        execute(args[0].toLowerCase());

        printResults();
    }

    public static void init(String fileName) {
        String line;
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                String[] input = line.split(" # ");
                frames = new int[Integer.parseInt(input[0])];
                String[] inputString = input[1].split(", ");
                inputArray = new int[inputString.length];
                for (int i=0; i<inputArray.length; i++) {
                    inputArray[i] = Integer.parseInt(inputString[i]);
                }
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

        for (int i=0; i<frames.length; i++) {
            frames[i] = -1;
        }
    }

    public static void execute(String algoName) {
        if (algoName.equals("fifo")) fifo();
        else if(algoName.equals("lru")) lru();
        else if(algoName.equals("optimal")) optimal();
    }

    public static void fifo() {
        System.out.println("\n> FIFO ALGORITHM");
        int f = 0;
        for(int i=0; i<inputArray.length; i++) {
            System.out.print("\n  |" + inputArray[i] + "| ");
            if (getFrameId(i)>=0) {
                System.out.println("Ok: " + Arrays.toString(frames));
                totalMemAccess++;
            } else {
                frames[f] = inputArray[i];
                System.out.println("Page fault: " + Arrays.toString(frames));
                totalFaults++;
                if (f==2) {
                    f=0;
                }
                else {
                    f++;
                }
            }
        }
    }

    public static void lru() {
        System.out.println("\n> LRU ALGORITHM");
        int f;
        int[] framesCount = new int[frames.length];
        for (int i=0; i<framesCount.length; i++) {
            framesCount[i] = i - framesCount.length;
        }

        for(int i=0; i<inputArray.length; i++) {
            System.out.print("\n  |" + inputArray[i] + "| ");
            if ((f = getFrameId(i)) >= 0) {
                framesCount[f]=i;
                System.out.println("Ok: " + Arrays.toString(frames));
                totalMemAccess++;
            } else {
                f = getSmallestFrameId(framesCount);
                frames[f] = inputArray[i];
                framesCount[f]=i;
                System.out.println("Page fault: " + Arrays.toString(frames));
                totalFaults++;
            }
        }
    }

    public static void optimal() {
        System.out.println("\n> OPTIMAL ALGORITHM");
        int f;
        int[] framesDistance = new int[frames.length];
        for (int i=0; i<framesDistance.length; i++) {
            framesDistance[i] = inputArray.length;
        }

        for(int i=0; i<inputArray.length; i++) {
            System.out.print("\n  |" + inputArray[i] + "| ");
            if ((getFrameId(i)) >= 0) {
                System.out.println("Ok: " + Arrays.toString(frames));
                totalMemAccess++;
            } else {
                f = getFarestFrameId(framesDistance,i);
                frames[f] = inputArray[i];
                System.out.println("Page fault: " + Arrays.toString(frames));
                totalFaults++;
            }
        }
    }

    public static int getFrameId(int input) {
        for (int f=0; f<frames.length; f++) {
            if (frames[f]==inputArray[input]) {
                return f;
            }
        }
        return -1;
    }

    public static int getSmallestFrameId(int[] framesCount) {
        int minId = 0;
        int minVal = framesCount[minId];
        for(int id=1; id<framesCount.length; id++) {
            if(framesCount[id] < minVal) {
                minVal = framesCount[id];
                minId = id;
            }
        }
        return minId;
    }

    public static int getFarestFrameId(int[] framesDistance, int curr) {
        for (int f=0; f<frames.length; f++) {
            for (int i=curr; i<inputArray.length; i++) {
                if (frames[f]==inputArray[i]) {
                    framesDistance[f] = i;
                    break;
                }
                framesDistance[f] = inputArray.length;
            }
        }
        int maxId = 0;
        int maxVal = framesDistance[maxId];
        for(int id=1; id<framesDistance.length; id++) {
            if(framesDistance[id] > maxVal) {
                maxVal = framesDistance[id];
                maxId = id;
            }
        }
        return maxId;
    }

    public static void printResults() {
        double totalSwitchTime = 2 * totalFaults;
        double totalMemAccessTime = 0.0002 * totalMemAccess;
        double totalExecutionTime = totalSwitchTime + totalMemAccessTime;
        double ratio = (totalSwitchTime/totalExecutionTime);

        System.out.println("\nTOTAL PAGE FAULTS: " + totalFaults);
        System.out.println("\nTOTAL MEMORY ACCESSES: " + totalMemAccess);
        System.out.println("\nRATIO SWITCH TIME/EXECUTION TIME: " + ratio + "\n");
    }
}
