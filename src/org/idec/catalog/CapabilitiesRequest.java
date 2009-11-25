package org.idec.catalog;
/**
 * @author Dominic Owen
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

public class CapabilitiesRequest {
	
	static Logger logger = Logger.getLogger(CatalogRequest.class);
	static MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager(); 
	static HttpClient httpclient = new HttpClient(connectionManager);
	private final static int TWENTY_SECONDS = 20000;

	
/*	public CapabilitiesRequest(){
		connectionManager = new MultiThreadedHttpConnectionManager(); 
		httpclient = new HttpClient(connectionManager);
		ut = new Utils();
	}*/
	
	/**
	 * Get capabilities for a catalog (and parse to determine 
	 * which outputSchemas we can use
	 * @param url is the catalog we are checking
	 * @return An XML response containing the catalog's capabilities
	 * @throws IOException
	 */
	public static String getCapabilities(String encoding, String catURL){
		String res = "";

		try {
			PostMethod httppost = new PostMethod(catURL);
			httppost.setRequestBody(genGetCapabilitiesRequest(encoding));
			httpclient.setConnectionTimeout(TWENTY_SECONDS);
	
			httppost.addRequestHeader("Content-type", "text/xml; charset="+encoding+"");
			
			httpclient.executeMethod(httppost);			
			InputStream is = httppost.getResponseBodyAsStream();
			BufferedReader entry = new BufferedReader(new InputStreamReader(is,encoding));
			String read = "";
			
			while (read != null) {
				res +=read;
				read = entry.readLine();		
			}
			is.close();
			entry.close();		
			httppost.releaseConnection();
												
		} catch (IOException e) {
			logger.error("CapabilitiesRequest Error: "+e);
			res = null;
		}
		
		//if(res!=null)
		//logger.info("URL:" + catURL +"\nCapabilities: "+res);
		
		return res;
	}
	
	/**
	 * Parse a catalog to create a request string for GetRecordByID
	 * @param id is the ID of the record to get
	 * @param cat Catalog The catalog to get the record from
	 * @return A properly formatted request string
	 * @throws IOException
	 */
	public static String genGetCapabilitiesRequest(String encoding) {		
		return "<?xml version=\"1.0\" encoding=\""+encoding+"\"?>\r\n"+
		"<csw:GetCapabilities xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" service=\"CSW\">\r\n"+
		"</csw:GetCapabilities>";
	}
	
	
	
	
	
	public static void main(String[] args){
		String url = "http://www.fao.org/geonetwork/srv/en/csw";
		String request = genGetCapabilitiesRequest("UTF-8");
		
		//String response = getCapabilities("UTF-8",url);
		System.out.println(request);
	}
}
