<!--
 /*
 * CatalogConnector - OpenSource CSW client
 * http://www.geoportal-idec.cat
 * http://www.icc.cat
 * @author Victor Pascual
 * @author Wladimir Szczerban
 *
 * Copyright (c) 2009, Spatial Data Infrastructure of Catalonia (IDEC)
 * Institut Cartogràfic de Catalunya (ICC)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL COPYRIGHT HOLDERS OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
 -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html >
<head>
<title>CatalogConnector CSW Client</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<!-- OpenSearch autodiscovery sample mixing various catalogues
<link rel="search" type="application/opensearchdescription+xml" href="Connector?Request=GetOpenSearchDescription&Catalogues=IDEC,IDEE,FAO" title="CatalogConnector" />
 -->
<link href="css/catalog.css" rel="stylesheet" type="text/css">
<link href="css/ajaxpagination.css" rel="stylesheet" type="text/css"  />
<!--Ajax Pagination Script- Author: Dynamic Drive (http://www.dynamicdrive.com)-->
<script type="text/javascript" src="scripts/OpenLayers.js"></script>
<script type="text/javascript" src="scripts/prototype.js"></script>
<script type="text/javascript" src="scripts/requests.js"></script>
<script type="text/javascript" src="scripts/easytabs.js"></script>
<script type="text/javascript" src="scripts/map.js"></script>
<!-- EASY TABS 1.2 Produced and Copyright by Koller Juergen www.kollermedia.at -->
<!--Ajax Pagination Script- Author: Dynamic Drive (http://www.dynamicdrive.com)-->
<link rel="stylesheet" href="css/menu.css" TYPE="text/css" MEDIA="screen">
<script src="http://ecn.dev.virtualearth.net/mapcontrol/mapcontrol.ashx?v=6.2&mkt=en-us"></script>
<!-- Importing OpenSearch link autodiscovery tags -->
<c:import url="/Connector">
  <c:param name="Request" value="GetOpenSearchDescription"/>  
  <c:param name="Format" value="HTML"/>
</c:import>
<script type="text/javascript">
var lon =  0;
var lat =  0;
var zoom = 0;
var map, layer, boxes;

function maxMap(){
	loadwindow($(map),600,400);
}

var road = new OpenLayers.Layer.VirtualEarth("Road", { type: VEMapStyle.Road } );
var shaded = new OpenLayers.Layer.VirtualEarth("Shaded", { type: VEMapStyle.Shaded } );
var aerial = new OpenLayers.Layer.VirtualEarth("Aerial", { type: VEMapStyle.Aerial } );
var hybrid = new OpenLayers.Layer.VirtualEarth("Hybrid", { type: VEMapStyle.Hybrid } );
function init(){
    var lon = 5;
    var lat = 40;
    var zoom = 5;

    map = new OpenLayers.Map('map');
	map.addControl(new OpenLayers.Control.LayerSwitcher());
	map.addLayers([shaded,hybrid,road,aerial]);

    boxes = new OpenLayers.Layer.Vector( "Bounding Box" );
    map.addLayer(boxes);

    /*
    This is how we add expandable option to map
    
	vlayer = new OpenLayers.Layer.Vector( "Editable" );
	map.addLayer(vlayer);

    
    var button = new OpenLayers.Control.Button({
        displayClass: "MyButton",
        title:"Toggle map size", 
        trigger: function(){maxMap();}
    });
	
    var panel = new OpenLayers.Control.Panel({defaultControl: button});
    panel.addControls([button]);
    map.addControl(panel);
    */
	
    map.zoomToMaxExtent();

}



