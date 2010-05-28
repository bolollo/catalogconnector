<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="xml" encoding="UTF-8"/>
<xsl:template match="//CatalogConnector">
<kml xmlns="http://www.opengis.net/kml/2.2">
<Document>
    <name>CatalogConnector - GetRecords Response</name>
    <Style id="s">
        <PolyStyle>
            <color>77209600</color>
        </PolyStyle>
    </Style>
    <xsl:apply-templates select="Catalogue"/>
</Document>
 </kml>
</xsl:template>
<xsl:template match="Catalogue">
    <Folder><name><xsl:value-of select="Id"/></name>
    <xsl:apply-templates select="GetRecordsResponse/Record"/>
    </Folder> 
</xsl:template>
<xsl:template match="GetRecordsResponse/Record">
    <Placemark>
        <name><xsl:value-of select="title"/></name>
        <styleUrl>s</styleUrl>
        <description>
            <div class="record">
                <p><xsl:value-of select="description"/></p>
                <p>ID: <span><xsl:value-of select="identifier"/></span></p>
            </div>
        </description>
    <xsl:apply-templates select="boundingBox[latlon=1]"/>
    </Placemark>
</xsl:template>
<xsl:template match="boundingBox[latlon=1]">
        <xsl:variable  name="Xmin" select="substring-before(lowerCorner, ' ')"/>
        <xsl:variable  name="Ymin" select="substring-after(lowerCorner, ' ')"/>
        <xsl:variable  name="Xmax" select="substring-before(upperCorner, ' ')"/>
        <xsl:variable  name="Ymax" select="substring-after(upperCorner, ' ')"/>                    
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
