import axios from 'axios';

export default class ProfilService {
    API_URL = process.env.VUE_APP_PROFIL_SERVICE_URI;
    token = "";
    
    constructor(url, token) {
        this.API_URL = url;
        this.token = token;
    }

    getDisponibilite() {
        const url = `${this.API_URL}/FR_FR/disponibilite`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            )
            .then(response => response.data);
    }

    getProfil() {
        const url = `${this.API_URL}/FR_FR`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            )
            .then(response => response.data);
    }

    mergeProfil(profil) {
        const url = `${this.API_URL}/FR_FR`;
        return axios
            .post(url, profil,{
                headers: {
                  "content-type": "application/json",
                  authorization: "Bearer " + this.token,
                },
              }
            )
            .then(response => response.data)
            .catch(e => {
                this.errors.push(e)
            })
    }

    changePassword(password) {
        const url = `${this.API_URL}/FR_FR/password`;
        return axios
            .post(url, 
                password, 
                {
                headers: {
                  "content-type": "application/json",
                  authorization: "Bearer " + this.token,
                },
              }
            )
            .then(response => response.data)
            .catch(e => {
                this.errors.push(e)
            })
    }
}