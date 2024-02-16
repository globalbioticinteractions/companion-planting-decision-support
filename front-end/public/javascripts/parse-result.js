
//  TODO: get API call. This should replace the function below

function parseResult(message) {
    $('#ResultTable tr').remove();
    let res_table = $('table#ResultTable')[0]
    let cols = Object.keys(message)
    let rows = []
    let header = res_table.insertRow(0)
    // rows.push(header)
    for (let i = 0; i < cols.length; i++) {
        let col_name = header.insertCell(i)
        col_name.innerHTML = cols[i]
        let elements = message[cols[i]]
        console.log(elements.length)
        for (let j = 0; j < elements.length; j++){
            if (rows.length <= j){
                let r = res_table.insertRow(j+1)
                for (let k = 0; k < i; k++) {
                    r.insertCell(k)
                }
                rows.push(r)
            } 
            let cell = rows[j].insertCell(i)
            cell.innerHTML = elements[j]
        }
    }
}

export default parseResult