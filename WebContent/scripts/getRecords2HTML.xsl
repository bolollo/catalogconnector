<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="xml" encoding="UTF-8" indent="yes" />
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
    <p class="querystring"><b>QUERY:</b><span><xsl:value-of select="QueryString"/></span></p>
    <p class="results">Showing results <b><span><xsl:value-of select="Position - GetRecordsResponse/numberOfRecordsReturned"/></span>
        - <span><xsl:value-of select="Position - 1"/></span> /
        <span><xsl:value-of select="GetRecordsResponse/numberOfRecordsMatched"/></span></b>.</p>
    <xsl:apply-templates select="GetRecordsResponse/Record"/>
    </div>
</xsl:template>
<xsl:template match="GetRecordsResponse/Record">
	<div class="record">
    <h3><xsl:value-of select="title"/></h3>
    <p><span><b>DESCRIPTION:</b></span><xsl:value-of select="description"/></p>
    <p class="id"><span><b>UUID:</b></span><span><xsl:value-of select="identifier"/></span></p>
    <xsl:apply-templates select="boundingBox[latlon=1]"/>
     </div>
</xsl:template>
<xsl:template match="boundingBox[latlon=1]">
    <p class="bbox"><span><b>BBOX:</b></span><span> <xsl:value-of  select="translate(lowerCorner,' ',',')"/>,<xsl:value-of  select="translate(upperCorner,' ',',')"/></span></p>
</xsl:template>
</xsl:stylesheet>
