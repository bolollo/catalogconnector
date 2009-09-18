/*
var lon = 5;
var lat = 40;
var zoom = 3;
var map, layer;

function init(){
	map = new OpenLayers.Map('map');
    layer = new OpenLayers.Layer.WMS( "OpenLayers WMS",
    	"http://labs.metacarta.com/wms/vmap0", {layers: 'basic'} );
    map.addLayer(layer);
    //map.addControl(control);
    map.setCenter(new OpenLayers.LonLat(lon, lat), zoom);
}
*/

function addBox(object, lowerCorner, upperCorner){
	object.className = "metadata-selected";
	//object.style.backgroundColor = "#AAAAAA";
	var lc = lowerCorner.split(" ");
	var uc = upperCorner.split(" ");
	var md_bounds = new OpenLayers.Bounds(lc[0], lc[1], uc[0], uc[1]);
	map.zoomToExtent(md_bounds, true);
	map.zoomOut();
	var box = new OpenLayers.Feature.Vector(md_bounds.toGeometry());
	boxes.addFeatures(box);
	 //console.debug("addBox");
	 /*
	 for (var i = 0; i < box_extents.length; i++) {
	    ext = box_extents[i];
	    bounds = new OpenLayers.Bounds(ext[0], ext[1], ext[2], ext[3]);
	    box = new OpenLayers.Feature.Vector(bounds.toGeometry());
	    boxes.addFeatures(box);
	}
	*/
}

function removeBox(object){
	 //console.debug("removeBox");
	 object.className = "";
	 //object.style.backgroundColor = "";
	 boxes.destroyFeatures();
}
            
            