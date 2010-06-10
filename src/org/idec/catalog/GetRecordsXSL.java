/*
 * CatalogConnector - OpenSource CSW client
 * http://www.geoportal-idec.cat
 * 
 * Copyright (c) 2009, Spatial Data Infrastructure of Catalonia (IDEC)
 * Institut Cartogràfic de Catalunya (ICC)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL COPYRIGHT HOLDERS OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.idec.catalog;

import org.apache.log4j.Logger;

import java.io.CharArrayReader;
import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author Oscar Fonts
 */
public class GetRecordsXSL {
	private static Logger logger = Logger.getLogger(ResponseXSL.class);
	
	/**
	 * Transform from XML response to another XML-based format, such as KML, Atom or HTML.
	 * Gets lots of additional parameters to complete the response elements with details
	 * not present in the original XML-
	 * 
	 * @param xmlDocument The original XML getRecords response.
	 * @param xsltFilePath The path to the XSLT document used for transformation.
	 * @param catalog The catalog properties.
	 * @param selfURL The base (servlet) URL that originated the request.
	 * @param params The request parameters that accompanied selfURL.
	 * @param formats The list of possible response formats, for linking between them.
	 * @return The transformed response, ready to send.
	 */
	public static String Transform(String xmlDocument, String xsltFilePath, Catalog catalog, String selfURL, HashMap<String, String> params, Map<String, String> formats) {
		StringWriter out = new StringWriter();

		try {
			Source xml = new StreamSource(new CharArrayReader(xmlDocument.toCharArray()));
			Source xslt = new StreamSource(new File(xsltFilePath));
			Transformer transformer = TransformerFactory.newInstance().newTransformer(xslt);	
			
			// Adding parameters for XSLT transforms
			if (catalog != null) {
				transformer.setParameter("getRecordByIdBaseURL", getRecordByIdBaseURL(catalog));
			}
			
			Map<String, String> links = getLinks(selfURL, params, formats);
			for(Map.Entry<String,String> link : links.entrySet()) {
				transformer.setParameter(link.getKey(), link.getValue());
			}
			
			transformer.transform(xml, new StreamResult(out));
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.error("ERROR:" + e.getMessage());
			e.printStackTrace();
		}
		return out.toString();
	}
	
	/**
	 * 
	 * @param catalog the catalog used.
	 * @return a URI to request the full metadata for a catalog register; except for the ID.
	 */
	private static String getRecordByIdBaseURL(Catalog catalog) {
		return catalog.urlcatalog +"?request=GetRecordById&elementSetName=full&outputFormat=application/xml&service=CSW&version="+catalog.cswversion+"&id=";
	}
	
	/**
	 * 
	 * @param selfURL the base URL for this request
	 * @param params the parameters for this request
	 * @param formats the available alternate formats
	 * @return a collection of useful links to add into response (anternate formats, search, prev, next, first, last)
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, String> getLinks(String selfURL, HashMap<String, String> params, Map<String, String> formats) {
		Map<String, String> links = new HashMap<String, String>();
		
		// Self link
		links.put("SELF", getLink(selfURL, params));
		
		// Search link
		links.put("SEARCH", selfURL + "?Request=GetOpenSearchDescription&Catalogues=" + params.get("CATALOGUES"));		
		
		// Alternate links
		Map<String, String> alternateParams = (Map<String, String>) params.clone();
		for(Map.Entry<String,String> format : formats.entrySet()) {
			alternateParams.put("OUTPUTFORMAT", format.getKey());
			if(format.getKey().equalsIgnoreCase("KML")) { // Little hack for GoogleEarth...
				links.put(format.getKey(), getLink(selfURL+".kml", alternateParams));
			} else {
				links.put(format.getKey(), getLink(selfURL, alternateParams));
			}
		}
		
		// 3 Pagination parameters: startposition, maxrecords and pagination base url;
		// XSLT will generate the definitive pagination links from these and other XML
		// response elements
		Map<String, String> paginationParams = (Map<String, String>) params.clone();
		String start = "1", maxRecords = "10";
		if (paginationParams.containsKey("STARTPOSITION")) {
			start = params.get("STARTPOSITION");
			paginationParams.remove("STARTPOSITION");
		}
		if (paginationParams.containsKey("MAXRECORDS")) {
			maxRecords = params.get("MAXRECORDS");
		}
		links.put("startIndex", start);
		links.put("itemsPerPage", maxRecords);
		links.put("PAGINATION", getLink(selfURL, paginationParams));
		
		logger.debug("GetRecordsLinks:" + links.toString());
		return links;
	}
	
	/**
	 * Get a complete 'get' URL concatenating a selfURL and a collection of params 
	 * @param selfURL the base URL
	 * @param params the parameter collection
	 * @return The complete URL
	 */
	private static String getLink(String selfURL, Map<String, String> params) {
	    StringBuffer result = new StringBuffer(selfURL);
	    if (params.size() > 0) {
	    	result.append("?");	    	
			for(Map.Entry<String,String> param : params.entrySet()) {
				result.append(param.getKey());
				result.append("=");
				result.append(param.getValue());
				result.append("&");
			}
			result.deleteCharAt(result.length()-1);
	    }
	    return result.toString();
	}
	
}
