#Author - Shalin Sitwala
#NUID: 001664333

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	HW5: Evaluate retrieval effectiveness
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

====================================================================================================
Evaluating the search engine provided in HW3.
====================================================================================================

results-q12.txt / results-q12.xlsx 
results-q13.txt / results-q13.xlsx 
results-q19.txt / results-q19.xlsx -> 
   Document-IDs in each file are for the respective query; sorted by their BM25 score along with values like:
   Relevance, Precision, Recall, Normalized Discounted Cumulative Gain (NDCG).

Map_P@K.txt -> Mean Average Precision (MAP) & P@K values.

====================================================================================================

How to run:
- in cmd, run:
java -jar "..path-to-the-unzipped-folder\HW5\dist\HW5.jar"

OR

- Import the project, unzipped folder 'HW5' in NetBeans.
- Run the project / 'HW5.java'

====================================================================================================

Functions and their description: (Explains how implementation is done)

getQueryDocRel - Gets the document IDs which are relevant to each query from the given input file.

getTotalRelevantDocsForQuery - Returns the number of documents which are Relevant for the given query-id.

calcAverageOfPrecisionList - Returns the Average Precision of all the documents' values for any particular given query.

checkForRelevance - Checks if the given document-id is relevant to the given query-id.

processQuery - Reads from the input query file and processes each query by computing the BM25 score values for each document. 
	       Sorts the result as per the max number of outputs required.
	       Calculates the Precision, Recall, NDCG, MAP & P@K values for each query and document.
	       Result is printed in results.txt

getLengthOfThisDoc - Gets length of the given document from the corpus.

computeBM25 - The function which calculates the BM25 value for the given document input values.

calcAvdl - Calculates the average document length which is required for calculating BM25 score.

indexer - Takes input of the corpus text file, makes an index in HashMap and writes the index as text to the given output file name.

checkForDocInVector - Checks if the given document is present in a vector which contains HashMaps having Documents as keys.

indexFromFile - Reads from the index txt file and makes a HashMap of index which shall be used in processing queries.

isNumeric - Checks if given string has all numbers only.

isAlpha - Checks if given string has all alphabets only.

====================================================================================================

Input Files to the program:
queries.txt - List of queries.
cacm.rel - Document-Query Relevance details.
tccorpus.txt - Corpus

Output Files:
indexout.txt - Resulted Index.
results-q12.txt / results-q12.xlsx - Documents sorted by BM25 score for query-ID 12 along with Precision, Recall, NDCG.
results-q13.txt / results-q13.xlsx - Documents sorted by BM25 score for query-ID 13 along with Precision, Recall, NDCG.
results-q19.txt / results-q19.xlsx - Documents sorted by BM25 score for query-ID 19 along with Precision, Recall, NDCG.
Map_P@K.txt - Mean Average Precision (MAP) & P@K values.

====================================================================================================

References:
Java Documentation: https://docs.oracle.com/javase/8/docs/api/index.html
Search Engines: Information Retrieval in Practice by Croft, Metzler and Strohman