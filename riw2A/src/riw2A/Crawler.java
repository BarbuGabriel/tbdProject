package riw2A;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {

	private static LinkedList<String> Q;
	private static int numberOfPagesCrawled = 0;
	private static Boolean pageVisited = false;
	private static LinkedList<GraphNode> adjacencyList;


	static void createFolderPath(String url) {
		URL currentURL = null;
		String htmlFilePath;
		try {
			currentURL = new URL(url);

			String domain = currentURL.getHost();
			String path = currentURL.getPath();
			String protocol = currentURL.getProtocol();
			if (path.equals("")) {
				path = "/";
			}

			URLConnection conn = currentURL.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			htmlFilePath = "crawledLinks" + "/" + protocol + "/" + domain + path;
			
			if (!(htmlFilePath.endsWith(".html"))) {
				if (!htmlFilePath.endsWith("/")) {
					htmlFilePath += "/";
				}
				htmlFilePath += "index.html";
			}
			
			File file = new File(htmlFilePath);
			File parentDirectory = file.getParentFile();
			if (!parentDirectory.exists()) {
				parentDirectory.mkdirs();
			}
	
			BufferedWriter out = new BufferedWriter(new FileWriter(htmlFilePath));

			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				out.write(inputLine);
			}
			
			out.close();

		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static Boolean pageVisited(String link) {
		String path = "crawledLinks/" + link.replace("://", "/");
		if (!path.endsWith("/")) {
			path += "/";
		}
		File file = new File(path);
		Boolean exists = file.isDirectory();
		return exists;
	}

	static void readHtmlUrls(LinkedList<String> Q) {

		Document document = null;
		while (!Q.isEmpty() && numberOfPagesCrawled < 2000) {
			String link = Q.pop();

			if (pageVisited(link)) {
				continue;
			} else {
				createFolderPath(link);
			}

			try {
				document = Jsoup.connect(link).get();
			} catch (IOException e) {
				Q.remove(link);
				continue;
			}
			Element robots = document.selectFirst("meta[name=robots]");
			String robotsString = "";
			if (robots == null) {

			} else {
				robotsString = robots.attr("content");
			}

			if (!robotsString.equals("")) {

				if (robotsString.contains("nofollow")) {
					continue;
				}
				if (robotsString.contains("none")) {
					continue;
				}

			}

			++numberOfPagesCrawled;

			System.out.println("Crawling ->" + link);

			GraphNode node = new GraphNode(link);

			Elements elements = document.select("a[href]");
			for (Element element : elements) {
				String absoluteLink = element.attr("abs:href");
				if (absoluteLink.indexOf("#") != -1) {
					StringBuilder tempLink = new StringBuilder(absoluteLink);
					tempLink.replace(absoluteLink.indexOf("#"), tempLink.length(), "");
					absoluteLink = tempLink.toString();
				}
				node.addLink(absoluteLink);
				Q.add(absoluteLink);
			}
			adjacencyList.add(node);

// File f = new File(linkHref);
// f.mkdirs();

		}
	}

	public static void main(String[] args) throws IOException {
// TODO Auto-generated method stub
		Q = new LinkedList<String>();
		adjacencyList = new LinkedList<GraphNode>();
		Q.add("https://www.crawler-test.com/");

		readHtmlUrls(Q);

	}

}