

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.lang.ProcessBuilder.*;


public class javaBLAST {

    public static ArrayList<File> fileNames = new ArrayList<File>();
    public static BlockingQueue<String> queue = new ArrayBlockingQueue<String>(1024);

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

        // return list of file names
        return;
    }

    // public class parseBLAST implements Runnable {

    //     private final int threadID; 

    //     public parseBLAST(int threadID) {

    //         this.threadID = threadID;
    //     }

    //     public void run() {
            


    //     }

    // }
    


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
            
        
        // PART 2: read in files parse ther results


        // read in blast results and illumina breakpoint
        System.out.println("UPDATE: START Reading in Blast Results");
        HashMap<String, ArrayList<String>> blastHash = TextFiles.readBlastResults( new File("/Users/bturne48/Documents/GitHub/AdvCodingClass/Final_Project/BLAST_Results/ST_1_16.blast"));
        System.out.println("UPDATE: DONE Reading in Blast Results\nUPDATE: START Read in Illumina Results");
        ArrayList<String> illumBPs = TextFiles.readIlluminaBPs(new File("/Users/bturne48/Documents/GitHub/AdvCodingClass/Final_Project/Input/clusteredVariants.txt"));
        System.out.println("UPDATE: DONE Read in Illumina Results");
        ArrayList<String> confirmedReads = new ArrayList<String>();

        //loop over the hash map and see if the coords match any of the illum bps
        for (String ill: illumBPs) {  
            
            // variables for comparison from illumina stuff
            String[] illSplit = ill.split("\t");
            String illChrom1 = illSplit[0];
            Integer illStartPos1 = Integer.parseInt(illSplit[1]);
            Integer illEndPos1 = Integer.parseInt(illSplit[2]);
            String illChrom2 = illSplit[3];
            Integer illStartPos2 = Integer.parseInt(illSplit[4]);
            Integer illEndPos2 = Integer.parseInt(illSplit[5]);

            // confirm that one pacbio read confirms both sides of an illumina mutation
            ArrayList<String> confirmedBPs = new ArrayList<String>();
        
            for (Map.Entry<String, ArrayList<String>> entry : blastHash.entrySet()) {

                // if both bps verified, illumina mutation is confirmed, go to the next one
                if ( confirmedBPs.size() == 2 ) {
                    confirmedReads.add(ill);
                    System.out.println(ill);
                    break;
                } 

                // looping over pac bio blast coords and mapping them to see if they fall in bounds of illumina mutations
                for (String pac: entry.getValue()) {
                    
                    // if both bps verified, read is confirmed, go to the next one
                    if ( confirmedBPs.size() == 2 ) {
                        confirmedReads.add(ill);
                        break;
                    } 
                    
                    // variables for comparison from the pacbio reads
                    String[] pacSplit = pac.split("\t");
                    String pacChrom = pacSplit[0];
                    Integer pacStartPos = Integer.parseInt(pacSplit[1]);
                    Integer pacEndPos = Integer.parseInt(pacSplit[2]);

                    // comparison, if the pacbio read falls within the bounds of the illumina breakpoints
                    if ( illChrom1.equals(pacChrom) && Math.min(pacStartPos, pacEndPos) > illStartPos1 && Math.max(pacStartPos, pacEndPos) < illEndPos1 ) {
                        
                        // if this breakpoint has found a match with a pacbio blast result already move on
                        if ( confirmedBPs.contains(illChrom1+"\t"+illStartPos1+"\t"+illEndPos1) ) {
                            break;
                        } else {
                            System.out.println(illChrom1+"\t"+illStartPos1+"\t"+illEndPos1);
                            confirmedBPs.add(illChrom1+"\t"+illStartPos1+"\t"+illEndPos1);
                        }

                    } else if ( illChrom2.equals(pacChrom) && Math.min(pacStartPos, pacEndPos) > illStartPos2 && Math.max(pacStartPos, pacEndPos) < illEndPos2 ) {
                        
                        // if this breakpoint has found a match with a pacbio blast result
                        if ( confirmedBPs.contains(illChrom2+"\t"+illStartPos2+"\t"+illEndPos2) ) {
                            break;
                        } else {
                            confirmedBPs.add(illChrom2+"\t"+illStartPos2+"\t"+illEndPos2);
                            System.out.println(illChrom2+"\t"+illStartPos2+"\t"+illEndPos2);
                        }
                    }
                }
            }
        }
    }
}
