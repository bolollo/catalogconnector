/* Copyright (c) 2006-2010 MetaCarta, Inc., published under the Clear BSD
 * license.  See http://svn.openlayers.org/trunk/openlayers/license.txt for the
 * full text of the license. */

/**
 * @requires OpenLayers/Format/XML.js
 * @requires OpenLayers/Request/XMLHttpRequest.js
 * @requires OpenLayers/Console.js
 */

/**
 * Class: OpenLayers.Format.OpenSearchDescription
 * Parse OpenSearch description document with Geo extension, see
 * http://www.opensearch.org/Specifications/OpenSearch/1.1 and
 * http://www.opensearch.org/Specifications/OpenSearch/Extensions/Geo/1.0/Draft_1.
 * Create a new instance with the <OpenLayers.Format.OpenSearchDescription>
 * constructor. 
 * 
 * Inherits from:
 *  - <OpenLayers.Format.XML>
 */
OpenLayers.Format.OpenSearchDescription = OpenLayers.Class(OpenLayers.Format.XML, {

    xmlns: "http://a9.com/-/spec/opensearch/1.1/",
    geons: "http://a9.com/-/opensearch/extensions/geo/1.0/",

    /**
     * Constructor: OpenLayers.Format.OpenSearchDescription
     * Create a new parser for Open Search description document.
     *
     * Parameters:
     * options - {Object} An optional object whose properties will be set on
     *     this instance.
     */
    initialize: function(options) {
        // compile regular expressions once instead of every time they are used
        this.regExes = {
            extractParams: (/\{(\w|:)+\??\}/g),
            trimCurly: (/(^\{|\??\}$)/g)
        };
    
        this.mimeTypes = {
            "application/vnd.google-earth.kml+xml": "KML",
            "application/atom+xml": "Atom",
            "application/rss+xml": "GeoRSS",
            "application/json": "GeoJSON"
        };
    
        OpenLayers.Format.XML.prototype.initialize.apply(this, [options]);
    },
    
    /**
     * APIMethod: read
     * Read data from a string. Return a name for the search service
     * and a list of OpenLayers-compatible response formats, along with
     * accepted parameters for each one. 
     * 
     * Parameters: 
     * data    - {String} or {DOMElement} data to read/parse.
     *
     * Returns an object with the structure:
     * {
     *   name: 'search service short name',
     *   attribution: 'copyright notice',
     *   formats: {
     *   	Atom: { // also KML, GeoRSS and/or GeoJSON if present
     *   		URLTemplate: 'Url to construct the query',
     *   		searchParams: {
     *   			SearchTerms: "",
     *   			"geo:box": "",
     *   			//... other params accepted
     *   			}
     *   		}
     *   	}
     *   }
     * }
     */
    read: function(data) {
        var desc = {};
    
        if(typeof data == "string") {
            data = OpenLayers.Format.XML.prototype.read.apply(this, [data]);
        }
    
        // Short name
        var shortNameNode = this.getElementsByTagNameNS(data, "*", "ShortName")[0];
        desc.name = this.getChildValue(shortNameNode) || "[no name]";
        // URLs
        var urlNodes = this.getElementsByTagNameNS(data, "*", "Url");
        desc.formats = this.parseURLs(urlNodes);
        // Attribution
        var attributionNode = this.getElementsByTagNameNS(data, "*", "Attribution")[0];
        desc.attribution = this.getChildValue(attributionNode);
        return desc;
    },
    
    parseURLs: function(nodes) {
        var URLs = {};
        for(var i=0, len=nodes.length; i<len; i++) {
            var URL = {};
            type = nodes[i].getAttribute("type");
            format = this.mimeTypes[type];
            if (format) { // Only add recognized formats
                URL.URLTemplate = nodes[i].getAttribute("template");
                URL.searchParams = this.parseURLTemplate(URL.URLTemplate);
                URLs[format]=URL;        	
            }
        }
        return URLs;
    },
    
    parseURLTemplate: function(template) {
        var searchParams = {};
        paramArray = template.match(this.regExes.extractParams);
        for(var i=0, len=paramArray.length; i<len; i++) {
            var paramName = paramArray[i].replace(this.regExes.trimCurly, "");
            searchParams[paramName] = "";
        }
        return searchParams;
    },
    
    CLASS_NAME: "OpenLayers.Format.OpenSearchDescription" 
});