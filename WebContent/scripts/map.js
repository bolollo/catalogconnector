function selectMetadata(object){
	object.className = "metadata-selected";
}

function unselectMetadata(object){
	object.className = "";
}

function addBox(latlon, lowerCorner, upperCorner){
	lowerCorner = String.interpret(lowerCorner);
	upperCorner = String.interpret(upperCorner);
	lowerCorner = lowerCorner.strip();
	upperCorner = upperCorner.strip();
	var lc = $w(lowerCorner);
	var uc = $w(upperCorner);
	var xmin = 0;
	var ymin = 0;
	var xmax = 0;
	var ymax = 0;
	if (latlon == 0){
		xmin = lc[1];
		ymin = lc[0];
		xmax = uc[1];
		ymax = uc[0];
	}else{
		xmin = lc[0];
		ymin = lc[1];
		xmax = uc[0];
		ymax = uc[1];
	}
	var md_bounds = new OpenLayers.Bounds(xmin, ymin, xmax, ymax);
	map.zoomToExtent(md_bounds, false);
	map.zoomOut();
	
	/////////TODO: Figure out how to set minZoom in OpenLayers//////////
	//This quickly takes care of zoom needs based on scale, but not
	//very accurately or efficiently..
	if((Math.abs(ymin-ymax)<1)||(Math.abs(ymax-ymin)<1)){
		map.zoomOut();
	}if((Math.abs(ymin-ymax)<.5)&&(Math.abs(ymax-ymin)<.5)){
		map.zoomOut();
		map.zoomOut();
		map.zoomOut();
	}if((Math.abs(ymin-ymax)<.1)&&(Math.abs(ymax-ymin)<.1)){
		map.zoomOut();
	}
	//////////The above block of code needs fixing (min zoom)///////////
	
	
	boxes.destroyFeatures();
	var box = new OpenLayers.Feature.Vector(md_bounds.toGeometry());
	boxes.addFeatures(box);
	boxes.redraw();
	
	/*
	 * this is for create a group of bbox's of all the results
	 for (var i = 0; i < box_extents.length; i++) {
	    ext = box_extents[i];
	    bounds = new OpenLayers.Bounds(ext[0], ext[1], ext[2], ext[3]);
	    box = new OpenLayers.Feature.Vector(bounds.toGeometry());
	    boxes.addFeatures(box);
	}
	*/
}
            
            