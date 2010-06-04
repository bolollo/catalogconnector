<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://www.w3.org/2005/Atom" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:georss="http://www.georss.org/georss" version="1.0">
<xsl:output method="xml" encoding="UTF-8" indent="yes" />

<xsl:template match="//CatalogConnector">
<feed xmlns="http://www.w3.org/2005/Atom" 
          xmlns:opensearch="http://a9.com/-/spec/opensearch/1.1/"
          xmlns:georss="http://www.georss.org/georss">
    <title>CatalogConnector</title>
    <author>CatalogConnector</author>
    <xsl:apply-templates select="Catalogue/GetRecordsResponse/Record"/>
</feed>
</xsl:template>

<xsl:template match="GetRecordsResponse/Record">
	<entry>
        <title><xsl:value-of select="../../Id"/> - <xsl:value-of select="title"/></title>
        <content type="text"><xsl:value-of select="description"/></content>
        <id><xsl:value-of select="identifier"/></id>
        <xsl:apply-templates select="boundingBox[latlon=1]"/>
     </entry>
</xsl:template>

<xsl:template match="boundingBox[latlon=1]">
    <xsl:variable name="Xmin" select="substring-before(lowerCorner, ' ')"/>
    <xsl:variable name="Ymin" select="substring-after(lowerCorner, ' ')"/>
    <xsl:variable name="Xmax" select="substring-before(upperCorner, ' ')"/>
    <xsl:variable name="Ymax" select="substring-after(upperCorner, ' ')"/>   
    <georss:box>
        <xsl:value-of select="$Ymin"/><xsl:text>&#x20;</xsl:text>
        <xsl:value-of select="$Xmin"/><xsl:text>&#x20;</xsl:text>
        <xsl:value-of select="$Ymax"/><xsl:text>&#x20;</xsl:text>
        <xsl:value-of select="$Xmax"/>
    </georss:box>
</xsl:template>
</xsl:stylesheet>
