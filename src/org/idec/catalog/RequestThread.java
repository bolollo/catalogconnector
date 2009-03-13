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
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

/**
 * 
 * @author Victor Pascual
 * @author Wladimir Szczerban
 */
public class RequestThread extends Thread {
	private static Logger logger = Logger.getLogger(CatalogRequest.class);
	Catalog cat;
	HttpServletResponse response;
	static HttpClient httpclient = new HttpClient();
	static Utils ut = new Utils();
	static java.io.InputStream is;

	/**
	 * 
	 * @param cat
	 * @param response
	 */
	public RequestThread(Catalog cat,HttpServletResponse response) {
		this.cat=cat;
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 */
	public void  run(){
		logger.debug("THREAD:"+cat.name);
		try {
			logger.debug("***********************");
			logger.debug("URL:" + cat.urlcatalog);
			logger.debug("***********************");
			try {
				String ruta = "E:/temp/res/"+cat.name+".xml";
				java.io.FileOutputStream arxiu = new java.io.FileOutputStream(
						ruta);
				java.io.PrintWriter out1 = new java.io.PrintWriter(arxiu);
				out1.println(cat.CSWRequest);
				
				out1.close();
			} catch (Throwable e) {
				e.getMessage();
				e.printStackTrace();
			}
			PostMethod httppost = new PostMethod(cat.urlcatalog);
			httppost.setRequestBody(cat.CSWRequest);
			httppost.addRequestHeader("Content-type", "text/xml; charset=UTF-8");
			httpclient.executeMethod(httppost);
			logger.debug(httppost.getStatusText());
			is = httppost.getResponseBodyAsStream();
			BufferedReader entrada = new BufferedReader(new InputStreamReader(is));
			String leido = "";
			while (leido != null) {
				leido = entrada.readLine();
				if (leido != null) {
				}
			}
			logger.debug("Response:");
			logger.debug("***********************************+");
			entrada.close();
			httppost.releaseConnection();
		} catch (URIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
