package bm25;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import static java.lang.Math.log;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Shalin
 */
public class BM25 {

    public static HashMap<String, Vector> index = new HashMap<String, Vector>();
    public static HashMap<Integer, Integer> docLength = new HashMap<Integer, Integer>();
    public static HashMap<String, Vector> indexFile = new HashMap<String, Vector>();
    public static String[] tokens;
    public static String[] queryWords;
    public static Vector<String> tokensOnlyString = new Vector<String>();
    public static int totalDocsInCorpus = 0;
    public static int doc; // To keep track of which document are we indexing presently.
    public static double avdl;

    public static double computeBM25(int n, int N, int dl, double avdl, int f, int qf) {

        double k1 = 1.2;
        double b = 0.75;
        double k2 = 100;
        double r = 0;
        double R = 0;

        double c = (double) dl / (double) avdl;
        double K = k1 * ((1 - b) + b * c);
        double p1 = log(((r + 0.5) / (R - r + 0.5)) / ((n - r + 0.5) / (N - n - R + r + 0.5)));

        double p2 = (((k1 + 1) * f) / (K + f)) * (((k2 + 1) * qf) / (k2 + qf));

        return p1 * p2;
    }

    public static double calcAvdl(HashMap<Integer, Integer> docLn) {

        int totalDocs = docLn.size();
        int totalLength = 0;
        for (Map.Entry<Integer, Integer> entry : docLn.entrySet()) {
            totalLength = totalLength + entry.getValue();
        }
        double avdl = (double) totalLength / (double) totalDocs;

        return avdl;
    }

    public static void indexer(String inFile, String outFileName) {

        // HashMap for (docid, tf) = (string, int)
        // word -> (docid, tf), (docid, tf) ...
        File file = new File(inFile);
        String filePath = file.getAbsolutePath();

        String line = null;

        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);

            int countInDoc = 0;

            while ((line = br.readLine()) != null) {

                tokens = line.split(" ");

                for (int i = 0; i < tokens.length; i++) {

                    if (tokens[i].equals("#")) {

                        if (doc != 0) {
                            // bcoz for first initial values are 0,0 which should not be added to map
                            docLength.put(doc, countInDoc);
                        }
                        doc = Integer.parseInt(tokens[i + 1]);
                        totalDocsInCorpus++;

                        countInDoc = 0;
                    } else if (!(tokens[i].equals("#") || isNumeric(tokens[i]))) {
                        // under this means it is a proper word and not a hash or number
                        countInDoc++;
                        if (!index.containsKey(tokens[i])) {
                            // word encountered for first time
                            // then add to (docid, tf)

                            HashMap<Integer, Integer> docIdTf = new HashMap<Integer, Integer>();
                            Vector<HashMap> wordDocsTf = new Vector<HashMap>();
                            docIdTf.put(doc, 1);
                            wordDocsTf.add(docIdTf);
                            index.put(tokens[i], wordDocsTf);

                        } // if it contains the word already.
                        else {
                            // get its Vector, then get its HashMap and do stuff
                            HashMap<Integer, Integer> docIdTf = new HashMap<Integer, Integer>();
                            Vector<HashMap> wordDocsTf = new Vector<HashMap>();
                            wordDocsTf = index.get(tokens[i]);

                            if (checkForDocInVector(doc, wordDocsTf)) {
                                // increment for that doc
                                for (int j = 0; j < wordDocsTf.size(); j++) {
                                    docIdTf = wordDocsTf.get(j);
                                    if (docIdTf.containsKey(doc)) {
                                        docIdTf.replace(doc, docIdTf.get(doc) + 1); // increasing count for that doc.
                                    }
                                }

                            } else {

                                // add new (doc, 1) to Vector
                                HashMap<Integer, Integer> docIdTf1 = new HashMap<Integer, Integer>();
                                docIdTf1.put(doc, 1);
                                wordDocsTf.add(docIdTf1);
                            }

                        }

                    }

                } // New Token now

            } // Line finished
            docLength.put(doc, countInDoc);
            // When all lines finished, putting the last doc's length in the Map.

