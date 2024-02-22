
// TODO: Replace this with an API call
// import data from '../data/taxon_product.js';
var data = [];
$.get({
    url: "http://localhost:8080/getPlants",
    headers: {'Access-Control-Allow-Origin':'*'}, // <-------- set this
    dataType: 'json', // // <-------- use JSONP
    success: function(response){
        data = response;
        console.log(data);
    },
    // error: function(xhr, status, error) {
    //     window.alert(xhr.status,status,error);
        
    // }
});


addEventListener('DOMContentLoaded', (event) =>
{
    let multi_selectors = document.querySelectorAll('select.multiple-select');

    multi_selectors.forEach(selector => {
        
        
        
        data.forEach(item => {
            let id = item['iri'];
            let text = item['name'].concat(' (', item['scientificName'], ')');
            let option = new Option(text, id, false, false) 
            selector.append(option);
        })
    })
});
