Brandon Turner Final Project

This program is run on the cmd line. It is meant to read in a large CCS read file from Pacbio data and 1) run blast 2) parse blast 3) calculate allele freq.

Notes
________
Part 1: RUN A BLAST ON CCS FILES

        RUN WITH: "javaBlast.java 1 fooCCS1.fasta fooCCS2.fasta ..."
        1) Creates a process builder that will run BLAST and will continue running on a a thread even once the main thread is closed and program is done
        2) On line 42 you need to specify the location of your local BLAST if you want to run it
        3) Creates the results for part 1 that are used in part 2 and stores them in a directory called BLAST_Results
        
Part 2: PARSE BLAST RESULTS AND COMPARE TO SHORT READ DATA

        RUN WITH: "javaBlast.java 2"
        1) Parses the output from part1 into a hash map, and reads in illumina breakpoints so you can check which breakpoints are validated with BLAST
        2) Outputs a list of variants to the std out, can be piped to a file which is how part 2 results were created

Part 3: CREATE ALLELE FREQS

        RUN WITH: "javaBlast.java 3"
        1) Using the list of confirmed variants calculate the allele freq for all confirmed vars. This could be fed to R to make a density plot
        2) Used to make part 3 results
