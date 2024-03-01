import parseResult from './parse-result.js'

$(document).ready(function(){
    $('#SubmitButton').click(function(){
        submitPreferences()
    });
  });

function submitPreferences() {
    var div = document.getElementById('loading'); 
    var loadingtext = document.createTextNode('loading...');
    div.appendChild(loadingtext);

    let musts = $('#must-select').select2('data');
    // let mays = $('#may-select').select2('data');

    let must_ids = [];
    musts.forEach(item => {
        must_ids.push(item.id)
    });
   
    let plantlist = JSON.stringify(must_ids);
   
    $.post({
        url: new window.URL("/check",globalThis.apiurl),
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
