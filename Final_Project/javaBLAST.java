import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;


public class javaBLAST {

     // pass in file name from command line
     public static List<String> argList = new ArrayList<String>();

     // what directory was this program run in
     public static String workingDirec = System.getProperty("user.dir")+"/";


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

     }

     public static void main(String[] args) throws Exception {
          javaBLAST jb = new javaBLAST();

          // input files
          String dyakRef = "/Users/bturne48/Documents/GitHub/AdvCodingClass/Final_Project/DyakRef/dyakRef.fasta";
          List<String> ccsFiles = new ArrayList<String>();

          // process cmd line args, check if they exist, all except last are fasta files for blast
          for(int i = 0; i < args.length-1; i++) {
               System.out.println(workingDirec+args[i]);
               ccsFiles.add(workingDirec+args[i]);
          }
          
          // run blast on all files, 1 file per thread
          jb.runBlast("/usr/local/ncbi/blast/bin/blastn",
                    dyakRef,
                    ccsFiles,
                    "7");
     }
}