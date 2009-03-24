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
 * 
 * @author Victor Pascual
 * @author Wladimir Szczerban
 */
var A_PROJECTS;
var A_CATALOGS;
var A_CONNECTIONS;
var A_ENCODINGS = new Array ("UTF-8","ISO-8859-1");
var urlServer="/catalogConnector/AdminConnector";
var ACTIVE_CONNECTION;
var ACTIVE_PROJECT;

function createCatalog(){
	createForm(null,null);
}

function init(){
	ACTIVE_PROJECT = "catalogues.xml";
	loadProjects();
	loadCatalogues();
	loadConnections();
}

function loadProjects(){
	new Ajax.Request(urlServer+'?REQUEST=GetProjects', {method:'get',   onSuccess: function(transport){
		var json = transport.responseText.evalJSON();
		A_PROJECTS = json;
		var s_product = $('selProject');
		s_product.length = 0;
		for(i=0; i < json.length;i++){
			var o_product = document.createElement( "option" );
			o_product.value = json[i].project;
			o_product.innerHTML = json[i].project;
			s_product.appendChild(o_product);
		}
	}
	});
}

function loadCatalogues(){
	new Ajax.Request(urlServer+'?REQUEST=GetCatalogues', {method:'get',   onSuccess: function(transport){
		var json = transport.responseText.evalJSON();
		A_CATALOGS = json; 
	}
	});
}

function loadConnections(){
	new Ajax.Request(urlServer+'?REQUEST=GetConnections&PROJECT='+ACTIVE_PROJECT, {method:'get',   onSuccess: function(transport){
		var json = transport.responseText.evalJSON();
		A_CONNECTIONS = json;
		$( 'divList' ).innerHTML = "";
		for(i=0; i < json.length;i++){
			var a = document.createElement( "a" );
  			a.href = "javascript:createForm("+i+",null)";
  			a.innerHTML = json[i].name;
  			var tr = document.createElement( "br" );
 			$( 'divList' ).appendChild( a );
 			$( 'divList' ).appendChild( tr );
		} 
	}
	});
}

function loadVersions(){
	var s_cswversion = $( 'csw_version' );
	s_cswversion.options.length = 0;
	var A_VERSIONS = A_CATALOGS[this.selectedIndex].version;
	for (i = 0; i < A_VERSIONS.length; i++){
		var o_cswversion = document.createElement( "option" );
		o_cswversion.value = A_VERSIONS[i];
		o_cswversion.innerHTML = A_VERSIONS[i];
		s_cswversion.appendChild( o_cswversion );
	}
}

function validateForm(){
	if ($F('name') == ""){
		writeMessage("Name field is empty","ERROR");
		return false;
	}
	else if ($F('title') == ""){
		writeMessage("Title field is empty","ERROR");
		return false;
	}
	else if ($F('abstract') == ""){
		writeMessage("Abstract field is empty","ERROR");
		return false;
	}
	else if ($F('urlcatalog') == ""){
		writeMessage("URL field is empty","ERROR");
		return false;
	}
	else{
		return true;
	} 
}

function writeMessage(message, type){
	$('textMessage').innerHTML = "";
	var t_msg = document.createTextNode(message);
	$('textMessage').appendChild(t_msg);
	if (type == "OK"){
		$('textMessage').className = "message-ok";
	}
	else if (type == "ERROR"){
		$('textMessage').className = "message-error";
	}
	else{
		$('textMessage').className = "message-warning";
	}
	$('divMessage').style.display = "block";
}

function hideMessage(){
	$('divMessage').style.display = "none";
}

function createConnection(){
	if (validateForm()){
		sendConnection("ADD");
	}
}

function updateConnection(){
	sendConnection("UPDATE");
}

function deleteConnection(){
	sendConnection("DELETE");
}

