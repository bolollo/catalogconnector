<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://www.w3.org/2005/Atom" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:georss="http://www.georss.org/georss" version="1.0">

<xsl:output method="xml" encoding="UTF-8" indent="yes" />
<xsl:param name="getRecordByIdBaseURL"/>
<xsl:param name="SEARCH"/>
<xsl:param name="KML"/>
<xsl:param name="HTML"/>
<xsl:param name="startIndex"/>
<xsl:param name="itemsPerPage"/>
<xsl:param name="PAGINATION"/>
<xsl:param name="SELF"/>

<xsl:template match="//CatalogConnector">
<feed xmlns="http://www.w3.org/2005/Atom" 
          xmlns:opensearch="http://a9.com/-/spec/opensearch/1.1/"
          xmlns:georss="http://www.georss.org/georss">
    <title>CatalogConnector</title>
    <author>
    	<name>CatalogConnector contributors</name>
    	<uri>http://sourceforge.net/projects/catalogconnecto/</uri>
    </author>
    <link href="./"/>
    <link rel="self" type="application/atom+xml" href="{$SELF}"/>
    <link rel="alternate" type="application/vnd.google-earth.kml+xml" href="{$KML}"/>
    <link rel="alternate" type="text/html" href="{$HTML}"/>

    <xsl:if test="count(Catalogue)=1">
	    <xsl:variable name="cat" select="Catalogue[1]/Id"/>
	    <xsl:variable name="totalResults" select="Catalogue[1]/GetRecordsResponse/numberOfRecordsMatched"/> 
	    <opensearch:totalResults><xsl:value-of select="$totalResults"/></opensearch:totalResults>
	    <opensearch:startIndex><xsl:value-of select="$startIndex"/></opensearch:startIndex>
	    <opensearch:itemsPerPage><xsl:value-of select="$itemsPerPage"/></opensearch:itemsPerPage>
	    <link rel="search"
	           type="application/opensearchdescription+xml" 
	           href="{$SEARCH}"
	           title="{$cat} (CatalogConnector)" />	    
	    <xsl:if test="$startIndex > 1"> <!-- Not the first page -->
			<xsl:element name="link">
				<xsl:attribute name="rel">first</xsl:attribute>
				<xsl:attribute name="href"><xsl:value-of select='concat($PAGINATION, "&amp;STARTPOSITION=1")'/></xsl:attribute>
			</xsl:element>
			<xsl:element name="link">
				<xsl:attribute name="rel">previous</xsl:attribute>
				<xsl:attribute name="href"><xsl:value-of select='concat($PAGINATION, "&amp;STARTPOSITION=", string($startIndex - $itemsPerPage))'/></xsl:attribute>
			</xsl:element>
		</xsl:if>
		<xsl:if test="$startIndex &lt;= $totalResults - $itemsPerPage "> <!-- not the last page -->
			<xsl:element name="link">
				<xsl:attribute name="rel">next</xsl:attribute>
				<xsl:attribute name="href"><xsl:value-of select='concat($PAGINATION, "&amp;STARTPOSITION=", string(Catalogue[1]/Position))'/></xsl:attribute>
			</xsl:element>
			<xsl:element name="link">
				<xsl:attribute name="rel">last</xsl:attribute>
				<xsl:attribute name="href"><xsl:value-of select='concat($PAGINATION, "&amp;STARTPOSITION=", string(floor($totalResults div $itemsPerPage) * $itemsPerPage + 1))'/></xsl:attribute>
			</xsl:element>
		</xsl:if>	    	    
    </xsl:if>
    
    <xsl:apply-templates select="Catalogue/GetRecordsResponse/Record"/>
</feed>
</xsl:template>

<xsl:template match="GetRecordsResponse/Record">
	<entry>
        <title><xsl:value-of select="../../Id"/> - <xsl:value-of select="title"/></title>
        <xsl:if test="$getRecordByIdBaseURL">
        	<xsl:variable name="identifier" select="identifier"/> 
    		<link href="{$getRecordByIdBaseURL}{$identifier}" />
    	</xsl:if>
        <id><xsl:value-of select="identifier"/></id>
        <content type="text"><xsl:value-of select="description"/></content>
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
