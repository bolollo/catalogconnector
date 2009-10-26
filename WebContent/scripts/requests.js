/*
 * 	CatalogConnector - OpenSource CSW client
 * 	http://www.geoportal-idec.cat
 * 	Copyright (C) 2009, Spatial Data Infrastructure of Catalonia (IDEC)	
 * 
 * 	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 * 	This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 
 * @author Victor Pascual
 * @author Wladimir Szczerban
 * @author Dominic Owen
 */
var urlServer="/catalogConnector/Connector";

function sendRequestAll(){
$('divResults').addClassName('loader').show();
new Ajax.Request(urlServer, { parameters: $('frmRequest').serialize(true), OnLoading:function(){ 

    $('divCapabilities').addClassName('loader').show();
   
    },

onSuccess: function(transport){  
alert(transport.responseText);
    //var json = transport.responseText.evalJSON(); 
   //alert(json);
	$('divCapabilities').removeClassName('loader').show();
	$('divResults').innerHTML=transport.responseText;
	$('divCapabilities').addClassName('div');
   }, OnCreate:function(){ 
   
    $('divCapabilities').addClassName('loader').show();
   
    }
  }); 

//End function
}

//adapted function to insert elements after DOM objects
function insertAfter(newElement,targetElement) {
	//target is what you want it to go after. Look for this elements parent.
	var parent = targetElement.parentNode; 
	//if the parents lastchild is the targetElement...
	if(parent.lastchild == targetElement) {
		//add the newElement after the target element.
		parent.appendChild(newElement);
	} else {
		// else the target has siblings, insert the new element between the target and it's next sibling.
		parent.insertBefore(newElement, targetElement.nextSibling);
	}
}

//Does Html Conversion 
function metaDataToHTML(id,url,version,catName,product,encoding){
	var catalogTable = $(id); 	
	var cName = unescape(catName);
	var divID = "div_new"+id;
	
	//this checks to see if metadaata has been inserted yet
	if($(divID)==null){

		var div = document.createElement('div');
		div.id=divID;
		div.innerHTML="<br><br><br><br><br><br><br><br>";
		
		//$(div).setAttribute("align","center");
		
		$(div).style.marginRight ="auto";
		$(div).style.marginLeft = "auto";
		$(div).style.width = "92%";
		$(div).setAttribute("textAlign","left");		
		
		div.addClassName('myloader').show();
				
		insertAfter(div,catalogTable);
		new Ajax.Request(urlServer+'?REQUEST=metaDataToHTML', 
		{
			method: 'get',parameters: {recordID: unescape(id), recordVersion: unescape(version), recordURL: decodeURI(url), productName: escape(product),encodingType: escape(encoding)},
			onSuccess: function(transport)
			{	 
				div.removeClassName('myloader').show();	
				div.innerHTML=transport.responseText;
		  	},
	  		onFailure: function(error)
	  		{ 
				div.removeClassName('myloader').show();	
				div.innerHTML="<div><h1><center><br>This operation is not yet supported for " +cName+"</br></center></h1></div>";
	  		}
	  	});
	}else{
		//This means we are minimizing re-maximizing the div
		invertDisplay(divID);
	}
}

function invertDisplay(div){
	var display = $(div).style.display.toLowerCase();
	//console.debug("display: " + display);
	
	if(display=="none"){

		$(div).style.display="";
	}else{
		$(div).style.display = "none";
	}
}



function ajaxTest(urlServer){
 $('DIV_test').innerHTML="<h2>&nbsp;Searching....</h2>";
						 $('txtTest').value="";						  
	new Ajax.Request(urlServer,
  {
    method:'get',
    onSuccess: function(transport){
      var response = transport.responseText || "no response text";
      //var json=response.evalJSON(); 
    
     
      $('DIV_test').innerHTML="<h1>OK</h1>";
      //alert(response);
      $('txtTest').value=response;
    },
    onFailure: function(){  $('txtTest').value="Error:"+error; }
  });
	

//End function
}

//This method switches all catalogs to selected or unselected
function switchAll()
{
	var cl=new Array();
	var nodes = $A(document.frmRequest.chkCatalog);
	
	nodes.each(function(node)
	{
		if(document.getElementById("chkallboxes").checked == true)
		{
			node.checked=true;
		}
		else{
			node.checked=false;
		}	
	}				
	);
}



