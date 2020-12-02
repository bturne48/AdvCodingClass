import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.math.BigInteger;


public class Lab7 extends JFrame implements ActionListener {

     // misc stuff
     private static final long serialVersionUID = 0000000000000001;
     private static long longCalcCount = 0;
     public static BigInteger latestFac = null;
     public static Boolean firstTime = true;
     public static int doneCode = 0;

     // timer stuff
     public static long startTime = 0;
     public static long endTime = 0;
     private static JLabel timeLabel;
     public Timer timer = new Timer(1000, this);

     // interface stuff
     private static JTextField longCalculationTextField = new JTextField("Long calc count: " + longCalcCount);
     private JButton startButton = new JButton("Start!");

     // interface for GUI
     public Lab7() {

          super("Lab7");

          // timer stuff
          timeLabel = new JLabel("0");
          add(timeLabel);
          timer.setInitialDelay(1);
          timer.start();

          // interface stuff
          setSize(800, 600);
          setLocationRelativeTo(null);
          setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

          // look for quiz start or cancel
          startButton.addActionListener(new calcButton());

          // update text boxes with results
          getContentPane().add(timeLabel, BorderLayout.NORTH);
          getContentPane().add(longCalculationTextField, BorderLayout.CENTER);
          getContentPane().add(startButton, BorderLayout.SOUTH);

          setVisible(true);

     }

     // use to update with results of calculations, updates every second with latest result
     @Override
     public void actionPerformed(ActionEvent e) {

          // will not run if the other thread was killed because calcs are done
          if (doneCode!=1){
               timeLabel.setText(String.valueOf(latestFac));
               longCalculationTextField.setText("Long calc count: " + longCalcCount);
          } 
     }

     // start off the long calculation in the background thread when the start button
     private class calcButton implements ActionListener {

          @Override
          public void actionPerformed(ActionEvent arg0) {

               // if first time it's a start button, else it's a cancel button
               if (firstTime == true) {
                    
                    //initial info for first button press
                    firstTime = false;
                    startTime = System.currentTimeMillis();
                    startButton.setText("Cancel");

                    // start calcs on background thread
                    MyThread2 myThread = new MyThread2();
                    myThread.start();

               } else {
                    
                    // kill thread 2
                    doneCode = 1;
                    MyThread2.interrupted();

                    // final updates before close
                    endTime = System.currentTimeMillis() - startTime;
                    timeLabel.setText(String.valueOf(endTime / 1000) + " seconds");
                    try {
                         Thread.sleep(3000);
                    } catch (InterruptedException e) {
                         e.printStackTrace();
                    }
                    System.exit(1);

               }
          }
     }

     // THREAD 2: timer, long calculation, update GUI
     private static class MyThread2 extends Thread {

          // rng for what number to get the factorial of
          private static int getRandomNumberInRange(int min, int max) {

               Random r = new Random();
               return r.ints(min, (max + 1)).limit(1).findFirst().getAsInt();

          }

          // compute a big factorial
          public static BigInteger factorial(int rng) {

               BigInteger fact = BigInteger.valueOf(1);
               for (int i = 1; i <= rng; i++)
                    fact = fact.multiply(BigInteger.valueOf(i));

               return fact;

          }

          public void run() {

               while (!Thread.currentThread().isInterrupted()) {

                    // if we hit 25 calcs cancel everything
                    if (longCalcCount < 10) {

                         // rng to calculate big factorials
                         int rand_int1 = getRandomNumberInRange(10000, 100000);
                         latestFac = factorial(rand_int1);
                         longCalcCount += 1;

                    } else {

                         // will calculate the time since starting and update the GUI with that time
                         doneCode = 1;
                         endTime = System.currentTimeMillis() - startTime;
                         timeLabel.setText(String.valueOf(endTime / 1000) + " seconds");
                         try {
                              MyThread2.sleep(2000);
                         } catch (InterruptedException e) {
                              e.printStackTrace();
                         }

                         System.exit(1);

                    }
               }
          }
     }
     

     public static void main(String[] args) throws InterruptedException {

          new Lab7();

     }
}



