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

import java.io.CharArrayReader;
import java.io.File;
import java.io.Reader;
import java.io.StringWriter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.xml.XMLSerializer;
import org.apache.log4j.Logger;

/**
 * 
 * @author Victor Pascual
 * @author Wladimir Szczerban
 */
public class ResponseXSL {
	private static Logger logger = Logger.getLogger(ResponseXSL.class);

	/**
	 * Transform the response of a CSW using the XSL
	 * 
	 * @param cat
	 *            The catalog object
	 * @param is
	 *            The response string
	 * @return The catalog object with the new response
	 * @throws ParserConfigurationException
	 */
	public static Catalog Transform(Catalog cat, java.io.InputStream is)
			throws ParserConfigurationException {
		String xmlOrigen = cat.CSWResponse;
		logger.debug("XML Origin:" + xmlOrigen);
		String xslOrigen;
		try {
			xslOrigen = cat.getResponseXSL("response");

			logger.debug("Xsl Origin:" + xslOrigen);
			Reader rd = new CharArrayReader(xmlOrigen.toCharArray());
			Source xmlSource = new StreamSource(rd);
			logger.debug("Xsource:" + xmlSource.toString());
			Source xsltSource = new StreamSource(new File(xslOrigen));
			StringWriter cadenaSalida = new StringWriter();
			Result bufferResultado = new StreamResult(cadenaSalida);
			
			try {
				TransformerFactory factoriaTrans = TransformerFactory
						.newInstance();
				Transformer transformador = factoriaTrans
						.newTransformer(xsltSource);
				logger.debug("Transformation:" + transformador.toString());
				transformador.transform(xmlSource, bufferResultado);
				// logger.debug("buffer:"+cadenaSalida.toString());

				cadenaSalida.flush();
				cadenaSalida.close();
			} catch (TransformerException e) {
				logger.debug("ERROR:" + e.getMessage());
			}
			JSON json = new JSONArray();
			XMLSerializer xmlS = new XMLSerializer();
			String rep = cadenaSalida.toString();
			logger.debug("RESPONSE:" + rep);
			json = xmlS.read(rep);

			cat.setFinalResponse(rep);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			logger.error("ERROR:" + e1.getMessage());
			e1.printStackTrace();
		}
		return cat;
	}
}
