import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;




public class Lab3 {
     // pass in file name from command line
     public static List<String> argList = new ArrayList<String>();


     // count number of uniq characters found in a strin
     public static String countChar(String str, char c){
          int count = 0;
          for(int i=0; i < str.length(); i++){    
               if(str.charAt(i) == c)
               count++;
          }
          String stringCount = String.valueOf(count);
          return stringCount;
     }
     

     public static ArrayList<String> parseFasta(){
          List<String> fastaList = new ArrayList<String>();
          List<String> outList = new ArrayList<String>();

          // Add header to ouput file 
          String [] outHeader = {"seqID", "countA", "countC", "countG","countT", "sequence"};
          String concatHeader = "";
          for (int j=0; j<6; j++){
               concatHeader= concatHeader.concat(outHeader[j]).concat("\t");
          }
          outList.add(concatHeader.concat("\n"));

          //Read in file
          try {
          File myObj = new File(argList.get(0));
          Scanner myReader = new Scanner(myObj);  // Create a Scanner object
          while (myReader.hasNextLine()) {
               String data = myReader.nextLine();
               fastaList.add(data);
          }
          myReader.close();
          } catch (FileNotFoundException e) {
               System.out.println("An error occurred.");
               e.printStackTrace();

          }
          
          // iterate over list and look for >
          for (int i=0; i<fastaList.size(); i++){
               String seq = "";
               if (fastaList.get(i).startsWith(">")){
                    // append header
                    seq = seq.concat(fastaList.get(i).substring(1).concat("\t"));
                    // check the next line for new seq id
                    while (i<fastaList.size()-1 && !fastaList.get(i+1).startsWith(">")){
                         seq = seq.concat(fastaList.get(i+1));
                         i++;
                    }
                    // find number of uniq characters
                    String seqID = seq.split("\t")[0];
                    String sequence = seq.split("\t")[1];
                    String countA = countChar(seq.split("\t")[1], 'A');
                    String countC = countChar(seq.split("\t")[1], 'C');
                    String countG = countChar(seq.split("\t")[1], 'G');
                    String countT = countChar(seq.split("\t")[1], 'T');
                    String [] outArray = {seqID, countA, countC, countG, countT, sequence};

                    String concatString = "";
                    for (int j=0; j<6; j++){
                         concatString = concatString.concat(outArray[j]).concat("\t");
                    }
                    outList.add(concatString.concat("\n"));
               }
          }
          return (ArrayList<String>) outList;
     }
     

     public static void main(String[] args) {
          for(int i = 0; i< args.length; i++) {
               argList.add(args[i]);
          }

          try {
               File myObj = new File("fastaOut.txt");
               if (myObj.createNewFile()) {
                 System.out.println("File created: " + myObj.getName());
               } else {
                 System.out.println("File already exists.");
               }
             } catch (IOException e) {
               System.out.println("An error occurred.");
               e.printStackTrace();
             }

          try {
               FileWriter myWriter = new FileWriter("fastaOut.txt");
               for (int j=0; j<Lab3.parseFasta().size(); j++){
                    myWriter.write(Lab3.parseFasta().get(j));
               }
               myWriter.close();
               System.out.println("Successfully wrote to the file.");
             } catch (IOException e) {
               System.out.println("An error occurred.");
               e.printStackTrace();
             }   
      
          
     }    
}



