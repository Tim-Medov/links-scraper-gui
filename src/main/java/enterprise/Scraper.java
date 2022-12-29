
package enterprise;

import java.util.*;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Scraper implements Runnable {

    private String strUrl;
    private int resultsCount;
    private Queue<String> linksQueueToScrape = new LinkedList<>();
    private Set<String> links = new TreeSet<>();
    private List<String> domainList = new ArrayList<>();
    private long startTime = System.currentTimeMillis();

    public Scraper(String strUrl, int resultsCount) {
        this.strUrl = strUrl;
        this.resultsCount = resultsCount;
    }

    public Set<String> getLinks() {
        return links;
    }

    private StringBuilder htmlBuilder(String strUrl) {

        StringBuilder htmlBuilder = new StringBuilder();

        try {
            URL url = new URL(strUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine = reader.readLine();

            while (inputLine != null) {

                long finishTimeInHtmlBuilder = System.currentTimeMillis();

                if ((finishTimeInHtmlBuilder - startTime) > 30000) {
                    break;
                }

                htmlBuilder.append(inputLine);
                inputLine = reader.readLine();
            }
            reader.close();

        } catch (Exception ex) {
            System.out.println("Can't read the HTML page...");
        }

        return htmlBuilder;
    }

    @Override
    public void run () {

        String domainPage = strUrl;

        if (strUrl.endsWith("/")) {
            domainPage = strUrl.substring(0, strUrl.length() - 1);
        }

        domainList.add(domainPage);
        String domainRegex = domainList.get(0);
        linksQueueToScrape.add(strUrl);
        links.add(strUrl);
        System.out.println("Link added: " + strUrl);

        while (links.size() < resultsCount) {

            long finishTimeInWhileLoop = System.currentTimeMillis();

            if ((finishTimeInWhileLoop - startTime) > 30000) {
                break;
            }

            if (linksQueueToScrape.isEmpty()) {
                System.out.println("No more results...");
                break;
            }

            StringBuilder htmlContent = htmlBuilder(linksQueueToScrape.remove());

            Document htmlDocument = Jsoup.parse(htmlContent.toString());
            Elements linksOnPage = htmlDocument.select("a[href]");

            for (Element link : linksOnPage) {

                long finishTimeInForLoop = System.currentTimeMillis();

                if ((finishTimeInForLoop - startTime) > 30000) {
                    break;
                }

                if (links.size() >= resultsCount) {
                    break;
                }

                String actualUrl = link.attr("abs:href");

                if (!links.contains(actualUrl) && actualUrl.startsWith(domainRegex)) {

                    linksQueueToScrape.add(actualUrl);
                    links.add(actualUrl);
                    System.out.println("Link added: " + actualUrl);
                }
            }
        }
    }
}
