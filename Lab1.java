import java.util.Random;

public class Lab1 {
     static void q1q2() {
          // QUESTION 1
          Random random = new Random();
          // make an array that connects rng to nucletodies, use rng to index 
          char[] nucls = {'A', 'T', 'G', 'C'};
          // loop 1000 times and create 3mers, store how many times you see AAA
          int sawAAA = 0;
          for (int i=0; i<1000; i++){
               // string to append random nucleotides to as for loop continues
               String threeMer = "";
                    for (int j=0; j < 3; j++){
                    int x = random.nextInt(4);
                    threeMer = threeMer + nucls[x];
               }
               System.out.println(threeMer);
               if (threeMer.equals("AAA")){
                    sawAAA++; 
               }
          }
          // We would expect to see AAA, E(x) = 1/64 * 1000 ~ 16, I ran this twice and got 15 and 16
          System.out.println(sawAAA);
          }

     static void q3() {
          Random rand = new Random();
          // make an array that connects rng to nucletodies, use rng to index 
          char[] nucls = {'T', 'A', 'C', 'G'};
          // loop 1000 times and create 3mers, store how many times you see AAA
          int sawAAA = 0;
          for (int i=0; i<1000; i++){
               // string to append random nucleotides to as for loop continues
               String threeMer = "";
               for (int j=0; j < 3; j++){
                    // rng with weights
                    int r = rand.nextInt(10000000);
                    int m = r % 100;
                    int x = 0;
                    if (m < 11){
                         x=0; //11%
                    } else if (m < 23) { 
                         x=1; //12%
                    } else if (m < 61) {
                         x=2; //38%
                    } else {
                         x=3; //39%
                    }  
                    // use weighted rng to index nucl array
                    threeMer = threeMer + nucls[x];
               }
               System.out.println(threeMer);
               if (threeMer.equals("AAA")){
                    sawAAA++; 
               }
          }
          // We would expect to see AAA, E(x) = (0.12^3) * 1000 ~ 2, I ran this twice and got 1 and 2
          System.out.println(sawAAA);
          }
     

     public static void main(String[] args) {
          //q1q2();
          q3();
     }
}
