import axios from 'axios';

export default class UtilisateurService {
    API_URL = process.env.VUE_APP_UTILISATEUR_SERVICE_URI;
    token = "";
    
    constructor(url, token) {
        this.API_URL = url;
        this.token = token;
    }

    getJuristes() {
        const url = `${this.API_URL}/FR_FR/juristes`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            );
    }

    getUtilisateurs() {
        const url = `${this.API_URL}/FR_FR/all`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            )
            .then(response => response.data);
    }

    mergeUtilisateur(Utilisateur) {
        const url = `${this.API_URL}/FR_FR`;
        return axios
            .post(url, Utilisateur,
                {
                headers: {
                  "content-type": "application/json",
                  authorization: "Bearer " + this.token,
                },
              }
            );
    }
}