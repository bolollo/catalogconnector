<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" encoding="UTF-8" indent="yes" />
<xsl:param name="getRecordByIdBaseURL"/>
<xsl:param name="SEARCH"/>
<xsl:param name="ATOM"/>
<xsl:param name="HTML"/>
<xsl:param name="startIndex"/>
<xsl:param name="itemsPerPage"/>
<xsl:param name="PAGINATION"/>
<xsl:param name="SELF"/>

<xsl:template match="//CatalogConnector">
<kml xmlns="http://www.opengis.net/kml/2.2"
     xmlns:atom="http://www.w3.org/2005/Atom"
     xmlns:opensearch="http://a9.com/-/spec/opensearch/1.1/"
     xmlns:opensearchgeo="http://a9.com/-/opensearch/extensions/geo/1.0/">
    
    <xsl:if test="count(Catalogue)=1">
	    <xsl:variable name="cat" select="Catalogue[1]/Id"/>
	    <xsl:variable name="totalResults" select="Catalogue[1]/GetRecordsResponse/numberOfRecordsMatched"/> 
	    <opensearch:totalResults><xsl:value-of select="$totalResults"/></opensearch:totalResults>
	    <opensearch:startIndex><xsl:value-of select="$startIndex"/></opensearch:startIndex>
	    <opensearch:itemsPerPage><xsl:value-of select="$itemsPerPage"/></opensearch:itemsPerPage>
	    <atom:link rel="search"
	           type="application/opensearchdescription+xml" 
	           href="{$SEARCH}"
	           title="{$cat} (CatalogConnector)" />	    
	    <xsl:if test="$startIndex > 1"> <!-- Not the first page -->
	    	<xsl:element  name="atom:link">
				<xsl:attribute name="rel">first</xsl:attribute>
				<xsl:attribute name="href"><xsl:value-of select='concat($PAGINATION, "&amp;STARTPOSITION=1")'/></xsl:attribute>
			</xsl:element>
			<xsl:element name="atom:link">
				<xsl:attribute name="rel">previous</xsl:attribute>
				<xsl:attribute name="href"><xsl:value-of select='concat($PAGINATION, "&amp;STARTPOSITION=", string($startIndex - $itemsPerPage))'/></xsl:attribute>
			</xsl:element>
		</xsl:if>
		<xsl:if test="$startIndex &lt;= $totalResults - $itemsPerPage "> <!-- not the last page -->
			<xsl:element name="atom:link">
				<xsl:attribute name="rel">next</xsl:attribute>
				<xsl:attribute name="href"><xsl:value-of select='concat($PAGINATION, "&amp;STARTPOSITION=", string(Catalogue[1]/Position))'/></xsl:attribute>
			</xsl:element>
			<xsl:element name="atom:link">
				<xsl:attribute name="rel">last</xsl:attribute>
				<xsl:attribute name="href"><xsl:value-of select='concat($PAGINATION, "&amp;STARTPOSITION=", string(floor($totalResults div $itemsPerPage) * $itemsPerPage + 1))'/></xsl:attribute>
			</xsl:element>
		</xsl:if>	    	    
    </xsl:if>
    <atom:link rel="alternate" type="application/atom+xml" href="{$ATOM}"/>
    <atom:link rel="alternate" type="text/html" href="{$HTML}"/>
     
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
        <xsl:if test="$getRecordByIdBaseURL">
        	<xsl:variable name="identifier" select="identifier"/> 
    		<link href="{$getRecordByIdBaseURL}{$identifier}" />
    	</xsl:if>
        <description><xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
            <div class="record">
                <p><xsl:value-of select="description"/></p>
			    <xsl:if test="$getRecordByIdBaseURL">
			    	<xsl:variable name="identifier" select="identifier"/> 
			    	<p class="id"><span><b>UUID:</b></span>
			    	<a href="{$getRecordByIdBaseURL}{$identifier}" target="_blank"><xsl:value-of select="identifier"/></a>
			    	</p>
			    </xsl:if>
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
