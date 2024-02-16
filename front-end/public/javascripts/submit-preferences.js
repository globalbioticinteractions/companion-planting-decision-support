import parseResult from './parse-result.js'

$(document).ready(function(){
    $('#SubmitButton').click(function(){
        submitPreferences()
    });
  });

function submitPreferences() {
    let musts = $('#must-select').select2('data');
    let mays = $('#may-select').select2('data');

    let must_ids = [];
    musts.forEach(item => {
        must_ids.push(item.id)
    });
    let may_ids = [];
    mays.forEach(item => {
        may_ids.push(item.id)
    });
    let message = {'musts': must_ids, 'mays': may_ids}
    
    // TODO: init API call and pass the result to the following function. 
    parseResult(message)

}