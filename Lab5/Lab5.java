import java.awt.BorderLayout;
import javax.swing.*;
import java.util.Formatter;
import java.util.Random;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;


import java.awt.*;

public class Lab5 extends JFrame implements ActionListener {
    
     private static final long serialVersionUID = 0000000000000001; 
     // money
     private float startAmount = 20;
     private float initBet = 0;
     // interface stuff
     private JTextField aTextField = new JTextField("You have $" + startAmount);
     private JTextField yourInpuFieldt = new JTextField();
     private JButton gambleButton = new JButton("Enter bet in middle box then push");
     private JTextField resultText = new JTextField();     
     //menu + save
     JMenuBar menuBar = new JMenuBar();
     JMenuItem saveItem, saveAllItem;
     private String filename = null;
     private JTextArea textArea = new JTextArea();
     //misc stuff
     private Random random = new Random();
     private Boolean resultType = false;

     // gambling code
     private class Gamble implements ActionListener {
          
          public void actionPerformed(ActionEvent arg0) {
               
               // default to losing
               resultType = false;

               // take user input and check to see if is a float
               try {
                    initBet = Float.parseFloat(yourInpuFieldt.getText());
               } catch (NumberFormatException ignore){
                    resultType = null;
               }

               // 90% chance that you won't win
               float x = random.nextFloat();
               if ( x < 0.90 ){
                    startAmount = startAmount - initBet;
               } else {
                    startAmount = startAmount + initBet*5;
                    resultType = true;
               }

               // if you go below a penny you cant play anymore
               if ( startAmount < 0.01 ){
                    startAmount = 0;
               }

               // update with new money amount no matter what
               updateTextAmountField();
               updateTextResultsField(resultType, initBet);
               }
          }

     // code for saving results
     public void actionPerformed(ActionEvent e) {

          if (e.getSource() == saveItem)
            saveFile(null);

        }

     // saving
     private void saveFile(String name) {
          if (name == null) {  // get filename from user
               JFileChooser fc = new JFileChooser();
               if (fc.showSaveDialog(null) != JFileChooser.CANCEL_OPTION)
                    name = fc.getSelectedFile().getAbsolutePath();
          }
          if (name != null) {  // else user cancelled
               try {
                    Formatter out = new Formatter(new File(name));  // might fail
                    filename = name;
                    out.format("%s", textArea.getText());
                    out.close();
                    JOptionPane.showMessageDialog(null, "Saved to " + filename,
                         "Save File", JOptionPane.PLAIN_MESSAGE);
               }
               catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(null, "Cannot write to file: " + name,
                         "Error", JOptionPane.ERROR_MESSAGE);
               }
          }

          //write to file
          try {
               FileWriter myWriter = new FileWriter(filename);
               myWriter.write(String.valueOf(startAmount));
               myWriter.close();
               System.out.println("Successfully wrote to the file.");
          } catch (IOException e) {
               System.out.println("An error occurred.");
               e.printStackTrace();
          }
     }

     // update money text 
     private void updateTextAmountField(){
          aTextField.setText("You have $" + startAmount);
          validate();
     }

     // update results text 
     private void updateTextResultsField(Boolean resultType, float amount){
          try {
               if ( resultType == true ){
                    resultText.setText("You won: $" + amount*5);
               } else {
                    resultText.setText("You lost: $" + amount);
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
          panel.add(resultText);

          return panel;
     }

     // save menu
     private JMenuBar setJMenuBar(){

          // Menu
          JMenu fileMenu = new JMenu("File");

          // Menu Item (Drop down menus)
          saveItem = new JMenuItem("Save");

          // Adding menu items to menu
          fileMenu.add(saveItem);

          // adding menu to menu bar
          menuBar.add(fileMenu);

          return menuBar;
     }

     // interface
     public Lab5(){
          // Defualt stuff
          super("Lab5");
          setSize(800,600);
          setLocationRelativeTo(null);
          
          setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          // set up bottom panel and menu
          getContentPane().add(getBottomPanel(), BorderLayout.SOUTH);
          getContentPane().add(setJMenuBar(), BorderLayout.NORTH);
          // look for gamble and save code
          gambleButton.addActionListener(new Gamble());
          saveItem.addActionListener(this);
          // update text boxes with results
          getContentPane().add(aTextField, BorderLayout.CENTER);
          getContentPane().add(getBottomPanel(), BorderLayout.SOUTH);

          setVisible(true);
     }

     public static void main(String[] args) {
          new Lab5();
     }
     
}