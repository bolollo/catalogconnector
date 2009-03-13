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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.*;

/**
 * 
 * @author Victor Pascual
 * @author Wladimir Szczerban
 */
public class CatalogRequest {
	private static Logger logger = Logger.getLogger(CatalogRequest.class);
	static MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();

	static HttpClient httpclient = new HttpClient(connectionManager);
	static Utils ut = new Utils();
	static java.io.InputStream is;
	

	
	
	/**
	 * Send HTTP Post Request to Catalogue
	 * @param cat
	 * @return
	 */

	public static Catalog sendRequest(Catalog cat) {
		String res="";
		
	
		
		try {
			logger.debug("***********************");
			logger.debug("URL:" + cat.urlcatalog);
			logger.debug("Charset:" + cat.XMLencoding);
			logger.debug("REQUEST:" + cat.CSWRequest);
			logger.debug("***********************");
			PostMethod httppost = new PostMethod(cat.urlcatalog);
			httppost.setRequestBody(cat.CSWRequest);
			httpclient.setConnectionTimeout(20000);
			httppost.addRequestHeader("Content-type", "text/xml; charset="+cat.XMLencoding+"");
			httpclient.executeMethod(httppost);			
			is = httppost.getResponseBodyAsStream();
			BufferedReader entrada = new BufferedReader(new InputStreamReader(is,cat.XMLencoding));
			String leido = "";
			while (leido != null) {
				leido = entrada.readLine();
				if (leido != null) {
					res +=leido;					
				}
			}
			is.close();
			entrada.close();
			
		logger.debug("SERVER RESPONSE"+res);
			cat.setResponse(res);
			httppost.releaseConnection();
			try {
				cat=ResponseXSL.Transform(cat,is);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("Parser Configuration Exception:"+e);
			}
			
			
			
			
			
		} catch (URIException e) {
			logger.error("URI Exception:"+e);
			
			e.printStackTrace();
		} catch (HttpException e) {
			logger.error("HTTP Exception:"+e);
			
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("IO Exception:"+e);
			e.printStackTrace();
		}
		return cat;
		/*
		finally{
			////logger.debug("Finnaly");
			//cat.setResponse(res);
			return cat;
		}
		*/
	}

