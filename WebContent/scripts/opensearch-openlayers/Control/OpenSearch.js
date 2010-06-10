/* Copyright (c) 2006-2010 MetaCarta, Inc., published under the Clear BSD
 * license.  See http://svn.openlayers.org/trunk/openlayers/license.txt for the
 * full text of the license. */

/**
 * @requires OpenLayers/Control.js
 */

/**
 * Class: OpenLayers.Control.OpenSearch
 * A control that loads a collection of OpenSearch services and
 * manages queries. Support for BBOX filtering.
 *
 * Inherits from:
 *  - <OpenLayers.Control>
 */
OpenLayers.Control.OpenSearch = OpenLayers.Class(OpenLayers.Control, {
    
    /**
     * APIProperty: control div ID
     */
    div: null,

    /**
     * Property: results div ID
     */
    resultsDiv: null,
    
    /**
     * Property: array of descriptions
     */
    descriptions: null,
    
    /**
     * Property: form text input
     */
    input: null,
    
    /**
     * Property: selectFeature
     */
    selectFeature: null,

    /**
     * Constructor: OpenLayers.Control.OpenSearch
     * Create a new OpenSearch control.
     *
     * Parameters:
     * options - {Object} Optional object whose properties will be set on the
     *     instance. A "descriptions" array property is mandatory.
     */
    initialize: function (options) {
        OpenLayers.Control.prototype.initialize.apply(this, arguments);
        
        this.strategies = new Array();
        this.pendingDescriptions = this.descriptions.length;
        this.draw();
        
        for(var i=0;i<this.descriptions.length;++i){
        	this.strategies[i] = new OpenLayers.Strategy.OpenSearch({
        		descriptionurl: this.descriptions[i],
        		autoActivate: false});		
	        this.strategies[i].events.on({
	            "descriptionloadend": this.addSearchEngine, scope: this
	        });
        }

        this.layer = new OpenLayers.Layer.Vector("OpenSearch results", {
            strategies: this.strategies
        });
        
    },

    /**
     * Method: draw
     */
    draw: function() {
    	while(this.div.hasChildNodes())
    	    this.div.removeChild(this.div.lastChild);

    	var title = document.createElement("span");
        title.innerHTML = 'Search in:';
        title.className = this.displayClass + 'Title';
        this.div.appendChild(title);
        
        var form = document.createElement("form");
        
        this.select = document.createElement("select");      
        form.appendChild(this.select);
        this.select.onchange = OpenLayers.Function.bindAsEventListener(
        		this.selectSearchEngine, this);

        this.input = document.createElement("input");
        this.input.type = "text";
        this.input.className = this.displayClass + 'Input';
        form.appendChild(this.input);

        var button = document.createElement("input");
        button.type = "submit";
        button.value = "Search";
        form.appendChild(button);
        button.onclick = OpenLayers.Function.bind(this.search, this);
        
        this.div.appendChild(form);
        form.onsubmit = function(){return false;};
        
        this.resultsDiv = document.createElement("div");
        this.resultsDiv.className = this.displayClass + 'Results';
        this.div.appendChild(this.resultsDiv);
        
        this.resultsDiv.innerHTML = "[Parsing search engine descriptions (" +
        	this.pendingDescriptions + " pending) ]";      
    },
    
    /**
     * Method: addSearchEngine
     */
    addSearchEngine: function(engine) {
    	if(engine.description) {
        	var input = document.createElement("option");
    		input.value = this.descriptions.indexOf(engine.url);
    		input.text = engine.description.name;
    		this.select.appendChild(input);
    		this.selectSearchEngine({target: this.select});
    	}
        if(--this.pendingDescriptions==0) {
            this.resultsDiv.innerHTML = "[All search engines loaded]";        
        } else {
            this.resultsDiv.innerHTML = "[Parsing search engine descriptions (" +
        	this.pendingDescriptions + " pending) ]";      
        }
    },

    /**
     * Method: selectSearchEngine
     */
    selectSearchEngine: function(evt) {
    	i = evt.target.value;
    	for(var j=0;j<this.strategies.length;++j) {
    		if (i==j) this.strategies[j].activate();
    		else this.strategies[j].deactivate();
    	}
    	if(this.input.value.length>0) this.search();
    	OpenLayers.Console.debug("Selected search engine: "+this.strategies[i].description.name);    	
    },
    
    /**
     * Method: search
     */
    search: function() {
    	for(var i=0;i<this.strategies.length;++i) {
    		this.strategies[i].setSearchTerms(this.input.value);
    	}
    	return false;
    },
    
    /**
     * Method: setMap
     */
    setMap: function(map) {
        OpenLayers.Control.prototype.setMap.apply(this, [map]);
        this.map.addLayer(this.layer);
    
        this.selectFeature = new OpenLayers.Control.SelectFeature(this.layer, {hover: true});
        this.layer.events.on({
            "featureselected": this.onFeatureSelect,
            "featureunselected": this.onFeatureUnselect,
            "loadstart": this.onResultsLoadstart,
            "loadend": this.onResultsLoadend,
            scope: this
        });
        this.map.addControl(this.selectFeature);
        this.selectFeature.activate();
    },
    
    /**
     * Method: onResultsLoadstart
     */
    onResultsLoadstart: function(event) {
        this.resultsDiv.innerHTML = "[Loading response from " +
        	this.select.options[this.select.selectedIndex].text + "]";
    },
    
    /**
     * Method: onResultsLoadend
     */
    onResultsLoadend: function(event) {
        this.resultsDiv.innerHTML = "[Results for " +
        	this.select.options[this.select.selectedIndex].text + "]";
        if (this.layer.features.length == 0) {
        	this.resultsDiv.innerHTML += "<br/>[No results found]";	
        }
        for(var i=0;i<this.layer.features.length; i++) {
            var feature = this.layer.features[i];
            this.resultsDiv.innerHTML += this.renderFeatureAttributes(feature);
        }
        for(var i=0;i<this.layer.features.length; i++) {
            var feature = this.layer.features[i];
            $(feature.id).onmouseover = OpenLayers.Function.bindAsEventListener(this.onItemMouseOver, this);
            $(feature.id).onmouseout = OpenLayers.Function.bindAsEventListener(this.onItemMouseOut, this);
        }
    },
    
    /**
     * Method: renderFeatureAttributes
     */
    renderFeatureAttributes: function(feature) {
        var html = '<div class="olControlOpenSearchItem" id="'+feature.id+'">';
        for(var attribute in feature.attributes) {
            html += '<div class="olControlOpenSearchItemAttribute">';
            html += '<span class="olControlOpenSearchItemAttributeName">' + attribute + '</span>';
            html += '<span class="olControlOpenSearchItemAttributeValue">' + feature.attributes[attribute] + '</span>';
            html += '</div>';
        }
        html += "</div>";
        return html;
    },
    
    /**
     * Method: onItemMouseOver
     */
    onItemMouseOver: function(event) {
        var div = event.currentTarget;
        var feature = this.layer.getFeatureById(div.id);
        this.selectFeature.select(feature);
    },

    /**
     * Method: onItemMouseOut
     */
    onItemMouseOut: function(event) {
        var div = event.currentTarget;
        var feature = this.layer.getFeatureById(div.id);
        this.selectFeature.unselect(feature);
    },

    /**
     * Method: onFeatureSelect
     */
    onFeatureSelect: function(event) {
        var feature = event.feature;
        $(feature.id).className = this.displayClass + "SelectedItem";
    },
    
    /**
     * Method: onFeatureUnselect
     */
    onFeatureUnselect: function(event) {
        var feature = event.feature;
        $(feature.id).className = this.displayClass + "Item";
    },
    
    CLASS_NAME: "OpenLayers.Control.OpenSearch"
});