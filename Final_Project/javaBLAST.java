import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ProcessBuilder;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class javaBLAST {

    public static ArrayList<File> fileNames = new ArrayList<File>();
    public static BlockingQueue<String> queue = new ArrayBlockingQueue<String>(1024);

    // pass in file name from command line
    public static List<String> argList = new ArrayList<String>();

    // what directory was this program run in
    public static String workingDirec = System.getProperty("user.dir")+"/";


    public static class ReaderThread implements Runnable{

        protected BlockingQueue<String> blockingQueue = null;
        private String inFileName;
    
        public ReaderThread(BlockingQueue<String> blockingQueue, String inFileName){
            this.inFileName = inFileName;
            this.blockingQueue = blockingQueue;     
        }
    
        @Override
        public void run() {
        BufferedReader br = null;
        try {
                br = new BufferedReader(new FileReader(new File(inFileName)));
                String buffer =null;
                while((buffer=br.readLine())!=null){
                    blockingQueue.put(buffer);
                }
                blockingQueue.put("EOF");  //When end of file has been reached
    
            } catch (FileNotFoundException e) {
    
                e.printStackTrace();
            } catch (IOException e) {
    
                e.printStackTrace();
            } catch(InterruptedException e){
    
            }finally{
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class WriterThread implements Runnable{

        protected BlockingQueue<String> blockingQueue = null;
      
        public WriterThread(BlockingQueue<String> blockingQueue){
          this.blockingQueue = blockingQueue;     
        }
      
        @Override
        public void run() {
             PrintWriter writer = null;
      
          try {
              writer = new PrintWriter(new File("outputFile.txt"));
      
              while(true){
                  String buffer = blockingQueue.take();
                  //Check whether end of file has been reached
                  if(buffer.equals("EOF")){ 
                      break;
                  }
                  writer.println(buffer);
              }               
      
      
          } catch (FileNotFoundException e) {
      
              e.printStackTrace();
          } catch(InterruptedException e){
      
          }finally{
              writer.close();
          } 
      
        }
   }

    public void runBlast(String blastType, String db, List<String> query, String blastfmt) throws IOException {
          
        //output loc
        File outDirec = new File(workingDirec+"BLAST_Results");

        outDirec.mkdir();

        // prompt java to run blast
        for (int i=0; i<query.size(); i++) {

            // make output file for each blast to write to ouput direc
            String[] splitFileName = query.get(i).split("/");
            String fileName = splitFileName[splitFileName.length-1];
            File outFileName = new File(workingDirec+"/BLAST_Results/"+fileName.substring(0,fileName.length()-6) + ".blast");

            //run blasts
            ProcessBuilder pb = new ProcessBuilder(blastType, "-db", db, "-query", query.get(i), "-outfmt", blastfmt)
                                .redirectOutput(Redirect.appendTo(outFileName))
                                //.inheritIO()
                                .directory(new File("/usr/local/ncbi/blast/bin/"));

        pb.start();
        }

        // return list of file names
        return;
    }

    

    public static void main(String[] args) throws Exception {

        javaBLAST jb = new javaBLAST();

        //which part of the file to run
        //TODO: make try catch
        Integer whichPart = Integer.parseInt(args[1]);
        
        // input files
        // TODO: make the ref genome cmd line if you say pt 1
        String dyakRef = "/Users/bturne48/Documents/GitHub/AdvCodingClass/Final_Project/DyakRef/dyakRef.fasta";
        List<String> ccsFiles = new ArrayList<String>();

        // process cmd line args, check if they exist, all except last are fasta files for blast, last one tells you which part to run 
        for(int i = 0; i < args.length-1; i++) {
            ccsFiles.add(workingDirec+args[i]);
        }


        // PART 1: if whichPart=1 run the blast, otherwise specify where the files will be
        if (whichPart==1) {
            
            jb.runBlast("/usr/local/ncbi/blast/bin/blastn", dyakRef, ccsFiles, "6");

        // if the tag is not the initial blast run
        } else {

            // look for the blast directory
            File f = new File(System.getProperty("user.dir") + "/BLAST_Results");
            File[] listOfFiles = f.listFiles();

            // if there are files in the BLAST_Results directory
            if (listOfFiles.length > 0) {
                for (int i = 0; i < listOfFiles.length; i++) {
                    fileNames.add(listOfFiles[i]);
                }
            } else {
                System.out.println("ERROR: No files found ");
            }
        }
            
        // PART 2: read in file multi threaded and parse ther results
        for (Integer i=0; i<fileNames.size(); i++) {
            System.out.println(fileNames.get(i));
            ReaderThread reader = new ReaderThread(queue, "./BLAST_Results/ST_1_16.blast");
            WriterThread writer = new WriterThread(queue);
            new Thread(reader).start();
            new Thread(writer).start();
        }
    }
}

