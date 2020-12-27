import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class BlackBox {
    // main here is just a test/demo program - it's not "the" main for you

    public static int BLACK_BOX_MIN = -1000;
    public static int BLACK_BOX_MAX = 1000;

    public static void main(String[] args) {
        Scanner myScanner = new Scanner(System.in);
        ArrayList<String> strings = new ArrayList<String>();
        while(myScanner.hasNextLine()) {
            String s = myScanner.nextLine();
            strings.add(s);
        }
        for (int len = 1; len <= strings.size(); len++) {
            System.out.println("Length " + len + ":");
            for (int i = 0; i < strings.size() - (len-1); i++) {
                int j = i + (len - 1);
                System.out.println(evaluate(strings,i,j));
            }
        }            
    }

    // valueOf:  returns a value in range [-BLACK_BOX_MIN, BLACK_BOX_MIN]
    // that is deterministic, hard to predict, and depends on the string
    static public int valueOf(String s) {
        int modvalue = BLACK_BOX_MAX - BLACK_BOX_MIN + 1;
        return (s.hashCode() % modvalue) + BLACK_BOX_MIN;
    }


    // Get value for concatenation of strings; includes string at end index
    static int evaluate(ArrayList<String> strings, int start, int end) {
        StringBuilder toEvaluate = new StringBuilder();
        for (int i = start; i <= end; i++) {
            toEvaluate.append(strings.get(i));
        }   
        return BlackBox.valueOf(toEvaluate.toString());
    }


    // Read a file into an ArrayList<String>, one line per entry
    static ArrayList<String> readfile(String filename) {
        ArrayList<String> ret = new ArrayList<String>();
        try {
            File file = new File(filename);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String ln; 
            while ((ln = br.readLine()) != null) {
                ret.add(ln);
            }   
        } catch (Exception e) {
            System.err.println(e);
        }   
        return ret;
    }
}
