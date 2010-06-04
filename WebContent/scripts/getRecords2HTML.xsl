<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="xml" encoding="UTF-8" indent="yes" />
<xsl:template match="//CatalogConnector">
<html>
<head>
    <title>CatalogConnector - GetRecords Response</title>
    <style type="text/css">
    	body {
    		font-family: arial,sans-serif;
    		font-size: 13px;
    	}
        .catalogue {
            border: 1px solid #0000cc;
            padding: 10px;
            margin: 10px;
            background-color: #ddddff;
        }
        .record {
            border: 1px solid #cc0000;
            padding: 10px;
            margin: 10px;
            background-color: #ffdddd;
        }
        .bbox, .id {
            border: 1px solid #000000;
            padding: 5px;
            background-color: #dddddd;
        }
    </style>
</head>
<body>
    <h1>CatalogConnector - GetRecords Response</h1>
    <xsl:apply-templates select="Catalogue"/>
</body>
</html>
</xsl:template>
<xsl:template match="Catalogue">
	<div class="catalogue">
    <h2><xsl:value-of select="Id"/></h2>
    <p class="querystring">Query String: <span><xsl:value-of select="QueryString"/></span></p>
    <p class="results">Showing results <span> <xsl:value-of select="Position - GetRecordsResponse/numberOfRecordsReturned"/></span>
        - <span><xsl:value-of select="Position - 1"/></span> /
        <span><xsl:value-of select="GetRecordsResponse/numberOfRecordsMatched"/></span>.</p>
    <xsl:apply-templates select="GetRecordsResponse/Record"/>
    </div>
</xsl:template>
<xsl:template match="GetRecordsResponse/Record">
	<div class="record">
    <h3><xsl:value-of select="title"/></h3>
    <p><xsl:value-of select="description"/></p>
    <p class="id">ID: <span><xsl:value-of select="identifier"/></span></p>
    <xsl:apply-templates select="boundingBox[latlon=1]"/>
     </div>
</xsl:template>
<xsl:template match="boundingBox[latlon=1]">
    <p class="bbox">BBOX: <span> <xsl:value-of  select="translate(lowerCorner,' ',',')"/>,<xsl:value-of  select="translate(upperCorner,' ',',')"/></span></p>
</xsl:template>
</xsl:stylesheet>
