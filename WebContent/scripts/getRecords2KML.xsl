<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="xml" encoding="UTF-8" indent="yes" />
<xsl:template match="//CatalogConnector">
<kml xmlns="http://www.opengis.net/kml/2.2">
<Document>
    <name>CatalogConnector - GetRecords Response</name>
    <Style id="tronja">
		<LineStyle>
			<color>ff0080ff</color>
			<width>2</width>
		</LineStyle>
		<PolyStyle>
			<color>4c0080ff</color>
		</PolyStyle>
	</Style>
    <xsl:apply-templates select="Catalogue"/>
</Document>
 </kml>
</xsl:template>
<xsl:template match="Catalogue" xmlns="http://www.opengis.net/kml/2.2">
    <Folder>
    <name><xsl:value-of select="Id"/></name>
    <xsl:apply-templates select="GetRecordsResponse/Record"/>
    </Folder> 
</xsl:template>
<xsl:template match="GetRecordsResponse/Record" xmlns="http://www.opengis.net/kml/2.2">
	<xsl:element name="Placemark">
		<xsl:attribute name="id"><xsl:value-of select="identifier"/></xsl:attribute>
        <name><xsl:value-of select="title"/></name>
        <description><xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
            <div class="record">
                <p><xsl:value-of select="description"/></p>
                <p>ID: <span><xsl:value-of select="identifier"/></span></p>
            </div><xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
        </description>
    <xsl:apply-templates select="boundingBox[latlon=1]"/>
	</xsl:element>
</xsl:template>
<xsl:template match="boundingBox[latlon=1]" xmlns="http://www.opengis.net/kml/2.2">
        <xsl:variable name="Xmin" select="substring-before(lowerCorner, ' ')"/>
        <xsl:variable name="Ymin" select="substring-after(lowerCorner, ' ')"/>
        <xsl:variable name="Xmax" select="substring-before(upperCorner, ' ')"/>
        <xsl:variable name="Ymax" select="substring-after(upperCorner, ' ')"/>
        <styleUrl>#tronja</styleUrl>                    
        <Polygon>
        <outerBoundaryIs>
            <LinearRing>
                <coordinates>
                    <xsl:value-of select="$Xmin"/>,<xsl:value-of select="$Ymin"/><xsl:text>&#x20;</xsl:text>
                    <xsl:value-of select="$Xmin"/>,<xsl:value-of select="$Ymax"/><xsl:text>&#x20;</xsl:text>
                    <xsl:value-of select="$Xmax"/>,<xsl:value-of select="$Ymax"/><xsl:text>&#x20;</xsl:text>
                    <xsl:value-of select="$Xmax"/>,<xsl:value-of select="$Ymin"/><xsl:text>&#x20;</xsl:text>
                    <xsl:value-of select="$Xmin"/>,<xsl:value-of select="$Ymin"/>
                </coordinates>
            </LinearRing>
        </outerBoundaryIs>
    </Polygon>    
</xsl:template>
</xsl:stylesheet>
