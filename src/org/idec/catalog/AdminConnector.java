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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.apache.log4j.Logger;


/**
 * Servlet implementation class for Servlet: AdminConnector
 *
 * @author Victor Pascual
 * @author Wladimir Szczerban
 */
 public class AdminConnector extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;
   private static Logger logger = Logger.getLogger(AdminConnector.class);
   public String XML_CATALOGUES_FILE = "";
   public String XML_SERVICE_FILE="";
   public String AP_PATH="";
   public String CATALOGUES_DIR = "";
   public String PATH_CATALOGUES="";
   public String PATH_SERVICE="";
   public String USER_NAME="";
   public String PASSWORD="";
   public String XML_CATALOGUES_PROJECTS_FOLDER="";
   public String PATH_PROJECTS="";
   Capabilities cp = new Capabilities();
   public Catalog [] catalogue = null;
   
   /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
    public AdminConnector() {
		super();
	}   	
	
    /* (non-Javadoc)
	 * @see javax.servlet.Servlet#destroy()
	 */
	public void destroy() {
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
		PrintWriter out = response.getWriter();
		Map paramsRequest = Utils.getParametersToMap(request);
		Utils ut = new Utils();
		AdminCatalogues ac = new AdminCatalogues();
		boolean checkParams = paramsRequest.containsKey("REQUEST");
		
		if(checkParams){
			String methodRequest=(String)paramsRequest.get("REQUEST");
			if(methodRequest.equalsIgnoreCase("GetProjects")){
				File dir = new File(PATH_PROJECTS);
				if (dir.isDirectory()){
					List list = new ArrayList();
					File[] projects = dir.listFiles();
					for (int i = 0; i < projects.length; i++){
						File project = projects[i];
						Map map = new HashMap();
						map.put("project", project.getName());
						list.add(map);
					}
					response.setContentType("text/json;charset=ISO-8859-1");
					JSONArray jsonArray = JSONArray.fromObject( list );   
					out.println( jsonArray ); 
				}
			}
			else if(methodRequest.equalsIgnoreCase("SetProjects")){
				String xml_file = (String)paramsRequest.get("PROJECT");
				String operationRequest=(String)paramsRequest.get("OPERATION");
				if (operationRequest.equalsIgnoreCase("A")){
					JSONArray json = new JSONArray();
					String xml = ac.json2xmlCatalogues(json);
					ut.string2file(PATH_PROJECTS + xml_file, xml);
				}
				if (operationRequest.equalsIgnoreCase("D")){
					File project = new File(PATH_PROJECTS + xml_file);
					project.delete();
				}
			}
			else if(methodRequest.equalsIgnoreCase("GetCatalogues")){
				File dir = new File(AP_PATH + CATALOGUES_DIR);
				if (dir.isDirectory()){
					List list = new ArrayList();
					File[] subdir = dir.listFiles();
					for (int i = 0; i < subdir.length; i++){
						File product = subdir[i];
						if (product.isDirectory()){
							Map map = new HashMap();
							map.put("product", product.getName());
							File[] versions = product.listFiles();
							List list_versions = new ArrayList();
							for (int j = 0; j < versions.length; j++){
								File version = versions[j];
								if (version.isDirectory()){
									list_versions.add(version.getName());
								}
							}
							map.put("version", list_versions);
							list.add(map);
						}
					}
					response.setContentType("text/json;charset=ISO-8859-1");
					JSONArray jsonArray = JSONArray.fromObject( list );   
					out.println( jsonArray ); 
				}
			}
			else if (methodRequest.equalsIgnoreCase("GetConnections")){
				String xml_file = XML_CATALOGUES_FILE; 
				if (paramsRequest.get("PROJECT") != null){
					xml_file = (String)paramsRequest.get("PROJECT");
				}
				String xml = ut.file2string(PATH_PROJECTS + xml_file);
				JSONArray json = new JSONArray();
				XMLSerializer serializer = new XMLSerializer();
				try{
					json = (JSONArray)serializer.read( xml );
				}catch (Exception e){
					
				}finally{
					out.println(json);
				}
			}
			else if (methodRequest.equalsIgnoreCase("SetConnections")){
				String xml_file = XML_CATALOGUES_FILE; 
				if (paramsRequest.get("PROJECT") != null){
					xml_file = (String)paramsRequest.get("PROJECT");
				}
				String operationRequest=(String)paramsRequest.get("OPERATION");
				String name = (String)paramsRequest.get("NAME");
				String title = (String)paramsRequest.get("TITLE");
				String description = (String)paramsRequest.get("ABSTRACT");
				String urlcatalog = (String)paramsRequest.get("URLCATALOG");
				String product = (String)paramsRequest.get("PRODUCT");
				String cswversion = (String)paramsRequest.get("CSW_VERSION");
				String XMLencoding = (String)paramsRequest.get("XML_ENCODING");
				String activeConnection = (String)paramsRequest.get("ACTIVE_CONNECTION");
				if (operationRequest.equalsIgnoreCase("A")){
					String xml = ut.file2string(PATH_PROJECTS + xml_file);
					JSONArray json = new JSONArray();
					XMLSerializer serializer = new XMLSerializer();
					try{
						json = (JSONArray)serializer.read( xml );
					}catch (Exception e){
						
					}finally{
						out.println(json);
					}
					JSONObject connection_conf = ac.createConnection(name, title, description, urlcatalog, product, cswversion, XMLencoding);
					json.add(connection_conf);
					xml = ac.json2xmlCatalogues(json);
					ut.string2file(PATH_PROJECTS + xml_file, xml);
				}
				else if (operationRequest.equalsIgnoreCase("U")){
					String xml = ut.file2string(PATH_PROJECTS + xml_file);
					JSONArray json = new JSONArray();
					XMLSerializer serializer = new XMLSerializer();
					json = (JSONArray)serializer.read( xml );
					Integer ACTIVE_CONNECTION = Integer.parseInt(activeConnection);
					JSONObject connection_conf = ac.createConnection(name, title, description, urlcatalog, product, cswversion, XMLencoding);
					json.set(ACTIVE_CONNECTION, connection_conf);
					xml = ac.json2xmlCatalogues(json);
					ut.string2file(PATH_PROJECTS + xml_file, xml);
				}
				else if (operationRequest.equalsIgnoreCase("D")){
					Integer ACTIVE_CONNECTION = Integer.parseInt(activeConnection);
					String xml = ut.file2string(PATH_PROJECTS + xml_file);
					JSONArray json = new JSONArray();
					XMLSerializer serializer = new XMLSerializer();
					json = (JSONArray)serializer.read( xml );
					JSONObject connection_conf = json.getJSONObject(ACTIVE_CONNECTION);
					json.remove(connection_conf);
					xml = ac.json2xmlCatalogues(json);
					ut.string2file(PATH_PROJECTS + xml_file, xml);
				}else{
					//operation no reconnize
				}
			}else{
				//interface no reconnize
			}
		}else{
			//Client Administrator
			response.setContentType("text/html;charset=ISO-8859-1");
			String url = "";
			if (request.getParameter("user") != null && request.getParameter("password") != null){
				if (request.getParameter("user").equals(USER_NAME) && request.getParameter("password").equals(PASSWORD)){
					String page = "";
					page += "<html>";
					page += "<head>";
					page += "<meta http-equiv='Content-Type' content='text/html; charset=ISO-8859-1'>";
					page += "<title>CatalogConector</title>";
					page += "<link href='css/catalog.css' rel='stylesheet' type='text/css'>";
					page += "<script type='text/javascript' src='scripts/prototype.js'></script>";
					page += "<script type='text/javascript' src='scripts/admin.js'></script>";
					page += "</head>";
					page += "<body onload='init();'>";
					page += "<div id='divHeader' class='header' style='position:absolute;top:0px;left:0px;width:99%;height:68px;'>";
					page += "CatalogConector Administrator";
					page += "</div>";
					page += "<div align='left' style='position:absolute;top:0px:left:0px;'><img src='images/Catalog_connector.png' width='182px' height='68px'></div>";
					page += "<div id='divProjectes' style='position:absolute;top:70px;left:10px;height:30px;'>";
					page += "Project:&nbsp;<select id='selProject' onchange='updateList(this.value)'></select>";
					page += "&nbsp;&nbsp;&nbsp;<input type='button' class='boto' value='New Project' onclick='newProject();'>";
					page += "&nbsp;&nbsp;&nbsp;<input type='button' class='boto' value='Delete Project' onclick='deleteProject();'>";
					page += "</div>";
					
					page += "<div id='divNewProject' style='position:absolute;top:70px;left:450px;display:none;'>";
					page += "Project name:&nbsp;<input type='text' id='project_name' value=''>&nbsp;&nbsp;&nbsp;";
					page += "<input type='button' class='boto' value='Create Project' onclick='createProject();'>&nbsp;";
					page += "<input type='button' class='boto' value='Cancel' onclick='cancelProject();'>&nbsp;";
					page += "</div>";
					
					page += "<div id='divButtons' style='position:absolute;top:110px;left:10px;height:30px;'>";
					page += "<input type='button' class='boto' value='New Catalog' onclick='createCatalog();'>";
					page += "</div>";
					page += "<div id='divList' style='position:absolute;top:130px;left:10px;width:40%'>";
					page += "</div>";
										
					page += "<div id='divProperties' style='position:absolute;top:110px;right:0px;width:60%'>";
					page += "</div>";
					page += "<div id='divLoading' class='loader' style='position:absolute;top:0px;left:0px;width:99%;height:99%;display:none;background-color:#FFFFFF;'>";
					page += "</div>";
					page += "<div id='divMessage' class='message' style='display:none;'>";
					page += "<table>";
					page += "<tr>";
					page += "<td>&nbsp;</td>";
					page += "</tr>";
					page += "<tr>";
					page += "<td align='center'><div id='textMessage'></div></td>";
					page += "</tr>";
					page += "<tr>";
					page += "<td>&nbsp;</td>";
					page += "</tr>";
					page += "<tr>";
					page += "<td align='center'><input type='button' class='boto' value='OK' onclick='hideMessage();'></td>";
					page += "</tr>";
					page += "</table>";
					page += "</div>";
					page += "</body>";
					page += "</html>";
					out.write(page);
				}else{
					url = "/login.html";
					getServletConfig().getServletContext().getRequestDispatcher(url).forward(request, response);
				}
			}else{
				url = "/login.html";
				getServletConfig().getServletContext().getRequestDispatcher(url).forward(request, response);
			}
			//error no request param
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
		XML_CATALOGUES_PROJECTS_FOLDER = getInitParameter("catalog_projects_folder");
		XML_CATALOGUES_FILE=getInitParameter("catalog_config");
		XML_SERVICE_FILE=getInitParameter("catalog_service");
		AP_PATH=getServletContext().getRealPath("/");
		CATALOGUES_DIR =getInitParameter("catalogues_dir");
		USER_NAME = getInitParameter("user_name");
		PASSWORD = getInitParameter("password");
		
		PATH_CATALOGUES=AP_PATH + XML_CATALOGUES_FILE;
		PATH_SERVICE=AP_PATH + XML_SERVICE_FILE;
		PATH_PROJECTS=AP_PATH + XML_CATALOGUES_PROJECTS_FOLDER;
		
		if (XML_CATALOGUES_FILE == null || XML_CATALOGUES_FILE.length() == 0
				|| !(new File(PATH_PROJECTS + XML_CATALOGUES_FILE)).isFile()) {
			System.err.println("ERROR:Can't find log file:" + PATH_CATALOGUES );
			logger.error("ERROR:Can't find log file:" + PATH_CATALOGUES);
			throw new ServletException();
		}else{
			logger.info("Catalogues.xml found");
			
			catalogue=Capabilities.parseCataloguesXML(PATH_PROJECTS + XML_CATALOGUES_FILE,AP_PATH + CATALOGUES_DIR);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString();
	} 
}