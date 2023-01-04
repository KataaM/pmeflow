import axios from 'axios';

export default class RefFormulaireService {
    API_URL = process.env.VUE_APP_REF_FORMULAIRE_SERVICE_URI;
    token = "";
    
    constructor(url, token) {
        this.API_URL = url;
        this.token = token;
    }

    getFormulaires() {
        const url = `${this.API_URL}/FR_FR/all`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            )
    }

    mergeFormulaire(formulaire) {
        const url = `${this.API_URL}/FR_FR`;
        return axios
            .post(url, formulaire, {
                headers: {
                  "content-type": "application/json",
                  authorization: "Bearer " + this.token,
                },
              }
            )
    }
}