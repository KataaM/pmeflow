import axios from 'axios';

export default class RendezVousService {
    API_URL = process.env.VUE_APP_RENDEZ_VOUS_SERVICE_URI;
    token = "";
    
    constructor(url, token) {
        this.API_URL = url;
        this.token = token;
    }


    getRendezVous() {
        const url = `${this.API_URL}/FR_FR/all`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            );
    }


    getRendezVousForDossier(dossierId) {
        const url = `${this.API_URL}/FR_FR/dossier/${dossierId}`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            );
    }


    getRendezVousActifs() {
        const url = `${this.API_URL}/FR_FR/actifs`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            );
    }


    determinerRdvsPossiblesForJuriste(start, juristeId) {
        const url = `${this.API_URL}/FR_FR/juriste/${juristeId}/dispo?start=${start}`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json"
                }}
            )
    }

    
    mergeRendezVous(rendezVous) {
        const url = `${this.API_URL}/FR_FR`;
        return axios
            .post(url, rendezVous,
                {
                headers: {
                  "content-type": "application/json",
                  authorization: "Bearer " + this.token,
                },
              }
            );
    }
}