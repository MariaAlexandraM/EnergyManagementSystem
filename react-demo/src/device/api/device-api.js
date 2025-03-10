import {HOST} from '../../commons/hosts';
import RestApiClient from "../../commons/api/rest-client";


const endpoint = {
    device: '/device'
};

function getDevices(callback) {
    let request = new Request(HOST.device_backend_api + endpoint.device, {
        method: 'GET',
    });
    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function getDeviceById(params, callback){
    let request = new Request(HOST.device_backend_api + endpoint.device + params.id, {
       method: 'GET'
    });

    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function addDevice(device, callback){
    let request = new Request(HOST.device_backend_api + endpoint.device , {
        method: 'POST',
        headers : {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(device)
    });

    console.log("URL: " + request.url);

    RestApiClient.performRequest(request, callback);
}

function updateDevice(device, callback) {
    let request = new Request(HOST.device_backend_api + endpoint.device + '/' + device.id, {
        method: 'PUT',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(device),  
    });

    console.log("URL: " + request.url);

    RestApiClient.performRequest(request, callback);
}


// function deleteDevice(device, callback){
//     let request = new Request(HOST.device_backend_api + endpoint.device  + '/' + device.id, {
//         method: 'DELETE',
//         headers : {
//             'Accept': 'application/json',
//             'Content-Type': 'application/json',
//         },
//         // body: JSON.stringify(device)
//         body: null
//     });

//     console.log("URL: " + request.url);

//     RestApiClient.performRequest(request, callback);
// }

function deleteDevice(deviceId, callback){
    let request = new Request(HOST.device_backend_api + endpoint.device  + '/' + deviceId, {
        method: 'DELETE',
        headers : {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        // body: JSON.stringify(device)
        body: null
    });

    console.log("URL: " + request.url);

    RestApiClient.performRequest(request, callback);
}

function getMaxHECByDeviceId(deviceId, callback) {
    let request = new Request(HOST.device_backend_api + `/device/${deviceId}`, {
        method: 'GET',
    });

    console.log("Fetching maxHEC for device:", deviceId);
    
    RestApiClient.performRequest(request, (response) => {
        if (response && response.maxHEC !== undefined) {
            callback(response.maxHEC);
        } else {
            console.error("Error: maxHEC not found for device", deviceId);
            callback(null);
        }
    });
}



export {
    getDevices,
    getDeviceById,
    addDevice,
    updateDevice,
    deleteDevice,
    getMaxHECByDeviceId
};