function sendConnection(type){
	$('divLoading').style.display = "block";
	var params;
	var urlRequest;
	var message;
	if (type == "ADD"){
		params = $('formConnection').serialize();
		urlRequest = urlServer+'?REQUEST=SetConnections&PROJECT='+ACTIVE_PROJECT+'&OPERATION=A&'+params;
		message = "Connection created";
	}
	else if (type == "UPDATE"){
		params = $('formConnection').serialize();
		params += "&ACTIVE_CONNECTION="+ACTIVE_CONNECTION;
		urlRequest = urlServer+'?REQUEST=SetConnections&PROJECT='+ACTIVE_PROJECT+'&OPERATION=U&'+params;
		message = "Connection updated";
	}
	else if (type == "DELETE"){
		var params = "ACTIVE_CONNECTION="+ACTIVE_CONNECTION;
		urlRequest = urlServer+'?REQUEST=SetConnections&PROJECT='+ACTIVE_PROJECT+'&OPERATION=D&'+params;
		message = "Connection deleted";
	}
	new Ajax.Request(urlRequest, {method:'get',
		onSuccess: function(transport){
			loadConnections();
			createCatalog();
			$('divLoading').style.display = "none";
			writeMessage(message,"OK");
		}
	}); 
}

function createForm(connection, type){
	ACTIVE_CONNECTION = connection;
	var name = "";
	var title = "";
	var abstract = "";
	var urlcatalog = "";
	var product = "";
	var csw_version = "";
	var xml_encoding = "";
	if (connection != null){
		name = A_CONNECTIONS[connection].name;
		title = A_CONNECTIONS[connection].title;
		abstract = A_CONNECTIONS[connection].abstract;
		urlcatalog = A_CONNECTIONS[connection].urlcatalog;
		product = A_CONNECTIONS[connection].product;
		csw_version = A_CONNECTIONS[connection]["csw-version"];
		xml_encoding = A_CONNECTIONS[connection]["xml-encoding"];	
	}
	formConnectio(name, title, abstract, urlcatalog, product, csw_version, xml_encoding);
}

