package org.idec.catalog;
/**
 * 
 * @author Dominic Owen
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

public class RecordRequest {
	
	private static Logger logger = Logger.getLogger(CatalogRequest.class);
	static MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();

	static HttpClient httpclient = new HttpClient(connectionManager);
	static Utils ut = new Utils();
	static java.io.InputStream is;
	
	/**
	 * Get a metadata entry through the use of the GetRecordByID function
	 * @param id is the ID of the record to get
	 * @param cat Catalog The catalog to get the record from
	 * @return An XML response containing the record's metadata
	 * @throws IOException
	 */
	public static String getRecordByIdRequest(String recordID, Catalog cat){
		String res = "";
		String request = parseCatalogForGetRecById(recordID, cat);

		try {
			PostMethod httppost = new PostMethod(cat.urlcatalog);
			httppost.setRequestBody(request);
			httpclient.setConnectionTimeout(20000);
	
			httppost.addRequestHeader("Content-type", "text/xml; charset="+cat.XMLencoding+"");
			//logger.info("GET RECORD BY ID REQ:\n"+request);
			httpclient.executeMethod(httppost);			
			is = httppost.getResponseBodyAsStream();
			BufferedReader entry = new BufferedReader(new InputStreamReader(is,cat.XMLencoding));
			String read = "";
			while (read != null) {
				read = entry.readLine();
				if (read != null) {
					res +=read;					
				}
			}
			is.close();
			entry.close();		
			logger.debug("SERVER RESPONSE"+res);
			httppost.releaseConnection();
												
		} catch (IOException e) {
			logger.error("IO Exception:"+e);
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * Parse a catalog to create a request string for GetRecordByID
	 * @param id is the ID of the record to get
	 * @param cat Catalog The catalog to get the record from
	 * @return A properly formatted request string
	 * @throws IOException
	 */
	//This may need to be more generalized for a few params
	public static String parseCatalogForGetRecById(String id, Catalog cat) {
		return "<?xml version=\"1.0\" encoding=\""+cat.XMLencoding+"\"?>\r\n"+
		"<csw:GetRecordById \r\n"+
			"service=\"CSW\"\r\n"+
			//"outputSchema=\"csw:IsoRecord\"\r\n"+
			"version=\""+cat.cswversion+"\"\r\n"+ 
			"outputFormat=\"application/xml\"\r\n"+ 
			//"outputSchema=\"http://www.opengis.net/cat/csw/"+cat.cswversion+"\"\r\n"+
			"xmlns:csw=\"http://www.opengis.net/cat/csw/"+cat.cswversion+"\">\r\n"+
			"<csw:Id>"+id+"</csw:Id>\r\n"+
			"<csw:ElementSetName>full</csw:ElementSetName>\r\n"+
			"</csw:GetRecordById>\r\n";
	}
}
