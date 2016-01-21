#Author - Shalin Sitwala
#NUID: 001664333

;;;;;;;;;;;;;;;;;;;;;;;;;;;
 HW4: Lucene Introduction
;;;;;;;;;;;;;;;;;;;;;;;;;;;

================================================================

How to run:
- in cmd, run:
java -jar "..path-to-the-unzipped-folder\HW4\dist\HW4.jar"

OR

- Import the project, unzipped folder 'HW4' in NetBeans/Eclipse.
- Run the project / 'HW4.java'

================================================================

Files to submit:

Q: A sorted (by frequency) list of (term, term_freq pairs)
A: RankTermFreq.txt OR RankTermFreq.xls

Q: A plot of the resulting Zipfian curve
A: ZipPlot.pdf OR Rank-Term-Freq.xlsx

Q: Four lists (one per query) each containing at MOST 100 docIDs ranked by score
A: Q01.txt, Q02.txt, Q03.txt, Q04.txt

Q: A table comparing the total number of documents retrieved per query using Lucene’s scoring function vs. using your search engine (index with BM25) from the previous assignment
A: Comparison-HW3-HW4.pdf OR Comparison-HW3-HW4.xlsx

================================================================

Input to be given by user:
 - Full path where the Index will be created.
 - Full path of files to be added for indexing.
 - One or more query.

================================================================

Abstract Idea on how the program works:

- Program takes input of the Destination where the Index will be created.
- Old index files are deleted from that path (if any)
- Old corpus files which were processed (tags removed) are deleted.
- User enters path of files to be indexed.
- All HTML tags are removed.
- All unique terms and their frequency is printed & written in file; in decreasing order of Frequency.
- User enters query to be processed.
- Calculates score for each document and returns top 100 documents with scores.
- Writes that to files Q0x.txt
- All queries and their Total Hits are written in file: Query-Total-Hits.txt

================================================================

References:
Lucene Documentation: http://lucene.apache.org/core/documentation.html
Java Documentation: https://docs.oracle.com/javase/8/docs/api/index.html
Search Engines: Information Retrieval in Practice by Croft, Metzler and Strohman