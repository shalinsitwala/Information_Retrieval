package pagerank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Math.abs;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static javafx.scene.input.KeyCode.K;

/**
 *
 * @author Shalin Sitwala
 */
public class PageRank {

    public static int totalPages;
    public static double TELEPORT_FACTOR = 0.85;
    public static int sourcelinks = 0;
    public static int count = 0;
    public static double oldPer = 0;
    public static HashMap<String, HashSet> inLinks = new HashMap<String, HashSet>();
    public static HashMap<String, Integer> inLinksWithSize = new HashMap<String, Integer>();
    public static HashMap<String, HashSet> outLinks = new HashMap<String, HashSet>();
    public static HashSet<String> allPages = new HashSet<String>();
    public static HashSet<String> sinkNodes = new HashSet<String>();
    public static HashMap<String, Double> PagesWithRanks = new HashMap<String, Double>();
    public static HashMap<String, Double> initialPR = new HashMap<String, Double>();

    public static void getTop50PageRank(HashMap<String, Double> pagesRanks) {
        List<Map.Entry<String, Double>> list
                = new LinkedList<Map.Entry<String, Double>>(pagesRanks.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return (o1.getValue()).compareTo(o2.getValue()) * (-1);
            }
        });

        try {
            File file = new File("./Top50PageRank.txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write("Document ID --> Page Rank");
            bw.newLine();
            bw.write("=====================================");
            bw.newLine();
            String DocIDRank;
            for (int i = 0; i < 50; i++) {
                DocIDRank = list.get(i).getKey() + " --> " + list.get(i).getValue();
                bw.write(DocIDRank);
                bw.newLine();
                //System.out.println(list.get(i));
            }
            System.out.println("Top 50 Page Ranks written in file Top50PageRank.txt");
            bw.close();

        } catch (Exception e) {
            System.out.println("Error handling the file for Top50 Page Rank.");
        }
    }

    public static void getTop50InLinks(HashMap<String, Integer> pagesRanks) {
        List<Map.Entry<String, Integer>> list
                = new LinkedList<Map.Entry<String, Integer>>(pagesRanks.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue()) * (-1);
            }
        });

        try {
            File file = new File("./Top50InLinks.txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write("Document ID --> Number of In Links");
            bw.newLine();
            bw.write("=====================================");
            bw.newLine();
            String DocIDInLink;
            for (int i = 0; i < 50; i++) {
                DocIDInLink = list.get(i).getKey() + " --> " + list.get(i).getValue();
                bw.write(DocIDInLink);
                bw.newLine();
            }
            System.out.println("Top 50 In Links written in file Top50InLinks.txt");
            bw.close();

        } catch (Exception e) {
            System.out.println("Error handling the file for Top50 In Link.");
        }
    }

    public static int getPagesLessThanInit(HashMap<String, Double> pagesRanks) {
        int count = 0;
        String page;
        double rank, initRank;
        for (Map.Entry<String, Double> entry : pagesRanks.entrySet()) {
            page = entry.getKey();
            rank = entry.getValue();
            initRank = initialPR.get(page);
            if (rank < initRank) {
                count++;
            }
        }

        return count;
    }

    public static double calcPerplexity(HashMap<String, Double> pagesRanks) {
        double H = 0;
        for (Map.Entry<String, Double> entry : pagesRanks.entrySet()) {
            double rank = (double) entry.getValue();
            H += rank * log(rank) / log(2);
        }
        H *= -1;

        double perplex = pow(2, H);
        return perplex;
    }

    public static boolean checkConvergence(HashMap<String, Double> pagesRanks) {

        double perplexity = calcPerplexity(pagesRanks);

        if (abs(oldPer - perplexity) < 1) {
            count++;

        } else {
            count = 0;
        }

        if (count == 4) {
            return false;
        }

        // Perplexity values needed until Convergence is obtained.
        System.out.println(perplexity);
        oldPer = perplexity;
        return true;

    }

    public static HashMap<String, Double> calcPageRank() {

        double rank = 0;
        double newRank = 0;
        double sinkPR;

        // Setting Initial value.
        for (String page : allPages) {
            rank = (double) 1 / (double) totalPages;
            PagesWithRanks.put(page, rank);
        }

        // Preserving Initial value as we need to check for pages
        // whose value has been decreased than this.
        initialPR = PagesWithRanks;

        
        oldPer = calcPerplexity(PagesWithRanks);
        
        /* The 'for' loop is used just for the question 1.
         * To check and test the code for the six-node example.
         * For the rest of the questions, we use do-while.
        */

        //for(int i =1; i<=100; i++)
        do {
            sinkPR = 0;
            for (String page : sinkNodes) {
                sinkPR += PagesWithRanks.get(page);
            }

            HashMap<String, Double> newPR = new HashMap<String, Double>();

            // page is p
            for (String page : allPages) {
                newRank = 0;
                newRank = (1 - TELEPORT_FACTOR) / (double) totalPages;
                newRank += (double) TELEPORT_FACTOR * ((double) sinkPR / (double) totalPages);
                HashSet in = inLinks.get(page);

                // entry is q
                for (Object en : in) {
                    String entry = (String) en;
                    newRank += (double) TELEPORT_FACTOR * ((double) PagesWithRanks.get(entry) / (double) outLinks.get(entry).size());
                }

                newPR.put(page, newRank);

            }

            PagesWithRanks = newPR;

        } while (checkConvergence(PagesWithRanks));
        return PagesWithRanks;

    }

    public static void WriteToFile(HashMap<String, Double> hmap) throws IOException {
        File outFile = new File("./OutFile.txt");
        if (!outFile.exists()) {
            outFile.createNewFile();
        }

        FileWriter fw = new FileWriter(outFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);

        String linkWithRank;
        for (Map.Entry<String, Double> entry : hmap.entrySet()) {

            linkWithRank = entry.getKey() + ": " + entry.getValue();
            System.out.println(linkWithRank);
            bw.write(linkWithRank);
            bw.newLine();

        }
        bw.close();

    }

    public static void main(String[] args) {

        //File file = new File("ABCD_Graph_Input_File.txt");
        File file = new File("WT2G_Collection.txt");
        String filePath = file.getAbsolutePath();

        String line = null;

        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);

            while ((line = br.readLine()) != null) {
                String[] pages = line.split(" ");
                //inLinks.put(pages[0],Arrays.copyOfRange(pages, 1, pages.length));             

                if (!inLinks.containsKey(pages[0])) {
                    inLinks.put(pages[0], new HashSet());

                }

                for (int i = 1; i <= pages.length - 1; i++) {
                    inLinks.get(pages[0]).add(pages[i]);

                }

                if (!outLinks.containsKey(pages[0])) {
                    outLinks.put(pages[0], new HashSet<String>());
                }

                for (int i = 1; i <= pages.length - 1; i++) {
                    if (outLinks.containsKey(pages[i])) {
                        outLinks.get(pages[i]).add(pages[0]);
                    } else {
                        HashSet<String> hset = new HashSet<String>();
                        hset.add(pages[0]);
                        outLinks.put(pages[i], hset);
                    }
                }
                totalPages++;

            }

            br.close();
            System.out.println("Total Pages: " + totalPages);

            for (String entry : outLinks.keySet()) {
                allPages.add(entry);
            }

            for (Map.Entry<String, HashSet> entry : outLinks.entrySet()) {
                if (entry.getValue().isEmpty()) {
                    sinkNodes.add(entry.getKey());
                }
            }

            System.out.println("Total Sink Nodes: " + sinkNodes.size());

            for (Map.Entry<String, HashSet> entry : inLinks.entrySet()) {
                if (entry.getValue().size() == 0) {
                    sourcelinks++;
                }

            }

            for (Map.Entry<String, HashSet> entry : inLinks.entrySet()) {
                inLinksWithSize.put(entry.getKey(), entry.getValue().size());
            }

            System.out.println("Total Source Links:" + sourcelinks);

            
            //WriteToFile(calcPageRank());
            calcPageRank();
            getTop50PageRank(PagesWithRanks);
            getTop50InLinks(inLinksWithSize);

            System.out.println("No. of Pages having PageRank less than their Initial (Uniform) Value: "
                    + getPagesLessThanInit(PagesWithRanks));

        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
