import {HOST} from '../../commons/hosts';
import RestApiClient from "../../commons/api/rest-client";


const endpoint = {
    user: '/user',
    login: '/login'
};

function getUsers(callback) {
    let request = new Request(HOST.user_backend_api + endpoint.user, {
        method: 'GET',
    });
    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function getUserById(params, callback){
    let request = new Request(HOST.user_backend_api + endpoint.user + params.id, {
       method: 'GET'
    });

    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function addUser(user, callback){
    let request = new Request(HOST.user_backend_api + endpoint.user , {
        method: 'POST',
        headers : {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user)
    });

    console.log("URL: " + request.url);

    RestApiClient.performRequest(request, callback);
}

function updateUser(user, callback){
    let request = new Request(HOST.user_backend_api + endpoint.user + '/' + user.id, {
        method: 'PUT',
        headers : {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user)
    });

    console.log("URL: " + request.url);

    RestApiClient.performRequest(request, callback);
}

function deleteUser(user, callback){
    let request = new Request(HOST.user_backend_api + endpoint.user  + '/' + user.id, {
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

function login(credentials, callback) {
    let request = new Request(HOST.user_backend_api + endpoint.login, {

        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials)
    });

    RestApiClient.performRequest(request, (result, status, err) => {
       
        callback(result, status, err);
    });
}

export {
    getUsers,
    getUserById,
    addUser,
    login,
    updateUser,
    deleteUser
};
