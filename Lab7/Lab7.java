import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.math.BigInteger;
import java.io.ObjectInputFilter.Status;

import javax.swing.Timer;


public class Lab7 extends JFrame implements ActionListener {

     // misc stuff
     private static final long serialVersionUID = 0000000000000001;
     private static long longCalcCount = 0;
     public static BigInteger latestFac = null;
     public static Boolean firstTime = true;

     // timer stuff
     public static final long startTime = System.currentTimeMillis();
     public static long endTime = System.currentTimeMillis();
     private static JLabel timeLabel; 
     public Timer timer = new Timer(1000, this);
     
     // interface stuff
     private static JTextField longCalculationTextField = new JTextField("Long calc count: " + longCalcCount);
     private JButton startButton = new JButton("Start!");


     // interface for GUI 
     public Lab7() {

          super("Lab7");
          
          //timer stuff
          timeLabel = new JLabel( "0" );
          add( timeLabel );
          timer.setInitialDelay(1);
          timer.start();

          // interface stuff
          setSize(800,600);
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

          timeLabel.setText( String.valueOf(latestFac));
          longCalculationTextField.setText("Long calc count: " + longCalcCount);

     }

     // start off the long calculation in the background thread when the start button is pushed
     private class calcButton implements ActionListener { 
          
          @Override
          public void actionPerformed(ActionEvent arg0) {
                    
                    // if first time it's a start button, else it's a cancel button
                    if (firstTime==true) {

                         firstTime = false;
                         startButton.setText("Cancel");
                         // start calcs on background thread
                         MyThread myThread = new MyThread();
                         myThread.start();

                    } else {

                         timer.stop();
                         endTime = System.currentTimeMillis() - startTime;
                         timeLabel.setText(String.valueOf(endTime/1000) + " seconds");
                         MyThread.interrupted();

                    }    
               }
          }

     // THREAD 2: timer, long calculation, update GUI
     private static class MyThread extends Thread {

          // rng for what number to get the factorial of
          private static int getRandomNumberInRange(int min, int max) {
    
               Random r = new Random();
               return r.ints(min, (max + 1)).limit(1).findFirst().getAsInt();

          }

          // compute a big factorial
          public static BigInteger factorial(int rng) {
               
               BigInteger fact = BigInteger.valueOf(1);
               for (int i = 1; i <= rng; i++) fact = fact.multiply(BigInteger.valueOf(i));
               
               return fact;

          }

          public void updateGUI(final Status status) {

               if (!SwingUtilities.isEventDispatchThread()) {

                    SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                         updateGUI(status);
                         return;
                    }
                    });
                    return;

               }

               //Now edit your gui objects
               timeLabel.setText(String.valueOf(endTime/1000) + " seconds");
               return;

            }

          public void run(){

               while (!Thread.currentThread().isInterrupted()) {
                    
                    // if we hit 25 calcs cancel everything 
                    if (longCalcCount<5){
                    
                         // rng to calculate big factorials
                         int rand_int1 = getRandomNumberInRange(10000, 100000);
                         latestFac = factorial(rand_int1); 
                         longCalcCount += 1;

                    } else {
                         
                         // will calculate the time since starting and update the GUI with that time
                         endTime = System.currentTimeMillis() - startTime;
                         updateGUI(Status.ALLOWED);
                         Lab7.MyThread.interrupted();
                         
                    }
               }
          }
     }
     

     public static void main(String[] args) throws InterruptedException {

          new Lab7();

     }
}



