import parseResult from './parse-result.js'
// import fetch from node-fetch
const URL = "localhost:8080/greeting"


$(document).ready(function(){
    $('#SubmitButton').click(function(){
        submitPreferences()
    });
  });

function submitPreferences() {
    let musts = $('#must-select').select2('data');
    // let mays = $('#may-select').select2('data');

    let must_ids = [];
    musts.forEach(item => {
        must_ids.push(item.id)
    });
    must_ids.push("http://www.semanticweb.org/kai/ontologies/2024/companion-planting#Carrot");
    must_ids.push("http://www.semanticweb.org/kai/ontologies/2024/companion-planting#Shallot");
    must_ids.push("http://www.semanticweb.org/kai/ontologies/2024/companion-planting#Mint");
    
    // let may_ids = [];
    // mays.forEach(item => {
    //     may_ids.push(item.id)
    // });
    // let message = {'musts': must_ids, 'mays': may_ids}
    // let message = {'selectedplants': must_ids}
    // parseResult(message);
    

    // TODO: init API call and pass the result to the following function. 
    
   
    $.post({
        url: "http://localhost:8080/check",
        headers: {'Access-Control-Allow-Origin':'*'}, // <-------- set this
        data: JSON.stringify(must_ids),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        
        success: function(response){
            console.log(response)
            parseResult(response)
        },
        error: function(xhr, status, error) {
            console.log(JSON.stringify(must_ids));
            // window.alert(xhr.status,status,error);
            
        }
    });

    // window.alert(data);
    // parseResult(data)

}