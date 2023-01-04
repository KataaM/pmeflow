import axios from 'axios';

export default class PrestationService {
    API_URL = process.env.VUE_APP_PRESTATION_SERVICE_URI;
    token = "";
    
    constructor(url, token) {
        this.API_URL = url;
        this.token = token;
    }

    getPrestationById(id) {
        const url = `${this.API_URL}/FR_FR/${id}`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            );
    }

    getPrestationsByDossier(dossierId) {
        const url = `${this.API_URL}/FR_FR/dossier/${dossierId}`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            );
    }

    getPrestationsByAbonnement(abonnementId) {
        const url = `${this.API_URL}/FR_FR/abonnement/${abonnementId}`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            );
    }

    getPrestations() {
        const url = `${this.API_URL}/FR_FR/all`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            );
    }

    mergePrestation(prestation) {
        const url = `${this.API_URL}/FR_FR`;
        return axios
            .post(url, prestation,
                {
                headers: {
                  "content-type": "application/json",
                  authorization: "Bearer " + this.token,
                },
              }
            );
    }
    
    removePrestation(prestationId){
        const url = `${this.API_URL}/FR_FR/${prestationId}`;
        return axios
            .delete(url,
                {
                headers: {
                  "content-type": "application/json",
                  authorization: "Bearer " + this.token,
                },
              }
            );
    }
    
    refundPrestation(prestationId){
        const url = `${this.API_URL}/FR_FR/${prestationId}`;
        return axios
            .put(url,
                {
                headers: {
                  "content-type": "application/json",
                  authorization: "Bearer " + this.token,
                },
              }
            );
    }
}