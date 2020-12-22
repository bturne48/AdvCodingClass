

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

    // binomial coefficient
    // Returns value of Binomial  
    // Coefficient C(n, k) 
    

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

    public void parseBlast(){
        // read in blast results and illumina breakpoint
        System.out.println("UPDATE: START Reading in Blast Results");
        HashMap<Integer, ArrayList<String>> blastHash = TextFiles.readBlastResults( new File("/Users/bturne48/Documents/GitHub/AdvCodingClass/Final_Project/BLAST_Results/ST_1_16.blast"));
        System.out.println("UPDATE: DONE Reading in Blast Results\nUPDATE: START Read in Illumina Results");
        ArrayList<String> illumBPs = TextFiles.readIlluminaBPs(new File("/Users/bturne48/Documents/GitHub/AdvCodingClass/Final_Project/Input/clusteredVariants.txt"));
        System.out.println("UPDATE: DONE Read in Illumina Results");


        // loop through all illumina mutation calls 
        for (String ill: illumBPs) {
            
            // variables for comparison from illumina stuff
            String[] illSplit = ill.split("\t");
            String illChrom1 = illSplit[0];
            Integer illStartPos1 = Integer.parseInt(illSplit[1]);
            Integer illEndPos1 = Integer.parseInt(illSplit[2]);
            String illChrom2 = illSplit[3];
            Integer illStartPos2 = Integer.parseInt(illSplit[4]);
            Integer illEndPos2 = Integer.parseInt(illSplit[5]);

            // bin size used from readBlastResults()
            Integer blastBin1 = illStartPos1 - illStartPos1%30000;
            Integer blastBin2 = illStartPos2 - illStartPos2%30000;

            // empty list used to confirm both illumina bps, once both found go to next read
            ArrayList<String> confirmedBPs1 = new ArrayList<String>();
            ArrayList<String> confirmedBPs2 = new ArrayList<String>();

            // TODO: Multithread target?
            if ( blastHash.containsKey(blastBin1) ) {

                for ( String blastRes: blastHash.get(blastBin1) ) {
                    
                    String[] splitBlastRes = blastRes.split("\t");
                    String readName = splitBlastRes[0];
                    String readChrom = splitBlastRes[1];
                    Integer readStartPos = Integer.valueOf(splitBlastRes[2]);
                    Integer readEndPos = Integer.valueOf(splitBlastRes[3]);

                    // creates a list of all reads that verifies the first variant breakpoint
                    if ( illChrom1.equals(readChrom) && Math.min(readStartPos, readEndPos) < illStartPos1 && Math.max(readStartPos, readEndPos) > illEndPos1 && !confirmedBPs1.contains(readName)) {
                        confirmedBPs1.add(readName);
                    }
                }
            } 
            if ( blastHash.containsKey(blastBin2) ){

                for ( String blastRes: blastHash.get(blastBin2) ) {
                    
                    String[] splitBlastRes = blastRes.split("\t");
                    String readName = splitBlastRes[0];
                    String readChrom = splitBlastRes[1];
                    Integer readStartPos = Integer.valueOf(splitBlastRes[2]);
                    Integer readEndPos = Integer.valueOf(splitBlastRes[3]);

                    if ( illChrom2.equals(readChrom) && Math.min(readStartPos, readEndPos) < illStartPos2 && Math.max(readStartPos, readEndPos) > illEndPos2 && !confirmedBPs2.contains(readName)) {
                        confirmedBPs2.add(readName);
                    }
                }
            }

            // check the confirmedBP lists to see if both lists share the same read, this means 1 or more read confirmed both sides of the variant
            for ( String read: confirmedBPs1) {
                if ( confirmedBPs2.contains(read) ) System.out.println(ill); break;
            }
        }
    }

    public void makeSFS(File observedHaps, File sampledHaps, Integer resampleSize){

        ArrayList<String> obsvHaps = TextFiles.readIlluminaBPs(observedHaps);
        ArrayList<String> sampHaps = TextFiles.readIlluminaBPs(sampledHaps);

        // store the numerator/denominator of allele freq in hashmaps
        HashMap<String, Integer> numHaps = new HashMap<String, Integer>();
        HashMap<String, Integer> denHaps = new HashMap<String, Integer>();

        // list of allele freqs for SFS
        ArrayList<Float> alleleFreqList = new ArrayList<Float>();

        // create a list that stores result of resampling later
        ArrayList<Long> resampleList = new ArrayList<Long>();
        Long data[] = new Long[resampleSize+1];
        Arrays.fill(data, (long) 0);
        List<Long> list = Arrays.asList(data);
        resampleList.addAll(list);

        // calculate numerator for allele freq
        for (String mut: obsvHaps) {

            // subset the list so that you separate the breakpoints from the strain/haplotype info
            List<String> splitMut = Arrays.asList(mut.split("\t"));
            List<String> strains_haps = splitMut.subList(6, splitMut.size());
            List<String> mutBps = splitMut.subList(0, 6);

            // count the number of times you see inbred or hetero (OBSERVED haplotypes, numerator of allele freq)
            int numInbred = Collections.frequency(strains_haps, "Inbred");
            int numHetero = Collections.frequency(strains_haps, "hetero");
            int alleleFreqNumerator = numInbred + (2*numHetero);

            //results go to numerator hash map <MutBreakpoints, numeratorOfAlleleFreq>
            numHaps.put(String.join("\t", mutBps), alleleFreqNumerator);
            
        }

        // calculate numerator for allele freq
        for (String mut: sampHaps) {

            // subset the list so that you separate the breakpoints from the strain/haplotype info
            List<String> splitMut = Arrays.asList(mut.split("\t"));
            List<String> strains_haps = splitMut.subList(6, splitMut.size());
            List<String> mutBps = splitMut.subList(0, 6);

            // count the number of times you see inbred or hetero (OBSERVED haplotypes, numerator of allele freq)
            int denInbred = Collections.frequency(strains_haps, "Inbred");
            int denHetero = Collections.frequency(strains_haps, "hetero");
            int alleleFreqDenom = denInbred + (2*denHetero);

            //results go to numerator hash map <MutBreakpoints, numeratorOfAlleleFreq>
            denHaps.put(String.join("\t", mutBps), alleleFreqDenom);
            
        }
     
        // iterate over observed hash map and calculate allele freq
        for (String mut: numHaps.keySet()) {

            Integer numerator = numHaps.get(mut);
            Integer denominator = denHaps.get(mut);
            Float alleleFreq = (float) numerator/denominator;
            //alleleFreqList.add(alleleFreq);

            // resample using the hypergeometric distribution so we can project down to a small/consistent sample size for 
            if (denominator >= resampleSize) {
               for (Integer i=0; i<resampleSize+1; i++) {
                   resampleList.set(i, resampleList.get(i) + BinomialCoefficient.binomialCoeff(numerator, i) 
                                                                * BinomialCoefficient.binomialCoeff(denominator-numerator, resampleSize-i) 
                                                                                   / BinomialCoefficient.binomialCoeff(denominator, resampleSize));              
               }
            }
        }

        for (Long s: resampleList) System.out.println(s);

        // create an SFS by having java create and run an R script
        
        
        //if denominator >=maxno:
        //                      for i in range(0, maxno+1):
        //                           SFSVec[i] = SFSVec[i] + choose(numerator, i) * choose(denominator-numerator, maxno-i)/ (choose(denominator, maxno))
                    


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

        // part 2
        //jb.parseBlast();

        //part 3
        jb.makeSFS(new File("/Users/bturne48/Documents/GitHub/AdvCodingClass/Final_Project/Input/clusteredVariants.txt"), 
                   new File("/Users/bturne48/Documents/GitHub/AdvCodingClass/Final_Project/Input/sampledHaps.txt"),
                            15);
    }
}