package org.idec.catalog;

/**
 * 
 * @author Dominic Owen
 */

import java.io.CharArrayReader;
import java.io.File;
import java.io.Reader;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.xml.XMLSerializer;



public class GetCapabilitiesXSL {
	private static Logger logger = Logger.getLogger(GetCapabilitiesXSL.class);
	
	public static String Transform(String recordMetadata,String xslPath) throws ParserConfigurationException
	{
		boolean failed = false;
		JSON json = null;
		String result = null;
		try {
			//Prepare the xml string
			Reader rd = new CharArrayReader(recordMetadata.toCharArray());
			Source xmlMetadata = new StreamSource(rd);
			
			//Prepare the xsl script
			Source xsltScript = new StreamSource(new File(xslPath));
			StringWriter output = new StringWriter();
			Result bufferedResult = new StreamResult(output);
			
			
			//Transform the string with the xsl script
				Transformer transformer = TransformerFactory.newInstance().newTransformer(xsltScript);	
				transformer.transform(xmlMetadata, bufferedResult);
				output.flush();
				output.close();
			
			
			//Set up output
			result = output.toString();
/*			json = new JSONArray();
			XMLSerializer xmlS = new XMLSerializer();
			String rep = output.toString();
			json = xmlS.read(rep);*/


			//result = output.toString();
		} catch (Exception e1) {	
			//logger.info("Parse Error");
			//e1.printStackTrace();
			failed = true;
		}
		if( failed){
			json=null;
		}
		return result;
	}
}
