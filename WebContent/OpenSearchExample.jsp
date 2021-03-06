<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>CatalogConnector and OpenLayers integration example (via OpenSearch)</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" href="css/opensearch-openlayers.css" type="text/css"><!-- Importing OpenSearch link autodiscovery tags -->
	<c:import url="/Connector">
		<c:param name="Request" value="GetOpenSearchDescription"/>
		<c:param name="Format" value="HTML"/>
	</c:import>
	<script src="http://www.openlayers.org/api/OpenLayers.js" type="text/javascript"></script>
	<script src="scripts/opensearch-openlayers/Format/Atom.js" type="text/javascript"></script>
	<script src="scripts/opensearch-openlayers/Format/OpenSearchDescription.js" type="text/javascript"></script>
	<script src="scripts/opensearch-openlayers/Strategy/OpenSearch.js" type="text/javascript"></script>
	<script src="scripts/opensearch-openlayers/Control/OpenSearch.js" type="text/javascript"></script>
	<script type="text/javascript">

    var map;

    function init() {
        // Generic OpenLayers options
        OpenLayers.IMAGE_RELOAD_ATTEMPTS = 2;
        OpenLayers.Util.onImageLoadErrorColor = "transparent";

        /**********
         * Layers *
         **********/
        var osmMapnik = new OpenLayers.Layer.OSM("OpenStreetMap - Mapnik", [
	            "http://a.tile.openstreetmap.org/\${z}/\${x}/\${y}.png",
	            "http://b.tile.openstreetmap.org/\${z}/\${x}/\${y}.png",
	            "http://c.tile.openstreetmap.org/\${z}/\${x}/\${y}.png"
	        ], { transitionEffect : 'resize'});

        var layers = [osmMapnik];
        
        /************
         * Controls *
         ************/
        var mapControls = [
	        new OpenLayers.Control.Navigation(),
	        new OpenLayers.Control.LayerSwitcher(),
	        new OpenLayers.Control.PanZoomBar(),
	        new OpenLayers.Control.Attribution({separator : "<br/>"}),
	        new OpenLayers.Control.MousePosition()
        ];

        /***************
         * Map Options *
         ***************/
        var mapOptions = {
            controls : mapControls,
            projection : new OpenLayers.Projection("EPSG:900913"),
            displayProjection : new OpenLayers.Projection("EPSG:4326"),
            units : "m",
            numZoomLevels : 18,
            maxResolution : 156543.0339,
            maxExtent : new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508.34)
        };

        /*********************
         * Map instantiation *
         *********************/
        map = new OpenLayers.Map('map', mapOptions);
        map.addLayers(layers);

        if (!map.getCenter()) {
            map.zoomToMaxExtent();
        }

        /**************
         * OpenSearch *
         **************/
        OpenLayers.loadURL("Connector", "?Request=GetOpenSearchDescription", this, onOpenSearchJSONDescriptions);

        // Add OpenSearch Control and load search engines
        function onOpenSearchJSONDescriptions(response) {
	        map.addControl(new OpenLayers.Control.OpenSearch({
	            div : $('os'),
	            descriptions: eval(response.responseText)
	        }));
        }
    }
	</script>
</head>

<body onload="init();" id="body">
	<div id="leftbar">
		<div id="os"></div>
	</div>
	<div id="map"></div>
</body>
</html>
