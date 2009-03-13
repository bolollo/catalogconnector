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


import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 *
 * @author w.szczerban
 */

public class test {
    void depura (Object pCadena)
    {
        System.out.println("Mensaje: " + pCadena);
    }
    
    
    public static void main(String [] args) {
        test p = new test();
        NodeList records;
        
        try
        {
            //p.depura("Comenzamos transformación");
            //p.depura(p.transformar());
            String resp = p.transformar();
            
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Reader reader=new CharArrayReader(resp.toCharArray());
            Document doc = builder.parse(new org.xml.sax.InputSource(reader)); 
         
            p.depura(String.valueOf(doc.getElementsByTagName("Record").getLength()));
            records = doc.getElementsByTagName("Record");
            for (int i = 0; i < records.getLength(); i++){
                Node rec = records.item(i);
                p.depura(rec.getNodeName());
            }
            
            //p.depura("Terminamos");
        }
        catch(Exception e)
        {
            p.depura("Errores en aplicación");
            e.printStackTrace();
        }
    }
    
    public String transformar() throws Exception 
    {
        String xmlOrigen = "E:\\usuaris\\w.szczerban\\NetBeansProjects\\xsl\\resposta_iaaa_2.xml";
        String xslOrigen = "E:\\usuaris\\w.szczerban\\NetBeansProjects\\xsl\\iaaa.xsl";
        
        Source xmlSource = new StreamSource(new File(xmlOrigen));
        Source xsltSource = new StreamSource(new File(xslOrigen));
        
        StringWriter cadenaSalida = new StringWriter();
        
        Result bufferResultado = new StreamResult(cadenaSalida);
        
        TransformerFactory factoriaTrans = TransformerFactory.newInstance();
        Transformer transformador = factoriaTrans.newTransformer(xsltSource);
        transformador.transform(xmlSource, bufferResultado);
                        
        return cadenaSalida.toString();
    }
    
}