function formConnectio(name, title, abstract, urlcatalog, product, csw_version, xml_encoding){
	var i_product = 0;
		
	$( 'divProperties' ).innerHTML = "";
	var form = document.createElement("form");
	form.name = "formConnection";
	form.id = "formConnection";
	var table = document.createElement("table");
	var tb = document.createElement("tbody");
	
	//catalog name
	var tr_name = document.createElement("tr");
	var td_l_name = document.createElement("td");
	var td_t_name = document.createElement("td");
	td_t_name.colSpan = 2;
	
	var l_name = document.createTextNode( "Name:");
	var t_name = document.createElement( "input" );
	t_name.type = "text";
	t_name.value = name;
	t_name.name = "name";
	t_name.id = "name";
	t_name.size = 65;
	td_l_name.appendChild( l_name );
	td_t_name.appendChild( t_name );
	tr_name.appendChild(td_l_name);
	tr_name.appendChild(td_t_name);
	
	//catalog title
	var tr_title = document.createElement("tr");
	var td_l_title = document.createElement("td");
	var td_t_title = document.createElement("td");
	td_t_title.colSpan = 2;
	var l_title = document.createTextNode( "Title:");
	var t_title = document.createElement( "input" );
	t_title.type = "text";
	t_title.value = title;
	t_title.name = "title";
	t_title.id = "title";
	t_title.size = 65;
	td_l_title.appendChild( l_title );
	td_t_title.appendChild( t_title );
	tr_title.appendChild(td_l_title);
	tr_title.appendChild(td_t_title);
	
	//catalog description
	var tr_description = document.createElement("tr");
	var td_l_description = document.createElement("td");
	var td_t_description = document.createElement("td");
	td_t_description.colSpan = 2;
	var l_description = document.createTextNode( "Abstract:");
	var t_description = document.createElement( "textarea" );
	t_description.value = abstract;
	t_description.name = "abstract";
	t_description.id = "abstract";
	t_description.cols = 50;
	t_description.rows = 4;
	td_l_description.appendChild( l_description );
	td_t_description.appendChild( t_description );
	tr_description.appendChild(td_l_description);
	tr_description.appendChild(td_t_description);
	
	//catalog url
	var tr_urlcatalog = document.createElement("tr");
	var td_l_urlcatalog = document.createElement("td");
	var td_t_urlcatalog = document.createElement("td");
	td_t_urlcatalog.colSpan = 2;
	var l_urlcatalog = document.createTextNode( "URL:");
	var t_urlcatalog = document.createElement( "input" );
	t_urlcatalog.type = "text";
	t_urlcatalog.value = urlcatalog;
	t_urlcatalog.name = "urlcatalog";
	t_urlcatalog.id = "urlcatalog";
	t_urlcatalog.size = 65;
	td_l_urlcatalog.appendChild( l_urlcatalog );
	td_t_urlcatalog.appendChild( t_urlcatalog );
	tr_urlcatalog.appendChild(td_l_urlcatalog);
	tr_urlcatalog.appendChild(td_t_urlcatalog);
	
	//catalog type of product
	var tr_product = document.createElement("tr");
	var td_l_product = document.createElement("td");
	var td_t_product = document.createElement("td");
	td_t_product.colSpan = 2;
	var l_product = document.createTextNode( "product:");
	var s_product = document.createElement( "select" );
	s_product.name = "product";
	s_product.id = "product";
	s_product.onchange = loadVersions;
	for (i = 0; i < A_CATALOGS.length; i++){
		var o_product = document.createElement( "option" );
		o_product.value = A_CATALOGS[i].product;
		o_product.innerHTML = A_CATALOGS[i].product;
		if (A_CATALOGS[i].product == product){
			o_product.selected = true;
			i_product = i;
		}
		s_product.appendChild(o_product);
	}
	td_l_product.appendChild( l_product );
	td_t_product.appendChild( s_product );
	tr_product.appendChild(td_l_product);
	tr_product.appendChild(td_t_product);
	
	//catalog product version
	var tr_cswversion = document.createElement("tr");
	var td_l_cswversion = document.createElement("td");
	var td_t_cswversion = document.createElement("td");
	td_t_cswversion .colSpan = 2;
	var l_cswversion = document.createTextNode( "csw-version:");
	var s_cswversion = document.createElement( "select" );
	s_cswversion.name = "csw_version";
	s_cswversion.id = "csw_version";
	var A_VERSIONS = A_CATALOGS[i_product].version;
	for (i = 0; i < A_VERSIONS.length; i++){
		var o_cswversion = document.createElement( "option" );
		o_cswversion.value = A_VERSIONS[i];
		o_cswversion.innerHTML = A_VERSIONS[i];
		if (A_VERSIONS[i] == csw_version){
			o_cswversion.selected = true;
		}
		s_cswversion.appendChild( o_cswversion );
	}
	td_l_cswversion.appendChild( l_cswversion );
	td_t_cswversion.appendChild( s_cswversion );
	tr_cswversion.appendChild( td_l_cswversion );
	tr_cswversion.appendChild( td_t_cswversion );
	
	//catalog xml encoding
	var tr_XMLencoding = document.createElement("tr");
	var td_l_XMLencoding = document.createElement("td");
	var td_t_XMLencoding = document.createElement("td");
	td_t_XMLencoding.colSpan = 2;
	var l_XMLencoding = document.createTextNode( "xml-encoding:");
	var s_XMLencoding = document.createElement( "select" );
	s_XMLencoding.name = "xml_encoding";
	s_XMLencoding.id = "xml_encoding";
	for (i = 0; i < A_ENCODINGS.length; i++){
		var o_XMLencoding = document.createElement( "option" );
		o_XMLencoding.value = A_ENCODINGS[i];
		o_XMLencoding.innerHTML = A_ENCODINGS[i];
		if (A_ENCODINGS[i] == xml_encoding){
			o_XMLencoding.selected = true;
		}
		s_XMLencoding.appendChild(o_XMLencoding);
	}
	td_l_XMLencoding.appendChild( l_XMLencoding );
	td_t_XMLencoding.appendChild( s_XMLencoding );
	tr_XMLencoding.appendChild(td_l_XMLencoding);
	tr_XMLencoding.appendChild(td_t_XMLencoding);
	
	//messages 
	var tr_message = document.createElement("tr");
	var td_message = document.createElement("td");
	td_message.colSpan = 3;
	td_message.className = "message-warning";
	var l_message = document.createTextNode( "All fields must be filled");
	td_message.appendChild( l_message );	
	tr_message.appendChild( td_message );
	
	//empty row in the table
	var tr_blank = document.createElement("tr");
	var td_blank = document.createElement("td");
	td_blank.colSpan = 3;
	td_blank.innerHTML = "&nbsp;";
	tr_blank.appendChild( td_blank );
	
	//buttons create or modifi and delete
	var tr_button = document.createElement("tr");
	if (ACTIVE_CONNECTION != null){
		var td_button_delete = document.createElement("td");
		td_button_delete.align = "center";
		var td_button_create = document.createElement("td");
		td_button_create.align = "center";
		td_button_create.colSpan = 2;
		
		var b_button_del = document.createElement( "input" );
		b_button_del.type = "button";
		b_button_del.className = "boto";
		b_button_del.value = "Delete Connection";
		b_button_del.onclick = deleteConnection;
		
		var b_button = document.createElement( "input" );
		b_button.type = "button";
		b_button.className = "boto";
		b_button.value = "Update Connection";
		b_button.onclick = updateConnection;
		
		td_button_delete.appendChild( b_button_del );
		td_button_create.appendChild( b_button );
		tr_button.appendChild( td_button_delete );
		tr_button.appendChild( td_button_create );
	}else{
		var td_button = document.createElement("td");
		td_button.colSpan = 3;
		td_button.align = "center";
		var b_button = document.createElement( "input" );
		b_button.type = "button";
		b_button.className = "boto";
		b_button.value = "Create Connection";
		b_button.onclick = createConnection;
		td_button.appendChild( b_button );
		tr_button.appendChild( td_button );
	}
		
	//add rows to the table
	tb.appendChild( tr_name );
	tb.appendChild( tr_title );
	tb.appendChild( tr_description );
	tb.appendChild( tr_urlcatalog );
	tb.appendChild( tr_product );
	tb.appendChild( tr_cswversion );
	tb.appendChild( tr_XMLencoding );
	tb.appendChild( tr_message );
	tb.appendChild( tr_blank );
	tb.appendChild( tr_button );
	
	table.appendChild( tb );
	
	form.appendChild( table );
	$('divProperties').appendChild( form );			 
}

