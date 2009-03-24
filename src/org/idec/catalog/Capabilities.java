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

import java.io.IOException;

import net.sf.json.JSONArray;
import net.sf.json.xml.XMLSerializer;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * 
 * 
 * @author Victor Pascual
 * @author Wladimir Szczerban
 */
public class Capabilities {
	private static Logger logger = Logger.getLogger(Capabilities.class);
	Utils ut = new Utils();
	CatalogRequest cr = new CatalogRequest();

	/**
	 * Return a XML string with capabilities
	 * 
	 * @param PATH_CATALOGUES
	 *            The complete path of catalogues.xml file
	 * @param PATH_SERVICE
	 *            The complete path of service.xml file
	 * @return XML string with capabilities
	 * @throws IOException
	 */
	public static String getCapabilitiesXML(String PATH_CATALOGUES,
			String PATH_SERVICE) throws IOException {
		logger.debug("PATH_SERVICE:" + PATH_SERVICE);
		String service_path = Utils.checkSlashes(PATH_SERVICE);
		String service_file = Utils.file2string(service_path);
		logger.debug("PATH_CATALOGUES:" + PATH_CATALOGUES);
		String catalogues_path = Utils.checkSlashes(PATH_CATALOGUES);
		String catalogues_file = Utils.file2string(catalogues_path);
		return "<Capabilities>" + service_file + catalogues_file
				+ "</Capabilities>";
	}

	/**
	 * Return the List of configured catalogues names
	 * 
	 * @param PATH_CATALOGUES
	 *            The complete path of catalogues.xml file
	 * @param PATH_SERVICE
	 *            The complete path of service.xml file
	 * @return List of catalogues names
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static JSONArray getCapabilitiesJSON(String PATH_CATALOGUES,
			String PATH_SERVICE) throws JDOMException, IOException {
		
		String xml = Utils.file2string(PATH_CATALOGUES);
		JSONArray json = new JSONArray();
		XMLSerializer serializer = new XMLSerializer();
		json = (JSONArray) serializer.read(xml);

		return json;

	}

	/**
	 * Read the xml file catalogues.xml and create a array of Catalog
	 * 
	 * @param filePath
	 *            The complete path of catalogues.xml file
	 * @param XMLfilePath
	 *            The path of catalogues directory
	 * @return Array of Catalog
	 */
	public static Catalog[] parseCataloguesXML(String filePath,
			String XMLfilePath) {
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		Catalog[] cat = null;
		try {
			Document docXMLResponse = builder.build(filePath);
			Element root = docXMLResponse.getRootElement();
			Namespace NS = root.getNamespace();
			List entry = root.getChildren("catalog", NS);
			cat = new Catalog[entry.size()];
			int j = 0;
			Iterator i = entry.iterator();
			while (i.hasNext()) {
				Element entradatmp = (Element) i.next();

				cat[j] = new Catalog(entradatmp.getChild("name", NS).getText(),
						entradatmp.getChild("title", NS).getText(), entradatmp
								.getChild("abstract", NS).getText(), entradatmp
								.getChild("urlcatalog", NS).getText(),
						entradatmp.getChild("product", NS).getText(),
						entradatmp.getChild("csw-version", NS).getText(),
						XMLfilePath, entradatmp.getChild("xml-encoding", NS)
								.getText());
				j = j + 1;
			}

		} catch (JDOMException e) {
			logger.error("JDOM Exception:" + e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("IO Exception:" + e);
			e.printStackTrace();
		} finally {
			return cat;
		}
	}
}