            br.close();

        } catch (Exception e) {
            System.out.println("Error in indexer." + e);
        }

        // Write index to file now.
        try {
            File fil = new File("./" + outFileName);
            if (!fil.exists()) {
                fil.createNewFile();
            }

            FileWriter fw = new FileWriter(fil.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            //bw.newLine();
            String docWithVector;
            for (Map.Entry<String, Vector> entry : index.entrySet()) {
                String word = entry.getKey();
                bw.write(word);
                bw.write("||");
                Vector<HashMap> v = entry.getValue();

                for (HashMap<Integer, Integer> h : v) {
                    for (Map.Entry<Integer, Integer> ent : h.entrySet()) {
                        String merge = "::" + "(" + ent.getKey() + "," + ent.getValue() + ")";
                        bw.write(merge);
                    }
                }
                bw.newLine();
                //bw.write(" ");

            }
            bw.close();
            System.out.println("Total Docs: " + totalDocsInCorpus);

        } catch (Exception e) {
            System.out.println("Error while writing to file" + e);
        }

    }

    public static Boolean checkForDocInVector(int d, Vector<HashMap> v) {

        for (HashMap<Integer, Integer> hm : v) {
            if (hm.containsKey(d)) {
                return true;
            }

        }
        return false;
    }

    public static void main(String[] args) {

        indexer("tccorpus.txt", "indexout.txt");
        processQuery("indexout.txt", "queries.txt", 100);
    }

    public static HashMap indexFromFile(String indexfile) {
        try {
            File file = new File(indexfile);
            String filePath = file.getAbsolutePath();
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            int doc, tf;
            String line = null;
            String[] numbers;

            while ((line = br.readLine()) != null) {
                Vector<HashMap> v = new Vector<HashMap>();
                tokens = line.split("\\|\\|");

                if (isAlpha(tokens[0])) {
                    if (!indexFile.containsKey(tokens[0])) {
                        // does not contain in the hashmap.
                        String[] token = tokens[1].split("::");
                        for (int i = 1; i < token.length; i++) {
                            HashMap<Integer, Integer> h = new HashMap<Integer, Integer>();
                            token[i] = token[i].substring(1);
                            token[i] = token[i].substring(0, token[i].length() - 1);
                            numbers = token[i].split(",");
                            doc = Integer.parseInt(numbers[0]);
                            tf = Integer.parseInt(numbers[1]);
                            h.put(doc, tf);
                            v.add(h);
                        }
                        indexFile.put(tokens[0], v);
                    }

                }

            }

        } catch (Exception e) {
            System.out.println("Error in reading from indexfile." + e);
        }
        return indexFile;
    }

    public static void processQuery(String indexfile, String queryfile, int maxResults) {
        avdl = calcAvdl(docLength);
        indexFile = indexFromFile(indexfile);
        Vector<HashMap> vec = new Vector<HashMap>();

        try {

            File file = new File(queryfile);
            String filePath = file.getAbsolutePath();
            FileReader fr = new FileReader(filePath);
            BufferedReader queryReader = new BufferedReader(fr);

            File outfile = new File("./results.txt");
            if (!outfile.exists()) {
                outfile.createNewFile();
            }

            FileWriter fw = new FileWriter(outfile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            String line = null;
            int lineNumber = 0;
            while ((line = queryReader.readLine()) != null) {
                lineNumber++;
                HashMap<Integer, Double> docScore = new HashMap<Integer, Double>();

                queryWords = line.split(" "); // will have one line's words

                for (int i = 0; i < queryWords.length; i++) { // this for is for 1 query bcoz its for 1 line.

                    for (Map.Entry<String, Vector> entry : index.entrySet()) {
                        String word = entry.getKey();
                        if (word.equals(queryWords[i])) {
                            vec = entry.getValue();
                        }
                    }
                    // vec has the vector<Hashmap> now for that word
                    for (HashMap<Integer, Integer> h : vec) {
                        for (Map.Entry<Integer, Integer> ent : h.entrySet()) {
                            int doc = ent.getKey();
                            int length = getLengthOfThisDoc(doc, docLength);

                            if (!docScore.containsKey(doc)) {
                                // does not contain the score, then add
                                docScore.put(doc, computeBM25(vec.size(), totalDocsInCorpus, length, avdl, ent.getValue(), 1));

                            } else {
                                // if doc is already there for this query.
                                // then add new score to existing score
                                for (Map.Entry<Integer, Double> e : docScore.entrySet()) {
                                    if (doc == e.getKey()) {
                                        double score;
                                        score = e.getValue() + computeBM25(vec.size(), totalDocsInCorpus, length, avdl, ent.getValue(), 1);
                                        docScore.replace(doc, score);
                                    }
                                }

                            }

                        }
                    }

                }

                // sort and filter top
                List<Map.Entry<Integer, Double>> list
                        = new LinkedList<Map.Entry<Integer, Double>>(docScore.entrySet());
                Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
                    public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                        return (o1.getValue()).compareTo(o2.getValue()) * (-1);
                    }
                });
                System.out.println("\nTotal hits: " + list.size());
                for (int i = 0; i < maxResults; i++) {
                    System.out.println(lineNumber + " Q0 " + list.get(i).getKey() + " " + (i + 1) + " " + list.get(i).getValue() + " ShalinSitwala");
                }

                for (int i = 0; i < maxResults; i++) {
                    bw.write(lineNumber + " Q0 " + list.get(i).getKey() + " " + (i + 1) + " " + list.get(i).getValue() + " ShalinSitwala");
                    bw.newLine();
                }

            }
            bw.close();
        } catch (Exception e) {
            System.out.println("Error while processing query files. " + e);

        }
    }

    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAlpha(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }

        return true;
    }

    public static int getLengthOfThisDoc(int doc, HashMap<Integer, Integer> h) {

        for (Map.Entry<Integer, Integer> ent : h.entrySet()) {
            if (doc == ent.getKey()) {
                return ent.getValue();
            }
        }

        return 0; // if not found
    }
}
