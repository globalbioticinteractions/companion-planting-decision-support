class SPARQLQueryDispatcher {
	constructor( endpoint ) {
		this.endpoint = endpoint;
	}

	query( sparqlQuery ) {
		const fullUrl = this.endpoint + '?query=' + encodeURIComponent( sparqlQuery );
		const headers = { 'Accept': 'application/sparql-results+json' };

		return fetch( fullUrl, { headers } ).then( body => body.json() );
	}
}

class imageFetcher {
	constructor( chart, SPARQLDispatcher ) {
		this.chart = chart;
        this.openRequests = 0;
        this.SPARQLDispatcher = SPARQLDispatcher;
	}

    drawChart() {
        if (this.openRequests == 0){
            drawChart();
        }
    }

    getImage(node) {
        const endpointUrl = 'https://query.wikidata.org/sparql';
        const sparqlQuery = `
        SELECT ?itemImage
        WHERE {  
            #P31 : "Instance Of" property
            #Q5: "Human" entity 
            wd:Q23425 wdt:P18 ?itemImage .
            SERVICE wikibase:label { bd:serviceParam wikibase:language "en". }     
        } limit 1
        `;
    //     const sparqlQuery = `
    //   SELECT ?item ?itemLabel
    //   WHERE {  
    //     #P31 : "Instance Of" property
    //     #Q5: "Human" entity 
    //     ?item wdt:P31 wd:Q5.
    //     ?item ?label "Bond"@en. 
    //     SERVICE wikibase:label { bd:serviceParam wikibase:language "en". } 
    //   } limit 10
    // `;
    
        // const queryDispatcher = new SPARQLQueryDispatcher( endpointUrl );
        
        this.openRequests++; 
        this.queryDispatcher.query( sparqlQuery ).then( (response) => {
            this.openRequests--; node.img = JSON.stringify(response.results); drawChart()
        });
    }
}

const anticompsetting = {
    normal: {   stroke:  {
                        color: "red",
                        thickness: "3",
                    }
            },
    hovered: {  stroke: "6 red"},
    selected: { stroke: "6 red"}
    }

const compsetting = {
        normal: {   stroke:  {
                            color: "green",
                            thickness: "3",
                        }
                },
        hovered: {  stroke: "6 green"},
        selected: { stroke: "6 green"}
        }

$(document).ready(function(){
    $('#QueryButton').click(function(){
        queryCompanions()
    });
  });

function queryCompanions() {
    
    
    var div = document.getElementById('loading'); 
    var loadingtext = document.createTextNode(globalThis.loadMessage);
    div.appendChild(loadingtext);

    let musts = $('#must-select').select2('data');

    let plants = [];
    musts.forEach(item => {
        plants.push(item.id)
    });
    // console.log(plants)
    plants.push("http://www.semanticweb.org/kai/ontologies/2024/companion-planting#Carrot");
    plants.push("http://www.semanticweb.org/kai/ontologies/2024/companion-planting#Shallot");
    plants.push("http://www.semanticweb.org/kai/ontologies/2024/companion-planting#Mint");
    

    // let intersection = $('#intersection').is(':checked');
    // let companion = $('#companion').is(':checked');
    let plantlist = JSON.stringify({plants});
    console.log(plantlist);
   
    $.post({
        url: new URL("/getCompanionGraph",globalThis.apiurl),
        headers: {'Access-Control-Allow-Origin':'*'}, // <-------- set this
        data: plantlist,
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        
        success: function(response){
            // console.log(response);
            // parseResult(response,plantlist)
            parseData(response);
        },
        
        error: function(xhr, status, error) {
            window.alert("Something went wrong while sending the request: "+plantlist); 

            // window.alert(xhr.status,status,error);
            
        }
    });

}

function iriToWDNamespace(iri) {
    let toUpper = function(x){ 
        return x.toUpperCase();
      };
    let id = iri.split("/").slice(-1).map(toUpper);

    return "wd:".concat(id);
}

function fetchImage(node, SPARQLDispatcher) {
    let object = iriToWDNamespace(node.plant.wikilink);
    const sparqlQuery = `
        SELECT ?plantImage
        WHERE {  
            #P31 : "Instance Of" property
            #Q5: "Human" entity 
            ${object} wdt:P18 ?plantImage .
            SERVICE wikibase:label { bd:serviceParam wikibase:language "en". }     
        } limit 1
        `;
    return SPARQLDispatcher.query(sparqlQuery).then((response) => {node.img = JSON.stringify(response.results.bindings[0].plantImage.value)})
}

function parseEdge(edge) {
    if (edge.property == "companion"){
        edge.normal = compsetting.normal;
        edge.hovered = compsetting.hovered;
        edge.selected = compsetting.selected;
    }else{
        edge.normal = anticompsetting.normal;
        edge.hovered = anticompsetting.hovered;
        edge.selected = anticompsetting.selected;
    }
}

function parseNode(node) {
    node.scientificname = node.plant.scientificName;
    node.wikilink = node.plant.wikilink;
    node.img = 'not updated yet'
    return 
}

