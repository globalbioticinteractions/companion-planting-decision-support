
// TODO: Replace this with an API call
// import data1 from '../data/taxon_product.js';
// console.log(data1)

function parsePlants(data) {
    let multi_selectors = document.querySelectorAll('select.multiple-select');
    

    multi_selectors.forEach(selector => {
        console.log('populating multi-selector')
        
        data.forEach(item => {
            if (item['name'] != null) {
                let id = item['iri'];
                let text = item['name'].concat(' (', item['scientificName'], ')');
                let option = new Option(text, id, false, false) 
                selector.append(option);
            }
        })
    })
}

addEventListener('DOMContentLoaded', (event) =>
{
    // var data = [];
    $.get({
        url: "http://localhost:8080/getPlants",
        headers: {'Access-Control-Allow-Origin':'*'}, // <-------- set this
        dataType: 'json', // // <-------- use JSON
        success: function(response){
            parsePlants(response);
        },
        error: function(xhr, status, error) {
            window.alert(xhr.status,status,error);
            
        }
    });
});
