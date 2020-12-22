

import java.io.*;
import java.util.*;



public class TextFiles {

     public static ArrayList<String> readIlluminaBPs(File inFile) {
 
          ArrayList<String> list = new ArrayList<String>(); 
          try {

               Scanner scanner = new Scanner(inFile);
               while (scanner.hasNextLine()) {

                    // add breakpoint coords to linked list
                    list.add(scanner.nextLine());

               }
               scanner.close();

          } catch (FileNotFoundException e) {
               System.out.println("ERROR: Could not find Illumina BP file");
               e.printStackTrace();
          }

          return list;

     }

     public static HashMap<Integer, ArrayList<String>> readBlastResults(File inFile) {

          
          HashMap<Integer, ArrayList<String>> outArray = new HashMap<Integer, ArrayList<String>>();

          try {

               Scanner scanner = new Scanner(inFile);
               while (scanner.hasNextLine()) {

                    // add breakpoint coords to array list that 
                    String[] splitLine = scanner.nextLine().split("\t");
                    Integer startPosBin = Integer.valueOf(splitLine[8]) - (Integer.valueOf(splitLine[8])%30000);

                    // creates artificial bins of size 30k, avg read lngth for this set is 9-10k
                    if ( outArray.containsKey(startPosBin) ) {
                         ArrayList<String> currentValue = outArray.get(startPosBin);
                         currentValue.add(splitLine[0]+"\t"+splitLine[1]+"\t"+splitLine[8]+"\t"+splitLine[9]);
                         outArray.put(startPosBin, currentValue);
                    } else {
                         ArrayList<String> startValue = new ArrayList<String>();
                         startValue.add(splitLine[0]+"\t"+splitLine[1]+"\t"+splitLine[8]+"\t"+splitLine[9]);
                         outArray.put(startPosBin, startValue);
                    }

               }
               scanner.close();

          } catch (FileNotFoundException e) {
               System.out.println("ERROR: Could not find Illumina BP file");
               e.printStackTrace();
          }

          return outArray;

     }

     public static void main(String[] args) {
          // ArrayList<LinkedList<String>> test = readIlluminaBPs(new File("/Users/bturne48/Documents/GitHub/AdvCodingClass/Final_Project/Input/clusteredVariants.txt"));
          // for (LinkedList<String> s: test) System.out.println(s);
          
          // System.out.println(System.currentTimeMillis());
          // HashMap<String, ArrayList<String>> test = TextFiles.readBlastResults(new File("/Users/bturne48/Documents/GitHub/AdvCodingClass/Final_Project/BLAST_Results/ST_1_16.blast"));
          // //System.out.println(Arrays.asList(test));
          // System.out.println(System.currentTimeMillis());
     }
}