function parseData(message) {
    for (var i=0; i<message.edges.length;i++){
        parseEdge(message.edges[i]);
    }

    for (var i=0; i<message.nodes.length;i++){
        parseNode(message.nodes[i]);
    }

    let promiseArray = [];
    const endpointUrl = 'https://query.wikidata.org/sparql';
    let SPARQLDispatcher = new SPARQLQueryDispatcher(endpointUrl);

    for (var i=0; i<message.nodes.length;i++){
        let node = message.nodes[i];
        promiseArray.push(fetchImage(node, SPARQLDispatcher))
    }

    Promise.all(promiseArray).then((response) => {console.log(response); parseCompanionGraph(message)});
}

function parseCompanionGraph(message) {
    // remove graph 
    var div = document.getElementById('graphcontainer'); 
    while(div.firstChild) { 
        div.removeChild(div.firstChild); 
    };
    // remove loading message
    var div = document.getElementById('loading'); 
    while(div.firstChild) { 
        div.removeChild(div.firstChild); 
    };
    //remove table
    $('#ResultTable tr').remove();

    console.log(message);
    // create a stage
    var stage = anychart.graphics.create("graphcontainer");

    var chart = anychart.graph(message);

    chart.nodes().labels(true);
    // chart.nodes().labels().color('black'); //this is also not doing anything.

    // chart.nodes().normal().height(35);
    // chart.nodes().normal().width(35);

    var original = chart.group("original");
    var other = chart.group("default");

    original.normal().height(40);
    original.normal().width(40);
    original.normal().shape("star5");
    original.normal().fill('#ffa000');
    original.hovered().fill('white');
    original.selected().fill('black');
    original.normal().stroke(null);
    original.hovered().stroke("#ffa000", 4);
    original.selected().stroke("#ffa000", 4);
    original.labels().fontColor("black");
    original.labels().fontWeight(600);
    original.labels().fontSize(16);

    other.normal().height(25);
    other.normal().width(25);
    other.normal().fill('grey');
    other.hovered().fill('white');
    other.selected().fill('black');
    other.normal().stroke(null);
    other.hovered().stroke("grey", 4);
    other.selected().stroke("grey", 4);
    other.labels().fontColor("black");
    other.labels().fontSize(13);
    other.labels().fontWeight(500);

    chart.nodes().tooltip().fontSize(12);

    // LOOK HERE!!!! --> https://www.anychart.com/products/anychart/gallery/General_Features/HTML_Tooltip.php
    // var tooltip = chart.nodes().tooltip();

    // tooltip
    //     
    //     // .format('<p>"{%wikilink}"></p>')
    
    // // Prevent overriding tooltip content
    // tooltip.onBeforeContentChange(function () {
    //     return false;
    //   });
    
    // chart.nodes().tooltip().format('{%id}, {%scientificname}, {%wikilink}');
    chart.nodes().tooltip().useHtml(true).format(
        '<table><tr><th>Common name</th><th>{%id}</th></tr><tr><th>Scientific name</th><th>{%scientificname}</th></tr><tr><th>Image</th><th><img src={%img}></th></tr><tr><th>wikiData</th><th>{%wikilink}</th></tr></table>');
    chart.fit();
    chart.container(stage);
    chart.draw();
    // chart.addEventListener('pointMouseOver', onPointClick);


    

    
    // Legend configuration
    var legend = anychart.standalones.legend();
    // create an array for storing legend items
    var legendItems = [];
    legendItems.push({
        text: "Selected plants",
        iconType: original.normal().shape(),
        iconFill: original.normal().fill(),
        // iconStroke
    });
    legendItems.push({
        text: "Other plants",
        iconType: "circle",
        iconFill: other.normal().fill(),
        // iconStroke
    });

    legendItems.push({
        text: "companions",
        iconType: "line",
        iconStroke: compsetting.normal.stroke
        // iconStroke
    });
    legendItems.push({
        text: "anticompanions",
        iconType: "line",
        iconStroke: anticompsetting.normal.stroke
        // iconStroke
    });

    legend.items(legendItems);
    legend.container(stage);
    legend.positionMode("inside");
    legend.drag(true);
    legend.draw();
    
    // <div class = "embed-responsive embed-responsive-4by3">
    //         <iframe class = "embed-responsive-item" src = "https://wiki.ubc.ca/extensions/EmbedPage/getPage.php?title=/index.php/Help%3AEmbed_a_wiki_page&referer=https://wiki.ubc.ca/Help:Embed_a_wiki_page"></iframe>
    // </div>
    // drawLayout(data);
    // });
    // <script type="text/javascript">
    // document.write(
    //     '<script type="text/javascript" charset="utf-8" src="https://wiki.ubc.ca/extensions/EmbedPage/getPage.php?title=/index.php/Help%3AEmbed_a_wiki_page&referer=https://wiki.ubc.ca/Help:Embed_a_wiki_page "></script>');
    // </script>
}


