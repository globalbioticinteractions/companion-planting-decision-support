import parseResult from './parse-result.js'
// import fetch from node-fetch
const URL = "http://localhost:8080"


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
   
    let plantlist = JSON.stringify(must_ids);
   
    $.post({
        url: URL.concat("/check"),
        headers: {'Access-Control-Allow-Origin':'*'}, // <-------- set this
        data: plantlist,
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        
        success: function(response){
            console.log(response)
            parseResult(response,plantlist)
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
