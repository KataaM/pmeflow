import axios from 'axios';

export default class AbonnementService {
    API_URL = process.env.VUE_APP_DOSSIER_SERVICE_URI;
    token = "";
    
    constructor(url, token) {
        this.API_URL = url;
        this.token = token;
    }

    getAbonnements() {
        const url = `${this.API_URL}/FR_FR/all`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            );
    }

    mergeAbonnement(abonnement) {
        const url = `${this.API_URL}/FR_FR`;
        return axios
            .post(url, abonnement,
                {
                headers: {
                  "content-type": "application/json",
                  authorization: "Bearer " + this.token,
                },
              }
            );
    }

    resilierAbonnement(abonnementId, motif) {
        const url = `${this.API_URL}/FR_FR/${abonnementId}`;
        return axios
            .delete(url,
                {
                    headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                    },
                    data:motif
                }
            );
    }
}