
// TODO: Replace this with an API call
import data from '../data/taxon_product.js';

addEventListener('DOMContentLoaded', (event) =>
{
    let multi_selectors = document.querySelectorAll('select.multiple-select');

    multi_selectors.forEach(selector => {
        data.forEach(item => {
            let id = item['item'];
            let text = item['productLabel'].concat(' (', item['taxon'], ')');
            let option = new Option(text, id, false, false) 
            selector.append(option);
        })
    })
});
