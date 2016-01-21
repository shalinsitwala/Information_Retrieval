#Author - Shalin Sitwala
NU ID: 001664333
#Date: 09/23/2015


Web Crawler.

- Files having the pages(links) crawled are saved with 2 different executions of the Crawler.
	1) When the crawler is run with no keyphrase.
	File Name: Visited_without_Keyphrase.txt

	2) When the cralwer is run with the keyphrase 'concordance'.
	File Name: Visited_with_Keyphrase.txt


How to run:
Compile the src file : Crawler.java
- javac Crawler.java
- java Crawler

Program will ask for user's input if he/she wants to run the crawler with or without the keyphrase.
Accordingly, the program will make output files and show the location of them as well when it is finished.

System also shows the pages it is crawling on the console.


Total 1000 unique URLs were crawled with no keyphrase up to the depth of 5
and when using the keyphrase 'concordance', system provided ___ distinct URLs.
Proportion: 


References:
Documentation of Jsoup library: https://jsoup.org/cookbook/
Java Documentation: https://docs.oracle.com/javase/8/docs/api/index.html
Dictionary & Hashmap usage: http://stackoverflow.com/questions/30521620/hashmap-contains-and-get-methods-should-not-be-used-together

