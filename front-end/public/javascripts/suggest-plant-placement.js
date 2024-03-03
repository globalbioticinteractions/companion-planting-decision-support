// import parseResult from './parse-result.js'
// import fetch from node-fetch
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
            parseSuggestion(response);
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


function parseSuggestion(message) {
    
    // console.log(message)
    $('#ResultTable tr').remove();
    let res_table = $('table#ResultTable')[0];
    let col_names = Object.keys(message[0]);
    let header = res_table.insertRow(0);
    for (let i = 0; i < col_names.length; i++) {
        let col_name = header.insertCell(i);
        col_name.innerHTML = col_names[i];
    }
    // let explain_col = header.insertCell(col_names.length);
    // explain_col.innerHTML = 'explanation';

    // console.log(typeof message);
    // let array = message.split(',');
    // array.forEach((triple,i) => {
    //     let r = res_table.insertRow(i+1);
    //     t = triple.split('/t'),
    //     t.forEach((o,j) => {
    //         let cell = r.insertCell(j);
    //         cell.innerHTML = o;
    //     });

    // });

    for (let i = 0; i < message.length; i++) {
        let r = res_table.insertRow(i+1);
        for (let j = 0; j < col_names.length; j++) {
            let cell = r.insertCell(j);
            cell.innerHTML = message[i][col_names[j]];
        }
        // let explain_button = r.insertCell(col_names.lenth);
        // let button = $('<button />', {
        //     class: 'btn btn-primary btn-block',
        //     type: 'button',
        //     id: message[i]['property'],
        //     click: explainResult,
        //     text: 'Explain!'
        //   });
        
        // let button = document.createElement("BUTTON");
        // button.addEventListener("click", )
        // button.click(function(){
        //     explainResult(must_ids,message[i])
        // });

        // explain_button.append(button[0])
    }
}