//This is where check boxes for catalog selections are handled
var cataloguesJson;
function getCapabilities(){
$( 'divCapabilities' ).innerHTML="";
//console.info("REQUEST=" + urlServer+'?REQUEST=GetCapabilities&outputFormat=JSON&PROJECT='+$F('PROJECT'));

new Ajax.Request(urlServer+'?REQUEST=GetCapabilities&outputFormat=JSON&PROJECT='+$F('PROJECT'), {   method:'get',   onSuccess: function(transport){  
    cataloguesJson = transport.responseText.evalJSON();
    //console.debug(cataloguesJson.length);

   
for(i=0; i < cataloguesJson.length;i++){
			 var cb = document.createElement( "input" );
			  cb.type = "checkbox";cb.id = "chkCatalog";cb.value = cataloguesJson[i].name;cb.checked = false;cb.name="chkCatalog";
			 var text = document.createTextNode( cataloguesJson[i].name+' ('+cataloguesJson[i].title+')');
			 //console.debug(cataloguesJson[i].name);
			 var tr=document.createElement( "br" );
			 $( 'divCapabilities' ).appendChild( cb );
			 $( 'divCapabilities' ).appendChild( text );
			 $( 'divCapabilities' ).appendChild( tr );
}

    },OnCreate:function(){     
    $('divCapabilities').addClassName('loader');   
    }
    }); 


}

var catalogsArray= new Array();

function getRecords(){

	if($('chkbbox').checked){
		bounds = new OpenLayers.Bounds();
		bounds=map.getExtent();
		$('bbox').value=bounds.toBBOX();
	}else{
	$('bbox').value="";
	}

	catalogsArray=showOptions();
	var catalogs=catalogsArray.join(',');

	if(catalogs==""){

		alert("you must select one or more catalogues");

	}else{
	createTabs();
		$('catalogues').value=catalogs;
		createRequestByCatalogue();
		
		//This immediately selects this first tab
		easytabs(1,1);
	}

}



function createTabs(){


if($( 'divResults' ).innerHTML!=""){$( 'divResults' ).innerHTML=" ";}
catalogsArray=showOptions();

tabcount[0] = catalogsArray.size();


htmlText=new Array();
htmlText.push('<div id="menutab" class="menutab"><ul>');
var i=1;
catalogsArray.each(function(item) {


htmlText.push('<li><a href="#" onmouseover="return false" onfocus="return false;" onclick="easytabs(\'1\', \''+i+'\');"  title="" id="tablink'+i+'">'+item+'</a></li>');


i=i+1;
});

htmlText.push('</ul></div>');

var j=1;
catalogsArray.each(function(item) {
htmlText.push('<div style="border: 1px solid #2b66a5;" id="catalogue'+j+'"><div id="pag_'+item+'"></div><div id="extr_'+item+'"></div> <div id="div_'+item+'" class="infoTab"></div></div>');

j=j+1;
});

$( 'divResults' ).innerHTML=htmlText.join(' ');

}

function showOptions(){
		var cl=new Array();
		
		var nodes = $A(document.frmRequest.chkCatalog);

		nodes.each(function(node){
		if(node.checked){
		cl.push(node.value);
		}
				
			});
			
			return cl;
			
	}



function createRequestByCatalogue(){

var req="Request="+$F('Request')+"&Project="+$F('PROJECT')+"&startPosition="+$F('startPosition')+"&maxRecords="+$F('maxRecords')+"&any="+$F('any')+"&bbox="+$F('bbox')+"&description="+$F('description')+"&organization="+$F('organization')+"&outputFormat="+$F('outputFormat')+"&subject="+$F('subject')+"&title="+$F('title')+"&language="+$F('language')+"&";
	
	catalogsArray.each(function(item) {
	
	var cat=item;
	
	var reqF=req+"catalogues="+cat+"&";
	
	//console.debug("REQUEST: "+ reqF);
	sendRequestByCatalogue(cat, reqF,true);
	});


}

function sendRequestByCatalogue(catalogue, request,task){
	var divCatalogue="div_"+catalogue;
	$(divCatalogue).addClassName('infoTabL').show();
	$(divCatalogue).innerHTML="<h1><b>  Searching....</b></h1>";
	//console.info("REQUEST = "+request);
	
new Ajax.Request(urlServer, { parameters: request,

	onSuccess: function(transport){  
	
	
	$(divCatalogue).removeClassName('infoTabL').show();
	if(transport.responseText.length == 0){
		$(divCatalogue).innerHTML="<h2><center>Server did not respond. Make sure Catalog URL is correct.</center></h2>";
	}else{
		var json = transport.responseText.evalJSON(); 
		parseWriteCatalogues(divCatalogue,json,task);  

	}	
   }, 
   onFailure: function(error){    
	   //console.error("Error en la peticio");
	   $(divCatalogue).removeClassName('infoTabL').show();
	   $(divCatalogue).innerHTML="<h2><center>Server did not respond. Make sure Catalog URL is correct.</center></h2>";
    }
  });
}



