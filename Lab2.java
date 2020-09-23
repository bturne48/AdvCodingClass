import java.util.Random;

public class Lab2 {
     
     public static String[] SHORT_NAMES = 
          { "A","R", "N", "D", "C", "Q", "E", 
          "G",  "H", "I", "L", "K", "M", "F", 
          "P", "S", "T", "W", "Y", "V" };
     
     public static String[] FULL_NAMES = 
          {"alanine","arginine", "asparagine", 
          "aspartic acid", "cysteine",
          "glutamine",  "glutamic acid",
          "glycine" ,"histidine","isoleucine",
          "leucine",  "lysine", "methionine", 
          "phenylalanine", "proline", 
          "serine","threonine","tryptophan", 
          "tyrosine", "valine"};          
     
          public static void quiz(){
          Random random = new Random();
          long startTime = System.nanoTime(); long endTime = System.nanoTime(); int numberRight = 0;
          while ((endTime-startTime)/1000000000 < 31){
               // select a random full name nucleotide
               int x = random.nextInt(20);
               System.out.print(FULL_NAMES[x]);
               System.out.print("\n");
               // user input
               String aString = System.console().readLine().toUpperCase();
               // check short names and find out what the index of the given answer, compare to full name
               int matchIndex = 0;
               for (int i=0; i < SHORT_NAMES.length; i++){
                    if (aString.equals(SHORT_NAMES[i])){
                         matchIndex = i;
                         break;
                    }
               }
               // compare the index printed originally to the provided 
               if (x == matchIndex){
                    numberRight += 1;
                    System.out.println("Correct; Score:  "+numberRight+"; Time Elapsed: " + (endTime-startTime )/1000000000 +"\n");
               } else {
                    System.out.println("Incorrect; Score:  "+numberRight+"; Time Elapsed: " + (endTime-startTime )/1000000000 + "\n");
               }
               //update time
               endTime = System.nanoTime();
          }
          System.out.println("Final Number Correct: " + numberRight);
     }

     public static void main(String[] args) {
          Lab2.quiz();
     }
}
