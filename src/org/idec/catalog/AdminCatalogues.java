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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

/**
 * 
 * @author Victor Pascual
 * @author Wladimir Szczerban
 */
public class AdminCatalogues {
	private static Logger logger = Logger.getLogger(AdminCatalogues.class);

	/**
	 * Converts a JSON Array that contain JSON Objects with information of
	 * catalogues on a String with the file structure of XML catalogues
	 * 
	 * @param json
	 *            A JSON Array wich contain information of catalogues
	 * @return String with the file structure of catalogues.xml
	 */
	public String json2xmlCatalogues(JSONArray json) {
		String xml = "";
		xml += "<catalogues>";
		xml += "<!--";
		xml += "<product>indicio</product> Must exists a folder inside WEB-INF\\catalogues with the same name -";
		xml += "<csw-version>2.0.1</csw-version>     Must exists a folder inside WEB-INF\\catalogues with the same name";
		xml += "-->";
		for (int i = 0; i < json.size(); i++) {
			JSONObject catalog = json.getJSONObject(i);
			xml += "<catalog>";
			xml += "<name>" + catalog.get("name") + "</name>";
			xml += "<title>" + catalog.get("title") + "</title>";
			xml += "<abstract>" + catalog.get("abstract") + "</abstract>";
			xml += "<urlcatalog>" + catalog.get("urlcatalog") + "</urlcatalog>";
			xml += "<product>" + catalog.get("product") + "</product>";
			xml += "<csw-version>" + catalog.get("csw-version")
					+ "</csw-version>";
			xml += "<xml-encoding>" + catalog.get("xml-encoding")
					+ "</xml-encoding>";
			xml += "</catalog>";
		}
		xml += "</catalogues>";
		return xml;
	}

	/**
	 * Create a JSON Object with information of catalog connection
	 * 
	 * @param name
	 *            The name of catalog connection
	 * @param title
	 *            Descriptive name of catalog connection
	 * @param description
	 *            A abstract that describes the catalog connection
	 * @param urlcatalog
	 *            The URL of the catalog
	 * @param product
	 *            The type of catalog
	 * @param cswversion
	 *            The version of the product
	 * @param XMLencoding
	 *            The xml encoding response from the catalog
	 * @return JSONObject with information of catalog connection
	 */
	public JSONObject createConnection(String name, String title,
			String description, String urlcatalog, String product,
			String cswversion, String XMLencoding) {
		JSONObject connection_conf = new JSONObject();
		connection_conf.element("name", name);
		connection_conf.element("title", title);
		connection_conf.element("abstract", description);
		connection_conf.element("urlcatalog", urlcatalog);
		connection_conf.element("product", product);
		connection_conf.element("csw-version", cswversion);
		connection_conf.element("xml-encoding", XMLencoding);
		return connection_conf;
	}
}
