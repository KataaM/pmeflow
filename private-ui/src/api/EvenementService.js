import axios from 'axios';

export default class EvenementService {
    API_URL = process.env.VUE_APP_EVENEMENT_SERVICE_URI;
    token = "";
    
    constructor(url, token) {
        this.API_URL = url;
        this.token = token;
    }


    getEvenements() {
        const url = `${this.API_URL}/FR_FR/all`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            );
    }


    getEvenementById(id) {
        const url = `${this.API_URL}/FR_FR/${id}`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            );
    }

    
    mergeEvenement(evenement) {
        const url = `${this.API_URL}/FR_FR`;
        return axios
            .post(url, evenement,
                {
                headers: {
                  "content-type": "application/json",
                  authorization: "Bearer " + this.token,
                },
              }
            );
    }


    removeEvenement(id) {
        const url = `${this.API_URL}/FR_FR/${id}`;
        return axios
            .delete(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            );
    }
}