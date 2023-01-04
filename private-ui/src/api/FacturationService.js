import axios from 'axios';

export default class FacturationService {
    API_URL = process.env.VUE_APP_FACTURATION_SERVICE_URI;
    token = "";
    
    constructor(url, token) {
        this.API_URL = url;
        this.token = token;
    }

    
    
    getTransactionsOfSubscription(abonnementId) {
        const url = `${this.API_URL}/FR_FR/abonnements/${abonnementId}/transactions`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            );
    }

    
    
    getTransactionsOfPrestation(prestationId) {
        const url = `${this.API_URL}/FR_FR/prestation/${prestationId}`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            );
    }
}