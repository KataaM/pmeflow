import axios from 'axios';

export default class ClientService {
    API_URL = process.env.VUE_APP_CLIENT_SERVICE_URI;
    token = "";
    
    constructor(url, token) {
        this.API_URL = url;
        this.token = token;
    }

    getClients() {
        const url = `${this.API_URL}/FR_FR/all`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            );
    }

    mergeClient(client) {
        const url = `${this.API_URL}/FR_FR`;
        return axios
            .post(url, client,
                {
                headers: {
                  "content-type": "application/json",
                  authorization: "Bearer " + this.token,
                },
              }
            );
    }
}