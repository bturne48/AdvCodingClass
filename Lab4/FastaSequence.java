import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FastaSequence {

     // pass in file name from command line
     public static List<String> argList = new ArrayList<String>();

     // factory stuff
     private String header;
     private String sequence;
     
     public FastaSequence(String header, String sequence){
          this.header = header;
          this.sequence = sequence;
     }

     // returns the header of this sequence without the “>”
     public String getHeader() {
          return header;
     }

     // returns the Dna sequence of this FastaSequence
     public String getSequence() {
          return sequence;
     }

     // count number of uniq characters found in a strin
     public static float countChar(String str, char c){
          float count = 0;
          for(int i=0; i < str.length(); i++){    
               if(str.charAt(i) == c)
               count++;
          }
          return count;
     }

     // returns the number of G’s and C’s divided by the length of this sequence
     public float getGCRatio() {
          float countG = countChar(sequence, 'G');
          float countC = countChar(sequence, 'C');
          float countA = countChar(sequence, 'A');
          float countT = countChar(sequence, 'T');
          return (countG + countC)/(countC+countG+countA+countT);
     }

     // create list of objects as you read the fasta file in
     public static List<FastaSequence> readFastaFile(String filepath) throws Exception {
          List<FastaSequence> outList = new ArrayList<>();
          List<String> fastaList = new ArrayList<String>();

          // Read in file
          try {
               File myObj = new File(filepath);
               Scanner myReader = new Scanner(myObj); // Create a Scanner object
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
               String head = "";
               if (fastaList.get(i).startsWith(">")){
                    head = fastaList.get(i).substring(1);
                    // append seqID
                    // check the next line for new seq id, if not append sequence
                    while (i<fastaList.size()-1 && !fastaList.get(i+1).startsWith(">")){
                         seq = seq.concat(fastaList.get(i+1));
                         i++;
                    }
               }
			FastaSequence newFS = new FastaSequence(head, seq);
               outList.add(newFS);
          }
          return outList;
     }


     // print lines with unique sequences
     public static void writeUnique(File inFile, File outFile ) throws Exception {
          List<String> fastaList = new ArrayList<String>();
          HashMap<String, Integer> numberMap = new HashMap<String, Integer>();
          FileWriter myWriter = new FileWriter(outFile);

          //Read in file
          try {
               Scanner myReader = new Scanner(inFile);  // Create a Scanner object
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
                    // append seqID
                    // check the next line for new seq id, if not append sequence
                    while (i<fastaList.size()-1 && !fastaList.get(i+1).startsWith(">")){
                         seq = seq.concat(fastaList.get(i+1));
                         i++;
                    }
               }

               // look for unique srings 
               if ( numberMap.containsKey(seq) ){
                    numberMap.put(seq, numberMap.get(seq)+1);
               } else {
                    numberMap.put(seq, 1);
               }
          }
          // Create a list from elements of HashMap 
          List<Map.Entry<String, Integer> > list = new LinkedList<Map.Entry<String, Integer> >(numberMap.entrySet()); 
          // Sort the list 
          Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() { 
          public int compare(Map.Entry<String, Integer> o1,  Map.Entry<String, Integer> o2) { 
               return (o1.getValue()).compareTo(o2.getValue()); 
          } 
          }); 
   
          // put data from sorted list to hashmap  
          HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>(); 
          for (Map.Entry<String, Integer> aa : list) { 
               temp.put(aa.getKey(), aa.getValue()); 
          }    
          
          // write data from sorted hash into file
          for (String key: temp.keySet()) {
              myWriter.write(">"+key+"\n"+temp.get(key)+"\n");
          }
          myWriter.close();
     }


     public static void main(String[] args) throws Exception {
          // process cmd line args
          for(int i = 0; i< args.length; i++) {
               argList.add(args[i]);
          }
          
          List<FastaSequence> fastaList = FastaSequence.readFastaFile(argList.get(0));

          // // part 1
          for( FastaSequence fs : fastaList) {
          System.out.println(fs.getHeader());
          System.out.println(fs.getSequence());
          System.out.println(fs.getGCRatio());
          }

          // for part 2; i acknowledge this is ridiculous but i couldnt understand how to interact with the FastaSequence class
          for( FastaSequence fs : fastaList) {
               File myIn = new File(argList.get(1));
               File myOut = new File(argList.get(2));
               fs.writeUnique(myIn, myOut);
               break;
          }
     }

   

}
