package riw2A;

import java.util.HashSet;

public class GraphNode {

	private String URL;
	private HashSet<String> urlList;
	
	
	public GraphNode(String uRL) {
		URL = uRL;
		this.urlList = new HashSet<String>();
	}


	public String getURL() {
		return URL;
	}


	public void setURL(String uRL) {
		URL = uRL;
	}


	public HashSet<String> getUrlList() {
		return urlList;
	}

	public void setUrlList(HashSet<String> urlList) {
		this.urlList = urlList;
	}
	
	public void addLink(String absoluteLink) {
		urlList.add(absoluteLink);
	}
	
	
}
