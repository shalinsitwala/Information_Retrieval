#Author - Shalin Sitwala
#NUID: 001664333

;;;;;;;;;;;;;;;;;;;;;;;;;;
	   BM25
;;;;;;;;;;;;;;;;;;;;;;;;;;

================================================================
results.txt -> Documents sorted by their BM25 
               score for each query.
================================================================

How to run:
- in cmd, run:
java -jar "..path-to-the-unzipped-folder\BM25\dist\BM25.jar"

OR

- Import the project, unzipped folder 'BM25' in NetBeans.
- Run the project / 'BM25.java'

================================================================

Functions and their description: (Explains how implementation is done)

computeBM25 - The function which calculates the BM25 value for the given document input values.

calcAvdl - Calculates the average document length which is required for calculating BM25 score.

indexer - Takes input of the corpus text file, makes an index in HashMap and writes the index as text to the given output file name.

checkForDocInVector - Checks if the given document is present in a vector which contains HashMaps having Documents as keys.

indexFromFile - Reads from the index txt file and makes a HashMap of index which shall be used in processing queries.

processQuery - Reads from the input query file and processes each query by computing the BM25 score values for each document. Sorts the result as per the max number of outputs required.

isNumeric - Checks if given string has all numbers only.

isAlpha - Checks if given string has all alphabets only.

getLengthOfThisDoc - Gets length of the given document from the corpus.

================================================================

Input Files to the program:
queries.txt - List of queries.
tccorpus.txt - Corpus

Output Files:
indexout.txt - Resulted Index
results - Documents sorted by BM25 score for each query.

================================================================

References:
Java Documentation: https://docs.oracle.com/javase/8/docs/api/index.html
Search Engines: Information Retrieval in Practice by Croft, Metzler and Strohman