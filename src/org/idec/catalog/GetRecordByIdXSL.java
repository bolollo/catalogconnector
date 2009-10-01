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



public class GetRecordByIdXSL {
	
	//Pass in actual server XML response and the path to the xsl used to parse the file 
	//b/c for now, diff xsl files for diff WPS's
	public static String Transform(String recordMetadata,String xslPath) throws ParserConfigurationException
	{
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
			try {
				Transformer transformer = TransformerFactory.newInstance().newTransformer(xsltScript);	
				transformer.transform(xmlMetadata, bufferedResult);
				output.flush();
				output.close();
			} catch (TransformerException e) {
				e.printStackTrace();
				//logger.debug("ERROR:" + e.getMessage());
			}
			
/*			//Set up output
			JSON json = new JSONArray();
			XMLSerializer xmlS = new XMLSerializer();
			String rep = output.toString();
			json = xmlS.read(rep);
			result = rep;
*/
			result = output.toString();
		} catch (Exception e1) {	
			e1.printStackTrace();
		}

		return result;
	}
}
