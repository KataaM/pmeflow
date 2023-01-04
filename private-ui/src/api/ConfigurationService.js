import axios from 'axios';

export default class ConfigurationService {
    API_URL = process.env.VUE_APP_CONFIGURATION_SERVICE_URI;
    token = "";
    
    constructor(url, token) {
        this.API_URL = url;
        this.token = token;
    }

    getParametres() {
        const url = `${this.API_URL}/FR_FR/all`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            )
    }

    mergeParametre(parametre) {
        const url = `${this.API_URL}/FR_FR`;
        return axios
            .post(url, parametre, {
                headers: {
                  "content-type": "application/json",
                  authorization: "Bearer " + this.token,
                },
              }
            )
    }
}