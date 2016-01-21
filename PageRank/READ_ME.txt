#Author - Shalin Sitwala
#NUID: 001664333
#Date: 09/23/2015

;;;;;;;;;;;;;;;;;;;;;;;;;;
	Page Rank
;;;;;;;;;;;;;;;;;;;;;;;;;;

====================================================
Questions & their respective files having answers:

--> A list of the PageRank values you obtain for each of the six vertices after 1, 10, and 100 iterations of the PageRank algorithm.
Files: "ABCD_1_Iteration.txt", "ABCD_10_Iterations.txt", "ABCD_100_Iterations.txt" are having the Document Name and its PageRank after 1, 10 and 100 iterations of the PageRank algorithm respectively.

--> A list of the perplexity values you obtain in each round until convergence.
FILE: Perplexity_Till_Convergence.txt

--> A list of the document IDs of the top 50 pages as sorted by PageRank, together with their PageRank values
FILE: Top50PageRank.txt

--> A list of the document IDs of the top 50 pages by in-link count, together with their in-link counts:
FILE: Top50InLinks.txt

--> The proportion of pages with no in-links (sources), 
--> The proportion of pages with no out-links (sinks); and
--> The proportion of pages whose PageRank is less than their initial, uniform values.
FILE: Proportion_Pages.txt

--> Give an analysis of the PageRank results you obtain:
FILE: Examine_Top10_Pages.txt


====================================================

How to run:
- in cmd, run:
java -jar "..path-to-the-unzipped-folder\PageRank\dist\PageRank.jar"


OR

- Import the project, unzipped folder 'PageRank' in NetBeans.
- Run the project / 'PageRank.java'


====================================================

Input Files to the program: WT2G_Collection.txt or ABCD_Graph_Input_File.txt
Program will display the FileNames where the output has been stored for a particular task.

====================================================



References:
Java Documentation: https://docs.oracle.com/javase/8/docs/api/index.html
https://en.wikipedia.org/wiki/Perplexity
https://en.wikipedia.org/wiki/Entropy_(information_theory)
Discussed with Adib
http://stackoverflow.com/questions/8119366/sorting-hashmap-by-values