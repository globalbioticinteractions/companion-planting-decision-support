// import parseResult from './parse-result.js'
// import anychart from 'anychart'

const URL = "http://localhost:8080"

$(document).ready(function(){
    $('#SuggestButton').click(function(){
        suggestPlantPlacement()
    });
  });

function suggestPlantPlacement() {
    var div = document.getElementById('graphcontainer'); 
    while(div.firstChild) { 
        div.removeChild(div.firstChild); 
    };
    
    $('#ResultTable tr').remove();
    let musts = $('#must-select').select2('data');
    // let mays = $('#may-select').select2('data');

    let must_ids = [];
    musts.forEach(item => {
        must_ids.push(item.id)
    });

    
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
           
        }
    });

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
        chart.nodes().labels().color('black'); //this is also not doing anything.

        //the size is not changing
        chart.nodes().normal().size(35);

        chart.nodes().normal().fill('#2c974b');
        chart.nodes().hovered().fill('white');
        chart.nodes().selected().fill('black');

        chart.nodes().normal().stroke(null);
        chart.nodes().hovered().stroke("#2c974b", 3);
        chart.nodes().selected().stroke("#2c974b", 3);

        chart.edges().normal().stroke("grey", 2, "10 5", "round");
        chart.edges().hovered().stroke("black", 4);
        chart.edges().selected().stroke("black", 4);

        chart.nodes().labels().fontWeight(600);
        chart.nodes().labels().fontSize(16);
        chart.nodes().labels().format("{%label}");

        chart.fit();
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
