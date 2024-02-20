import parseResult from './parse-result.js'
import { v2 as compose } from 'docker-compose'

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

    const service = 'queries'
    compose.upOne(service, { cwd: path.join(_dirname), log: true})
    const result = await compose.ps({ cwd: path.join(__dirname), commandOptions: [["--format", "json"]] })
    result.data.services.forEach((service) => {
        console.log(service.name, service.command, service.state, service.ports)
        // state is one of the defined states: paused | restarting | removing | running | dead | created | exited
    })
    

    parseResult(message)

}