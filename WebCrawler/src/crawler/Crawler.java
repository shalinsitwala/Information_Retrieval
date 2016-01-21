package crawler;

import static com.sun.jmx.snmp.ThreadContext.contains;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.util.Scanner;

/**
 *
 * @author Shalin Sitwala
 */
class Link {

    String URL;
    int depth;

    Link() {
        this.URL = "";
        this.depth = 0;
    }

    Link(String s, int i) {
        this.URL = s;
        this.depth = i;

    }

}

public class Crawler {

    public static boolean keyphraseFlag;

    public static void main(String[] args) throws IOException {

        int counter = 0;
        int maxlinks = 1000;
        int maxdepth = 5;
        String keyphrase = "concordance";

        Link seed = new Link("https://en.wikipedia.org/wiki/Hugh_of_Saint-Cher", 1);
        Map<String, Boolean> dictionary = new HashMap<String, Boolean>();

        dictionary.put("", Boolean.TRUE);
        //Seed should be distinct in the list too.
        dictionary.put(seed.URL, Boolean.TRUE);

        Vector<Link> mainlist = new Vector();
        mainlist.setSize(0);

        mainlist.add(seed);

        while (true) {
            System.out.println("Would you like to crawl with the keyphrase?");
            System.out.println("Enter Y / N to proceed.");
            Scanner in = new Scanner(System.in);

            String temp = in.nextLine();

            if (temp.equalsIgnoreCase("y") || temp.equalsIgnoreCase("yes")) {
                keyphraseFlag = true;
                break;
            }
            if (temp.equalsIgnoreCase("n") || temp.equalsIgnoreCase("no")) {
                keyphraseFlag = false;
                break;
            } else {
                System.out.println("Invalid input. Try again.");
                continue;
            }
        }

        while (Boolean.TRUE) {

            // qarresult will have all the sites which are already filtered from the processed link.
            Link[] qarresult = processLink(mainlist.get(counter));
            counter++;

            // Checking the result of processlink for unique links and adding it to mainlist.
            for (int j = 0; j < qarresult.length - 1; j++) {
                if (qarresult[j] != null && !keyChecker(dictionary, qarresult[j].URL)) {
                    mainlist.add(qarresult[j]);
                    dictionary.put(qarresult[j].URL, Boolean.TRUE); // Not in the dictionary, so simaltaneously add it there too.
                    if (mainlist.size() >= maxlinks || counter == mainlist.size() || mainlist.lastElement().depth > maxdepth) {
                        break;
                    }
                }
            }
            if (mainlist.size() >= maxlinks || counter == mainlist.size() || mainlist.lastElement().depth > maxdepth) {
                break;
            }
        }

        if (mainlist.size() >= maxlinks) {
            System.out.println(maxlinks + " links reached. Writing them in file.");
        } else if (mainlist.lastElement().depth > maxdepth) {
            System.out.println("Crossed maximum depth which is: " + maxdepth);
        } else {
            System.out.println("No more links to crawl which meet the criteria.");
        }

        File dir = new File(".");
        String loc;
        if(keyphraseFlag)
        {
          loc = dir.getCanonicalPath() + File.separator + "Visited_with_Keyphrase.txt";
        }
        else
        {
            loc = dir.getCanonicalPath() + File.separator + "Visited_without_Keyphrase.txt";
        }
        File vfile = new File(loc);
        if (vfile.exists()) {
            vfile.delete();
        }

        FileWriter fstream = new FileWriter(loc, true);
        BufferedWriter out = new BufferedWriter(fstream);

        for (int t = 0; t < mainlist.size(); t++) {
            out.write(mainlist.get(t).URL);
            out.newLine();
            System.out.println(mainlist.get(t).URL);
        }
        out.close();

        System.out.println("Output File is at location: " + loc);

    }

    public static boolean keyChecker(Map<String, Boolean> d, String URL) {
        Boolean val = d.get(URL);
        if (val != null) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }

    }

    public static Link[] processLink(Link linkobj) {
        Link[] qarr;

        try {
            if (linkobj.URL.endsWith("/")) {
                linkobj.URL = linkobj.URL.substring(0, linkobj.URL.length() - 1);
            }

            System.out.println("Visited: " + linkobj.URL);

            //Getting details of that page in doc.
           // Thread.sleep(3000);
            Document doc = Jsoup.connect(linkobj.URL).get();

            // Selecting all hyperlink tags.
            Elements allLinksOnThatPage = doc.select("a");

            qarr = new Link[allLinksOnThatPage.size() + 1];

            int j = 0;
            for (int i = 0; i <= allLinksOnThatPage.size() - 1; i++) {

                if (filterLink(allLinksOnThatPage.get(i).attr("abs:href"))) {
                    qarr[j] = new Link(allLinksOnThatPage.get(i).attr("abs:href"), linkobj.depth + 1);
                    j++;
                }

            }

            return qarr;

        } catch (Exception e) {
            String aa = e.toString();
            if (aa.contains("UnknownHostException")) {
                System.out.println("Please check internet connection.");
            }
            System.out.println(e);
            qarr = new Link[0];
            return qarr;
        }

    }

    public static boolean filterLink(String URL) throws IOException {

        if ((URL.contains("https://en.wikipedia.org/wiki/") && URL.substring(30).contains(":"))
                || (URL.contains("http://en.wikipedia.org/wiki/") && URL.substring(29).contains(":"))
                || (URL.contains("https://www.en.wikipedia.org/wiki/") && URL.substring(34).contains(":"))
                || (URL.contains("http://www.en.wikipedia.org/wiki/") && URL.substring(33).contains(":"))
                || URL.contains("#") || URL.contains("Main_Page")) {
            return false;
            //Ignoring admin links, same page redirects(#) & Home page of Wikipedia.
        }

        if ((URL.contains("en.wikipedia.org/wiki") && !URL.endsWith("/"))
                || (URL.contains("en.wikipedia.org/wiki") && URL.endsWith("/"))) {

            if (!keyphraseFlag) {
                return true;
            } else { // when we have to see the keyphrase.
                Document doc = Jsoup.connect(URL).get();
                Elements bodyOfThatPage = doc.select(":contains(concordance)");
                if (bodyOfThatPage.size() == 0) {
                    // did not find the word.
                    return false;
                } else {
                    // found the word
                    return true;
                }
            }

        } else {
            // URL of other sites than wikipedia.
            return false;
        }

    }

}