function updateList(value){
	ACTIVE_PROJECT = value;
	loadConnections();
	createCatalog();
}

function newProject(){
	$('divNewProject').style.display = 'block';
	$('divProperties').innerHTML = "";
}

function createProject(){
	var urlRequest;
	var message;
	var project = $('project_name').value;
	if (project != ""){
		project = project + ".xml";
		urlRequest = urlServer+'?REQUEST=SetProjects&PROJECT='+project+'&OPERATION=A&';
		message = "Project created";
		new Ajax.Request(urlRequest, {method:'get',
			onSuccess: function(transport){
				loadProjects();
				cancelProject();
				$('divLoading').style.display = "none";
				writeMessage(message,"OK");
			}
		});
	}else{
		message = "Must specify a project name";
		writeMessage(message,"ERROR");
	}
}

function cancelProject(){
	$('divNewProject').style.display = 'none';
}

function deleteProject(){
	var urlRequest;
	var message;
	if (ACTIVE_PROJECT == "catalogues.xml"){
		message = "You can not delete this project";
		writeMessage(message,"ERROR");
	}
	else{
		if(confirm("Are you sure you want to delete the project "+ACTIVE_PROJECT+"!  ?")) {
			urlRequest = urlServer+'?REQUEST=SetProjects&PROJECT='+ACTIVE_PROJECT+'&OPERATION=D&';
			message = "Project deleted";
			new Ajax.Request(urlRequest, {method:'get',
				onSuccess: function(transport){
					cancelProject();
					init();
					$('divLoading').style.display = "none";
					writeMessage(message,"OK");
				}
			});
		}
	}
}