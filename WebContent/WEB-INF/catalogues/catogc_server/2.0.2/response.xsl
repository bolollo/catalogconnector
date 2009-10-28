<?xml version="1.0"?>
<xsl:stylesheet version="1.1" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:csw="http://www.opengis.net/cat/csw/2.0.2"
xmlns:dc="http://purl.org/dc/elements/1.1/"
xmlns:ows="http://www.opengis.net/ows"
>
<xsl:output method="xml" version="1.0" encoding="ISO-8859-1" indent="yes" />
  <xsl:template match="/">
      <GetRecordsResponse>
       <xsl:for-each select="/csw:GetRecordsResponse/csw:SearchResults">
           <numberOfRecordsMatched><xsl:value-of select="@numberOfRecordsMatched"/></numberOfRecordsMatched>
           <numberOfRecordsReturned><xsl:value-of select="@numberOfRecordsReturned"/></numberOfRecordsReturned>
           <nextRecord><xsl:value-of select="@nextRecord"/></nextRecord>
           <xsl:for-each select="/csw:GetRecordsResponse/csw:SearchResults/csw:Record">
            <Record>
                <title><xsl:value-of select="dc:title"/></title>
                <description><xsl:value-of select="dc:description"/></description>
                <identifier><xsl:value-of select="dc:identifier"/></identifier>
                <boundingBox>
                    <latlon>1</latlon>
                    <lowerCorner>
                        <xsl:value-of select="ows:BoundingBox/ows:LowerCorner"/>
                    </lowerCorner>
                    <upperCorner>
                        <xsl:value-of select="ows:BoundingBox/ows:UpperCorner"/>
                    </upperCorner>
                </boundingBox>
            </Record>
           </xsl:for-each>
       </xsl:for-each>
       </GetRecordsResponse>
  </xsl:template>
</xsl:stylesheet>