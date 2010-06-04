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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

/**
 * This class Catalog represent a catalog object.
 * 
 * @author Victor Pascual
 * @author Wladimir Szczerban
 */
public class Catalog {
	private static Logger logger = Logger.getLogger(Catalog.class);
	/**
	 * A short name of catalog
	 */
	public String name = "";
	/**
	 * Descriptive name of catalog
	 */
	public String title = "";
	/**
	 * Abstract thar describe the catalog
	 */
	public String description = "";
	/**
	 * The conection URL of the catalog
	 */
	public String urlcatalog = "";
	/**
	 * The product type of catalog
	 */
	public String product = "";
	/**
	 * The version of product
	 */
	public String cswversion = "";
	/**
	 * The path of the catalog directory
	 */
	public String XMLRequestsPath = "";
	/**
	 * The charset enconding request
	 */
	public String XMLencoding = "";
	/**
	 * Starting position for pagination
	 */
	public String Position = "0";
	/**
	 * Varible to store XML request
	 */
	public String XMLPartialRequest = null;
	/**
	 * Final CSW Request to catalogue
	 */
	public String CSWRequest = null;
	/**
	 * GET query string.Useful for pagination
	 */
	public String QueryString = null;
	/**
	 * CSW Response
	 */
	public String CSWResponse = null;
	/**
	 * CSW XML response tranformed to common XML
	 */
	public String XSLCSWResponse = null;
	/**
	 * 
	 */
	public String CSWFinalResponse = null;
	/**
	 * ProxyHost
	 */
	public String ProxyHost = "";
	/**
	 * ProxyPort
	 */
	public int ProxyPort = 0;
	
	Utils ut = new Utils();

	/**
	 * Constructor of new Catalog object
	 * 
	 * @param name
	 *            The name of catalog connection
	 * @param title
	 *            A descriptive name of catalog connection
	 * @param description
	 *            Abtract of catalog information
	 * @param urlcatalog
	 *            The URL of catalog
	 * @param product
	 *            The product type of catalog
	 * @param cswversion
	 *            The version of product
	 * @param XMLRequestsPath
	 *            The path of the catalog directory
	 * @param XMLencoding
	 *            The character encoding system
	 * @throws IOException
	 */
	public Catalog(String name, String title, String description,
			String urlcatalog, String product, String cswversion,
			String XMLRequestsPath, String XMLencoding, 
			String ProxyHost, int ProxyPort) throws IOException {
		this.name = name;
		this.title = title;
		this.description = description;
		this.urlcatalog = urlcatalog;
		this.product = product;
		this.cswversion = cswversion;
		this.XMLRequestsPath = XMLRequestsPath;
		this.XMLencoding = XMLencoding;
		this.getCswRequestXML(this.XMLPartialRequest);
		this.getResponseXSL(this.XMLPartialRequest);
		this.CSWRequest = "";
		this.Position = "1";
		this.QueryString = null;
		this.CSWResponse = null;
		this.XSLCSWResponse = null;
		this.ProxyHost = ProxyHost;
		this.ProxyPort = ProxyPort;
	}

	/**
	 * Set the CSWRequest of a Catalog
	 * 
	 * @param CSWRequest
	 *            The new CSWRequest
	 */
	public void setFinalRequest(String CSWRequest, String QueryString,
			String Position) {
		this.CSWRequest = CSWRequest;
		this.QueryString = QueryString;
		this.Position = Position;
	}

	/**
	 * Set the CSWFinalResponse of a Catalog
	 * 
	 * @param CSWFinalResponseXML
	 *            The new CSWFinalResponse
	 * @throws UnsupportedEncodingException
	 */
	public void setFinalResponse(String CSWFinalResponseXML)
			throws UnsupportedEncodingException {

		if (CSWFinalResponseXML.indexOf("?>") != -1) {
			CSWFinalResponseXML = CSWFinalResponseXML.substring(
					CSWFinalResponseXML.indexOf("?>") + 3, CSWFinalResponseXML
							.length());

			CSWFinalResponseXML = "<Catalogue><Id>" + this.name+"</Id><QueryString>"
					+ URLEncoder.encode(this.QueryString, "UTF-8")
					+ "</QueryString><Position>" + this.Position
					+ "</Position>" + CSWFinalResponseXML + "</Catalogue>";

		}
		logger.debug(CSWFinalResponseXML);

		this.CSWFinalResponse = CSWFinalResponseXML;
	}

	/**
	 * Set the CSWResponse of a Catalog
	 * 
	 * @param CSWResponse
	 */
	public void setResponse(String CSWResponse) {
		this.CSWResponse = CSWResponse;
	}

	/**
	 * Set the XSLCSWResponse of a Catalog
	 * 
	 * @param XSLCSWResponse
	 *            String with information of the XLS tranformation
	 */
	public void setTransformation(String XSLCSWResponse) {
		this.XSLCSWResponse = XSLCSWResponse;
	}

	/**
	 * Read a XML file with information of CSW request.
	 * 
	 * @param XMLPartialRequest
	 *            The name of XML file
	 * @return The XML string with information of CSW request
	 * @throws IOException
	 */
	public String getCswRequestXML(String XMLPartialRequest) throws IOException {
		String bodyPath = "";
		String bodyFile = "";
		BufferedReader reader = null;
		try {
			if (XMLPartialRequest != null) {
				bodyPath = ut.checkSlashes(XMLRequestsPath + product + "/"
						+ cswversion + "/" + XMLPartialRequest + ".xml");
				logger.debug("BodyPath:" + bodyPath);
				return ut.file2string(bodyPath);
			}
			return bodyFile;
		} catch (IOException e) {
			reader.close();
			logger.error("File not found:" + bodyPath);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Read a XLS file with information for transform a CSW response.
	 * 
	 * @param XMLPartialRequest
	 *            The name of XLS file
	 * @return The XLS string used to transform the response
	 * @throws IOException
	 */
	public String getResponseXSL(String XMLPartialRequest) throws IOException {
		String bodyPath = "";
		String bodyFile = "";
		BufferedReader reader = null;
		try {
			if (XMLPartialRequest != null) {
				bodyPath = ut.checkSlashes(XMLRequestsPath + product + "/"
						+ cswversion + "/" + XMLPartialRequest + ".xsl");
				logger.debug("BodyPath:" + bodyPath);
				return bodyPath;
			}
			return bodyFile;
		} catch (Exception e) {
			reader.close();
			logger.error("File not found:" + bodyPath);
			e.printStackTrace();
			return null;
		}
	}
}
