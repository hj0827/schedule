import request from "./request";
const http = new request({
    // baseURL: 'http://192.168.80.21:8089',
    baseURL: 'http://localhost:8089',
    timeout: 10000
})

export default http