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
var schemaInfo = new Object;
var currSchema = new Object;
var currMetadataSchema = new Object;

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

//url,json.GetRecordsResponse.Record[i].identifier,version
function loadMetaData(url,identifier,version,cname){
	var outSchemaSetting = "";
	if(currSchema[cname]!="Default"){
		outSchemaSetting = '&outputSchema='+currSchema[cname]
	}	
	window.open(url+'?request=GetRecordById&elementSetName=full&outputFormat=application/xml&service=CSW&id='+identifier+'&version='+version+outSchemaSetting);
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

		var outSchemaSetting = "";
		if(currSchema[cName]!="Default"){
			outSchemaSetting = currSchema[cName];
		}	

		//This remembers what the drop box was when metadata was loaded
		currMetadataSchema[cName]=currSchema[cName];
		
		div.style.marginRight ="auto";
		div.style.marginLeft = "auto";
		div.style.width = "92%";
		div.setAttribute("textAlign","left");		
		
		div.addClassName('loader').show();
				
		insertAfter(div,catalogTable);
		new Ajax.Request(urlServer+'?REQUEST=metaDataToHTML', 
		{
			method: 'get',parameters: {recordID: unescape(id), recordVersion: unescape(version), recordURL: decodeURI(url), productName: escape(product),encodingType: escape(encoding),outSchema: outSchemaSetting},
			onSuccess: function(transport)
			{	 
				div.removeClassName('loader').show();	
				div.innerHTML=transport.responseText;
		  	},
	  		onFailure: function(error)
	  		{ 
				div.removeClassName('loader').show();	
				div.innerHTML="<div><h1><center><br>This operation is not yet supported for this outputschema</br></center></h1></div>";
	  		}
	  	});
	}else if(currMetadataSchema[cName]!=currSchema[cName]){
		currMetadataSchema[cName]=currSchema[cName];
		
		var outSchemaSetting = "";
		if(currSchema[cName]!="Default"){
			outSchemaSetting = currSchema[cName];
		}	
		
		$(divID).innerHTML="<br><br><br><br><br><br><br><br>";
		$(divID).addClassName('loader').show();
		
		new Ajax.Request(urlServer+'?REQUEST=metaDataToHTML', 
		{
			method: 'get',parameters: {recordID: unescape(id), recordVersion: unescape(version), recordURL: decodeURI(url), productName: escape(product),encodingType: escape(encoding),outSchema: outSchemaSetting},
			onSuccess: function(transport)
			{	 
				$(divID).removeClassName('loader').show();	
				$(divID).innerHTML=transport.responseText;
		  	},
			onFailure: function(error)
			{ 
		  		$(divID).removeClassName('loader').show();	
		  		$(divID).innerHTML="<div><h1><center><br>This operation is not yet supported for this outputschema</br></center></h1></div>";
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




//This method switches all catalogs to selected or unselected
function switchAll()
{
	var nodes = $A(document.frmRequest.chkCatalog);
	
	nodes.each(function(node)
	{
		if(document.getElementById("chkallboxes").checked == true)
		{
			if(!node.disabled)
			node.checked=true;
		}
		else{
			if(!node.disabled)
			node.checked=false;
		}	
	}				
	);
}

function updateCheckbox(nameKey,failed){
	
	var nodes = $A(document.frmRequest.chkCatalog);
	
	nodes.each(function(node)
	{
		if(node.value==nameKey){
			if(!failed){
				node.disabled=false;
				$(nameKey+"font").style.color="black";
			}
			else{
				$(nameKey+"font").style.color="red";
			}
			throw $break;
		}
	}				
	);
	
}


//Each catalog will send one of these out and update itself in the catalog list
//to show that it is valid
function getIndividualSchema(nameKey){
	new Ajax.Request(urlServer+'?REQUEST=GetIndivSchemas&outputFormat=JSON&PROJECT='+$F('PROJECT'), 
		{
			method:'get',
			parameters: {IndivNameKey: unescape(nameKey)},
			onSuccess: function(transport){  
				updateCheckbox(nameKey,false);
				schemaInfo[nameKey]= transport.responseText.split(",");
				currSchema[nameKey]="Default";
		    }, onFailure: function(error){
		    	updateCheckbox(nameKey,true);
		    }
	    }); 
}

//This is where check boxes for catalog selections are handled
var cataloguesJson;
function getCapabilities(){
 $( 'divCapabilities' ).innerHTML="";


new Ajax.Request(urlServer+'?REQUEST=GetCapabilities&outputFormat=JSON&PROJECT='+$F('PROJECT'), {   method:'get',   
	onSuccess: function(transport){  
    cataloguesJson = transport.responseText.evalJSON();
    //console.debug(cataloguesJson);
		for(i=0; i < cataloguesJson.length;i++){
					 var cb = document.createElement( "input" );
					  cb.type = "checkbox";cb.id = "chkCatalog";cb.value = cataloguesJson[i].name;cb.checked = false;
					  cb.name="chkCatalog";cb.disabled=true;
					 var text = document.createTextNode( cataloguesJson[i].name+' ('+cataloguesJson[i].title+')');

					 //This font/id are used for validation stuff
					 var font = document.createElement("font");
					 font.id = cataloguesJson[i].name+"font";
					 font.style.color = "grey";
					 font.appendChild(text);
					
					 var tr=document.createElement( "br" );
					 $( 'divCapabilities' ).appendChild( cb );
					 $( 'divCapabilities' ).appendChild( font );
					 $( 'divCapabilities' ).appendChild( tr );
		}
		for(i=0; i < cataloguesJson.length;i++){
			getIndividualSchema(cataloguesJson[i].name);
		}
    },OnCreate:function(){     
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
		
		//This immediately selects this first tab when searching for multiple catalogs
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
	$(divCatalogue).addClassName('blueloader').show();
	$(divCatalogue).innerHTML="";
	//console.info("REQUEST = "+request);
	
new Ajax.Request(urlServer, { parameters: request,
	onSuccess: function(transport){  
	$(divCatalogue).removeClassName('blueloader').show();
	if(transport.responseText.length == 0){
		$(divCatalogue).innerHTML="<h2><center>Server did not respond. Make sure Catalog URL is correct.</center></h2>";
	}else{
		var json = transport.responseText.evalJSON(); 
		parseWriteCatalogues(divCatalogue,json,task);  
	}	
   }, 
   onFailure: function(error){    
	   $(divCatalogue).removeClassName('blueloader').show();
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
function updateSchemaHash(cname){
	currSchema[cname]=$(cname+"box").value;
	//could potentially gray out metadata options here too, if no XSL exists for transform
}
	
function parseWriteCatalogues(divCatalogue,json,task){
	$(divCatalogue).innerHTML="";
	
	if(json==null||json.GetRecordsResponse==null){ $(divCatalogue).innerHTML="<p>&nbsp;Invalid response returned</p>"/*+json.GetRecordsResponse*/;return }
	var candidates=json.GetRecordsResponse.numberOfRecordsReturned;
	var cName=divCatalogue.replace('div_','');
	var ct = extracPr(cName);
	var pagination = 'pag_'+json.Id;
	var pos = json.Position;
	//var boundingBoxResponse = new Array(0,0,0,0);
	var htmlText=new Array();
	var version = escape(ct["csw-version"]);
	var url = encodeURI(ct.urlcatalog);
	var prod = escape(ct["product"]);
	var encoding = escape(ct["xml-encoding"]);		

	//This block of code fills output schema drop down box
	
	
	var schemas = schemaInfo[cName];
	var schemaResults = "";
	
	for(i=0; i < schemas.length;i++){
		schemaResults = schemaResults+ '<option>'+schemaInfo[cName][i]+'</option>';
	}	
	
	if(candidates >0){
		var req=json.QueryString+"&";					
		req=req.replace(/%26/g,'&');
		req=req.replace(/%3D/g,'=');
		htmlText.push('<table border="0"  width="100%">');
		htmlText.push('<tr bgcolor="#ECECFF">');
		htmlText.push('<td width="10% align="left"><b><center>Found: '+json.GetRecordsResponse.numberOfRecordsMatched+'</b></center></td>');
		htmlText.push('<td width="90%" align="left"><b>&nbsp;'+cName+' Metadata OutputSchema: </b>');
		htmlText.push(' <select id='+(cName+'box')+' onChange="javascript:updateSchemaHash(\''+cName+'\');"><option>Default</option>'+schemaResults+'</select></td>');		
		htmlText.push('</tr>');
				
		htmlText.push('<tr><td colspan="4">');
		for (i=0;i <candidates;i++){
			var currRecord;
			if(candidates>1){
				currRecord = json.GetRecordsResponse.Record[i];	
			}else{
				currRecord = json.GetRecordsResponse.Record;
			}
			var identifier = escape(currRecord.identifier);
						
			htmlText.push('<table id='+identifier+' border="0" style="border:1px solid #F2F2F2" width="100%" onmouseover="selectMetadata(this)" onmouseout="unselectMetadata(this)">');
			htmlText.push('<tr width="100%" bgcolor="#ECECFF">');			
			if (!currRecord.boundingBox.lowerCorner.toString().blank() || !currRecord.boundingBox.upperCorner.toString().blank()){
				htmlText.push('<td width="70%"><h1>'+currRecord.title+'</h1></td>');
				htmlText.push('<td width="3%"><center><img src="images/zoom.png" onclick="addBox('+currRecord.boundingBox.latlon+',\''+currRecord.boundingBox.lowerCorner.toString()+'\',\''+currRecord.boundingBox.upperCorner.toString()+'\');"/></center></td>');			
			}else{
				htmlText.push('<td width="73%"><h1>'+currRecord.title+'</h1></td>');							
			}
			htmlText.push('<td width="14%"><center><a href="#" onclick="javascript:metaDataToHTML(\''+identifier+'\',\''+url+'\',\''+version+'\',\''+escape(cName)+'\',\''+prod+'\',\''+encoding+'\');">Show/Hide Metadata</a></center></td>');
			htmlText.push('<td width="13%"><center><a href="#" onclick="javascript:loadMetaData(\''+url+'\',\''+currRecord.identifier+'\',\''+version+'\',\''+escape(cName)+'\');">Raw Metadata File</a></center></td>');
			htmlText.push('</tr>');
			htmlText.push('<tr><td colspan="4"><h1>Description:</h1>'+currRecord.description+'</tr></td>');			
			htmlText.push('</table>');
		}
		htmlText.push('</td></tr></table>');
		/*
		var md_bounds = new OpenLayers.Bounds(boundingBoxResponse[0], boundingBoxResponse[1], boundingBoxResponse[2], boundingBoxResponse[3]);
		map.zoomToExtent(md_bounds, false);
		*/						
	}
	else{
		htmlText.push('<p> No records found </p>');
	}					
	$(divCatalogue).innerHTML=htmlText.join(' ');
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
	if(task){
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
			var	catPag=new ajaxpageclass.createBook(
				{pages:pg,selectedpage:0}
				, json.Id, [pagination]);
		}
	}
	json=null;
}