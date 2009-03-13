

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
            
            