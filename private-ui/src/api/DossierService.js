import axios from 'axios';

export default class DossierService {
    API_URL = process.env.VUE_APP_DOSSIER_SERVICE_URI;
    token = "";
    
    constructor(url, token) {
        this.API_URL = url;
        this.token = token;
    }


    verifDossierCreate() {
        const url = `${this.API_URL}/FR_FR/verif/create`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            );
    }


    getDossiers() {
        const url = `${this.API_URL}/FR_FR/all`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            );
    }


    
    getDossiersActifs() {
        const url = `${this.API_URL}/FR_FR/actif`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            );
    }



    getDossierById(id) {
        const url = `${this.API_URL}/FR_FR/${id}`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            );
    }

    
    mergeDossier(dossier) {
        const url = `${this.API_URL}/FR_FR`;
        return axios
            .post(url, dossier,
                {
                headers: {
                  "content-type": "application/json",
                  authorization: "Bearer " + this.token,
                },
              }
            );
    }

    addDocumentToDossier(dossierId, document) {
        const url = `${this.API_URL}/FR_FR/${dossierId}/document`;
        return axios
            .post(url, document,
                {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                },
                }
            );
    }
}