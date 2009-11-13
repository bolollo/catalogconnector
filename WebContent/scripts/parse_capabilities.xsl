<?xml version="1.0"?>
<xsl:stylesheet version="1.1" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:csw="http://www.opengis.net/cat/csw/2.0.2"
xmlns:dc="http://purl.org/dc/elements/1.1/"
xmlns:ows="http://www.opengis.net/ows">
<xsl:output method="xml" version="1.0" encoding="ISO-8859-1" indent="yes" />
  <xsl:template match="/">
  	<!-- Maybe change this to apply templates and have one for bad responses that handles errors more or less -->
      <GetCapabilitiesResponse>
		
		<xsl:apply-templates/>
				
		<!-- 
		<xsl:value-of select="csw:Capabilities//ows:Operation[@name='GetRecordById']//ows:Parameter[@name='outputSchema']"/>
		<xsl:apply-templates/>

		 -->
		
		
		</GetCapabilitiesResponse>
	</xsl:template>
	
	<xsl:template match="html">
		<xsl:value-of select="'Invalid Response Returned'"/>
	</xsl:template>
	
	<xsl:template match="csw:Capabilities">
		
		<!--This is more less covers for case sensitivity-->
		<xsl:value-of select="//ows:Operation[@name='GetRecordById']//ows:Parameter[@name='outputSchema']"/>
		<xsl:value-of select="//ows:Operation[@name='GetRecordById']//ows:Parameter[@name='OutputSchema']"/>
		<!--
		<xsl:value-of select="//ows:Operation[@name='GetRecordById']//ows:Parameter[translate(@name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='outputschema']"/>
		option[translate(@key,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz' = '" & lcase(aKey) & "']"
		<xsl:value-of select="//ows:Operation[@name='GetRecordById']//ows:Parameter[@name='outputSchema']"/>
		<xsl:value-of select="//ows:Operation[@name='GetRecordById']//ows:Parameter[@name='OutputSchema']"/>
		-->
	</xsl:template>
	
	
</xsl:stylesheet>