function extracPr(catalogue){


var ct=null;
	for(i=0; i < cataloguesJson.length;i++){
	
		if(cataloguesJson[i].name==catalogue){
		
		 ct= cataloguesJson[i];
		}
	
	}

return ct
}

function doMetaDataSelect(){
	
}

function parseWriteCatalogues(divCatalogue,json,task){
	
	
	//console.info("clean divs");
	$(divCatalogue).innerHTML="";
	//console.info(json);
	//console.info(json+".");
	
	
	
	if(json==null){ $(divCatalogue).innerHTML="<p>Invalid response returned</p>"+json;return }
	if(json.GetRecordsResponse==null){ $(divCatalogue).innerHTML="<p>Invalid response returned</p>"+json;return }
	//var candidates=json.numberOfRecordsMatched;
	var candidates=json.GetRecordsResponse.numberOfRecordsReturned;
	var cName=divCatalogue.replace('div_','');
	//console.warn(cName);
	var ct = extracPr(cName);
	var pagination = 'pag_'+json.Id;
	var pos = json.Position;
	//var boundingBoxResponse = new Array(0,0,0,0);
	var htmlText=new Array();
	var version = escape(ct["csw-version"]);
	var url = encodeURI(ct.urlcatalog);
	var prod = escape(ct["product"]);
	var encoding = escape(ct["xml-encoding"]);
	
	//console.info(ct);
	
	if(candidates >1){				
		var req=json.QueryString+"&";					
		req=req.replace(/%26/g,'&');
		req=req.replace(/%3D/g,'=');
		htmlText.push('<table border="0"  width="100%">');
		htmlText.push('<tr><td><b>Found:'+json.GetRecordsResponse.numberOfRecordsMatched+'</b></tr></td>');
		htmlText.push('<tr><td>');
		for (i=0;i < json.GetRecordsResponse.Record.length;i++){
			/*
			var lc = json.GetRecordsResponse.Record[i].boundingBox.lowerCorner.split(" ");
			var uc = json.GetRecordsResponse.Record[i].boundingBox.upperCorner.split(" ");
			if (lc[0] < boundingBoxResponse[0]){
				boundingBoxResponse[0] = lc[0];
			}
			if (lc[1] < boundingBoxResponse[1]){
				boundingBoxResponse[1] = lc[1];
			}
			if (uc[0] > boundingBoxResponse[2]){
				boundingBoxResponse[2] = uc[0];
			}
			if (uc[1] > boundingBoxResponse[3]){
				boundingBoxResponse[3] = uc[1];
			}
			*/

			
			var identifier = escape(json.GetRecordsResponse.Record[i].identifier);
			
			htmlText.push('<table id='+identifier+' border="0" style="border:1px solid #F2F2F2" width="100%" onmouseover="addBox(this,\''+json.GetRecordsResponse.Record[i].boundingBox.lowerCorner+'\',\''+json.GetRecordsResponse.Record[i].boundingBox.upperCorner+'\');" onmouseout="removeBox(this);" >');
			
			htmlText.push('<tr bgcolor="#ECECFF">');
			
			htmlText.push('<td width="73%"><h1>'+json.GetRecordsResponse.Record[i].title+'</h1></td>');
			htmlText.push('<td width="14%"><center><a href="#" onclick="javascript:metaDataToHTML(\''+identifier+'\',\''+url+'\',\''+version+'\',\''+escape(cName)+'\',\''+prod+'\',\''+encoding+'\');">Show/Hide Metadata</a></center></td>');
			htmlText.push('<td width="13%"><center><a href="'+ct.urlcatalog+'?request=GetRecordById&elementSetName=full&outputFormat=application/xml&service=CSW&id='+json.GetRecordsResponse.Record[i].identifier+'&version='+ct["csw-version"]+'" target="_blank">Raw Metadata File</a></center></td>');			
			htmlText.push('</tr>');
			
			
			htmlText.push('<tr><td colspan="3"><h1>Description:</h1>'+json.GetRecordsResponse.Record[i].description+'</tr></td>');
			
			htmlText.push('</table>');
		}
		htmlText.push('</td></tr></table>');
		/*
		var md_bounds = new OpenLayers.Bounds(boundingBoxResponse[0], boundingBoxResponse[1], boundingBoxResponse[2], boundingBoxResponse[3]);
		map.zoomToExtent(md_bounds, false);
		*/						
	}else if(candidates==1){

		var identifier = escape(json.GetRecordsResponse.Record.identifier);
		
	
		var req=json.QueryString+"&";					
		req=req.replace(/%26/g,'&');
		req=req.replace(/%3D/g,'=');
		htmlText.push('<table id='+identifier+' border="0"  width="100%">');
		htmlText.push('<tr><td><b>Found:'+json.GetRecordsResponse.numberOfRecordsMatched+'</b></tr></td>');		
		htmlText.push('<tr><td>');
		htmlText.push('<table border="0" style="border:1px solid #F2F2F2" width="100%" onmouseover="addBox(this,\''+json.GetRecordsResponse.Record.boundingBox.lowerCorner+'\',\''+json.GetRecordsResponse.Record.boundingBox.upperCorner+'\');" onmouseout="removeBox(this);" >');
		htmlText.push('<tr bgcolor="#ECECFF">');
		htmlText.push('<td width="73%"><h1>'+json.GetRecordsResponse.Record.title+'</h1></td>');
		htmlText.push('<td width="14%"><center><a href="#" onclick="javascript:metaDataToHTML(\''+identifier+'\',\''+url+'\',\''+version+'\',\''+escape(cName)+'\',\''+prod+'\',\''+encoding+'\');">Show/Hide Metadata</a></center></td>');
		htmlText.push('<td width="13%"><center><a href="'+ct.urlcatalog+'?request=GetRecordById&elementSetName=full&outputFormat=application/xml&service=CSW&id='+json.GetRecordsResponse.Record.identifier+'&version='+ct["csw-version"]+'" target="_blank">Raw Metadata File</a></center></td>');	
		htmlText.push('</tr>');
		htmlText.push('<tr><td colspan="3"><h1>Description:</h1>'+json.GetRecordsResponse.Record.description+'</tr></td>');
		htmlText.push('</table>');											
		
		htmlText.push('</td></tr></table>');		
	}
	else{
		htmlText.push('<p> No records found </p>');
	}					
	$(divCatalogue).innerHTML=htmlText.join(' ');
	//var nameCat=divCatalogue.replace('div_','');
	for (j=1;j < catalogsArray.size()+1;j++){
		var tb="tablink"+j;
		var cp=$(tb).innerHTML
		if(cName == cp){
			var po=json.GetRecordsResponse.numberOfRecordsMatched;
			if(!po){po=0;}
			$(tb).innerHTML=cName+ " ("+po+")";
		}
	} 
	$(divCatalogue).style.display='block';
	//console.info("task:"+task);
	if(task){
		//console.info(json.GetRecordsResponse.numberOfRecordsMatched/$F('maxRecords'));
		var pt=json.GetRecordsResponse.numberOfRecordsMatched/$F('maxRecords');
		if(pt=="NaN"){pt=0;}
		var numberPages=Math.ceil(pt);
		var cmExtra=new Array();
		if(numberPages > 15){
			var extr='extr_'+json.Id;
			for (j=0;j < numberPages;j++){
				var position=parseInt((j*$F('maxRecords'))+1);
				var rs='Request=GetRecords&outputFormat=JSON&catalogues='+json.Id+'&startPosition='+position+"&"+req;
				cmExtra += '<option value="'+rs+'">go to page:'+j+'</option>';
			}
			$(extr).innerHTML='<select onChange="sendRequestByCatalogue(\''+json.Id+'\', this.value,false)"><option selected>Go to page</option>'+cmExtra+'</select>';
			numberPages=15;
		}
		if(numberPages >1){
			var pg=new Array();
			for (k=0;k < numberPages;k++){
				var position=parseInt((k*$F('maxRecords'))+1);
				pg.push('Request=GetRecords&outputFormat=JSON&catalogues='+json.Id+'&startPosition='+position+"&"+req);
			}
			//console.info(pagination);
			var	catPag=new ajaxpageclass.createBook(
				{pages:pg,selectedpage:0}
				, json.Id, [pagination]);
		}
	}
	json=null;
}


