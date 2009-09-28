/*
 * CatalogConnector - OpenSource CSW client
 * http://www.geoportal-idec.cat
 *
 * Copyright (c) 2009, Spatial Data Infrastructure of Catalonia (IDEC)
 * Institut Cartogrāfic de Catalunya (ICC)
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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.xml.XMLSerializer;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;


/**
 * Servlet implementation class for Servlet: CatalogConnector
 *
 * @author Victor Pascual
 * @author Wladimir Szczerban
 */
 public class CatalogConnector extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
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
   public Catalog [] catalogue = null;

   Capabilities cp = new Capabilities();

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
		// TODO Auto-generated method stub
		super.doDelete(request, response);
	}



	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/xml;charset=UTF-8");


		//Check to see if client accepts gzip compression
		boolean doGZIP = false;
		String aEncoding = request.getHeader("accept-encoding");
		if((aEncoding != null && aEncoding.toLowerCase().contains("gzip"))){
			logger.debug("Gzip encoding is supported - Now using gzip encoding");
			response.setHeader("Content-Encoding", "gzip");
			doGZIP = true;
		}
		OutStreamWrapper writer = new OutStreamWrapper(doGZIP,response);


		Map paramsRequest = Utils.getParametersToMap(request);
		String resp="";
		String PROJECT="catalogues";
		boolean checkParams = paramsRequest.containsKey("REQUEST");
		if(checkParams){
			String methodRequest=(String)paramsRequest.get("REQUEST");
			//logger.debug("REQUEST:"+methodRequest);
			if(methodRequest.equalsIgnoreCase("GetCapabilities")){
				String ot="JSON";
				if(paramsRequest.containsKey("OUTPUTFORMAT")){ot=paramsRequest.get("OUTPUTFORMAT").toString();}
				if(paramsRequest.containsKey("PROJECT")){PROJECT=paramsRequest.get("PROJECT").toString();}
				
				
				if(ot.equalsIgnoreCase("XML")){
								response.setContentType("text/xml;charset=ISO-8859-1");
								writer.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
								writer.write(Capabilities.getCapabilitiesXML(PATH_PROJECTS + PROJECT+".xml", PATH_SERVICE));
				}else
				{
								response.setContentType("text/json;charset=ISO-8859-1");
								try {
									JSONArray jsonArray=Capabilities.getCapabilitiesJSON(PATH_PROJECTS + PROJECT+".xml", PATH_SERVICE);
									logger.debug(jsonArray);
									writer.write(jsonArray.toString());
								} catch (JDOMException e) {
									e.printStackTrace();
								}
				}

			}

			//Here we process a request to get records
			else if(methodRequest.equalsIgnoreCase("GetRecords")){
				if(paramsRequest.containsKey("PROJECT")){PROJECT=paramsRequest.get("PROJECT").toString();}
				boolean nCat=paramsRequest.get("CATALOGUES").toString().contains(",");
				if(paramsRequest.get("OUTPUTFORMAT").toString().equalsIgnoreCase("XML")){
					response.setContentType("text/xml;charset=UTF-8");
					writer.write("<CatalogConnector>");
				}else{
					if(nCat){
						writer.write("[");
						}
				}

				
				catalogue=Capabilities.parseCataloguesXML(PATH_PROJECTS  + PROJECT +".xml",AP_PATH + CATALOGUES_DIR);



				for (int i=0;i < catalogue.length;i++){

					Catalog cat =catalogue[i];
					cat.ProxyHost = PROXY_HOST;
					cat.ProxyPort = PROXY_PORT;

					if(Utils.checkExistsCatalogue(cat.name,(String)paramsRequest.get("CATALOGUES"))){
						cat=CatalogRequest.buildCSWQuery(paramsRequest, cat);
						cat=CatalogRequest.sendRequest(cat);
						if(paramsRequest.get("OUTPUTFORMAT").toString().equalsIgnoreCase("XML")){

							writer.write(cat.CSWFinalResponse);
						}else{ //JSON
							//TODO: Find out why UTF-8 causes unrecognized characters when using GZIP
							if(doGZIP){
								response.setContentType("text/json;charset=ISO-8859-1");
							}else{
								response.setContentType("text/json;charset="+catalogue[i].XMLencoding);
							}
							
							JSON  jsonResponse = new JSONArray();
						    XMLSerializer xmlS= new XMLSerializer();
							jsonResponse = xmlS.read(cat.CSWFinalResponse);//TODO: Fix this!!!
							if(nCat){
							resp +=jsonResponse+",";

							}else{
								writer.write(jsonResponse.toString());
							}
						}
					}
				}
				if(paramsRequest.get("OUTPUTFORMAT").toString().equalsIgnoreCase("XML")){
					writer.write("</CatalogConnector>");
				}else{
					if(resp.length()>1){
						resp=resp.substring(0, resp.length()-1);
						writer.write(resp);
					}
					if(nCat){
								writer.write("]");
					}
				}
			}else{
				//interface no reconnized
				writer.write("Unknown interface");
				logger.error("Unknown interface: Use GetCapabilities or GetRecords");
			}
		}else{
			//error no request param
			//response.getWriter().print("Error no request param");
			writer.write("Error: No request parameter");
			//writer.flush();
			logger.error("NO request parameter defined");
		}
		writer.flush();
		writer.close();
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
		// TODO Auto-generated method stub
		super.doPut(request, response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#getServletInfo()
	 */
	public String getServletInfo() {
		// TODO Auto-generated method stub
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
		String port = getInitParameter("proxyPort");
		if (port != null){
			PROXY_PORT = Integer.parseInt(port);
		}
		PROXY_HOST = getInitParameter("proxyHost");

		/*
		if (XML_CATALOGUES_FILE == null || XML_CATALOGUES_FILE.length() == 0
				|| !(new File(AP_PATH + XML_CATALOGUES_FILE)).isFile()) {
			System.err.println("ERROR:Can't find log file:" + PATH_CATALOGUES );
			logger.error("ERROR:Can't find log file:" + PATH_CATALOGUES);
			throw new ServletException();
		}else{
			logger.info("Catalog.xml found");
			catalogue=Capabilities.parseCataloguesXML(AP_PATH + XML_CATALOGUES_FILE,AP_PATH + CATALOGUES_DIR);
		}
	*/
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

}