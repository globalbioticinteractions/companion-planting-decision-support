// import parseResult from './parse-result.js'
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
    // let may_ids = [];
    // mays.forEach(item => {
    //     may_ids.push(item.id)
    // });
    // let message = {'musts': must_ids, 'mays': may_ids}
    let message = {'musts': must_ids}
    parseResult(message);
    

    // TODO: init API call and pass the result to the following function. 
    
   
    $.get({
        url: "http://localhost:8081/greeting",
        headers: {'Access-Control-Allow-Origin':'*'}, // <-------- set this
        dataType: 'json', // // <-------- use JSONP
        success: function(response){
            const parseData = response;
            console.log(parseData);
        },
        // error: function(xhr, status, error) {
        //     window.alert(xhr.status,status,error);
            
        // }
    });

    // window.alert(data);
    // parseResult(data)

}