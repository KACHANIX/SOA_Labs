export function GetServerUrl() {
    return "https://localhost:8163/1/organizations"
}

var basic2Address = "https://localhost:8164/2/"

export function fireAddress() {
    return basic2Address + "orgmanager/fire/all/";
}

export function acquiseAddress() {
    return basic2Address + "orgmanager/acquise/";
}


// export function keyPressHandler(event, id) {
//     var charCode = event.keyCode;
//     var key = event.key;
//     var str = document.getElementById(id).value;
//     console.log(str);
//     // Prevent EEEEEEEE, +
//     var list = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-', '.']
//
//     if (((str.includes('-') || str != '') && key == '-') || key == 'e' || key == 'E')
//         event.preventDefault();
// }

// export function textKeyPressHandler(event) {
//     var charCode = event.keyCode;
//     if (event.shiftKey && charCode == 55
//         || event.shiftKey && charCode == 53
//         || event.shiftKey && charCode == 51
//         || event.shiftKey && charCode == 49
//     )
//         event.preventDefault();
// }