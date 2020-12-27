import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SegmentationMain {

  public static void main(String[] args) {
    //Getting the fileName from the user
    String fileName;
    if (args.length > 0) {
      fileName = String.format("%s", args[0]);
    } else {
      System.out.println("Please provide a valid fileName");
      return;
    }

    //Computing the optimal value, and start idx table through the makeOpt function
    DPInformation dpInfo = SegmentationMain.makeOPT(fileName);
    int[][] optTable = dpInfo.getOpt();
    int[][] startTable = dpInfo.getStart();

    //Printing the optimal table
    printTable(optTable);

    //Computing the final optimal value based on the maximum in the last column of the opt table
    int opt = SegmentationMain.getOPT(optTable);

    //Printing the optimal value
    System.out.println(String.format("Optimal Value: %d", opt));

    //Backtracking to find the optimal solution(keeping track of the ending indexes)
    ArrayList<Partition> sol = new ArrayList<>();
    int end = optTable[0].length - 1;
    while (end >= 0) {
      if (optTable[0][end] > optTable[1][end] && optTable[0][end] > optTable[2][end]) {
        sol.add(new Partition(Camera.A, end));
        end = startTable[0][end] - 1;
      }
      else if (optTable[1][end] > optTable[0][end] && optTable[1][end] > optTable[2][end]) {
        sol.add(new Partition(Camera.B, end));
        end = startTable[1][end] - 1;
      }
      else {
        sol.add(new Partition(Camera.C, end));
        end = startTable[2][end] - 1;
      }
    }

    //Printing the optimal solution
    SegmentationMain.printSol(sol);
  }

  //Formats all 3 camera feeds into an ArrayList of each camera's frames
  private static ArrayList<ArrayList<String>> makeLists(String fileName) {
    //Lists to store the frames of each camera
    ArrayList<String> A = new ArrayList<String>();
    ArrayList<String> B = new ArrayList<String>();
    ArrayList<String> C = new ArrayList<String>();
    Scanner scannerA = null;
    Scanner scannerB = null;
    Scanner scannerC = null;

    //Making the file paths to the files
    File fileA;
    File fileC;
    File fileB;
    try {
      fileA = new File(String.format("%sA.txt", fileName));
      fileB = new File(String.format("%sB.txt", fileName));
      fileC = new File(String.format("%sC.txt", fileName));
    } catch (Exception e) {
      throw new IllegalArgumentException("Incorrect file path/name provided");
    }

    //Reading the input from the 3 files containing the 3 cameras frames
    try {
      scannerA = new Scanner(fileA);
      scannerB = new Scanner(fileB);
      scannerC = new Scanner(fileC);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    while (scannerA.hasNextLine()) {
      A.add(scannerA.nextLine());
      B.add(scannerB.nextLine());
      C.add(scannerC.nextLine());
    }

    //List to hold all 3 camera's frames
    ArrayList<ArrayList<String>> cameras = new ArrayList();
    cameras.add(A);
    cameras.add(B);
    cameras.add(C);

    //Returning the list of camera frames
    return cameras;
  }

  //Print the frames of the given camera feed
  private static void printFrames(List<String> frames) {
    for (String frame : frames) {
      System.out.println(frame);
    }
    System.out.println("**************************************");
  }

  //Prints the table of optimal values
  private static void printTable(int[][] opt) {
    for (int i = 0; i < opt.length; i++) {
      if (i == 0) {
        System.out.print("A \t");
      }
      if (i == 1) {
        System.out.print("B \t");
      }
      if (i == 2) {
        System.out.print("C \t");
      }

      for (int j = 0; j < opt[i].length; j++) {
        System.out.print(String.format("%" + 5 + "s", String.valueOf(opt[i][j])));
        System.out.print("\t \t");
      }
      System.out.println("");
    }
  }

  //Computes the optimal table
  private static DPInformation makeOPT(String fileName) {
    ArrayList<ArrayList<String>> cameras = SegmentationMain.makeLists(fileName);
    ArrayList<String> A = cameras.get(0);
    ArrayList<String> B = cameras.get(1);
    ArrayList<String> C = cameras.get(2);
    int frames = A.size();

    //Initializing
    int[][] opt = new int[3][frames];
    int[][] start = new int[3][frames];

    opt[0][0] = BlackBox.evaluate(A, 0, 0);
    opt[1][0] = BlackBox.evaluate(B, 0, 0);
    opt[2][0] = BlackBox.evaluate(C, 0, 0);
    start[0][0] = 0;
    start[1][0] = 0;
    start[2][0] = 0;

    //Iterating over all Frames
    for (int i = 1; i < frames; i++) {

      //Iterating over all Cameras
      for (int camera = 0; camera < 3; camera++) {

        //Figuring out the camera we are on(lastCamera) and the other two cameras
        ArrayList<String> lastCamera;
        int otherCamera1;
        int otherCamera2;
        if (camera == 0) {
          otherCamera1 = 1;
          otherCamera2 = 2;
          lastCamera = A;
        } else if (camera == 1) {
          otherCamera1 = 0;
          otherCamera2 = 2;
          lastCamera = B;
        } else {
          otherCamera1 = 0;
          otherCamera2 = 1;
          lastCamera = C;
        }

        //Finding optimal partition

        //Initialising temp vars based on which is camera is the better option for a partition at i
        int o = Math.max(opt[otherCamera1][i - 1] + BlackBox.evaluate(lastCamera, i, i),
            opt[otherCamera2][i - 1] + BlackBox.evaluate(lastCamera, i, i));
        int s = i;

        //Finding max partitions between j to i between the other two cameras
        for (int j = i - 1; j >= 0; j--) {
          int possiblyNewO;
          int possiblyNewS;
          if (j == 0) {
            possiblyNewO = BlackBox.evaluate(lastCamera, j, i);
            possiblyNewS = j;
          } else {
            possiblyNewO = Math.max(opt[otherCamera1][j - 1] + BlackBox.evaluate(lastCamera, j, i),
                opt[otherCamera2][j - 1] + BlackBox.evaluate(lastCamera, j, i));
            possiblyNewS = j;
          }
          if (possiblyNewO > o) {
            o = possiblyNewO;
            s = possiblyNewS;
          }
        }
        opt[camera][i] = o;
        start[camera][i] = s;
      }
    }

    //Returning the optimal and start table of values
    return new DPInformation(opt, start);
  }

  //Computes the optimal value given the opt table
  private static int getOPT(int[][] opt) {
    int optimalVal = Math.max(
        Math.max(opt[0][opt[0].length - 1], opt[1][opt[1].length - 1]),
        opt[2][opt[2].length - 1]);

    return optimalVal;
  }

  //Prints the optimal solution
  private static void printSol(ArrayList<Partition> sol) {
    System.out.println("Solution: ");
    for (Partition p : sol) {
      System.out.println(p.toString());
    }
  }
}