	/**
	 * Build CSW Query for each Catalogue
	 * @param parameters Get parameters sent by user
	 * @param cat Catalogue object
	 * @return Catalog object filled
	 * @throws IOException
	 */
	public static Catalog buildCSWQuery(Map parameters, Catalog cat)
			throws IOException {
		int queryParameters = parameters.size();
		int countParameters = 0;
		
		String[] bbox;
		String bboxRequest = "";
		String bodyRequest = "";
		String CSWRequest="";
		String QueryString="";
		String Position="1";
		String startPosition="1";
		 String maxRecords="10";
		
		if (parameters.containsKey("BBOX")) {
			logger.error("bbox value:" + parameters.get("BBOX").toString());
			bbox = ut.parseBBOX(parameters.get("BBOX").toString());
			bboxRequest = ut.findAndReplace(cat.getCswRequestXML("bbox"),"$XMIN", bbox[0]);
			bboxRequest = ut.findAndReplace(bboxRequest, "$YMIN", bbox[1]);
			bboxRequest = ut.findAndReplace(bboxRequest, "$XMAX", bbox[2]);
			bboxRequest = ut.findAndReplace(bboxRequest, "$YMAX", bbox[3]);
			logger.debug("BBOX REQUEST:" + bboxRequest);
			bodyRequest +=bboxRequest;
			QueryString +="&BBOX="+ parameters.get("BBOX").toString();
			countParameters=countParameters+1;
		}
		 if(parameters.containsKey("TITLE")){
			bodyRequest +=ut.findAndReplace(cat.getCswRequestXML("title"), "$VALUE", parameters.get("TITLE").toString());
			QueryString +="&TITLE="+ parameters.get("TITLE").toString();
			countParameters=countParameters+1;
		}
		 if(parameters.containsKey("DESCRIPTION")){
			bodyRequest +=ut.findAndReplace(cat.getCswRequestXML("description"), "$VALUE", parameters.get("DESCRIPTION").toString());
			QueryString +="&DESCRIPTION="+ parameters.get("DESCRIPTION").toString();
			countParameters=countParameters+1;
		}
		 if(parameters.containsKey("TITLE") && parameters.containsKey("DESCRIPTION")){
				bodyRequest +="<ogc:Or>"+bodyRequest+"</ogc:Or>";
				countParameters=countParameters+1;
		}
		 if(parameters.containsKey("SUBJECT")){
			bodyRequest +=ut.findAndReplace(cat.getCswRequestXML("subject"), "$VALUE", parameters.get("SUBJECT").toString());
			QueryString +="&SUBJECT="+ parameters.get("SUBJECT").toString();
			countParameters=countParameters+1;
		}
		 if(parameters.containsKey("ORGANIZATION")){
			bodyRequest +=ut.findAndReplace(cat.getCswRequestXML("organization"), "$VALUE", parameters.get("ORGANIZATION").toString());
			QueryString +="&ORGANIZATION="+ parameters.get("ORGANIZATION").toString();
			countParameters=countParameters+1;
		 
		 }
		 /*
		 if(parameters.containsKey("LANGUAGE")){
			//logger.debug("ENTRO****************LAnguge*");
			bodyRequest +=ut.findAndReplace(cat.getCswRequestXML("language"), "$LANGUAGE", parameters.get("LANGUAGE").toString());
		//logger.debug("TROBO LANGUGE"+bodyRequest);
			countParameters=countParameters+1;
		
		}
		
		*/
		 if(parameters.containsKey("ANY")){
			
			bodyRequest +=ut.findAndReplace(cat.getCswRequestXML("any"), "$VALUE", parameters.get("ANY").toString());
			QueryString +="&ANY="+ parameters.get("ANY").toString();
			countParameters=countParameters+1;
		}
		 
		
		 
		logger.debug("COUNT PARAMETERS:"+countParameters);
		
		if(countParameters >= 2){
			//if(cat.product.equalsIgnoreCase("geonetwork")){
				//CSWRequest= ut.findAndReplace(cat.getCswRequestXML("body"), "$FILTER", "<And>"+bodyRequest+"</And>");
			//}else{
			CSWRequest= ut.findAndReplace(cat.getCswRequestXML("body"), "$FILTER", "<ogc:And>"+bodyRequest+"</ogc:And>");
			//}
		
		}else if(countParameters == 1){
			CSWRequest= ut.findAndReplace(cat.getCswRequestXML("body"), "$FILTER", bodyRequest);	
		
		}else{	//0 parametres
			bodyRequest +=ut.findAndReplace(cat.getCswRequestXML("title"), "$VALUE", "");
			
			
			CSWRequest= ut.findAndReplace(cat.getCswRequestXML("body"), "$FILTER", bodyRequest);				
		}
		
		//Add startposition and max records parameters
		
		 if(parameters.containsKey("STARTPOSITION")){
				
			 startPosition=parameters.get("STARTPOSITION").toString();
			
		}
			 CSWRequest=ut.findAndReplace(CSWRequest, "$STARTPOSITION", startPosition);
			 logger.debug("STARTPOSITION VAUE:"+startPosition);
		
		
		 if(parameters.containsKey("MAXRECORDS")){
				maxRecords=parameters.get("MAXRECORDS").toString();
			 
		}
			 CSWRequest=ut.findAndReplace(CSWRequest, "$MAXRECORDS", maxRecords);
			 QueryString +="&MAXRECORDS="+ maxRecords;
		
		
		//hack for Indicio sorry
			if(cat.product.equalsIgnoreCase("indicio")&& parameters.containsKey("ORGANIZATION")){
		 
				CSWRequest=ut.findAndReplace(CSWRequest, "csw:Query typeNames=\"", "csw:Query typeNames=\"Organization=o Association=a1 ");
			}
			
			if(cat.product.equalsIgnoreCase("indicio")&& parameters.containsKey("BBOX")){
				 
				CSWRequest=Utils.findAndReplace(CSWRequest, "d/rim:Slot=", "d/rim:Slot=slotBbox,");
			}
			
			if(cat.product.equalsIgnoreCase("indicio")&& parameters.containsKey("SUBJECT")){
				 
				CSWRequest=ut.findAndReplace(CSWRequest, "d/rim:Slot=", "d/rim:Slot=keywordSlot,");
			}
			
			
		Position=Position.valueOf(Integer.parseInt(maxRecords)+ Integer.parseInt(startPosition));
		logger.debug("POSITION:"+Position);
		cat.setFinalRequest(CSWRequest,QueryString,Position);
		return cat;
	}
}