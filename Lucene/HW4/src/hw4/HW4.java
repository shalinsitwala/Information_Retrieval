package hw4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

/**
 * To create Apache Lucene index in a folder and add files into this index based
 * on the input of the user. Build a list of Unique Term & Frequency pairs over
 * the entire collection.
 */
public class HW4 {

    private static Analyzer sAnalyzer = new SimpleAnalyzer(Version.LUCENE_47);
    private static HashMap<String, Long> teFreq = new HashMap<String, Long>();
    private static HashMap<String, Integer> queryHits = new HashMap<String, Integer>();
    private IndexWriter writer;
    private ArrayList<File> queue = new ArrayList<File>();
    private static int queryNumber;

    public static void main(String[] args) throws IOException, InterruptedException {
        queryNumber = 0;
        System.out
                .println("Enter the FULL path where the index will be created: (e.g. /Usr/index or c:\\temp\\index)");

        String indexLocation = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String s = br.readLine();

        File indexDir = new File(s);
        String filesInDir[];

        // if the index directory given by user already has index files, then remove them.
        if (indexDir.isDirectory()) {
            filesInDir = indexDir.list();
            if (filesInDir.length > 0) {
                // if there are files in the folder
                System.out.println("--------------\nClearing old index files.");
                for (int i = 0; i < filesInDir.length; i++) {
                    File fileInDir = new File(indexDir, filesInDir[i]);
                    fileInDir.delete();
                }
            }
        }

        File newCorpusDir = new File("NewCorpus");
        //System.out.println(newCorpusDir.getAbsolutePath());
        String filesDir[];

        // remove existing processed HTML files from which tags have been removed.
        if (newCorpusDir.isDirectory()) {
            filesInDir = newCorpusDir.list();
            if (filesInDir.length > 0) {
                System.out.println("--------------\nClearing existing processed files of Corpus.");
                for (int i = 0; i < filesInDir.length; i++) {
                    File fileInDir = new File(newCorpusDir, filesInDir[i]);
                    fileInDir.delete();
                }
            }
        }

        HW4 indexer = null;
        try {
            indexLocation = s;
            indexer = new HW4(s);
        } catch (Exception ex) {
            System.out.println("Cannot create index..." + ex.getMessage());
            System.exit(-1);
        }

        // ===================================================
        // read input from user until he enters q for quit
        // ===================================================
        while (!s.equalsIgnoreCase("q")) {
            try {
                System.out
                        .println("\nEnter the FULL path to add into the index (q=quit): (e.g. /home/mydir/docs or c:\\Users\\mydir\\docs)");
                System.out
                        .println("[Acceptable file types: .xml, .html, .html, .txt]");
                s = br.readLine();
                if (s.equalsIgnoreCase("q")) {
                    break;
                }

                // check if it is a folder
                // if it is a folder, then get all files in it
                // remove tags from it.
                // pass new location on below indexer
                System.out.println("--------------\nRemoving HTML Tags from Corpus files (if any)");
                File file = new File(s);
                if (file.isDirectory()) {
                    File[] listOfFiles = file.listFiles();
                    for (int i = 0; i < listOfFiles.length; i++) {
                        removeTags(listOfFiles[i]);

                    }
                    s = "./NewCorpus";
                } else if (file.isFile()) {
                    s = removeTags(file);
                }
                System.out.println("--------------\nHTML tags removed.");

                // try to add file into the index
                indexer.indexFileOrDirectory(s);
            } catch (Exception e) {
                System.out.println("Error indexing " + s + " : "
                        + e.getMessage());
            }
        }

        // ===================================================
        // after adding, we always have to call the
        // closeIndex, otherwise the index is not created
        // ===================================================
        indexer.closeIndex();

        // =========================================================
        // Now search
        // =========================================================
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(
                indexLocation)));

        System.out.println("--------------\nGetting all terms and their frequency.");
        TermsEnum te = MultiFields.getFields(reader).terms("contents").iterator(null);
        BytesRef by;
        String byString;

        while ((by = te.next()) != null) {

            byString = by.utf8ToString();
            Term termInstance = new Term("contents", by);
            long termFreq = reader.totalTermFreq(termInstance);
            //System.out.println(byString + "  " + termFreq);
            teFreq.put(byString, termFreq);
        }

        System.out.println("Unique words are: " + teFreq.size());
        System.out.println("--------------\nSorting & Printing now:");
        sortMap(teFreq);

        IndexSearcher searcher = new IndexSearcher(reader);

        s = "";
        while (!s.equalsIgnoreCase("q")) {
            try {
                System.out.println("Enter the search query (q=quit):");
                s = br.readLine();
                if (s.equalsIgnoreCase("q")) {
                    break;
                }
                queryNumber++;
                Query q = new QueryParser(Version.LUCENE_47, "contents",
                        sAnalyzer).parse(s);
                TopScoreDocCollector collector = TopScoreDocCollector.create(100, true);
                searcher.search(q, collector);
                ScoreDoc[] hits = collector.topDocs().scoreDocs;

                // 4. display results & write to file
                int totalHits = collector.getTotalHits();
                
                System.out.println("Total Hits: " + totalHits);
                
                // maintaining query and it's total hits.
                queryHits.put(s, totalHits);

                File queryHitFile = new File(".\\Q0" + queryNumber + ".txt");
                if(!queryHitFile.exists()){
                    queryHitFile.createNewFile();
                }
                
                FileWriter fw1 = new FileWriter(queryHitFile.getAbsolutePath());
                BufferedWriter bw1 = new BufferedWriter(fw1);
                bw1.write("Query: " + s);
                bw1.newLine();
                bw1.write("Total Hits: " + totalHits);
                bw1.newLine();
                
                for (int i = 0; i < hits.length; ++i) {
                    int docId = hits[i].doc;
                    Document d = searcher.doc(docId);
                    String path = d.get("path");
                    path = getDocumentFileOnly(path);
                    String docScore = (i + 1) + ". " + path + " Score=" + hits[i].score;
                    System.out.println(docScore);
                    bw1.write(docScore);
                    bw1.newLine();
                }

                bw1.close();
                // 5. term stats --> watch out for which "version" of the term
                // must be checked here instead!
                Term termInstance = new Term("contents", s);
                long termFreq = reader.totalTermFreq(termInstance);
                long docCount = reader.docFreq(termInstance);
                System.out.println(s + " Term Frequency " + termFreq
                        + " - Document Frequency " + docCount);

            } catch (Exception e) {
                System.out.println("Error searching " + s + " : "
                        + e.getMessage());
                break;
            }

        }
        
        File queryHitsFile = new File(".\\Query-Total-Hits.txt");
        if(!queryHitsFile.exists()){
            queryHitsFile.createNewFile();
        }
        
        FileWriter qhfw = new FileWriter(queryHitsFile.getAbsolutePath());
        BufferedWriter qhbw = new BufferedWriter(qhfw);
        
        
        // print query & total hits
        System.out.println("--------------\nPrinting Query & it's Total Hits:");
        for (Entry<String, Integer> entry : queryHits.entrySet()) {
            String line = "Query: \"" + entry.getKey() + "\" Total Hits: " + entry.getValue();
            System.out.println(line);
            qhbw.write(line);
            qhbw.newLine();
        }
        System.out.println("--------------");
        System.out.println("\nQuery & Total Hits are written in file:\nQuery-Total-Hits.txt");
        qhbw.close();

    }

    /**
     * Constructor
     *
     * @param indexDir the name of the folder in which the index should be
     * created
     * @throws java.io.IOException when exception creating index.
     */
    HW4(String indexDir) throws IOException {

        FSDirectory dir = FSDirectory.open(new File(indexDir));

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47,
                sAnalyzer);

        writer = new IndexWriter(dir, config);
    }

    /**
     * Indexes a file or directory
     *
     * @param fileName the name of a text file or a folder we wish to add to the
     * index
     * @throws java.io.IOException when exception
     */
    public void indexFileOrDirectory(String fileName) throws IOException {
        // ===================================================
        // gets the list of files in a folder (if user has submitted
        // the name of a folder) or gets a single file name (is user
        // has submitted only the file name)
        // ===================================================
        addFiles(new File(fileName));

        int originalNumDocs = writer.numDocs();
        for (File f : queue) {
            FileReader fr = null;
            try {
                Document doc = new Document();

                // ===================================================
                // add contents of file
                // ===================================================
                fr = new FileReader(f);
                doc.add(new TextField("contents", fr));
                doc.add(new StringField("path", f.getPath(), Field.Store.YES));
                doc.add(new StringField("filename", f.getName(),
                        Field.Store.YES));

                writer.addDocument(doc);
                System.out.println("Added: " + f);
            } catch (Exception e) {
                System.out.println("Could not add: " + f);
            } finally {
                fr.close();
            }
        }

        int newNumDocs = writer.numDocs();
        System.out.println("");
        System.out.println("************************");
        System.out
                .println((newNumDocs - originalNumDocs) + " documents added.");
        System.out.println("************************");

        queue.clear();
    }

    private void addFiles(File file) {

        if (!file.exists()) {
            System.out.println(file + " does not exist.");
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                addFiles(f);
            }
        } else {
            String filename = file.getName().toLowerCase();
            // ===================================================
            // Only index text files
            // ===================================================
            if (filename.endsWith(".htm") || filename.endsWith(".html")
                    || filename.endsWith(".xml") || filename.endsWith(".txt")) {
                queue.add(file);
            } else {
                System.out.println("Skipped " + filename);
            }
        }
    }

    /**
     * Close the index.
     *
     * @throws java.io.IOException when exception closing
     */
    public void closeIndex() throws IOException {
        writer.close();
    }

    public static void sortMap(HashMap<String, Long> h) throws IOException {
        List<Map.Entry<String, Long>> list
                = new LinkedList<Map.Entry<String, Long>>(h.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                return (o1.getValue()).compareTo(o2.getValue()) * (-1);
            }
        });

        File file = new File("./RankTermFreq.xls");
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsolutePath());
        BufferedWriter bw = new BufferedWriter(fw);
        
        File rtf = new File ("./RankTermFreq.txt");
        if (!rtf.exists()){
            rtf.createNewFile();
        }
        
        FileWriter rtfw = new FileWriter(rtf.getAbsolutePath());
        BufferedWriter rtfbw = new BufferedWriter(rtfw);
        rtfbw.write("Rank, Term, Frequency");
        rtfbw.newLine();
        
        String line;
        bw.write("Rank,Term,Frequency");
        bw.newLine();
        for (int i = 0; i < list.size(); i++) {
            line = (i + 1) + "," + list.get(i).getKey() + "," + list.get(i).getValue();
            System.out.println("Rank: " + (i + 1) + " Term: " + list.get(i).getKey() + " Freq: " + list.get(i).getValue());
            bw.write(line);
            bw.newLine();
            rtfbw.write((i+1) + ":: "  + list.get(i).getKey() + " :: " + list.get(i).getValue());
            rtfbw.newLine();
        }
        bw.close();
        rtfbw.close();
        System.out.println("Rank, Term & Frequency written in file: RankTermFreq.xls");
    }

    public static String getDocumentFileOnly(String path) {

        String tokens[] = path.split("\\\\");
        int last = tokens.length;
        return tokens[(last - 1)];

    }

    public static String removeTags(File file) {

        String filePath = file.getAbsolutePath();
        String line = null;
        String tokens[];

        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fr);

            File dir = new File("NewCorpus");
            dir.mkdir();

            File outputFile = new File(dir, file.getName());
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }

            FileWriter fw = new FileWriter(outputFile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            while ((line = bufferedReader.readLine()) != null) {

                tokens = line.split("\\s");

                for (int i = 0; i < tokens.length; i++) {
                    if (!tokens[i].contains("<")) {
                        bw.write(tokens[i]);
                        bw.write(" ");
                    }

                }

                bw.newLine();
            }
            bw.close();
            bufferedReader.close();

            return outputFile.getAbsolutePath();

        } catch (Exception e) {
            System.out.println("Exception " + e);
            return filePath; // if some error, then return original path.
        }

    }
}
