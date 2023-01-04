import axios from 'axios';

export default class RefPrestationService {
    API_URL = process.env.VUE_APP_REF_PRESTATION_SERVICE_URI;
    token = "";
    
    constructor(url, token) {
        this.API_URL = url;
        this.token = token;
    }

    getPrestations() {
        const url = `${this.API_URL}/FR_FR/all`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            )
    }

    mergePrestation(prestation) {
        const url = `${this.API_URL}/FR_FR`;
        return axios
            .post(url, prestation, {
                headers: {
                  "content-type": "application/json",
                  authorization: "Bearer " + this.token,
                },
              }
            )
    }
}