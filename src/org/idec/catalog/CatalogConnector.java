/*
 * CatalogConnector - OpenSource CSW client
 * http://www.geoportal-idec.cat
 *
 * Copyright (c) 2009, Spatial Data Infrastructure of Catalonia (IDEC)
 * Institut Cartogr�fic de Catalunya (ICC)
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


import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;


/**
 * Servlet implementation class for Servlet: CatalogConnector
 *
 * @author Victor Pascual
 * @author Wladimir Szczerban
 * @author Dominic Owen
 */
 public class CatalogConnector extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   

	private final int TWENTY_SECONDS = 20000;
	
	String RECORD_OUTSCHEMA_KEY = "outSchema";
	
	 /**
	  * When processing a client request for GetRecordByID,
	  * this key hashes to the id being used
	  */	 
	String RECORD_ID_KEY = "recordID";

	 /**
	  * When processing a client request for GetRecordByID,
	  * this key hashes to the csw version being used
	  */	 
	String RECORD_VERSION_KEY = "recordVersion";

	 /**
	  * When processing a client request for GetRecordByID,
	  * this key hashes to the record's url 
	  */	 
	String RECORD_URL_KEY = "recordURL";
	 
	 /**
	  * When processing a client request for GetRecordByID,
	  * this key hashes to the record's catalog's name 
	  */	 
	String RECORD_ENCODING_KEY = "encodingType";
	
	 /**
	  * When processing a client request for GetRecordByID,
	  * this key hashes to the record's product name 
	  */	 
	String RECORD_PRODUCT_KEY = "productName";
	
	
	
	static final long serialVersionUID = 1L;
   
	private static Logger logger = Logger.getLogger(CatalogConnector.class);

	/**
	 * The path of file service.xml
	 */
	public String XML_SERVICE_FILE="";
	/**
	 * The application path
	 */
	public String AP_PATH="";
	/**
	 * The path of catalogues directori
	 */
	public String CATALOGUES_DIR = "";
	/**
	 * The full path of file catalogues.xml. AP_PATH + XML_CATALOGUES_FILE;
	 */
	public String PATH_CATALOGUES="";
	/**
	 * The full path of file service.xml. AP_PATH + XML_SERVICE_FILE;
	 */
	String PROJECT="catalogues";
	/**
	 * The full path of the OpenSearch description document template
	 */
	String OPENSEARCH_DESCRIPTION_TEMPLATE="";
	
	public String PATH_SERVICE="";
	public String XML_CATALOGUES_PROJECTS_FOLDER="";
	public String PATH_PROJECTS="";
   /**
    * The proxyHost
    */
   public String PROXY_HOST = "";
   /**
    * The proxyPort
    */
   public int PROXY_PORT = 0;
   /**
    * Array of catalogues
    */
   public Catalog [] catalogs = null;

   private HashMap<String,OutputSchemaContainer> capabilitesMap = new HashMap<String,OutputSchemaContainer>();
   
   Capabilities cp = new Capabilities();

   private static JSONArray capabilitiesArray;
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public CatalogConnector() {
		super();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub 
		super.destroy();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doDelete(request, response);
	}



	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String charset = "UTF-8";
		response.setContentType("text/xml");
		response.setCharacterEncoding(charset);

		//Check to see if client accepts gzip compression
		boolean doGZIP = false;
		String aEncoding = request.getHeader("accept-encoding");
		if((aEncoding != null && aEncoding.toLowerCase().contains("gzip"))){
			logger.debug("Gzip encoding is supported - Now using gzip encoding");
			response.setHeader("Content-Encoding", "gzip");
			doGZIP = true;
		}
		OutStreamWrapper writer = new OutStreamWrapper(doGZIP,response);
		//Writer writer = response.getWriter();


		Map paramsRequest = Utils.getParametersToMap(request);
		String resp="";
		
		boolean checkParams = paramsRequest.containsKey("REQUEST");
		if(checkParams){
			String methodRequest=(String)paramsRequest.get("REQUEST");
			//logger.debug("REQUEST:"+methodRequest);
			if(methodRequest.equalsIgnoreCase("GetCapabilities")){
				String ot="JSON";
				if(paramsRequest.containsKey("OUTPUTFORMAT")){ot=paramsRequest.get("OUTPUTFORMAT").toString();}
				if(paramsRequest.containsKey("PROJECT")){PROJECT=paramsRequest.get("PROJECT").toString();}
				if(ot.equalsIgnoreCase("XML")){
								response.setContentType("text/xml;charset="+charset);
								writer.write("<?xml version=\"1.0\" encoding=\""+charset+"\"?>");
								writer.write(Capabilities.getCapabilitiesXML(PATH_PROJECTS + PROJECT+".xml", PATH_SERVICE));
				} else {
					response.setContentType("text/json;charset="+charset);
					writer.write(capabilitiesArray.toString());
				}
			}else if(methodRequest.equalsIgnoreCase("revalidate")){
				validateRecords(capabilitiesArray);
			}
			//Here we process a request to get records
			else if(methodRequest.equalsIgnoreCase("GetRecords")){
				if (paramsRequest.containsKey("PROJECT")){
					PROJECT=paramsRequest.get("PROJECT").toString();
				}
				boolean nCat=paramsRequest.get("CATALOGUES").toString().contains(",");
				String outputFormat = paramsRequest.get("OUTPUTFORMAT").toString().toUpperCase();
				
				// Get all responses and put them into cswResponses array
				ArrayList<String> cswResponses = new ArrayList<String>();
				for (int i=0; i<catalogs.length; i++){
					Catalog cat =catalogs[i];
					cat.ProxyHost = PROXY_HOST;
					cat.ProxyPort = PROXY_PORT;

					if(Utils.checkExistsCatalogue(cat.name,(String)paramsRequest.get("CATALOGUES"))){
						cat=CatalogRequest.buildCSWQuery(paramsRequest, cat);
						cat=CatalogRequest.sendRequest(cat);
						cswResponses.add(cat.CSWFinalResponse);
					}
				}
				
				HashMap<String, String> knownFormats = new HashMap<String, String>();
				knownFormats.put("HTML","text/html");
				knownFormats.put("KML","application/vnd.google-earth.kml+xml");
				knownFormats.put("ATOM","application/atom+xml");
				knownFormats.put("XML","text/xml");
				
				Iterator<String> iter = cswResponses.iterator();
				
				// Serialize responses, depending on outputformat
				if(!knownFormats.containsKey(outputFormat)) { // Defaults to JSON
					response.setContentType("text/json;charset="+charset);
					if(nCat) writer.write("[");
					while (iter.hasNext()){
						JSON jsonResponse = new JSONArray();
					    XMLSerializer xmlS= new XMLSerializer();
						jsonResponse = xmlS.read(iter.next());
						writer.write(jsonResponse.toString());
						if(iter.hasNext()) writer.write(",");
					}
					if(nCat) writer.write("]");
				} else { // Any known format
					String xmlResponse = new String("<CatalogConnector>");
					while (iter.hasNext()){
						xmlResponse += iter.next();
					}
					xmlResponse += "</CatalogConnector>";
					response.setCharacterEncoding(null);
					response.setContentType(knownFormats.get(outputFormat)+";charset="+charset);
					response.setHeader("Content-Type", knownFormats.get(outputFormat)+"");

					if(outputFormat.equals("XML")) { // Write directly
						writer.write(xmlResponse);
					} else { // Need to transform
						String xslPath = AP_PATH+"/scripts/getRecords2"+outputFormat+".xsl";
						try {
							String transformedResponse = GetCapabilitiesXSL.Transform(xmlResponse, xslPath);
							writer.write(transformedResponse);
						}catch (Exception e) {
							e.printStackTrace();
							response.sendError(-1, "Transform failed for "+outputFormat);
						}
											
					}
				}
			}
			//Once we get schema request, wait on getcapabilities thread to finish,
			// or timeout and return results
			else if(methodRequest.equalsIgnoreCase("GetIndivSchemas")){
				//logger.info("GOT SCHEMA REQUEST: "+schemaReqs);								
				String idVal = request.getParameter("IndivNameKey");
				
				OutputSchemaContainer catalogInfo = capabilitesMap.get(idVal);				
				long startTime = System.currentTimeMillis();
				while(!catalogInfo.isFinishedLoadingSchemas()&&((System.currentTimeMillis()-startTime)<TWENTY_SECONDS)){
					;//busy wait for timeout or completion
				}
				
				//Test for timeout
				if(!catalogInfo.isFinishedLoadingSchemas()){
					catalogInfo.setValidationFailed(true);
				}
				
				if(catalogInfo.isValidationFailed()){
					response.sendError(-1, "Validation failed for "+ idVal);
				}else{	
					//done loading schemas
					ArrayList<String> schemas = catalogInfo.getOutputSchemas();
					
					for(int i=0;i<schemas.size();i++){
						writer.write(schemas.get(i));						
						if(i<(schemas.size()-1))
							writer.write(",");
					}
				}
			}
			else if(methodRequest.equalsIgnoreCase("metadatatohtml")){				
				String idVal = request.getParameter(this.RECORD_ID_KEY);
				String urlVal = request.getParameter(this.RECORD_URL_KEY);
				String versionVal = request.getParameter(this.RECORD_VERSION_KEY);
				String encodingType = request.getParameter(this.RECORD_ENCODING_KEY);		
				String outSchema = request.getParameter(this.RECORD_OUTSCHEMA_KEY);
				String product = request.getParameter(this.RECORD_PRODUCT_KEY);
				
				logger.info("PRODUCT: "+product);
				
				//String productName = request.getParameter(RECORD_PRODUCT_KEY);
				String catalogCharset = charset;

				//Gzip + UTF-8 seem to corrupt some Western European letters. So we
				//use ISO unless GZIP isn't utilized. That is what this check is for
				if(!doGZIP){
					catalogCharset=encodingType;
				}
				
				Catalog cat = Utils.generateRequestCatalog(idVal,urlVal,versionVal,catalogCharset,product);
				String htmlResult = null;
				try {
					logger.debug("XSL PATH:"+AP_PATH+"/scripts/metadata_to_html.xsl");
					String metadata = RecordRequest.getRecordByIdRequest(idVal,cat,outSchema);
					String xslPath = AP_PATH+"/scripts/metadata_to_html.xsl";	
					htmlResult = GetRecordByIdXSL.Transform(metadata, xslPath);
					writer.write(htmlResult); 
				}catch (Exception e) {
					e.printStackTrace();
					response.sendError(-1, "Transform failed for "+ cat);
				}
			}
			// Generates OpenSearch description documents
			else if(methodRequest.equalsIgnoreCase("GetOpenSearchDescription")) {
				String baseurl = request.getRequestURL().toString();
				// No catalogues parameter => generate a list referencing all descriptionDocuments
				if(!paramsRequest.containsKey("CATALOGUES")) {
					// HTML response format
					if(paramsRequest.containsKey("FORMAT") && paramsRequest.get("FORMAT").toString().equalsIgnoreCase("HTML")) {
						response.setContentType("text/html");
						for (int i=0; i<catalogs.length; i++){
							String catname = catalogs[i].name;
							String descriptionURL = baseurl + "?Request=" + methodRequest + "&Catalogues=" + catname;
							writer.write("<link rel=\"search\" type=\"application/opensearchdescription+xml\" ");
							writer.write("href=\"" + descriptionURL + "\" title=\"" + catname + " (CatalogConnector)\" />\r\n");
						}
					// Defaults to JSON response format
					} else {
						response.setContentType("text/json");
						JSONArray jsonResponse = new JSONArray();
						for (int i=0; i<catalogs.length; i++){
							String catname = catalogs[i].name;
							String descriptionURL = baseurl + "?Request=" + methodRequest + "&Catalogues=" + catname;
							jsonResponse.element(descriptionURL);
						}
						writer.write(jsonResponse.toString());
					}
				// There is a 'catalogues' param. Generate a descriptionDocument.
				} else {
					response.setContentType("application/opensearchdescription+xml");
					String descriptionTemplatePath = OPENSEARCH_DESCRIPTION_TEMPLATE;
					String description = Utils.file2string(descriptionTemplatePath);
					String catalogues = paramsRequest.get("CATALOGUES").toString();
					writer.write(description.replaceAll("\\$CATALOGUES", catalogues).replaceAll("\\$BASEURL", baseurl));
				}
			}
			else{
				//interface not recognized
				writer.write("Unknown interface");
				logger.error("Unknown interface: Use GetCapabilities or GetRecords");
			}
		}else{
			writer.write("Error: No request parameter");
			logger.error("NO request parameter defined");
		}
		writer.flush();
		writer.close();
	}

	private void validateRecords(JSONArray capabilitiesArray) {
		//start thread here that will run until all are validated but allows initial
		//response to get back w/ out any delay
		
		for(int i = 0; i<capabilitiesArray.size();i++){
			String name = (String) capabilitiesArray.getJSONObject(i).get("name");
			String url = (String) capabilitiesArray.getJSONObject(i).get("urlcatalog");
			String encoding = (String) capabilitiesArray.getJSONObject(i).get("xml-encoding");
			String version = (String) capabilitiesArray.getJSONObject(i).get("csw-version");

			//Here, initialize a catalog in the map. Used later to confirm 
			//whether all getcapabilities requests are finished.
			capabilitesMap.put(name, new OutputSchemaContainer());
			new ValidationThread(name,url,encoding,version).start();
			
		}
		
	}

	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPut(request, response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#getServletInfo()
	 */
	public String getServletInfo() {
		return super.getServletInfo();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {
		logger.info("Starting servlet...");
		//XML_CATALOGUES_FILE=getInitParameter("catalog_config");
		XML_SERVICE_FILE=getInitParameter("catalog_service");
		XML_CATALOGUES_PROJECTS_FOLDER=getInitParameter("catalog_projects_folder");
		AP_PATH=getServletContext().getRealPath("/");
		CATALOGUES_DIR =getInitParameter("catalogues_dir");
		//PATH_CATALOGUES=AP_PATH + XML_CATALOGUES_FILE;
		PATH_SERVICE=AP_PATH + XML_SERVICE_FILE;
		PATH_PROJECTS=AP_PATH + XML_CATALOGUES_PROJECTS_FOLDER;
		OPENSEARCH_DESCRIPTION_TEMPLATE=AP_PATH + getInitParameter("opensearch_description_template");
		
		String port = getInitParameter("proxyPort");
		if (port != null){
			PROXY_PORT = Integer.parseInt(port);
		}
		PROXY_HOST = getInitParameter("proxyHost");
		catalogs = Capabilities.parseCataloguesXML(PATH_PROJECTS  + PROJECT +".xml",AP_PATH + CATALOGUES_DIR);
	
		try {
			capabilitiesArray = Capabilities.getCapabilitiesJSON(PATH_PROJECTS + PROJECT+".xml", PATH_SERVICE);
			validateRecords(capabilitiesArray);
		} catch (JDOMException e) {
			logger.error("Problem fetching project.xml file. Make sure file exists");
		} catch (IOException e) {
			logger.error("Problem fetching project.xml file. Make sure file exists");
		}
		

	
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}


	
	
	//This class is a facade to either PrintWriter or GZIPOutputStream
	private class OutStreamWrapper{
		private PrintWriter pw;
		private GZIPOutputStream gz;
		private boolean doGZIP;
		
		public OutStreamWrapper(boolean isGZIPEnabled, HttpServletResponse response) throws IOException{
			doGZIP = isGZIPEnabled;

			if(doGZIP){
				gz=new GZIPOutputStream(response.getOutputStream());
			}else{
				pw = response.getWriter();
			}
		}

		public void write(String s) throws IOException{
			if(doGZIP){
				gz.write(s.getBytes());
			}else{
				pw.write(s);
			}
		}

		public void writeln(String s) throws IOException{
			if(doGZIP){
				gz.write((s+"\n").getBytes());
			}else{
				pw.write(s+"\n");
			}
		}

		public void close() throws IOException{
			if(doGZIP){
				gz.close();
			}else{
				pw.close();
			}
		}

		public void flush() throws IOException{
			if(doGZIP){
				gz.flush();
			}else{
				pw.flush();
			}
		}

	}
	
	//each service has important information regarding: id, status and outputSchema 
	private class ValidationThread extends Thread{		
		String name;
		String encoding;
		String url;
		String version;
		public ValidationThread(String nameArg,String urlArg,String encodingArg,String versionArg){
			name = nameArg;
			url = urlArg;
			encoding=encodingArg;
			version=versionArg;
		}
		
		//TODO: Make this work
		public void run() {			
			boolean error = false;			
			String capabilities = null;			
			
			try{
				capabilities =  CapabilitiesRequest.getCapabilities(encoding,version,url);			
			}catch(Exception e){
				error = true;
			}
			
			if(null == capabilities){
				error = true;
			}
			else if(capabilities.length()>0  
					&& !capabilities.toLowerCase().contains("404 not found")
					&& !capabilities.contains("internal server error")){
				logger.info("CAPABILITIES FOR URL: "+name+"\nSTRING: "+capabilities);
	
				String xslPath = AP_PATH+"scripts/parse_capabilities.xsl";
					String result = null;
					
					try {
						result = GetCapabilitiesXSL.Transform(capabilities, xslPath);
					} catch (ParserConfigurationException e){
						logger.info("Parse error for "+name);
					}
					
					
					if(result !=null){
						JSON json = new XMLSerializer().read(result);
						
							if(json!=null){
							Object results = null;
							try{
								results =  JSONObject.fromObject(json).get("outSchema");
							}catch(Exception e){
								logger.info("JSON Parse error for "+name);
							}
							if(results instanceof JSONArray){
								for(Object schema : (JSONArray)results){
									if(schema!=null){
										capabilitesMap.get(name).getOutputSchemas().add((String) schema);
									}
								}	
							}else if(results instanceof String){
										capabilitesMap.get(name).getOutputSchemas().add((String)results);
							}
						}else{
							error = false;
							logger.info("Server responded for "+name+" but no outputSchema parameter defined");
						}
					}else{
						//error = true;
						logger.info("Server responded for "+name+" but unable to parse results. This means" +
								" the server probably can't handle POSTs");
					}
			}else{
				error=true;
			}
			if(error){
				capabilitesMap.get(name).setValidationFailed(true);
			}
			capabilitesMap.get(name).setFinishedLoadingSchemas(true);
			return;
		}
	}
}