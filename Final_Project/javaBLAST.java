import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class javaBLAST {

     // pass in file name from command line
     public static List<String> argList = new ArrayList<String>();

     // factory stuff
     private String header;
     private String sequence;

     public List<String> runBlast(String blastType, String db, String query, String blastfmt) throws IOException {
          
          List<String> fastaList = new ArrayList<String>();

          // prompt java to run blast
          ProcessBuilder pb = new ProcessBuilder(blastType, "-db", db, "-query", query, "-outfmt", blastfmt)
                              .inheritIO()
                              .directory(new File("/usr/local/ncbi/blast/bin/"));
          pb.start();
          return fastaList;
     }

     public static void main(String[] args) throws Exception {
          javaBLAST jb = new javaBLAST();
          jb.runBlast("/usr/local/ncbi/blast/bin/blastn",
                    "/Users/bturne48/Documents/GitHub/AdvCodingClass/Final_Project/fasta.txt",
                    "/Users/bturne48/Documents/GitHub/AdvCodingClass/Final_Project/fasta.txt",
                    "6");
     }
}