<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="xml" encoding="UTF-8" indent="yes" />
<xsl:param name="getRecordByIdBaseURL"/>
<xsl:param name="SEARCH"/>
<xsl:param name="KML"/>
<xsl:param name="ATOM"/>
<xsl:param name="startIndex"/>
<xsl:param name="itemsPerPage"/>
<xsl:param name="PAGINATION"/>

<xsl:template match="//CatalogConnector">
<html>
<head>
    <title>CatalogConnector - GetRecords Response</title>
    <style type="text/css">
    	body {
			font: 10px Verdana,Arial,Helvetica,sans-serif;
		}
		h3 {
			font: bold 12px Verdana,Arial,Geneva,Helvetica,sans-serif;
			text-transform: uppercase;
			background-color: #ececff;
			padding: 2px;
			margin: 0px;
		}
		h2 {
			font: bold 16px Verdana,Arial,Geneva,Helvetica,sans-serif;
			text-transform: uppercase;
			background-color: #ececff;
			padding: 2px;
			margin: 0px;			
			text-align: center;
		}
		h1 {
			font: bold 16px Verdana,Arial,Geneva,Helvetica,sans-serif;
		}
		a img {
			border: 0px;
		}
        .catalogue {
			border: 3px solid #F2F2F2;
			padding: 3px;
			background-color: #f6f6f6;
        }
        .record {
			border: 1px solid #F2F2F2;
			padding: 3px;
			margin: 3px;
			background-color: #ffffff;
		}
		div.record:hover {
			background-color:#B3D4EF;
		}
		.results {
			background-color: #ececff;
			font: 13px Verdana,Arial,Geneva,Helvetica,sans-serif;
			text-align: center;
			
		}
		a.paginationlink:hover{
			background-color:#B3D4EF;
		}
		.querystring {
			text-align: center;
		}
    </style>
    <xsl:if test="count(Catalogue)=1">
	    <xsl:variable name="cat" select="Catalogue[1]/Id"/>
	    <xsl:variable name="totalResults" select="Catalogue[1]/GetRecordsResponse/numberOfRecordsMatched"/> 
	    <link rel="search"
	           type="application/opensearchdescription+xml" 
	           href="{$SEARCH}"
	           title="{$cat} (CatalogConnector)" />
	    <meta name="totalResults" content="{$totalResults}"/>
	    <meta name="startIndex" content="{$startIndex}"/>
	    <meta name="itemsPerPage" content="{$itemsPerPage}"/>
    </xsl:if>
</head>
<body>
    <h1>GetRecords Response (<a href="./">CatalogConnector</a>)</h1>
    <div>Alternative formats: <a href="{$ATOM}"><img src="./images/rss32.png" title="Atom"/></a><a href="{$KML}"><img src="./images/kml32.png" title="KML"/></a>
    </div>
    <xsl:apply-templates select="Catalogue"/>
</body>
</html>
</xsl:template>
<xsl:template match="Catalogue">
	<div class="catalogue">
    <h2><xsl:value-of select="Id"/></h2>

    <!-- Pagination stuff -->
    <div class="results">
	    <xsl:variable name="totalResults" select="GetRecordsResponse/numberOfRecordsMatched"/>
		<xsl:if test="$startIndex > 1"> <!-- Not the first page -->
			<xsl:element name="a">
				<xsl:attribute name="class">paginationlink</xsl:attribute>
				<xsl:attribute name="href"><xsl:value-of select='concat($PAGINATION, "&amp;STARTPOSITION=1")'/></xsl:attribute>
				<xsl:text>&lt;&lt;First</xsl:text>
			</xsl:element>
			<xsl:element name="a">
				<xsl:attribute name="class">paginationlink</xsl:attribute>
				<xsl:attribute name="href"><xsl:value-of select='concat($PAGINATION, "&amp;STARTPOSITION=", string($startIndex - $itemsPerPage))'/></xsl:attribute>
				<xsl:text>&lt;Prev</xsl:text>
			</xsl:element>
		</xsl:if>
	
	    Page <b><xsl:value-of select="ceiling($startIndex div $itemsPerPage)"/></b>
	   	of <b><xsl:value-of select="ceiling($totalResults div $itemsPerPage)"/></b>
	   	(results <span><xsl:value-of select="$startIndex"/></span>
	    - <span><xsl:value-of select="$startIndex + count(GetRecordsResponse/Record) - 1"/></span> /
	    <span><xsl:value-of select="$totalResults"/></span>)
	
		<xsl:if test="$startIndex &lt;= $totalResults - $itemsPerPage "> <!-- not the last page -->
			<xsl:element name="a">
				<xsl:attribute name="class">paginationlink</xsl:attribute>
				<xsl:attribute name="href"><xsl:value-of select='concat($PAGINATION, "&amp;STARTPOSITION=", string(Position))'/></xsl:attribute>
				<xsl:text>Next&gt;</xsl:text>
			</xsl:element>
			<xsl:element name="a">
				<xsl:attribute name="class">paginationlink</xsl:attribute>
				<xsl:attribute name="href"><xsl:value-of select='concat($PAGINATION, "&amp;STARTPOSITION=", string(floor($totalResults div $itemsPerPage) * $itemsPerPage + 1))'/></xsl:attribute>
				<xsl:text>Last&gt;&gt;</xsl:text>
			</xsl:element>
		</xsl:if>
	</div>

    <p class="querystring"><b>QUERY:</b><span><xsl:value-of select="QueryString"/></span></p>
    
    <xsl:apply-templates select="GetRecordsResponse/Record"/>
    </div>
</xsl:template>
<xsl:template match="GetRecordsResponse/Record">
	<div class="record">
    <h3><xsl:value-of select="title"/></h3>
    <p><span><b>DESCRIPTION:</b></span><xsl:value-of select="description"/></p>
    <xsl:variable name="identifier" select="identifier"/> 
    <xsl:if test="$getRecordByIdBaseURL">
    	<p class="id"><span><b>UUID:</b></span>
    	<a href="{$getRecordByIdBaseURL}{$identifier}" target="_blank"><xsl:value-of select="identifier"/></a>
    	</p>
    </xsl:if>
    <xsl:apply-templates select="boundingBox[latlon=1]"/>
     </div>
</xsl:template>
<xsl:template match="boundingBox[latlon=1]">
    <p class="bbox"><span><b>BBOX:</b></span><span> <xsl:value-of  select="translate(lowerCorner,' ',',')"/>,<xsl:value-of  select="translate(upperCorner,' ',',')"/></span></p>
</xsl:template>
</xsl:stylesheet>
