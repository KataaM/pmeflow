import axios from "axios";

export default class RefParametreService {
  API_URL = process.env.VUE_APP_PARAMETRE_SERVICE_URI;
  token = "";

  constructor(url, token) {
    this.API_URL = url;
    this.token = token;
  }

  getParametres(){
    const url = `${this.API_URL}/params/`;
    return axios.get(url, {
          headers: {
              "content-type": "application/json",
              authorization: "Bearer " + this.token,
          }}
      );
  }

  getParametreByCode(serviceId, code){
    const url = `${this.API_URL}/${serviceId}/param/${code}`;
    return axios.get(url, {
          headers: {
              "content-type": "application/json",
              authorization: "Bearer " + this.token,
          }}
      );

  }

  merge(param){
    const url = this.API_URL + "/param/sync";
    return axios.post(url, param, {
          headers: {
              "content-type": "application/json",
              authorization: "Bearer " + this.token,
          }}
      );
  }
  
  remove(key) {
    const url = this.API_URL + "/keys/" + key;
    return axios.delete(url, {
          headers: {
              "content-type": "application/json",
              authorization: "Bearer " + this.token,
          }}
      );
  }
}