function resetAllFields()
{
	document.getElementById("chkallboxes").checked = false;
	document.getElementById("chkbbox").checked = false;
	document.getElementById("any").value = "";
	document.getElementById("title").value = "";
	document.getElementById("description").value = "";
	document.getElementById("subject").value = "";
	document.getElementById("organization").value = "";

	document.getElementById("startPosition").value = "1";
	document.getElementById("maxRecords").value = "10";
}
</script>
</head>
<body  onLoad="resetAllFields();getCapabilities();init();">
<table width="99%" height="98%" align="center" border="1" cellspacing="0" cellpadding="1">
  <tr align="center" bgcolor="#0099CC" height="65">
    <td colspan="2">
        <div id="cc-header">
            <div id="cc-banner">
            	<img src="images/header/catalogconnector_banner.jpg" width="700" height="65" alt="CatalogConnector">
            </div>
            <div id="cc-logo">
            	<img src="images/header/logo_idec.jpg" width="152" height="50" alt="IDEC Logo" border="0">&nbsp;
            	<img src="images/header/logo_usgin.png" width="50" height="50" alt="USGIN Logo" border="0">
            </div>
        </div>
	</td>
  </tr>
  <tr align="center" height="100%" valign="top">
    <td colspan="1" valign="top" width="20%" align="left">
     <form action="" method="get" id="frmRequest" OnSubmit="getRecords();return false" name="frmRequest">
        <table align="center" width="99%" border="0" cellspacing="1" cellpadding="1">          
          
          <tr align="center" valign="middle">
            <td colspan="2" height="200px"><div id="map" class="smallmap" style="border: 1px solid #000000; height: 100%;"></div>           	
          	</td>
          </tr>
          
          <tr>
            <td><br></td>
          </tr>
          
          <tr class="labelText">
            <td colspan="2"><center><b>METADATA SEARCH</b></center></td>
          </tr>
         <tr class="basicText" >
			<td colspan="2"><input type="checkbox" id="chkbbox"  name="chkbbox" value="checkbox">
			Limit Search To Current Map Extent</td>
		</tr>
		
          <tr class="basicText" >
            <td>Projects</td>
            <td><select class="basicText"  name="PROJECT" id="PROJECT" onChange="getCapabilities();">
                <option value="catalogues">catalogues (by default)</option>
                <option value="project1">project1</option>
              </select></td>
          </tr>
          <tr class="basicText" >
            <td width="30%">AnyText: </td>
            <td ><input class="basicText" type="text" class="textbox" id="any" value="" name="any"></td>
          </tr>
          <tr class="basicText" >
            <td>Title: </td>
            <td width="52%"><input class="basicText" type="text" class="textbox" id="title" name="title"></td>
          </tr>
          <tr class="basicText" >
            <td>Description: </td>
            <td width="52%"><input class="basicText" type="text" class="textbox" id="description" name="description"></td>
          </tr>
          <tr class="basicText" >
            <td>Subject: </td>
            <td width="52%"><input class="basicText" type="text" class="textbox" id="subject" name="subject"></td>
          </tr>
          <tr class="basicText" >
            <td>Organization: </td>
            <td width="52%"><input class="basicText" type="text" class="textbox" id="organization" name="organization"></td>
          </tr>
          <tr class="basicText" >
            <td colspan="2"><b>Pagination parameters:</b></td>
          </tr>
          <tr class="basicText" >
            <td>Start position: </td>
            <td width="52%"><input class="basicText" type="text" size="5" value="1" class="textbox" id="startPosition" name="startPosition"></td>
          </tr>
          <tr class="basicText" >
            <td>Max records: </td>
            <td width="52%"><input class="basicText" type="text" size="5" class="textbox" value="10" id="maxRecords" name="maxRecords"></td>
          </tr>
          <tr class="basicText" >
            <td colspan="2"></td>
          </tr>
          
          <tr class="basicText" >
            <td><br></td>
          </tr>
          
          <tr class="labelText" >
            <td colspan="2"><center><b>CATALOGS TO SEARCH</b></center></td>
          </tr>


           <tr class="basicText">
            <td colspan="2">
              <input type="checkbox" id="chkallboxes"  name="chkallboxes" onclick='switchAll();'>
              Select/Deselect All
              &nbsp;&nbsp;&nbsp;&nbsp;
				<input type="button" value="Revalidate" class="textbox" onclick='revalidate();'>
              </td>
          </tr>
          
          <tr class="basicText" >
            <td height="95px" colspan="2"><div id="divCapabilities" style="overflow:auto;border: 1px solid #000000;width:99%; height:99%; z-index:1"></div></td>
          </tr>

          <tr class="basicText" >
            <td colspan="2"></td>
          </tr>
	
          <tr align="right" align="center" valign="middle">            
            <td colspan="2" align="center">
            	<div align="center">
					<div class="buttonwrapper" align="center">
		              		<a class="ovalbutton" href="#" onClick="getRecords()"><span>&nbsp;&nbsp;&nbsp;Search&nbsp;&nbsp;&nbsp;</span></a>
					</div>
				</div>
			</td>	
          </tr>
          
          <tr class="basicText"  valign="bottom" align="left">
            <td colspan="2" valign="bottom"><div style="color:#C00;">*Catalogs listed in red did not provide a valid response to a GetCapabilities request</div></td>
          </tr>
        </table>
        <input type="hidden" value=""  name="bbox" id="bbox" >
        <input type="hidden" value=""  name="catalogues" id="catalogues" >
        <input type="hidden" value="JSON"  name="outputFormat" id="outputFormat" >
        <input type="hidden" value="GetRecords"  name="Request" id="Request" >
        <input type="hidden" value="es" name="language"  id="language">
	</form>
      &nbsp; </td>
    <td width="80%" height="100%" align="left">
    	<div id="divResults" style="position:relative; overflow:auto; height:100%; z-index:1;">    	
    	</div>
    </td>
  </tr>
</table>
<table align="center" width="100%" cellspacing="0" cellpadding="0">
  <tr class="basicText"  valign="top">
    <td valign="top"><p align="center"class="style6"><a href="/catalogConnector/AdminConnector?" target="_blank">administration</a> | <a href="examples.html" target="_blank">examples</a> | <a href="catalogConnector.pdf">documentation</a></p></td>
  </tr>
</table>
</body>
</html>
