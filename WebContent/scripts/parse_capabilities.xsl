<?xml version="1.0"?>
<xsl:stylesheet version="1.1" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:csw="http://www.opengis.net/cat/csw/2.0.2"
xmlns:dct="http://purl.org/dc/terms/" 
xmlns:gmd="http://www.isotc211.org/2005/gmd"
xmlns:dc="http://purl.org/dc/elements/1.1/"
xmlns:dcmiBox="http://dublincore.org/documents/2000/07/11/dcmi-box/"
xmlns:ows="http://www.opengis.net/ows"
>
<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />
	<xsl:template match="/">
		<GetCapabilitiesResponse>
				<xsl:for-each select="//ows:Operation[@name='GetRecordById']//ows:Parameter[@name='outputSchema']/ows:Value">
				<outSchema>
						<xsl:value-of select="."/>
				</outSchema>	
				</xsl:for-each>
				
				<!--Cover case sensitivity like this for now...This should be fixed-->
				<xsl:for-each select="//ows:Operation[@name='GetRecordById']//ows:Parameter[@name='OutputSchema']/ows:Value">
				<outSchema>
						<xsl:value-of select="."/>
				</outSchema>	
				</xsl:for-each>	
									
		</GetCapabilitiesResponse>
	</xsl:template>	
</xsl:stylesheet>