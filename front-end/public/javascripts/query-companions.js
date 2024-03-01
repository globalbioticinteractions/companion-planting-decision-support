
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
    // let mays = $('#may-select').select2('data');

    let plants = [];
    musts.forEach(item => {
        plants.push(item.id)
    });

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
            parseCompanionGraph(response);
        },
        
        error: function(xhr, status, error) {
            window.alert("Something went wrong while sending the request: "+plantlist); 

            // window.alert(xhr.status,status,error);
            
        }
    });

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

    for (var i=0; i<message.edges.length;i++){
        if (message.edges[i].property == "companion"){
            message.edges[i].normal = compsetting.normal;
            message.edges[i].hovered = compsetting.hovered;
            message.edges[i].selected = compsetting.selected;
        }else{
            message.edges[i].normal = anticompsetting.normal;
            message.edges[i].hovered = anticompsetting.hovered;
            message.edges[i].selected = anticompsetting.selected;
        }
    }

    for (var i=0; i<message.nodes.length;i++){
        
        message.nodes[i].scientificname = message.nodes[i].plant.scientificName;
        message.nodes[i].wikilink = message.nodes[i].plant.wikilink;
        
    }

    console.log(message);

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
    original.labels().fontColor("black");
    original.labels().fontSize(13);
    original.labels().fontWeight(500);

    chart.nodes().tooltip().fontSize(12);
    // chart.nodes().tooltip().useHtml(true);
    chart.nodes().tooltip().format('{%id}, {%scientificname}, {%wikilink}');


    // chart.edges().normal().stroke("green", 2, "10 5", "round");
    // chart.edges().hovered().stroke("black", 4);
    // chart.edges().selected().stroke("black", 4);

    // chart.nodes().labels().fontWeight(600);
    // chart.nodes().labels().fontSize(16);
    // chart.nodes().labels().format("{%label}");

    chart.fit();
    chart.container('graphcontainer');
    chart.draw();

    // drawLayout(data);
    // });
}
