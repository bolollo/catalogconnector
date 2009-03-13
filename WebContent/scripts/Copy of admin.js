var A_CATALOGS;
var A_CONNECTIONS;
var A_ENCODINGS = new Array ("UTF-8","ISO-8859-1");
var urlServer="/catalogConnector/AdminConnector";
var ACTIVE_CONNECTION;

function createCatalog(){
	console.debug("create");
	createForm(null,null);
}

function init(){
	loadConnections();
	loadCatalogues();
}

function loadCatalogues(){
	new Ajax.Request(urlServer+'?REQUEST=GetCatalogues', {method:'get',   onSuccess: function(transport){
		var json = transport.responseText.evalJSON();
		A_CATALOGS = json; 
	}
	});
}

function loadConnections(){
	new Ajax.Request(urlServer+'?REQUEST=GetConnections', {method:'get',   onSuccess: function(transport){
		var json = transport.responseText.evalJSON();
		A_CONNECTIONS = json;
		$( 'divList' ).innerHTML = "";
		for(i=0; i < json.length;i++){
			var a = document.createElement( "a" );
  			a.href = "javascript:createForm("+i+",null)";
  			a.innerHTML = json[i].name;
  			//var text = document.createTextNode( json[i].name);
 			var tr = document.createElement( "br" );
 			$( 'divList' ).appendChild( a );
 			$( 'divList' ).appendChild( tr );
		} 
	}
	});
}

function loadVersions(){
	console.debug(this.selectedIndex);
	var s_cswversion = $( 'csw-version' );
	s_cswversion.options.length = 0;
	var A_VERSIONS = A_CATALOGS[this.selectedIndex].version;
	for (i = 0; i < A_VERSIONS.length; i++){
		var o_cswversion = document.createElement( "option" );
		o_cswversion.value = A_VERSIONS[i];
		o_cswversion.innerHTML = A_VERSIONS[i];
		s_cswversion.appendChild( o_cswversion );
	}
}

function createConnection(){
	sendConnection("ADD");
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
	if (type == "ADD"){
		params = $('formConnection').serialize();
		urlRequest = urlServer+'?REQUEST=SetConnections&OPERATION=A&'+params;
	}
	else if (type == "UPDATE"){
		params = $('formConnection').serialize();
		params += "&ACTIVE_CONNECTION="+ACTIVE_CONNECTION;
		urlRequest = urlServer+'?REQUEST=SetConnections&OPERATION=U&'+params;
	}
	else if (type == "DELETE"){
		var params = "ACTIVE_CONNECTION="+ACTIVE_CONNECTION;
		urlRequest = urlServer+'?REQUEST=SetConnections&OPERATION=D&'+params;
	}
	new Ajax.Request(urlRequest, {method:'get',
		onSuccess: function(transport){
			loadConnections();
			createCatalog();
			$('divLoading').style.display = "none";
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
		if (type == "DEFAULT"){
			ACTIVE_CONNECTION = null;
			name = LIST_CATALOGS[connection].name;
			title = LIST_CATALOGS[connection].title;
			abstract = LIST_CATALOGS[connection].abstract;
			urlcatalog = LIST_CATALOGS[connection].urlcatalog;
			product = LIST_CATALOGS[connection].product;
			csw_version = LIST_CATALOGS[connection]["csw-version"];
			xml_encoding = LIST_CATALOGS[connection]["xml-encoding"];
		}else{
			name = A_CONNECTIONS[connection].name;
			title = A_CONNECTIONS[connection].title;
			abstract = A_CONNECTIONS[connection].abstract;
			urlcatalog = A_CONNECTIONS[connection].urlcatalog;
			product = A_CONNECTIONS[connection].product;
			csw_version = A_CONNECTIONS[connection]["csw-version"];
			xml_encoding = A_CONNECTIONS[connection]["xml-encoding"];
		}
		
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
	//var td_b_urlcatalog = document.createElement("td");
	var l_urlcatalog = document.createTextNode( "URL:");
	var t_urlcatalog = document.createElement( "input" );
	t_urlcatalog.type = "text";
	t_urlcatalog.value = urlcatalog;
	t_urlcatalog.name = "urlcatalog";
	t_urlcatalog.id = "urlcatalog";
	t_urlcatalog.size = 65;
	/*button for predefined catalogues
	var b_urlcatalog = document.createElement( "input" );
	b_urlcatalog.type = "button";
	b_urlcatalog.className = "boto";
	b_urlcatalog.value = "View List";
	b_urlcatalog.onclick = viewListOfUrl;
	*/
	td_l_urlcatalog.appendChild( l_urlcatalog );
	td_t_urlcatalog.appendChild( t_urlcatalog );
	//td_b_urlcatalog.appendChild( b_urlcatalog );
	tr_urlcatalog.appendChild(td_l_urlcatalog);
	tr_urlcatalog.appendChild(td_t_urlcatalog);
	//tr_urlcatalog.appendChild(td_b_urlcatalog);
	
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
	s_cswversion.name = "csw-version";
	s_cswversion.id = "csw-version";
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
	s_XMLencoding.name = "xml-encoding";
	s_XMLencoding.id = "xml-encoding";
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
	table.appendChild( tr_name );
	table.appendChild( tr_title );
	table.appendChild( tr_description );
	table.appendChild( tr_urlcatalog );
	table.appendChild( tr_product );
	table.appendChild( tr_cswversion );
	table.appendChild( tr_XMLencoding );
	table.appendChild( tr_blank );
	table.appendChild( tr_button );
	
	form.appendChild( table );
	$( 'divProperties' ).appendChild( form ); 
}

/*******************************************
List of predefined catalogues

var LIST_CATALOGS;

function viewListOfUrl(){
	new Ajax.Request('resources/cataloguesUrls.json', {method:'get',   onSuccess: function(transport){
		var json = transport.responseText.evalJSON();
		LIST_CATALOGS = json;
		$( 'divUrl' ).innerHTML = "";
		var text = document.createTextNode("List of Catalogues");
		var tr = document.createElement( "br" );
		$( 'divUrl' ).appendChild( text );
 		$( 'divUrl' ).appendChild( tr );
 		$( 'divUrl' ).appendChild( document.createElement( "br" ) );
		for(i=0; i < json.length;i++){
			var a = document.createElement( "a" );
  			a.href = "javascript:createForm("+i+",'DEFAULT')";
  			a.innerHTML = json[i].name;
  			//var text = document.createTextNode( json[i].name);
 			var tr = document.createElement( "br" );
 			$( 'divUrl' ).appendChild( a );
 			$( 'divUrl' ).appendChild( tr );
 			$( 'divUrl' ).style.display = "block";
		} 
	}
	});
}
