
import explainResult from './explain-result.js'
//  TODO: get API call. This should replace the function below

function parseResult(message,plantlist) {
    
    // console.log(message)
    $('#ResultTable tr').remove();
    let res_table = $('table#ResultTable')[0];
    let col_names = Object.keys(message[0]);
    let header = res_table.insertRow(0);
    for (let i = 0; i < col_names.length; i++) {
        let col_name = header.insertCell(i);
        col_name.innerHTML = col_names[i];
    }
    let explain_col = header.insertCell(col_names.length);
    // explain_col.innerHTML = 'explanation'

    for (let i = 0; i < message.length; i++) {
        let r = res_table.insertRow(i+1);
        for (let j = 0; j < col_names.length; j++) {
            let cell = r.insertCell(j);
            cell.innerHTML = message[i][col_names[j]];
        }
                
        if (message[i]['result']=="true" ){
            let explain_button = r.insertCell(col_names.lenth);
            let button = $('<button />', {
            class: 'btn btn-primary btn-block',
            type: 'button',
            id: message[i]['property'],
            click: explainResult,
            text: 'Explain!'
          });
          explain_button.append(button[0])
        }

        
        // let button = document.createElement("BUTTON");
        // button.addEventListener("click", )
        // button.click(function(){
        //     explainResult(must_ids,message[i])
        // });

        

    }

    

    // let rows = []
    // // rows.push(header)
    // for (let i = 0; i < cols.length; i++) {
    //     let col_name = header.insertCell(i)
    //     col_name.innerHTML = cols[i]
    //     let elements = message[cols[i]]
    //     console.log(elements.length)
    //     for (let j = 0; j < elements.length; j++){
    //         if (rows.length <= j){
    //             let r = res_table.insertRow(j+1)
    //             for (let k = 0; k < i; k++) {
    //                 r.insertCell(k)
    //             }
    //             rows.push(r)
    //         } 
    //         let cell = rows[j].insertCell(i)
    //         cell.innerHTML = elements[j]
    //     }
    // }
    // window.alert(message);

}

export default parseResult
