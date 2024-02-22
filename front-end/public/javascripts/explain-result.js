
const URL = "http://localhost:8080"


function explainResult(event) {
    // window.alert(event.currentTarget.id);
    

    let musts = $('#must-select').select2('data');
    // // let mays = $('#may-select').select2('data');

    let must_ids = [];
    musts.forEach(item => {
        must_ids.push(item.id)
    });
    must_ids.push("http://www.semanticweb.org/kai/ontologies/2024/companion-planting#Carrot");
    must_ids.push("http://www.semanticweb.org/kai/ontologies/2024/companion-planting#Shallot");
    must_ids.push("http://www.semanticweb.org/kai/ontologies/2024/companion-planting#Mint");
    
    $.post({
        url: URL.concat("/explain"),
        headers: {'Access-Control-Allow-Origin':'*'}, // <-------- set this
        data: JSON.stringify({plantlist: must_ids, property: event.currentTarget.id}),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        
        success: function(response){
            window.alert(response)

            // parseResult(response)
        },
        error: function(xhr, status, error) {
            // console.log(JSON.stringify(must_ids));
            window.alert(xhr.status,status,error);
            
        }
    });

    // window.alert(data);
    // parseResult(data)


}

export default explainResult


