import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
//import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

public class Main{

    

    public static void print_kb(ArrayList<ArrayList<String>> kb){
        // print the 2D ArrayList
        int count = 1;
        for (ArrayList<String> clause : kb) {
            System.out.print(count++ + ". ");
            for (String literal : clause) {
                System.out.print(literal + " ");
            }
            System.out.print("{}");
            System.out.println();
        }
    }

    public static ArrayList<ArrayList<String>> load_kb(String inputFileName){

        // Read input data from file and load into KB as 2D ArrayList.
        ArrayList<ArrayList<String>> kb = new ArrayList<ArrayList<String>>();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(inputFileName));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split("\\s+"); // split line by whitespace
                ArrayList<String> clause = new ArrayList<String>();
                for (String literal : splitLine) {
                    clause.add(literal);
                }
                kb.add(clause);
            }
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException when trying to read from input file!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException when trying to read from input file!!");
            e.printStackTrace();
        }

        // Negate clauses of the last entry, and add individually.
        ArrayList<String> testEntry = kb.get( kb.size() - 1);
        kb.remove(kb.size() - 1);

        for(String literal : testEntry){
            ArrayList<String> clause = new ArrayList<String>();

            if(literal.startsWith("~")){
                clause.add(literal.substring(1));
            } else {
                clause.add("~"+literal);
            }

            kb.add(clause);
        }

        return kb;

    }

    public static boolean checkResolvent(ArrayList<ArrayList<String>> kb, ArrayList<String> resolvent){
        for(ArrayList<String> clause: kb){
            if(clause.size() == resolvent.size()){
                HashSet<String> clauseSet = new HashSet<String>(clause);
                HashSet<String> resSet = new HashSet<String>(resolvent);

                if(clauseSet.equals(resSet)){
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean resolve( ArrayList<ArrayList<String>> kb) {

        for(int i = 1; i < kb.size(); i++){
            for(int j = 0; j < i; j++){
                ArrayList<String> resolvent = attemptResolve(kb.get(i), kb.get(j));

                //Check for contradiction
                if(resolvent.isEmpty()){
                    System.out.println( (int)(kb.size()+1) + ". Contradiction {" + (int)(i + 1) + ", " + (int)(j + 1) + "}");
                    return true;
                }

                //Add to KB if new clause
                if(!resolvent.equals(kb.get(i))){
                    if(checkResolvent(kb, resolvent)){
                        kb.add(resolvent);
    
                        System.out.print(kb.size() + ". ");
                        for (String literal : resolvent) {
                            System.out.print(literal + " ");
                        }
                        System.out.printf("{%d, %d}\n", i+1, j+1);
                    }
                }
            }
        }
        return false;
    }

    public static ArrayList<String> attemptResolve(ArrayList<String> clauseX, ArrayList<String> clauseY){
        //Combine clauses into same clause
        ArrayList<String> resolvent = new ArrayList<String>();
        int tautologyCheck = 0;
        

        //Loop thru clauses, if not able to resolve literal, add to resolvent.
        for(String literal: clauseX ){
            if(literal.startsWith("~")){
                if(!clauseY.contains(literal.substring(1))){
                    resolvent.add(literal);
                } else {
                    tautologyCheck++;
                }
            } else {
                if(!clauseY.contains("~"+literal)){
                    resolvent.add(literal);
                } else {
                    tautologyCheck++;
                }
            }
            
        }

        for(String literal: clauseY ){
            if(literal.startsWith("~")){
                if(!clauseX.contains(literal.substring(1))){
                    if(!resolvent.contains(literal)){
                        resolvent.add(literal);
                    }
                } else {
                    tautologyCheck++;
                }
            } else {
                if(!clauseX.contains("~"+literal)){
                    if(!resolvent.contains(literal)){
                        resolvent.add(literal);
                    }
                } else {
                    tautologyCheck++;
                }
            }
        }

        if(tautologyCheck != 2){ //Always true, therefore return something already in KB
            return clauseX; 
        }

        return resolvent;
    }
    public static void main(String[] args){

        ArrayList<ArrayList<String>> kb = new ArrayList<ArrayList<String>>();
        
        kb = load_kb(args[0]);
        print_kb(kb);
        if(resolve(kb)){
            System.out.println("Valid");
        } else {
            System.out.println("Fail");
        }

    }
}