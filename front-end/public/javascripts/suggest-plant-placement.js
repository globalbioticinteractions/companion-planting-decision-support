// import parseResult from './parse-result.js'
// import anychart from 'anychart'

const URL = "http://localhost:8080"

$(document).ready(function(){
    $('#SuggestButton').click(function(){
        suggestPlantPlacement()
    });
  });

function suggestPlantPlacement() {
    let musts = $('#must-select').select2('data');
    // let mays = $('#may-select').select2('data');

    let must_ids = [];
    musts.forEach(item => {
        must_ids.push(item.id)
    });
    // must_ids.push("http://www.semanticweb.org/kai/ontologies/2024/companion-planting#Carrot");
    // must_ids.push("http://www.semanticweb.org/kai/ontologies/2024/companion-planting#Shallot");
    // must_ids.push("http://www.semanticweb.org/kai/ontologies/2024/companion-planting#Mint");
    
    // let may_ids = [];
    // mays.forEach(item => {
    //     may_ids.push(item.id)
    // });
    // let message = {'musts': must_ids, 'mays': may_ids}
    // let message = {'selectedplants': must_ids}
    // parseResult(message);
    

    // TODO: init API call and pass the result to the following function. 
   
    
    $.post({
        url: URL.concat("/suggest"),
        headers: {'Access-Control-Allow-Origin':'*'}, // <-------- set this
        data: JSON.stringify(must_ids),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        
        success: function(response){
            // window.alert(response);
            console.log("Response:".concat(response));
            // drawLayout(response)
            parseSuggestionAsGraph(response);
        },
        
        error: function(xhr, status, error) {
            window.alert("Something went wrong while sending the request: "+plantlist); 

            // window.alert(xhr.status,status,error);
            
        }
    });

    // window.alert(data);
    // parseResult(data)

    // return must_ids;
}

function parseSuggestionAsGraph(message) {
    $('div#container').remove();
    $('#ResultTable tr').remove();
    // let graph = $('div#container')[0];
    let nodes = [];
    let edges = [];

    for (let i = 0; i < message.length; i++){
        var triple = message[i];

        var subject = triple.subject;
        var pred = triple.property;
        var object = triple.object;

        if(pred=="Type"& subject.includes("plant")){
            nodes.push({"id":subject,"label":object});
        }
        if(pred=="neighbour"){
            edges.push({"from":subject,"to":object});
        }

    }

    var json = {"nodes":nodes,"edges":edges};
    let jsondata = JSON.stringify(json);
    console.log(json);

    // anychart.data.loadJsonFile('../data/placement_test.json', function (data) {
        
        var chart = anychart.graph(json);

        chart.nodes().labels(true);
        chart.nodes().labels().fontColor("green");
        // nodes.labels().fontWeight(900);
        chart.nodes().labels().format("${%label}");

        // chart.nodes().normal().height(40);
        // chart.nodes().hovered().height(55);
        // chart.nodes().selected().height(55);

        // chart.edges().normal().stroke("#ffa000", 2, "10 5", "round");
        // chart.edges().hovered().stroke("#ffa000", 4, "10 5", "round");
        // chart.edges().selected().stroke("#ffa000", 4);

        chart.container('graphcontainer');
        chart.draw();

    // drawLayout(data);
    // });
}


function parseSuggestionAsTable(message) {
    
    // console.log(message)
    $('#ResultTable tr').remove();
    let res_table = $('table#ResultTable')[0];
    let col_names = Object.keys(message[0]);
    let header = res_table.insertRow(0);
    for (let i = 0; i < col_names.length; i++) {
        let col_name = header.insertCell(i);
        col_name.innerHTML = col_names[i];
    }

    for (let i = 0; i < message.length; i++) {
        let r = res_table.insertRow(i+1);
        for (let j = 0; j < col_names.length; j++) {
            let cell = r.insertCell(j);
            cell.innerHTML = message[i][col_names[j]];
        }
    }
}

function drawLayout(jsondata) {
         
    anychart.onDocumentReady(function (data = jsondata) {
        var chart = anychart.fromJson(data)
        
        // anychart.data.loadJsonFile(
        // The data used in this sample can be obtained from the CDN
        //   'https://cdn.anychart.com/samples-data/graph/knowledge_graph/data.json',
        //   function (data=message) {
        // create graph chart
        // var chart = anychart.graph(data);

        // set container id for the chart
        chart.container('container');
        // initiate chart drawing
        chart.draw();
    
    });
}
        // );
    //   });
