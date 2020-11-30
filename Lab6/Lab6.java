import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.Timer;

public class Lab6 extends JFrame {

     private static final long serialVersionUID = 0000000000000001;

     // quiz vars
     private String answer = "";
     private Integer numberRight = 0;
     private Integer numberWrong = 0;
     private String nextFullName = "Alanine";
     private String nextRight = "A";

     // timer stuff
     private static long START;
     public Timer timer = null;

     // interface stuff
     private JTextField aTextField = new JTextField("Number Correct: " + numberRight);
     private JTextField bTextField = new JTextField("Number Incorrect: " + numberWrong);
     private JTextField cTextField = new JTextField("AA Name: Alanine");
     private JTextField yourInpuFieldt = new JTextField();
     private JButton gambleButton = new JButton("Start!");
     private JButton cancelButton = new JButton("Cancel");
     private JTextField resultText = new JTextField();

     // misc stuff
     private Random random = new Random();
     private Boolean resultType = false;
     private Boolean firstTime = true;

     // names for quiz
     public static String[] SHORT_NAMES = { "A", "R", "N", "D", "C", "Q", "E", "G", "H", "I", "L", "K", "M", "F", "P",
               "S", "T", "W", "Y", "V" };
     public static String[] FULL_NAMES = { "alanine", "arginine", "asparagine", "aspartic acid", "cysteine",
               "glutamine", "glutamic acid", "glycine", "histidine", "isoleucine", "leucine", "lysine", "methionine",
               "phenylalanine", "proline", "serine", "threonine", "tryptophan", "tyrosine", "valine" };

     //timer on new thread
     private static class MyThread extends Thread {

          public void startTimer() {
               // // timer code
               final int totalSeconds = 30;

               final Timer tm = new Timer(1000, e -> {
                    System.out.println("Seconds remaining: " + (totalSeconds * 1000 - System.currentTimeMillis() + START) / 1000d);
               });
               
               START = System.currentTimeMillis();
               
               new Timer(totalSeconds * 1000, e -> {
                    tm.stop();
                    System.out.println("Finished!");
                    System.exit(0);
               }).start();
          
               tm.start();  
          }
     }  

     // cancel button
     private class Cancel implements ActionListener{
          @Override
          public void actionPerformed(ActionEvent e) {
               System.exit(0);

          }  
     }

     // quiz code
     private class Gamble implements ActionListener { 
          
          @Override
          public void actionPerformed(ActionEvent arg0) {
               // if first button push start time and change button
               if (firstTime == true){
                    firstTime = false;
                    gambleButton.setText("Enter Answer");

                    // start kill timer on background thread
                    MyThread myThread = new MyThread();
                    myThread.start();
                    myThread.startTimer();
                    return;
               }

               // default quiz answer to wrong
               resultType = false;

               // take user input and check to see if is a character
               try {
                    answer = yourInpuFieldt.getText();
               } catch (NumberFormatException ignore){
                    resultType = null;
               }

               // check to see input, rigged for first round to be alanine
               if (answer.equals(nextRight)) {
                    numberRight += 1;
                    resultType = true;
               } else {
                    numberWrong += 1;
               }

               // setup for next round of quiz, update quiz with full name, store next right answer
               Integer x = random.nextInt(20);
               nextFullName = FULL_NAMES[x];
               nextRight = SHORT_NAMES[x];

               // update with new money amount no matter what
               updateTextAmountField();
               updateTextResultsField(resultType, numberRight);
          }
     }   

     // update all text 
     private void updateTextAmountField() {
          aTextField.setText("Number Correct: " + numberRight);
          bTextField.setText("Number Incorrect: " + numberWrong);
          cTextField.setText("AA Name: " + nextFullName);
          validate();
     }

     // update results text 
     private void updateTextResultsField(Boolean resultType, float amount){
          try {
               if ( resultType == true ){
                    resultText.setText("Correct! Total Correct: " + numberRight);
               } else {
                    resultText.setText("Incorrect. Total Incorrect: " + numberWrong);
               }
          } catch (NullPointerException ignore) {
               resultText.setText("Invalid input");
          }
          validate();
     }

     // merge bet button and amount into panel
     private JPanel getBottomPanel() { 
          JPanel panel = new JPanel();
          panel.setLayout(new GridLayout(0,3));
          panel.add(gambleButton);
          panel.add(yourInpuFieldt);
          panel.add(cancelButton);
          return panel;
     }

     // merge text boxes in mid panel
     private JPanel getMiddlePanel() {
          JPanel panel = new JPanel();
          panel.setLayout(new GridLayout(0,3));
          panel.add(aTextField);
          panel.add(cTextField);
          panel.add(bTextField);
          return panel;
     }

     // interface
     public Lab6() {
          // Defualt stuff
          super("Lab6");
          setSize(800,600);
          setLocationRelativeTo(null);
          setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

          // set up bottom panel and menu
          getContentPane().add(getBottomPanel(), BorderLayout.SOUTH);

          // timer
          //getContentPane().add(dTextField, BorderLayout.NORTH);
          //updateTimerText(getRemainingTime());

          // look for quiz start
          gambleButton.addActionListener(new Gamble());
          cancelButton.addActionListener(new Cancel());

          // update text boxes with results
          getContentPane().add(getMiddlePanel(), BorderLayout.CENTER);
          getContentPane().add(getBottomPanel(), BorderLayout.SOUTH);

          setVisible(true);
     }


     public static void main(String[] args) throws InterruptedException {
          new Lab6();
     }

     
}



